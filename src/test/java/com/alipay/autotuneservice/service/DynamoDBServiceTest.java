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
package com.alipay.autotuneservice.service;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.repository.JvmMonitorMetricDataService;
import org.junit.Test;

public class DynamoDBServiceTest {

    private JvmMonitorMetricDataService repository = new JvmMonitorMetricDataService();

    @Test
    public void getPodLatestOneMinuteJvmMetric() {
        String podName = "xx";
        JvmMonitorMetricData res = repository.getPodLatestOneMinuteJvmMetric(podName);

        System.out.println(res);
    }

    @Test
    public void getJvmMetric() {
        String podName = "xx";
        long start = 0l;
        long end = 0l;
        System.out.println(JSON.toJSONString(repository.getPodJvmMetric(podName, start, end)));
    }

}