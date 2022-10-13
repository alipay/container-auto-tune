/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author huangkaifei
 * @version : MeterMetric.java, v 0.1 2022年08月19日 2:47 PM huangkaifei Exp $
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterMetric implements Serializable {

    private static final long serialVersionUID = -6925209918473130535L;

    private String metricName;
    private String metricPath;
}