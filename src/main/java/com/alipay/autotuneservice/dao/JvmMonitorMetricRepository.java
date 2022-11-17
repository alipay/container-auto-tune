/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataRepository.java, v 0.1 2022年06月22日 3:02 PM huangkaifei Exp $
 */
public interface JvmMonitorMetricRepository {

    public void insertGCData(JvmMonitorMetricData jvmMonitorMetricData);

}