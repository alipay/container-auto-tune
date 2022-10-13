/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author huangkaifei
 * @version : MeterType.java, v 0.1 2022年08月19日 2:48 PM huangkaifei Exp $
 */
public enum MeterType {
    PROMETHEUS("prometheus", "prometheus monitor"),
    DATADOG("datadog", "datadog")
    ;

    private String name;
    private String desc;

    MeterType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static MeterType find(String meterName){
        for (MeterType meterType: values()){
            if (StringUtils.equalsIgnoreCase(meterType.name, meterName)) {
                return meterType;
            }
        }
        throw new UnsupportedOperationException(String.format("meterName:%s is not supported now", meterName));
    }
}