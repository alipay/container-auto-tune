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
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dao.jooq.tables.records.ThreadpoolMonitorMetricDataRecord;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.ThreadPoolMonitorMetricData;

import java.util.List;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataRepository.java, v 0.1 2022年06月22日 3:02 PM huangkaifei Exp $
 */
public interface JvmMonitorMetricRepository {

    List<JvmMonitorMetricData> queryByPodNameAndDt(String podName, long dt);

    List<JvmMonitorMetricData> queryByPodName(String partitionKey, Long start, Long end);

    JvmMonitorMetricData getPodLatestOneMinuteJvmMetric(String nodeName);

    List<JvmMonitorMetricData> getPodJvmMetric(String podName, long start, long end);

    List<JvmMonitorMetricData> getAppJvmMetric(Integer appId, long start, long end, String rangeKey, String aggregateType,
                                                     List<String> groupBy, String... aggregateFields);

    void insertThreadPoolData(List<ThreadpoolMonitorMetricDataRecord> metrics);

    void initThreadPoolCache(List<ThreadPoolMonitorMetricData> metrics);

   void insertGCData(JvmMonitorMetricData jvmMonitorMetricData);

}