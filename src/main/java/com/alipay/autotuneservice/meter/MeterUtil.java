/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter;

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author huangkaifei
 * @version : MeterUtil.java, v 0.1 2022年09月18日 11:16 PM huangkaifei Exp $
 */
public class MeterUtil {

    public static String getMeterScheme(MeterMeta meterMeta){
        meterMeta = Objects.requireNonNull(meterMeta, "meterMeta can not be null.");
        if (StringUtils.startsWith((meterMeta.getMeterDomain()), "https://")) {
            return "https";
        }
        return "http";
    }

    public static String getMeterDomain(MeterMeta meterMeta){
        meterMeta = Objects.requireNonNull(meterMeta, "meterMeta can not be null.");
        String meterDomain = meterMeta.getMeterDomain();
        String[] split = meterDomain.split("//");
        if (split.length == 1) {
            return meterDomain;
        }
        if (split.length == 2) {
            return split[1];
        }
        throw new UnsupportedOperationException("Input MeterDomain not supported.");
    }
}