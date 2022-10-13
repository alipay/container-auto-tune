/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.handler;

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.controller.model.meter.ValidateMeterResult;
import com.alipay.autotuneservice.meter.MeterService;
import com.alipay.autotuneservice.meter.model.MeterType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author huangkaifei
 * @version : MeterHandler.java, v 0.1 2022年08月19日 4:27 PM huangkaifei Exp $
 */
public abstract class MeterHandler extends BaseMeterService {

    @Autowired
    private MeterHandlerFactory meterHandlerFactory;

    @Autowired
    private MeterService meterService;

    @PostConstruct
    public void register() {
        meterHandlerFactory.registerMeterHandler(this);
    }

    public abstract Map<String, String> queryMetric(MeterMeta meterMeta, long startTime, long endTime, long step);

    public abstract Boolean register(MeterMeta meterMeta);

    public abstract ValidateMeterResult validateMeter(MeterMeta meterMeta);

    public abstract MeterType getMeterType();

}