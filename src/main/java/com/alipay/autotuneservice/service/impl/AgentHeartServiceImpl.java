package com.alipay.autotuneservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.agent.twatch.DoInvokeRunner;
import com.alipay.autotuneservice.agent.twatch.model.AgentActionRequest;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.agent.CallBackRequest;
import com.alipay.autotuneservice.model.common.PodStatus;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import com.alipay.autotuneservice.schedule.FillMetaAppDataTask;
import com.alipay.autotuneservice.service.AgentHeartService;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.MetaAnalyzeService;
import com.alipay.autotuneservice.service.PodService;
import com.alipay.autotuneservice.util.AgentConstant;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.util.AgentConstant.TWATCH_TABLE;

@Slf4j
@Service
public class AgentHeartServiceImpl implements AgentHeartService {

    private static final Map<String, AppInfoRecord> APP_MAP_CACHE    = Maps.newHashMap();
    private static final String                     POD_CHECK_KEY    = "DO_CHECK_POD_UNION_%s";
    private static final List<String>               FILTER_NAMESPACE = ImmutableList.of("kube-system");

    @Autowired
    private RedisClient    redisClient;
    @Autowired
    private DoInvokeRunner doInvokeRunner;
    @Autowired
    private AppInfoService      appInfoService;
    @Autowired
    private PodInfo             podInfo;
    @Autowired
    private FillMetaAppDataTask fillMetaAppDataTask;
    @Autowired
    private MetaAnalyzeService  metaAnalyzeService;
    @Autowired
    private PodService          podService;
    @Autowired
    private AsyncTaskExecutor   podEventExecutor;
    @Autowired
    private NosqlService        nosqlService;

