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
package com.alipay.autotuneservice.agent.twatch.monitor;

import com.alipay.autotuneservice.agent.twatch.model.PodHealthIndexEnum;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.dynamodb.repository.ContainerProcessInfoService;
import com.alipay.autotuneservice.dynamodb.repository.ContainerStatisticsService;
import com.alipay.autotuneservice.dynamodb.repository.TwatchInfoService;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.agent.ContainerMetric;
import com.alipay.autotuneservice.model.agent.ContainerMetricRequest;
import com.alipay.autotuneservice.model.common.PodAttach;
import com.alipay.autotuneservice.model.common.PodAttachStatus;
import com.alipay.autotuneservice.service.AgentInvokeService;
import com.alipay.autotuneservice.service.PodAttachService;
import com.alipay.autotuneservice.util.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : ContainerMetricRunner.java, v 0.1 2022年04月29日 12:40 AM huangkaifei Exp $
 */
@Slf4j
@Service
public class ContainerMetricRunner {

    @Autowired
    private ContainerProcessInfoService processInfoRepository;
    @Autowired
    private ContainerStatisticsService  statisticsRepository;
    @Autowired
    private TwatchInfoService           twatchInfoRepository;
    @Autowired
    private PodInfo                       podInfo;
    @Autowired
    private RedisClient                    redisClient;
    @Autowired
    private AgentInvokeService             agentInvokeService;
    @Autowired
    @Qualifier("subExecutor")
    private AsyncTaskExecutor              asyncTaskExecutor;
    @Autowired
    private PodAttachService               podAttachService;

    /**
     * 分发从twatch上报的container监控
     *
     * @param request
     */
    public void dispatchContainerMetric(ContainerMetricRequest request) {
        log.info("dispatchContainerMetric start, agent={}", request.getAgentName());
        String agentName = request.getAgentName();
        List<TwatchInfoDo> infoByAgent = twatchInfoRepository.findInfoByAgent(agentName);
        // agent下的所管理的所有containerId
        List<String> containerIds = Optional.ofNullable(infoByAgent).orElse(new ArrayList<>()).stream().filter(
                item -> StringUtils.equals(item.getAgentName(), agentName)).map(TwatchInfoDo::getContainerId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(containerIds)) {
            log.info("dispatchContainerMetric - can not find containers for agent={}", agentName);
            return;
        }
        List<PodInfoRecord> allPods = podInfo.getAllAlivePods();
        if (CollectionUtils.isEmpty(allPods)) {
            log.info("dispatchContainerMetric - can not scan alive pods to monitor, so will skip.");
            return;
        }
        Map<String, Integer> podAppIdMap = allPods.stream().collect(Collectors.toMap(k -> k.getPodName(), v -> v.getAppId()));
        refreshProcessInfo(request, containerIds, podAppIdMap);
        refreshContainerStats(request, containerIds, podAppIdMap);
        refreshPodInstallAgent(allPods);
    }

    @Async("dynamoDBTaskExecutor")
    public void refreshProcessInfo(ContainerMetricRequest request, List<String> containerIds, Map<String, Integer> podAppIdMap) {
        if (request.checkProcessInfosEmpty()) {
            return;
        }
        log.info("refreshProcessInfo start.");
        List<ContainerMetric> processInfos = request.getProcessInfos().stream().filter(Objects::nonNull).filter(
                item -> containerIds.contains(item.getContainerId()) && podAppIdMap.containsKey(item.getPodName())).collect(
                Collectors.toList());
        if (CollectionUtils.isEmpty(processInfos)) {
            return;
        }
        processInfos.stream().filter(Objects::nonNull).forEach(item -> {
            try {
                processInfoRepository.batchInsertProcessInfo(
                        ConvertUtils.convert2ContainerProcessInfos(podAppIdMap.get(item.getPodName()), item));
            } catch (Exception e) {
                log.error("handleProcessInfo batchInsertProcessInfo occurs an error.", e);
            }
        });
    }

