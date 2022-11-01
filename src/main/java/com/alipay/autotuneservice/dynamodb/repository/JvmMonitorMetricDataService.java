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

import com.alipay.autotuneservice.dao.JvmMonitorMetricDataRepository;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import com.alipay.autotuneservice.util.LogUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataService.java, v 0.1 2022年06月22日 3:02 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class JvmMonitorMetricDataService {

    private static final String JVM_MONITOR_METRIC_TABLE = "jvm_monitor_metric_data";

    @Autowired
    private NosqlService                   nosqlService;
    @Autowired
    private JvmMonitorMetricDataRepository jvmMonitorRepository;

    public List<JvmMonitorMetricData> queryByPodNameAndDt(String podName, long dt) {
        try {
            return jvmMonitorRepository.queryByPodNameAndDt(podName, dt);
            //return nosqlService.queryByPkSkLongIndex(JVM_MONITOR_METRIC_TABLE, "pod-dt-index",
            //    "pod", podName, "dt", dt, JvmMonitorMetricData.class);
        } catch (Exception e) {
            log.error("queryByPodNameAndDt for podName={}, dt={} occurs an error", podName, dt, e);
            return Lists.newArrayList();
        }
    }

    public List<JvmMonitorMetricData> queryByPodName(String partitionKey, Long start, Long end) {
        try {
            return getPodJvmMetric(partitionKey, start, end);
            //return nosqlService.queryRange(JVM_MONITOR_METRIC_TABLE, "pod", partitionKey, "period",
            //    start, end, JvmMonitorMetricData.class);
        } catch (Exception e) {
            log.error("queryByPodName podName={}", partitionKey);
            return org.apache.commons.compress.utils.Lists.newArrayList();
        }
    }

    public JvmMonitorMetricData getPodLatestOneMinuteJvmMetric(String podName) {
        try {
            long end = System.currentTimeMillis();
            long start = end - 2 * 60 * 1000;

            //Optional<JvmMonitorMetricData> first = nosqlService.queryRange(JVM_MONITOR_METRIC_TABLE, "pod", nodeName, "period", start, end,
            //        JvmMonitorMetricData.class).stream().min((o1, o2) -> o1.getPeriod() > o2.getPeriod() ? -1 : 1);
            //if (first.isPresent()) {
            //    return first.get();
            //}
            //log.info(LogUtil.scureLogFormat("getPodLatestOneMinuteJvmMetric 获取监控数据为空 %s", nodeName));
            //return JvmMonitorMetricData.builder().build();
            List<JvmMonitorMetricData> podJvmMetric = getPodJvmMetric(podName, start, end);
            return CollectionUtils.isEmpty(podJvmMetric) ? null : podJvmMetric.get(0);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<JvmMonitorMetricData> getPodJvmMetric(String podName, long start, long end) {
        try {
            return jvmMonitorRepository.getPodJvmMetric(podName, start, end);
            //return nosqlService.queryRange(JVM_MONITOR_METRIC_TABLE, "pod", podName, "period",
            //    start, end, JvmMonitorMetricData.class);
        } catch (Exception e) {
            log.error("getPodJvmMetric for podName={} occurs an error.", podName, e);
            return Lists.newArrayList();
        }
    }

    public void insertGCData(JvmMonitorMetricData jvmMonitorMetricData) {
        if (jvmMonitorMetricData == null) {
            log.info("insertGCData failed due to input jvmMonitorMetricData is null.");
            return;
        }
        try {
            jvmMonitorRepository.insert(jvmMonitorMetricData);
            //nosqlService.insert(jvmMonitorMetricData, JVM_MONITOR_METRIC_TABLE);
        } catch (Exception e) {
            log.error("insert jvmMonitorMetricData occurs an error.", e);
        }
    }

}