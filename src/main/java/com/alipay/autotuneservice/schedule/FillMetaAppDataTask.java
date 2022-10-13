/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.schedule;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.configuration.EnvHandler;
import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.common.NodeStatus;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.CommandService;
import com.alipay.autotuneservice.service.MetaAnalyzeService;
import com.alipay.autotuneservice.service.NodeService;
import com.alipay.autotuneservice.service.PodService;
import com.alipay.autotuneservice.util.TraceIdGenerator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version FillMetaAppDataTask.java, v 0.1 2022年04月18日 1:56 下午 huoyuqi
 */
@Slf4j
@Component
public class FillMetaAppDataTask {

    private static final List<String> FILTER_NAMESPACE = ImmutableList.of("kube-system");
    private static final String       LOCK_LEY         = "FillMetaDataTask_V";

    @Autowired
    private MetaAnalyzeService metaAnalyzeService;
    @Autowired
    private AppInfoService     appInfoService;
    @Autowired
    private NodeService        nodeService;
    @Autowired
    private K8sAccessTokenInfo k8sAccessTokenInfo;
    @Autowired
    private RedisClient        redisClient;
    @Autowired
    private EnvHandler         envHandler;
    @Autowired
    private CommandService     commandService;
    @Autowired
    private PodService         podService;

    @Scheduled(fixedRate = 60 * 5000)
    public void doTask() {
        if (envHandler.isDev()) {
            return;
        }
        try {
            TraceIdGenerator.generateAndSet();
            redisClient.doExec(LOCK_LEY, this::invoke);
        } finally {
            TraceIdGenerator.clear();
        }
    }

    void invoke() {
        long startTime = System.currentTimeMillis();
        try {
            log.info("FillMetaAppDataTask invoke start.");
            //处理pod最终一致性逻辑,获取k8s管理的权限信息
            List<K8sAccessTokenInfoRecord> k8sAccessTokenInfoRecords = k8sAccessTokenInfo.getK8sAccessTokenInfoRecord();
            if (CollectionUtils.isEmpty(k8sAccessTokenInfoRecords)) {
                log.info("FillMetaDataTask getK8sAccessTokenInfoRecord res=null");
                return;
            }
            k8sAccessTokenInfoRecords.parallelStream().forEach(item -> {
                K8sClient eksClient = null;
                try {
                    eksClient = metaAnalyzeService.createEksClient(item.getAccessToken(), item.getClusterName());
                    if (eksClient == null) {
                        log.error("获取eksClient为null,{}||{}", item.getAccessToken(), item.getClusterName());
                        return;
                    }
                    updateNode(eksClient.listNode().getItems(), item.getAccessToken());
                    //获取podlist,进行app更新
                    List<Pod> podList = eksClient.listPods().getItems();
                    log.info("accessToken is: {}, clusterName is: {}, podList size is: {}", item.getAccessToken(), item.getClusterName(),
                            podList.size());
                    //按照podList,进行app汇总
                    Map<String, Map<String, String>> appMap = Maps.newConcurrentMap();
                    podList.parallelStream().forEach(pod -> {
                        try {
                            String metaData = pod.getMetadata().getName();
                            log.info("metaData is: {}", metaData);
                            if (StringUtils.isEmpty(metaData)) {
                                return;
                            }
                            try {
                                String appName = metaData.substring(0, StringUtils.lastOrdinalIndexOf(metaData, "-", 2));
                                String nameSpace = pod.getMetadata().getNamespace();
                                log.info("nameSpace is: {}", nameSpace);
                                checkMap(appMap, appName, nameSpace);
                            } catch (Exception e) {
                                //do noting
                            }
                        } catch (Exception e) {
                            //do noting
                            log.error("find app is error", e);
                        }
                    });
                    //进行app更新
                    log.info("alive app:{},accessToken:{},ClusterName:{}", JSONObject.toJSONString(appMap), item.getAccessToken(),
                            item.getClusterName());
                    updateApp(appMap, item.getAccessToken(), item.getClusterName());
                    doReplace(appMap, item.getAccessToken(), item.getClusterName());
                } catch (Exception e) {
                    log.error("eksClient  exec error", e);
                } finally {
                    //执行缓存删除
                    if (eksClient != null) {
                        eksClient.close();
                    }
                }
            });
            log.info("FillMetaAppDataTask#doTask 结束执行任务");
        } catch (Exception ex) {
            log.error("FillMetaAppDataTask is error", ex);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("FillMetaAppDataTask 执行时间为: {}", endTime - startTime);
        }
    }

