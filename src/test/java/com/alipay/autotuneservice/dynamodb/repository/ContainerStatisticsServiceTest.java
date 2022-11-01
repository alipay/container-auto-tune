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
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class ContainerStatisticsServiceTest {

    @Autowired
    private ContainerStatisticsService  repository;
    @Autowired
    private JvmMonitorMetricDataService dynamoDbService;

    @Test
    public void insert() {
        String str = "xx";
        ContainerStatistics containerStatistics = JSON.parseObject(str,
            new TypeReference<ContainerStatistics>() {
            });
        System.out.println(JSON.toJSONString(containerStatistics));

    }

    @Test
    public void queryContainerStats() {
        String containerId = "xx";
        long start = 0l;
        long end = 0l;
        List<ContainerStatistics> containerStatistics = repository.queryContainerStats(containerId,
            start, end);
        System.out.println(JSON.toJSONString(containerStatistics));
    }

    @Test
    public void testInsertJVM() {
        String json = "xx";
        JvmMonitorMetricData jvmMonitorMetricData = JSON.parseObject(json,
            new TypeReference<JvmMonitorMetricData>() {
            });
        dynamoDbService.insertGCData(jvmMonitorMetricData);

    }

    @Test
    public void testCpuJVM(){
        List<ContainerStatistics> containerStatistics = repository.queryContainerStatsByPodName("xx",
                System.currentTimeMillis() - 60 * 60 * 1000,
                System.currentTimeMillis());
        System.out.println(JSON.toJSONString(containerStatistics));
        double res = containerStatistics.stream().
                mapToDouble(ContainerStatistics::getUsedCpuCores).max().orElse(0.0);
        System.out.println(res);
    }

    @Test
    public void testGetDomain() {
        System.out.println(SystemUtil.getDomainUrl());
    }
}
