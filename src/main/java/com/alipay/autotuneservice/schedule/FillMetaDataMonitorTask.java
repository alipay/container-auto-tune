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
package com.alipay.autotuneservice.schedule;

import com.alipay.autotuneservice.configuration.EnvHandler;
import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.AppMonitorInfo;
import com.alipay.autotuneservice.dynamodb.bean.NodeMonitorInfo;
import com.alipay.autotuneservice.dynamodb.bean.PodMonitorInfo;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import com.alipay.autotuneservice.service.MetaAnalyzeService;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.TraceIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

/**
 * @author huoyuqi
 * @version FillMetaDataMonitorTask.java, v 0.1 2022年04月19日 12:50 下午 huoyuqi
 */
@Slf4j
@Component
public class FillMetaDataMonitorTask {

    private static final String LOCK_LEY = "FillMetaDataMonitorTask_yifan";

    @Autowired
    private MetaAnalyzeService  metaAnalyzeService;
    @Autowired
    private K8sAccessTokenInfo  k8sAccessTokenInfo;
    @Autowired
    private RedisClient         redisClient;
    @Autowired
    private EnvHandler          envHandler;
    @Autowired
    private NosqlService        nosqlService;

    // TODO 枫水测试完取消注释定时任务
    //@Scheduled(cron = "0 1/1 * * * ?")
    public void doTask() {
        if (envHandler.isDev()) {
            return;
        }
        try {
            TraceIdGenerator.generateAndSet();
            redisClient.doExec(LOCK_LEY, () -> {
                log.info("FillMetaDataMonitorTask#doTask scheduled start");
                //1.检查是否新增新集群
                List<K8sAccessTokenInfoRecord> k8sAccessTokenInfoRecords = k8sAccessTokenInfo.getK8sAccessTokenInfoRecord();
                k8sAccessTokenInfoRecords.forEach(this::accept);
            });

        } finally {
            TraceIdGenerator.clear();
        }
    }

    @Async("dynamoDBTaskExecutor")
    void accept(K8sAccessTokenInfoRecord item) {
        K8sClient eksClient = null;
        try {
            eksClient = metaAnalyzeService.createEksClient(item.getAccessToken(),
                item.getClusterName());
            insertNode(eksClient);
            HashSet<String> appSet = insertApp(eksClient);
            insertPod(eksClient, appSet);
        } catch (Exception e) {
            log.error("accept is error", e);
        } finally {
            if (eksClient != null) {
                eksClient.close();
            }
        }
    }

    private void insertPod(K8sClient eksClient, HashSet<String> appSet) {
        eksClient.listPods().getItems().forEach(pod -> {
            try {
                String appName1 = convertAppName(pod.getMetadata().getName());
                if (appSet.contains(appName1)) {
                    PodMonitorInfo podMonitorInfo = new PodMonitorInfo();
                    podMonitorInfo.setPodName(pod.getMetadata().getName());
                    podMonitorInfo.setGmtCreated(DateUtils.truncate2Minute(System.currentTimeMillis()));
                    podMonitorInfo.setAppName(appName1);
                    podMonitorInfo.setNodName(pod.getSpec().getNodeName());
                    podMonitorInfo.setNameSapce(pod.getMetadata().getNamespace());
                    try {
                        podMonitorInfo.setCpu(eksClient.getPodCpu(pod.getMetadata().getNamespace(), pod.getMetadata().getName()));
                    } catch (Exception e) {
                        podMonitorInfo.setCpu(-1d);
                    }
                    try {
                        podMonitorInfo.setMem(eksClient.getPodMem(pod.getMetadata().getNamespace(), pod.getMetadata().getName()));
                    } catch (Exception e) {
                        podMonitorInfo.setMem(-1d);
                    }
                    podMonitorInfo.setPodStatus(pod.getStatus().getPhase());
                    nosqlService.insert(podMonitorInfo, "podMonitorInfo");
                }
            } catch (Exception e) {
                log.error("FillMetaDataMonitorTask#insertPod execute insert pod occurs an error", e);
            }
        });
    }

    private HashSet<String> insertApp(K8sClient eksClient) {
        HashSet<String> appSet = new HashSet<>();
        eksClient.listDeployment().getItems().forEach(app -> {
            try {
                AppMonitorInfo appMonitorInfo = new AppMonitorInfo();
                appMonitorInfo.setAppName(app.getMetadata().getName());
                appMonitorInfo.setGmtCreated(DateUtils.truncate2Minute(System.currentTimeMillis()));
                appMonitorInfo.setNameSpace(app.getMetadata().getNamespace());
                nosqlService.insert(appMonitorInfo, "appMonitorInfo");
                appSet.add(app.getMetadata().getName());
            } catch (Exception e) {
                log.error("FillMetaDataMonitorTask#insertApp execute insert app occurs an error", e);
            }
        });
        return appSet;
    }

    private void insertNode(K8sClient eksClient) {
        eksClient.listNode().getItems().forEach(node -> {
            try {
                NodeMonitorInfo nodeMonitorInfo = new NodeMonitorInfo();
                nodeMonitorInfo.setNodeName(node.getMetadata().getName());
                nodeMonitorInfo.setHost(node.getMetadata().getName());
                nodeMonitorInfo.setGmtCreated(DateUtils.truncate2Minute(System.currentTimeMillis()));
                nosqlService.insert(nodeMonitorInfo, "nodeMonitorInfo");
            } catch (Exception e) {
                log.error("FillMetaDataMonitorTask#insertNode execute insert node occurs an error", e);
            }
        });
    }

    private String convertAppName(String appName) {
        try {
            return appName.substring(0, StringUtils.lastOrdinalIndexOf(appName, "-", 2));
        } catch (Exception e) {
            return "";
        }
    }
}