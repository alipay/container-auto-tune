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
package com.alipay.autotuneservice.dynamodb.repository;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.agent.twatch.DoInvokeRunner;
import com.alipay.autotuneservice.dao.ContainerStatisticRepository;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author huangkaifei
 * @version : ContainerStatisticsService.java, v 0.1 2022年04月20日 11:37 AM huangkaifei Exp $
 */
@Slf4j
@Service
public class ContainerStatisticsService {

    private static final String TABLE_NAME        = "ContainerStatistics";
    private static final String CONTAINERID_INDEX = "containerId-index";
    private static final String PARTITION_KEY     = "containerId";

    @Autowired
    private DoInvokeRunner      doInvokeRunner;
    @Autowired
    private ContainerStatisticRepository containerStatisticRepository;

    /**
     * insert
     *
     * @param statistics
     */
    @Async("dynamoDBTaskExecutor")
    public void insert(ContainerStatistics statistics) {
        if (Objects.isNull(statistics)) {
            log.info("insert statistics  - input is null, pls check.");
            return;
        }
        try {
            log.debug("ContainerStatisticsService#insert enter. statistics={}",
                JSON.toJSONString(statistics));
            //nosqlService.insert(statistics, TABLE_NAME);
            containerStatisticRepository.insert(statistics);
        } catch (Exception e) {
            log.error("insert statistics for containerId={} occurs an error.",
                statistics.getContainerId(), e);
        }
    }

    public List<ContainerStatistics> queryContainerStats(String containerId, long start, long end) {
        return containerStatisticRepository.queryRange(containerId, start, end);
        //return nosqlService.queryRange(TABLE_NAME, "containerId", containerId, "gmtCreated", start,
        //    end, ContainerStatistics.class);
    }

    public List<ContainerStatistics> queryContainerStatsByPodName(String podName, long start,
                                                                  long end) {
        if (StringUtils.isBlank(podName)) {
            return Lists.newArrayList();
        }
        List<TwatchInfoDo> infoByPod = doInvokeRunner.findInfoByPod(podName);
        if (CollectionUtils.isEmpty(infoByPod)) {
            return Lists.newArrayList();
        }
        log.info("getProcessByPod for podName={}, containers={}", podName,
            JSON.toJSONString(infoByPod));
        String containerId = infoByPod.get(0).getContainerId();
        return queryContainerStats(containerId, start, end);
    }

}