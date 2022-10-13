/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.handler;

import com.alipay.autotuneservice.meter.model.MeterType;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangkaifei
 * @version : MeterHandlerFactory.java, v 0.1 2022年08月26日 1:00 AM huangkaifei Exp $
 */
@Service
public class MeterHandlerFactory {
    private static final Map<MeterType, MeterHandler> METER_RESOURCE = new ConcurrentHashMap<>();

    public void registerMeterHandler(MeterHandler meterHandler){
        MeterType meterType = meterHandler.getMeterType();
        if (!METER_RESOURCE.containsKey(meterType)) {
            METER_RESOURCE.put(meterType, meterHandler);
            return;
        }
    }

    public MeterHandler getMeterHandler(MeterType meterType){
        if (METER_RESOURCE.containsKey(meterType)) {
            return METER_RESOURCE.get(meterType);
        }
        throw new UnsupportedOperationException(String.format("MeterHandlerFactory does not support meterType=%s", meterType.name()));
    }
}