    @Async("dynamoDBTaskExecutor")
    public void refreshContainerStats(ContainerMetricRequest request, List<String> containerIds, Map<String, Integer> podAppIdMap) {
        if (request.checkContainerStatsEmpty()) {
            return;
        }
        log.info("refreshContainerStats start.");
        List<ContainerMetric> containerStats = request.getContainerStats().stream().filter(Objects::nonNull).filter(
                item -> containerIds.contains(item.getContainerId()) && podAppIdMap.containsKey(item.getPodName())).collect(
                Collectors.toList());
        List<ContainerStatistics> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(containerStats)) {
            log.info("refreshContainerStats - get containerStats is empty.");
            return;
        }
        containerStats.stream().filter(Objects::nonNull).forEach(item -> {
            try {
                ContainerStatistics containerStatistics = ConvertUtils.convert2ContainerStats(podAppIdMap.get(item.getPodName()), item);
                list.add(containerStatistics);
                statisticsRepository.insert(containerStatistics);
            } catch (Exception e) {
                log.error("handleContainerStats insert occurs an error.", e);
            }
        });
        addPodResourceLimit(list);
    }

    @Async("dynamoDBTaskExecutor")
    public void addPodResourceLimit(List<ContainerStatistics> statisticsList) {
        log.info("addPodResourceLimit enter.");
        List<PodInfoRecord> allAlivePods = podInfo.getAllAlivePods();
        if (CollectionUtils.isEmpty(allAlivePods)) {
            log.info("addPodResourceLimit - allAlivePods size is 0, so will skip.");
            return;
        }
        Map<String, PodInfoRecord> podNameRecordMap = allAlivePods.stream().filter(Objects::nonNull).collect(
                Collectors.toMap(PodInfoRecord::getPodName, v -> v, (e, u) -> e));
        statisticsList.stream().filter(Objects::nonNull).filter(
                o -> podNameRecordMap.containsKey(o.getPodName())).forEach(item -> {
            try {
                String savedVal = redisClient.get(item.generateResourceKey(), String.class);
                log.info("addPodResourceLimit - pod={}, resourceKey={}, resourceVal={}, cpuCore={}, memLimit={}", item.getPodName(),
                        item.generateResourceKey(), item.getResourceValue(), item.getOnlineCpus(), item.getMemLimit());
                if (StringUtils.equals(savedVal, item.getResourceValue())) {
                    return;
                }
                redisClient.setNx(item.generateResourceKey(), item.getResourceValue(), 3, TimeUnit.MINUTES);
                String podName = item.getPodName();
                log.info("start to update pod resource for podName={}, resourceVal={}", podName, item.getResourceValue());
                PodInfoRecord podInfoRecord = podNameRecordMap.get(podName);
                podInfoRecord.setMemLimit(Long.valueOf(item.getMemLimit()).intValue());
                podInfoRecord.setCpuCoreLimit(Long.valueOf(item.getOnlineCpus()).intValue());
                podInfo.updatePodInfoResourceFields(podInfoRecord);
            } catch (Exception e) {
                log.error("addPodResourceLimit occurs an error", e);
                redisClient.del(item.generateResourceKey());
            }
        });
    }

    public void refreshPodInstallAgent(List<PodInfoRecord> allAlivePods) {
        if (CollectionUtils.isEmpty(allAlivePods)) {
            return;
        }
        allAlivePods.forEach(item -> {
            if (item != null && item.getAgentInstall() > 0) {
                return;
            }
            // 处理只包含java的pod
            if (!StringUtils.contains(item.getPodJvm(), "java")) {
                return;
            }
            asyncTaskExecutor.execute(() -> {
                try {
                    String podHealthIndex = agentInvokeService.getPodHealthIndex(item.getPodName(),
                            PodHealthIndexEnum.IS_TUNE_AGENT_INSTALL);
                    log.info("refreshPodInstallAgent pod={} res={}", item.getPodName(), podHealthIndex);
                    if (Boolean.parseBoolean(podHealthIndex)) {
                        item.setAgentInstall(1);
                        podInfo.updatePodInstallTuneAgent(item);
                        return;
                    }
                    PodAttach podAttach = podAttachService.findByPodId(item.getId());
                    if (Objects.nonNull(podAttach) && PodAttachStatus.INSTALLED == podAttach.getStatus()) {
                        item.setAgentInstall(2);
                        podInfo.updatePodInstallTuneAgent(item);
                    }
                } catch (Exception e) {
                    log.error("refreshPodInstallAgent occurs an error", e);
                }
            });
        });
    }
}