/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.model;

/**
 * @author huangkaifei
 * @version : MetricType.java, v 0.1 2022年08月19日 2:57 PM huangkaifei Exp $
 */
public enum MetricType {

    QPS("qps", ""),
    RT("rt", "request cost time from request start to end."),
    ERROR("error", "service error.")
    ;

    private String name;
    private String desc;

    MetricType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}