/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dynamodb.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangkaifei
 * @version : MeterMetricInfo.java, v 0.1 2022年10月08日 2:07 PM huangkaifei Exp $
 */
@Data
@Builder
public class MeterMetricInfo {
    private long appId;
    private String appName;
    private String metricName;
    private String data;
    private String meterVendor;
    private long gmtCreated;
    /**
     * day partition
     */
    private long dt;

}