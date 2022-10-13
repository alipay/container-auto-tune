/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller.model.meter;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huangkaifei
 * @version : MeterMetricValue.java, v 0.1 2022年08月25日 2:17 PM huangkaifei Exp $
 */
@Data
@Builder
public class MeterMetricResult implements Serializable {

    private static final long serialVersionUID = 3405336410402514633L;

    /**
     * metric name
     */
    private String metricName;
    /**
     * query metric value by specific PromQL
     */
    private String metricResult;

}