/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dynamodb.bean.MeterMetricInfo;

/**
 * @author huangkaifei
 * @version : MeterMetricInfoService.java, v 0.1 2022年11月01日 2:36 PM huangkaifei Exp $
 */
public interface MeterMetricInfoRepository {
    /**
     * insert MeterMetricInfo record
     *
     * @param meterMetricInfo MeterMetricInfo record
     */
    void insert(MeterMetricInfo meterMetricInfo);

}