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
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.UserInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.infrastructure.rpc.CostOrderHandler;
import com.alipay.autotuneservice.infrastructure.rpc.model.CostCell;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.common.UserInfo;
import com.alipay.autotuneservice.util.TraceIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version CostStatisticTask.java, v 0.1 2022年05月18日 15:28 dutianze
 */
@Slf4j
@Component
public class CostStatisticTask {

    private static final String LOCK_LEY = "tmaster_CostStatisticTask";

    @Autowired
    private RedisClient         redisClient;
    @Autowired
    private EnvHandler          envHandler;
    @Autowired
    private UserInfoRepository  userInfoRepository;
    @Autowired
    private CostOrderHandler    costOrderHandler;
    @Autowired
    private PodInfo             podInfo;

    /**
     * every day 00:00
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void run() {
        if (envHandler.isDev()) {
            return;
        }
        try {
            TraceIdGenerator.generateAndSet();
            redisClient.doExec(LOCK_LEY, () -> {
                log.info("CostStatisticTask scheduled start");
                try {
                    this.doTask();
                } catch (Exception e) {
                    log.error("CostStatisticTask error", e);
                }
            });
        } finally {
            TraceIdGenerator.clear();
        }
    }

    /**
     * for each tenant
     */
    private void doTask() {
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        log.info("userInfoRepository.findAll(), userInfoList:{}", userInfoList);
        if (CollectionUtils.isEmpty(userInfoList)) {
            return;
        }
        Map<String, CostCell> tenantMapCostCell = userInfoList.stream().collect(
                Collectors.toMap(UserInfo::getTenantCode, CostCell::new, (e, m) -> e));
        for (Entry<String, CostCell> entry : tenantMapCostCell.entrySet()) {
            try {
                CostCell costCell = entry.getValue();
                int agentInstallCount = calcAgentInstallCount(costCell);
                costOrderHandler.submit(costCell, agentInstallCount);
            } catch (Exception e) {
                log.error("calc cost error, entry:{}", entry);
            }
        }
    }

    /**
     * calc agent count by tenant
     */
    private int calcAgentInstallCount(CostCell costCell) {
        List<PodInfoRecord> podInfoRecords = podInfo.findByAccessToken(costCell.getAccessToken());
        if (CollectionUtils.isEmpty(podInfoRecords)) {
            return 0;
        }
        return podInfoRecords.stream()
                .map(PodInfoRecord::getAgentInstall)
                .filter(Objects::nonNull)
                .mapToInt(count -> count)
                .sum();
    }
}