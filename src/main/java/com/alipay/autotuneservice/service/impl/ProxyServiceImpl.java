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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.dynamodb.repository.ContainerStatisticsService;
import com.alipay.autotuneservice.dynamodb.repository.TwatchInfoService;
import com.alipay.autotuneservice.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fangxueyang
 * @version ProxyService.java, v 0.1 2022年08月08日 17:57 hongshu
 */
@Slf4j
@Service
public class ProxyServiceImpl implements ProxyService {

    private final TwatchInfoService           twatchInfoRepository;
    private final ContainerStatisticsService  statisticsRepository;

    public ProxyServiceImpl(TwatchInfoService twatchInfoRepository,
                            ContainerStatisticsService statisticsRepository) {
        this.twatchInfoRepository = twatchInfoRepository;
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public List<TwatchInfoDo> findTwatchsByPodName(String podName) {
        return this.twatchInfoRepository.findInfoByPod(podName);
    }

    @Override
    public List<ContainerStatistics> findCStatistic(String containerId, long start, long end) {
        return this.statisticsRepository.queryContainerStats(containerId, start, end);
    }
}
