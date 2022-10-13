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

import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.dynamodb.repository.ContainerProcessInfoRepository;
import com.alipay.autotuneservice.dynamodb.repository.ContainerStatisticsRepository;
import com.alipay.autotuneservice.dynamodb.repository.TwatchInfoRepository;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.service.AgentInvokeService;
import com.alipay.autotuneservice.service.impl.AgentInvokeServiceImpl.InvokeType;
import com.alipay.autotuneservice.util.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author huangkaifei
 * refs to ContainerMetricRunner
 * @version : MonitorContainerSchedule.java, v 0.1 2022年04月19日 12:54 PM huangkaifei Exp $
 */
@Deprecated
@Slf4j
@Component
public class MonitorContainerSchedule {

    private static final String            LOCK_LEY = "MonitorContainerSchedule";

    @Autowired
    private AgentInvokeService             agentInvokeService;
    @Autowired
    private TwatchInfoRepository           twatchInfoRepository;
    @Autowired
    private ContainerProcessInfoRepository processInfoRepository;
    @Autowired
    private ContainerStatisticsRepository  containerStatRepository;
    @Autowired
    private RedisClient                    redisClient;
    @Autowired
    private PodInfo                        podInfo;

    // second, minute, hour, day of month, month, day of week
    public void monitorProcessTask() {
        redisClient.doExec(LOCK_LEY, () -> {
            log.info("monitorProcessTask scheduled start");
            List<PodInfoRecord> allPods = podInfo.getAllPods();
            if (CollectionUtils.isEmpty(allPods)) {
                log.info("monitorProcessTask - can scan pods to monitor, so will skip.");
                return;
            }
            log.info("monitorProcessTask scan alive pods size={}", allPods.size());
            Map<String, Integer> podAppIdMap = allPods.stream().collect(Collectors.toMap(k -> k.getPodName(), v -> v.getAppId()));
            List<TwatchInfoDo> allTwatchInfo = twatchInfoRepository.getAllTwatchInfoBasedPods(allPods);
            if (CollectionUtils.isEmpty(allTwatchInfo)) {
                return;
            }
            //去重
            allTwatchInfo = allTwatchInfo.stream().collect(
                    collectingAndThen(toCollection(() -> new TreeSet<>(comparing(s -> s.getContainerId()))), ArrayList::new));
            //并发存储
            allTwatchInfo.parallelStream().forEach(item -> {
                Integer appId = podAppIdMap.get(item.getPodName());
                handleContainerStat(item, appId);
                handleProcessInfo(item, appId);
            });
        });
    }

    @Async("dynamoDBTaskExecutor")
    public void handleProcessInfo(TwatchInfoDo item, Integer appId) {
        try {
            // save process info
            String response = agentInvokeService
                .getProcessByPod(InvokeType.SYNC, item.getPodName());
            processInfoRepository.saveProcessInfos(appId, item.getPodName(), item.getContainerId(),
                response);
        } catch (Exception e) {
            log.error("handleProcessInfo container={} occurs an error", item.getContainerId(), e);
        }
    }

    @Async("dynamoDBTaskExecutor")
    public void handleContainerStat(TwatchInfoDo item, Integer appId) {
        try {
            // save container metric
            String statsResponse = agentInvokeService.execStats(InvokeType.SYNC,
                item.getContainerId());
            containerStatRepository.insert(ConvertUtils.convert2ContainerStat(appId,
                item.getPodName(), item.getContainerId(), statsResponse));
        } catch (Exception e) {
            log.error("handleContainerStat container={} stat occurs an error.",
                item.getContainerId(), e);
        }
    }
}