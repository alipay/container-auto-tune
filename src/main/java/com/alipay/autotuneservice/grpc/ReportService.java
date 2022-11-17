/*
 * Ant Group
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.autotuneservice.grpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.base.cache.LocalCache;
import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.grpc.handler.ActionParam;
import com.alipay.autotuneservice.grpc.handler.RuleHandlerComponent;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.util.AgentConstant;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.auto.tune.client.*;
import com.auto.tune.client.CommandResponse.Builder;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

/**
 * @author dutianze
 * @version ReportService.java, v 0.1 2021年12月23日 20:57 dutianze
 */
@Slf4j
@GrpcService(interceptors = {GzipInterceptor.class})
public class ReportService extends ReportServiceGrpc.ReportServiceImplBase {

    @Autowired
    private RuleHandlerComponent       ruleHandlerComponent;
    @Autowired
    private JvmMonitorMetricRepository jvmMonitorMetricRepository;
    @Autowired
    private StorageInfoService         storageInfoService;
    @Autowired
    private AppInfoService             appInfoService;
    @Autowired
    private LocalCache<Object, Object> localCache;

    @Override
    public void report(MetricsGrpcRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("report system:{}, meta:{}, jstate:{}", request.getSystemCommon(), request.getMetaMemoryMetric(),
                request.getJstateMetrics());
        GrpcCommon grpcCommon = GrpcCommon.build(request.getSystemCommon());
        //判断应用是否在列表中,不在者进行添加
        Integer appId = appInfoService.checkAppName(grpcCommon.getAppName(), grpcCommon.getNamespace(), grpcCommon.getAccessToken(),
                grpcCommon.getServerType());
        grpcCommon.setAppId(appId);
        //判断单机是否存在
        appInfoService.checkVm(grpcCommon);
        // metrics
        jvmMonitorMetricRepository.insertGCData(ConvertUtils.convert2JvmMonitorMetricData(request, appInfoService));
        // app tag
        AppTag appTag = AppTag.builder()
                .withJavaVersion(grpcCommon.getJavaVersion())
                .withCollector(GarbageCollector.matchGarbageCollector(grpcCommon.getCollectors()))
                .build();
        appInfoService.patchAppTag(grpcCommon, appTag);
        // return
        CommonResponse resp = CommonResponse.newBuilder()
                .setSuccess(true)
                .setMessage("success receive metrics")
                .build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void heartBeat(SystemCommonGrpc request, StreamObserver<CommandResponse> responseObserver) {
        log.info("heartBeat unionCode:{}", request.getUnionCode());
        GrpcCommon grpcCommon = GrpcCommon.build(request);
        List<ActionParam> actionParams = ruleHandlerComponent.checkRuleFlag(grpcCommon);
        Builder builder = CommandResponse.newBuilder()
                .setSuccess(true)
                .setMessage("success receive heartBeat")
                .setType(ReportType.REPORT_TYPE_OK)
                .setUnionCode(grpcCommon.getUnionCode());
        log.info("actionParams is: {}", JSON.toJSONString(actionParams));
        //更新缓存
        refreshCache(request.getHostname());
        //处理事件
        if (CollectionUtils.isNotEmpty(actionParams)) {
            builder.setType(ReportType.REPORT_TYPE_EXEC_COMMAND);
            for (ActionParam actionParam : actionParams) {
                if (StringUtils.isEmpty(actionParam.getSessionId())) {
                    return;
                }
                ExecuteStep executeStep = ExecuteStep.newBuilder()
                        .setStepClass(actionParam.getRuleAction().getStepClassName())
                        .putAllArgs(actionParam.getParams())
                        .setSessionId(actionParam.getSessionId())
                        .build();
                builder.addExecuteSteps(executeStep);
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<FileRequest> upload(StreamObserver<FileResponse> responseObserver) {
        return new StreamObserver<FileRequest>() {
            // upload core variables
            private OutputStream writer;
            private int status = 200;
            private Path filePath;
            private String fileName;
            private GrpcCommon grpcCommon;
            private String sessionId;

            @Override
            public void onNext(FileRequest request) {
                log.info("ReportService upload onNext, offset:{}", request.getOffset());
                try {
                    this.sessionId = request.getSessionId();
                    // init
                    if (filePath == null) {
                        grpcCommon = GrpcCommon.build(request.getSystemCommon());
                        fileName = request.getName();
                        filePath = Files.createTempFile("", request.getName());
                    }
                    writer = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    writeFile(writer, request.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                    this.onError(e);
                }
                FileResponse successful = FileResponse.newBuilder().setStatus(status).setMessage("successful").build();
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
                FileResponse successful = FileResponse.newBuilder().setStatus(status).setMessage("successful").build();
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
                try {
                    storageInfoService.saveDumpFile(grpcCommon, filePath, fileName, sessionId);
                } catch (Exception e) {
                    log.error("uploadFileToS3 error, filePath:{}, fileName:{}", filePath, fileName, e);
                }
            }
        };
    }

    @Override
    public void callBack(CallBackRequest request, StreamObserver<CommonResponse> responseObserver) {
        //根据sessionId,更新状态
        String sessionId = request.getSessionId();
        Map<String, String> tagsMap = request.getTagsMap();
        log.info("callBack sessionId:{},tagsMap:{}", sessionId, JSONObject.toJSON(tagsMap));
        if (tagsMap.containsKey(AgentConstant.STATUS)) {
            ruleHandlerComponent.updateBySessionId(sessionId, tagsMap);
        }
        if (tagsMap.containsKey(AgentConstant.RESULT)) {
            ruleHandlerComponent.updateResult(sessionId, tagsMap, null);
        }
        responseObserver.onNext(CommonResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private void writeFile(OutputStream writer, ByteString content) throws IOException {
        writer.write(content.toByteArray());
        writer.flush();
    }

    private void refreshCache(String hostName) {
        localCache.put(String.format(AgentConstant.AGENT_HEART_KEY, hostName), System.currentTimeMillis());
    }
}