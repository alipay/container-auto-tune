/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangkaifei
 * @version : MetricData.java, v 0.1 2022年09月22日 9:56 PM huangkaifei Exp $
 */
@Data
@Builder
public class MetricData {
    private long timestamp;
    private double value;
}