    private synchronized void checkMap(Map<String, Map<String, String>> appMap, String appName, String nameSpace) {
        if (StringUtils.isEmpty(appName)) {
            return;
        }
        if (StringUtils.isEmpty(nameSpace)) {
            return;
        }
        String key = generateKey(appName, nameSpace);
        if (appMap.containsKey(key)) {
            return;
        }
        if (FILTER_NAMESPACE.contains(nameSpace)) {
            return;
        }
        appMap.put(key, ImmutableMap.of(appName, nameSpace));
    }

    private void updateNode(List<Node> nodeList, String accessToken) {
        if (CollectionUtils.isEmpty(nodeList)) {
            return;
        }
        nodeList.parallelStream().filter(node -> {
            String nodeName = node.getMetadata().getName();
            return nodeService.getByNodeAndAT(nodeName, accessToken) == -1;
        }).forEach(node -> nodeService.insertOrUpdateNode(node.getMetadata().getName(), node.getMetadata().getName(), NodeStatus.ALIVE,
                node.getMetadata().getLabels().toString(), accessToken));
    }

    public void insertPod(String accessToken, String clusterName, K8sClient eksClient, int appId, String nameSpace, String podName,
                          String dHostName, String nodeIP, String nodeName) {
        try {
            log.info("insertPod get pod start!podName={}", podName);
            Pod pod = eksClient.getPod(nameSpace, podName);
            int nodeId = nodeService.getByNodeAndAT(pod.getSpec().getNodeName(), accessToken);
            log.info("insertPod getPodJvm start!podName={}", podName);
            String processJvm = commandService.getPodJvm(podName);
            log.info("insertPod patchAppTag start!podName={}", podName);
            if (StringUtils.isNotEmpty(processJvm)) {
                appInfoService.patchAppTag(appId, AppTag.ofLang(AppTag.Lang.JAVA));
            }
            //pod_template  这个暂时获取不了
            podService.insertPod(appId, nodeId, pod.getMetadata().getName(), pod.getStatus().getHostIP(), pod.getStatus().getPhase(),
                    processJvm, "", "deployment", "", pod.getMetadata().getLabels().toString(), accessToken, clusterName,
                    pod.getMetadata().getNamespace(), dHostName, nodeIP, nodeName);
        } catch (Exception e) {
            log.error("FillMetaDataTask#insertPod occurs an error", e);
        }
    }

    private void updateApp(Map<String, Map<String, String>> appMap, String accessToken, String clusterName) {
        appMap.values().parallelStream().forEach(appMapValues -> {
            appMapValues.entrySet().forEach(entry -> {
                String appName = entry.getKey();
                String namespace = entry.getValue();
                AppInfoRecord appInfoRecord = appInfoService.getByAppAndATAndNamespace(appName, accessToken, namespace);
                if (appInfoRecord != null && StringUtils.equals(appInfoRecord.getStatus(), AppStatus.ALIVE.name())) {
                    return;
                }
                //更新
                log.info("update or insert appName:{},accessToken:{},clusterName:{}", appName, accessToken, clusterName);
                appInfoService.insertAppRecord(0, accessToken, "", appName, "", "", AppStatus.ALIVE.name(), "", clusterName, namespace);
            });
        });
    }

    private void doReplace(Map<String, Map<String, String>> appMap, String accessToken, String clusterName) {
        //获取存活的app信息
        List<AppInfoRecord> appVOS = appInfoService.getAllAlivePodsByToken(accessToken, clusterName);
        if (CollectionUtils.isEmpty(appVOS)) {
            return;
        }
        List<AppInfoRecord> unAppVOS = appVOS.stream().filter(appVO -> {
            String key = generateKey(appVO.getAppName(), appVO.getNamespace());
            return !appMap.containsKey(key);
        }).collect(Collectors.toList());
        //进行淘汰
        unAppVOS.forEach(appVO -> appInfoService.updateAppStatue(appVO.getId(), AppStatus.INVALID));
    }

    private String generateKey(String appName, String namespace) {
        return String.format("%s_%s", appName, namespace);
    }
}