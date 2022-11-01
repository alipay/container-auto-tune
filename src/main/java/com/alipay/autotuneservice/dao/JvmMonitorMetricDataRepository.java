/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;

import java.util.List;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataService.java, v 0.1 2022年10月31日 9:08 PM huangkaifei Exp $
 */
public interface JvmMonitorMetricDataRepository {
    List<JvmMonitorMetricData> queryByPodNameAndDt(String podName, long dt);

    void insert(JvmMonitorMetricData jvmMonitorMetricData);

    List<JvmMonitorMetricData> getPodJvmMetric(String podName, long start, long end);
}