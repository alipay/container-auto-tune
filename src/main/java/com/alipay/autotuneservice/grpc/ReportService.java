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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.base.cache.LocalCache;
import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.ThreadpoolMonitorMetricDataRecord;
import com.alipay.autotuneservice.dynamodb.bean.ThreadPoolMonitorMetricData;
import com.alipay.autotuneservice.grpc.handler.ActionParam;
import com.alipay.autotuneservice.grpc.handler.RuleHandlerComponent;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.report.ReportActionType;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.service.alarmManger.AviatorAlarm;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.service.chronicmap.ChronicleMapService;
import com.alipay.autotuneservice.util.AgentConstant;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.auto.tune.client.ActionReportReq;
import com.auto.tune.client.CallBackRequest;
import com.auto.tune.client.CommandResponse;
import com.auto.tune.client.CommandResponse.Builder;
import com.auto.tune.client.CommonResponse;
import com.auto.tune.client.ExecuteStep;
import com.auto.tune.client.FileRequest;
import com.auto.tune.client.FileResponse;
import com.auto.tune.client.MetricsGrpcRequest;
import com.auto.tune.client.ReportServiceGrpc;
import com.auto.tune.client.ReportType;
import com.auto.tune.client.RequestMessage;
import com.auto.tune.client.ResponseMessage;
import com.auto.tune.client.SystemCommonGrpc;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;

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
    private JvmMonitorMetricRepository jvmMetriRepository;
    @Autowired
    private StorageInfoService         storageInfoService;
    @Autowired
    private AppInfoService             appInfoService;
    @Autowired
    private ChronicleMapService        redisClient;
    @Autowired
    private AsyncTaskExecutor          podEventExecutor;
    @Autowired
    private AviatorAlarm               aviatorAlarm;
    @Autowired
    private LocalCache<Object, Object> localCache;

    @Override
    public void report(MetricsGrpcRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("report system:{}, meta:{}, jstate:{}", request.getSystemCommon(), request.getMetaMemoryMetric(),
                request.getJstateMetrics());
        GrpcCommon grpcCommon = GrpcCommon.build(request.getSystemCommon());
        //判断应用是否在列表中,不在者进行添加
        Integer appId = appInfoService.checkAppName(grpcCommon.getAppName(), grpcCommon.getNamespace(), grpcCommon.getAccessToken(),
                grpcCommon.getJavaVersion(), grpcCommon.getJvmConfig());
        grpcCommon.setAppId(appId);

        //判断单机是否存在
        log.info("checkVm grpcCommon:{}", grpcCommon);
        appInfoService.checkVm(grpcCommon);
        //判断单机是否存在
        appInfoService.checkJavaInfo(grpcCommon);

        try {
            aviatorAlarm.invoke(appId);
        } catch (Exception e) {
            log.error("report occurs an error, appId: {}", appId, e);
        }
        // metrics
        log.info("insert gcData");
        jvmMetriRepository.insertGCData(ConvertUtils.convert2JvmMonitorMetricData(request, appInfoService));
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
        if (CollectionUtils.isNotEmpty(request.getThreadPoolReqList())) {
            podEventExecutor.execute(() -> {
                try {
                    List<ThreadpoolMonitorMetricDataRecord> metricData = ConvertUtils.convert2ThreadPoolMonitorMetricData(
                            request.getThreadPoolReqList(),
                            grpcCommon.getAppName(),
                            grpcCommon.getHostname(), grpcCommon.getTimestamp());
                    //缓存
                    List<ThreadPoolMonitorMetricData> threadPoolMetricData = ConvertUtils.convert2ThreadPoolMonitorMetricData(metricData);
                    jvmMetriRepository.initThreadPoolCache(threadPoolMetricData);
                    //doThreadPool Monitor
                    jvmMetriRepository.insertThreadPoolData(metricData);
                } catch (Exception e) {
                    log.error("heartBeat is error", e);
                }
            });
        }
        List<ActionParam> actionParams = ruleHandlerComponent.checkRuleFlag(grpcCommon);
        Builder builder = CommandResponse.newBuilder()
                .setSuccess(true)
                .setMessage("success receive heartBeat")
                .setType(ReportType.REPORT_TYPE_OK)
                .setUnionCode(grpcCommon.getUnionCode());
        log.info("actionParams is: {}", JSON.toJSONString(actionParams));
        //更新缓存
        refreshCache(request.getHostname());
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

    @Override
    public void actionReport(ActionReportReq request, StreamObserver<CommonResponse> responseObserver) {
        try {
            log.info("actionReport request:{}", request);
            String actionKey = request.getActionKey();
            if (StringUtils.isEmpty(actionKey)) {
                return;
            }
            //根据actionKey处理任务
            ReportActionType.valueOf(actionKey).doFunc(request.getJsonParams());
        } catch (Exception ex) {
            log.error("actionReport is error:{}", ex.getMessage(), ex);
        } finally {
            responseObserver.onNext(CommonResponse.getDefaultInstance());
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<RequestMessage> arthasStreamFunc(StreamObserver<ResponseMessage> responseObserver) {
        return new StreamObserver<RequestMessage>() {
            @Override
            public void onNext(RequestMessage requestMessage) {
                String resultMsg = requestMessage.getReqMsg();
                log.info("[收到客户端消息]: " + resultMsg);
                if (StringUtils.equals(resultMsg, "bound")) {
                    log.info("Arthas初始化绑定: " + requestMessage.getHostname());
                    AgentConstant.ARTHAS_STREAM_POOL.put(requestMessage.getHostname(), responseObserver);
                    return;
                }
                if (StringUtils.equals(resultMsg, "broken pipe")) {
                    log.info("Arthas重新建立连接: " + requestMessage.getHostname());
                    AgentConstant.ARTHAS_STREAM_POOL.remove(requestMessage.getHostname());
                    redisClient.set(AgentConstant.generateArthasCallBackKey(requestMessage.getSessionId()),
                            "broken pipe,retry connection!");
                    return;
                }
                if (StringUtils.isEmpty(resultMsg)) {
                    return;
                }
                redisClient.set(AgentConstant.generateArthasCallBackKey(requestMessage.getSessionId()), resultMsg);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.fillInStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    private void writeFile(OutputStream writer, ByteString content) throws IOException {
        writer.write(content.toByteArray());
        writer.flush();
    }

    private void refreshCache(String hostName) {
        localCache.put(String.format(AgentConstant.AGENT_HEART_KEY, hostName), System.currentTimeMillis());
    }
}