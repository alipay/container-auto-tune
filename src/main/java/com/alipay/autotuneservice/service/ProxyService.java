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

import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.alipay.autotuneservice.dynamodb.bean.HealthCheckData;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;

import java.util.List;

/**
 * @author fangxueyang
 * @version ProxyService.java, v 0.1 2022年08月08日 17:57 hongshu
 */
public interface ProxyService {

    /**
     * query twatchInfo list by pod name
     * @param podName
     * @return
     */
    public List<TwatchInfoDo> findTwatchsByPodName(String podName);

    /**
     * query ContainerStatistics list by time range and containerId
     * @param containerId
     * @param start
     * @param end
     * @return
     */
    public List<ContainerStatistics> findCStatistic(String containerId, long start, long end);

    /**
     * query JvmMonitorMetricData list by time range and podName
     * @param podName
     * @param start
     * @param end
     * @return
     */
    public List<JvmMonitorMetricData> findJvmMonMetricRange(String podName, Long start, Long end);

    /**
     * query jvmProblemData day
     * @param dt
     * @return
     */
    List<HealthCheckData> getJvmProblemPerDay(String dt);

    /**
     * query JvmMonitorMetricData list by time  and podName
     * @param podName
     * @param dt
     * @return
     */
    public List<JvmMonitorMetricData> findJvmMonMetricDay(String podName, long dt);

}