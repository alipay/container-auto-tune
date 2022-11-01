/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.grpc;

import com.alipay.autotuneservice.dynamodb.repository.JvmMonitorMetricDataService;
import com.alipay.autotuneservice.grpc.handler.ActionParam;
import com.alipay.autotuneservice.grpc.handler.RuleHandlerComponent;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.auto.tune.client.*;
import com.auto.tune.client.CommandResponse.Builder;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * @author dutianze
 * @version ReportService.java, v 0.1 2021年12月23日 20:57 dutianze
 */
@Slf4j
@GrpcService(interceptors = { GzipInterceptor.class })
public class ReportService extends ReportServiceGrpc.ReportServiceImplBase {

    @Autowired
    private RuleHandlerComponent        ruleHandlerComponent;
    @Autowired
    private JvmMonitorMetricDataService jvmMetriRepository;
    @Autowired
    private StorageInfoService          storageInfoService;
    @Autowired
    private AppInfoService                 appInfoService;

    @Override
    public void report(MetricsGrpcRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("report system:{}, meta:{}", request.getSystemCommon(),
            request.getMetaMemoryMetric());
        GrpcCommon grpcCommon = GrpcCommon.build(request.getSystemCommon());
        //判断accessToken是否存在 TODO 测试先注释掉
        //if (!userInfoService.checkTokenValidity(request.getSystemCommon().getAccessToken())) {
        //    log.info("report receive but this access token:{} not found!", request.getSystemCommon().getAccessToken());
        //    CommonResponse resp = CommonResponse.newBuilder()
        //            .setSuccess(false)
        //            .setMessage("invalid access token")
        //            .build();
        //    responseObserver.onNext(resp);
        //    responseObserver.onCompleted();
        //    return;
        //}

        // metrics
        jvmMetriRepository.insertGCData(ConvertUtils.convert2JvmMonitorMetricData(request,
            appInfoService));
        // app tag
        AppTag appTag = AppTag.builder().withJavaVersion(grpcCommon.getJavaVersion())
            .withCollector(GarbageCollector.matchGarbageCollector(grpcCommon.getCollectors()))
            .build();
        appInfoService.patchAppTag(grpcCommon, appTag);
        // return
        CommonResponse resp = CommonResponse.newBuilder().setSuccess(true)
            .setMessage("success receive metrics").build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void heartBeat(SystemCommonGrpc request, StreamObserver<CommandResponse> responseObserver) {
        GrpcCommon grpcCommon = GrpcCommon.build(request);
        List<ActionParam> actionParams = ruleHandlerComponent.checkRuleFlag(grpcCommon);
        if (CollectionUtils.isNotEmpty(actionParams)) {
            Builder builder = CommandResponse.newBuilder();
            builder.setSuccess(true);
            builder.setMessage("success receive heartBeat");
            builder.setType(ReportType.REPORT_TYPE_EXEC_COMMAND);
            for (ActionParam actionParam : actionParams) {
                ExecuteStep executeStep = ExecuteStep.newBuilder()
                    .setStepClass(actionParam.getRuleAction().getStepClassName())
                    .putAllArgs(actionParam.getParams()).build();
                builder.addExecuteSteps(executeStep);
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

        CommandResponse resp = CommandResponse.newBuilder().setSuccess(true)
            .setMessage("success receive heartBeat").setType(ReportType.REPORT_TYPE_OK).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<FileRequest> upload(StreamObserver<FileResponse> responseObserver) {
        return new StreamObserver<FileRequest>() {
            // upload core variables
            private OutputStream writer;
            private int          status = 200;
            private GrpcCommon   grpcCommon;
            private String       fileName;
            private Path         filePath;

            @Override
            public void onNext(FileRequest request) {
                log.info("ReportService upload onNext, offset:{}", request.getOffset());
                try {
                    // init
                    if (filePath == null) {
                        grpcCommon = GrpcCommon.build(request.getSystemCommon());
                        fileName = grpcCommon.generateFileName(request.getName());
                        filePath = Files.createTempFile("", fileName);
                    }
                    writer = Files.newOutputStream(filePath, StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                    writeFile(writer, request.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                    this.onError(e);
                }
                FileResponse successful = FileResponse.newBuilder().setStatus(status)
                    .setMessage("successful").build();
                responseObserver.onNext(successful);
            }

            @Override
            public void onError(Throwable t) {
                log.error("ReportService upload onError");
                status = 500;
                onCompleted();
            }

            @Override
            public void onCompleted() {
                log.info("ReportService upload onCompleted, file:{}", filePath);
                FileResponse successful = FileResponse.newBuilder().setStatus(status)
                    .setMessage("successful").build();
                responseObserver.onNext(successful);
                responseObserver.onCompleted();
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        writer = null;
                    }
                }
                //try {
                //    //storageInfoService.saveAppLogFile(grpcCommon, filePath, fileName, AppLogType.GC_LOG);
                //} catch (FileNotFoundException e) {
                //    log.error("uploadFileToS3 error, grpcCommon:{}, filePath:{}, fileName:{}", grpcCommon, filePath, fileName, e);
                //}
            }
        };
    }

    private void writeFile(OutputStream writer, ByteString content) throws IOException {
        writer.write(content.toByteArray());
        writer.flush();
    }
}