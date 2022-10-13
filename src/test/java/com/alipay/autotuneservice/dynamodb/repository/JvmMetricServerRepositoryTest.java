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
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author huangkaifei
 * @version : JvmMetricServerRepoistoryTest.java, v 0.1 2022年06月22日 10:13 AM huangkaifei Exp $
 */
public class JvmMetricServerRepositoryTest {

    private JvmMonitorMetricDataRepository repository = new JvmMonitorMetricDataRepository();

    @Test
    public void getJvmMetric1() {
        String podName = "xx";
        long dt = 0L;
        List<JvmMonitorMetricData> jvmMonitorMetricData = repository.queryByPodNameAndDt(podName,
            dt);
        System.out.println(JSON.toJSONString(jvmMonitorMetricData));
    }
}