    @Override
    public Set<AgentActionRequest> askAction(String agentName) {
        //询问的action--->从redis的队列里获取
        List<Object> actionResult = redisClient.lrange(AgentConstant.generateQueueKey(agentName));
        if (CollectionUtils.isEmpty(actionResult)) {
            return Sets.newHashSet();
        }
        redisClient.del(AgentConstant.generateQueueKey(agentName));
        //进行去重
        return actionResult.stream()
                .map(o -> (AgentActionRequest) o)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean doCallBack(CallBackRequest request) {
        redisClient.setEx(AgentConstant.generateCallBackKey(request.getSessionId()),
                JSONObject.toJSONString(request.getData()),
                5, TimeUnit.MINUTES);
        return Boolean.TRUE;
    }

    @Override
    public void boundUnion(List<TwatchInfoDo> infoDos) {
        if (CollectionUtils.isEmpty(infoDos)) {
            return;
        }
        List<TwatchInfoDo> collect = infoDos.stream().filter(item -> StringUtils.isNotBlank(item.getContainerName())).collect(
                Collectors.toList());
        log.info("boundUnion request:{}", JSONObject.toJSONString(collect));
        //线程池处理
        podEventExecutor.execute(() -> boundTWatch(collect));
        boolean isEmpty = CollectionUtils.isEmpty(collect);
        String dHostName = infoDos.get(0).getAgentName();
        if (isEmpty) {
            log.info("TWatch:{} request empty", dHostName);
        }
        podEventExecutor.execute(() -> boundPod(collect, CollectionUtils.isEmpty(collect) ? dHostName : ""));
    }

    private void boundTWatch(List<TwatchInfoDo> infoDos) {
        infoDos.stream().filter(infoDo -> {
            try {
                List<TwatchInfoDo> result = doInvokeRunner.findInfoByContainer(infoDo.getContainerId());
                if (CollectionUtils.isEmpty(result)) {
                    return Boolean.TRUE;
                }
                Optional optional = result.stream()
                        .filter(item -> StringUtils.equals(infoDo.getAgentName(), item.getAgentName()))
                        .findAny();
                return !optional.isPresent();
            } catch (Exception ex) {
                return Boolean.FALSE;
            }
        }).forEach(infoDo -> {
            try {
                nosqlService.insert(infoDo, TWATCH_TABLE);
            } catch (Exception e) {
                log.error("boundUnion is error", e);
            }
        });
    }

    private void boundPod(List<TwatchInfoDo> infoDos, String dHostName) {
        //check为空
        if (CollectionUtils.isEmpty(infoDos)) {
            //进行全部汰换
            List<PodInfoRecord> records = podInfo.getDHostNameAlivePods(dHostName);
            records.parallelStream().forEach(podInfo -> podService.updatePodStatue(podInfo.getId(), PodStatus.INVALID));
            return;
        }
        //按照hostname进行分类
        Map<String, List<TwatchInfoDo>> podMap = Maps.newHashMap();
        infoDos.forEach(info -> {
            if (StringUtils.isEmpty(info.getContainerName())) {
                return;
            }
            if (FILTER_NAMESPACE.contains(info.getNameSpace())) {
                return;
            }
            String agentName = info.getAgentName();
            if (StringUtils.isEmpty(agentName)) {
                return;
            }
            String appName = getAppName(info.getPodName());
            if (StringUtils.isEmpty(appName)) {
                return;
            }
            if (!podMap.containsKey(agentName)) {
                podMap.put(agentName, Lists.newArrayList());
            }
            podMap.get(agentName).add(info);
        });
        //进行汰换<hostname,listPod>
        podMap.entrySet().parallelStream().forEach(entry -> {
            String hostName = entry.getKey();
            if (!doCheck(hostName)) {
                log.info("{} is running", hostName);
                return;
            }
            try {
                //获取应用信息
                List<TwatchInfoDo> twatchInfoDos = entry.getValue();
                if (CollectionUtils.isEmpty(twatchInfoDos)) {
                    return;
                }
                Replace replace = new Replace(hostName);
                twatchInfoDos.parallelStream().forEach(twatchInfoDo -> {
                    try {
                        if (StringUtils.isEmpty(twatchInfoDo.getContainerName())) {
                            return;
                        }
                        String podName = twatchInfoDo.getPodName();
                        String appName = getAppName(twatchInfoDo.getPodName());
                        //判断appName
                        AppInfoRecord appInfoRecord = getAppInfo(appName, twatchInfoDo.getNameSpace());
                        if (appInfoRecord == null) {
                            log.info("appInfoRecord is empty --> appName={}", appName);
                            return;
                        }
                        PodInfoRecord record = replace.getPodByName(podName);
                        boolean isNodeUpdate = Boolean.TRUE;
                        boolean isJvmUpdate = Boolean.TRUE;
                        if (record != null) {
                            isNodeUpdate = (record.getNodeId() != null && record.getNodeId() != -1);
                            isJvmUpdate = StringUtils.isNotEmpty(record.getPodJvm());
                        }
                        if (record != null) {
                            if (StringUtils.isEmpty(record.getDHostname()) || StringUtils.equals(record.getDHostname(), "null")) {
                                return;
                            }
                        }
                        if (replace.dHostPods.contains(podName) && isNodeUpdate && isJvmUpdate) {
                            //TODO 查询有的话，判断node为-1或者jvm为空给予重试
                            replace.newAlivePods.add(new PodVO(podName));
                            return;
                        }
                        PodVO podVO = new PodVO();
                        podVO.setAccessToken(appInfoRecord.getAccessToken());
                        podVO.setAppId(appInfoRecord.getId());
                        podVO.setNameSpace(twatchInfoDo.getNameSpace());
                        podVO.setPodName(podName);
                        podVO.setClusterName(appInfoRecord.getClusterName());
                        podVO.setNodeIp(twatchInfoDo.getNodeIp());
                        podVO.setNodeName(twatchInfoDo.getNodeName());
                        replace.newAlivePods.add(podVO);
                    } catch (Exception e) {
                        log.error("generate PodVO is error", e);
                    }
                });
                replace.doReplace();
            } catch (Exception e) {
                log.error("boundPod is error", e);
            } finally {
                //删除锁
                redisClient.del(String.format(POD_CHECK_KEY, entry.getKey()));
            }
        });
    }

    private synchronized AppInfoRecord getAppInfo(String appName, String nameSpace) {
        if (APP_MAP_CACHE.containsKey(appName)) {
            return APP_MAP_CACHE.get(appName);
        }
        AppInfoRecord appInfoRecord = appInfoService.getByAppAndATAndNamespace(appName, nameSpace);
        if (appInfoRecord != null) {
            APP_MAP_CACHE.put(appName, appInfoRecord);
        }
        return appInfoRecord;
    }

    private boolean doCheck(String hostName) {
        //判断任务是否有提交记录
        String key = String.format(POD_CHECK_KEY, hostName);
        String value = redisClient.get(key, String.class);
        if (StringUtils.isEmpty(value)) {
            redisClient.set(key, key, 60 * 3);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private String getAppName(String podName) {
        try {
            return podName.substring(0, StringUtils.lastOrdinalIndexOf(podName, "-", 2));
        } catch (Exception e) {
            return "";
        }
    }

    private class Replace {

        @Getter
        private final List<String>               dHostPods   = Lists.newArrayList();
        private final Map<String, PodInfoRecord> dHostPodMap = Maps.newHashMap();
        private final List<PodVO>                newAlivePods;
        private final List<String>               invalidPods;
        private final String                     dHostName;

        Replace(String dHostName) {
            if (StringUtils.isEmpty(dHostName)) {
                throw new RuntimeException("dHostName is required, please check");
            }
            this.dHostName = dHostName;
            //获取dHostPod节点
            List<PodInfoRecord> records = podInfo.getDHostNameAlivePods(dHostName);
            records.forEach(record -> {
                String podName = record.getPodName();
                dHostPods.add(podName);
                dHostPodMap.put(podName, record);
            });
            this.newAlivePods = Collections.synchronizedList(Lists.newArrayList());
            this.invalidPods = Collections.synchronizedList(Lists.newArrayList());
        }

        void doReplace() {
            log.info("newAlivePods is [{}]", JSONObject.toJSONString(newAlivePods));
            //进行插入
            Map<String, K8sClient> k8sClientMap = Maps.newConcurrentMap();
            newAlivePods.forEach(newAlivePod -> {
                if (StringUtils.isEmpty(newAlivePod.accessToken)) {
                    return;
                }
                K8sClient eksClient;
                try {
                    eksClient = getK8sClient(k8sClientMap, newAlivePod.accessToken, newAlivePod.clusterName);
                    if (eksClient == null) {
                        return;
                    }
                } catch (Exception e) {
                    log.error("getK8sClient is error-->[{}]", JSONObject.toJSONString(newAlivePod), e);
                    return;
                }
                newAlivePod.setK8sClient(eksClient);
            });
            newAlivePods.parallelStream().forEach(newAlivePod -> {
                if (StringUtils.isEmpty(newAlivePod.accessToken)) {
                    return;
                }
                if (newAlivePod.k8sClient == null) {
                    log.error("k8sClient is empty-->[{}]", JSONObject.toJSONString(newAlivePod));
                    return;
                }
                fillMetaAppDataTask.insertPod(newAlivePod.accessToken, newAlivePod.clusterName, newAlivePod.k8sClient, newAlivePod.appId,
                        newAlivePod.nameSpace, newAlivePod.podName, dHostName, newAlivePod.nodeIp, newAlivePod.nodeName);
            });
            if (MapUtils.isNotEmpty(k8sClientMap)) {
                k8sClientMap.values().forEach(K8sClient::close);
            }
            //进行淘汰
            List<String> newAlivePodNames = newAlivePods.stream().map(PodVO::getPodName).collect(Collectors.toList());
            log.info("dHostPods is [{}]", JSONObject.toJSONString(dHostPods));
            dHostPods.forEach(dHostPod -> {
                if (newAlivePodNames.contains(dHostPod)) {
                    return;
                }
                invalidPods.add(dHostPod);
            });
            log.info("invalidPods is [{}]", JSONObject.toJSONString(invalidPods));
            if (CollectionUtils.isNotEmpty(invalidPods)) {
                invalidPods.parallelStream()
                        .filter(dHostPodMap::containsKey)
                        .forEach(podInfo -> podService.updatePodStatue(dHostPodMap.get(podInfo).getId(), PodStatus.INVALID));
            }
        }

        public PodInfoRecord getPodByName(String podName) {
            return dHostPodMap.getOrDefault(podName, null);
        }

        private synchronized K8sClient getK8sClient(Map<String, K8sClient> k8sClientMap, String accessToken, String clusterName) {
            String key = String.format("%s_%s", accessToken, clusterName);
            if (k8sClientMap.containsKey(key)) {
                return k8sClientMap.get(key);
            }
            K8sClient eksClient = metaAnalyzeService.createEksClient(accessToken, clusterName);
            if (eksClient != null) {
                k8sClientMap.put(key, eksClient);
            }
            return eksClient;
        }

    }

    @Data
    @NoArgsConstructor
    private class PodVO {
        private String    accessToken;
        private String    clusterName;
        private int       appId;
        private String    nameSpace;
        private String    podName;
        private K8sClient k8sClient;
        private String    nodeIp;
        private String    nodeName;

        public PodVO(String podName) {
            this.podName = podName;
        }
    }
}
