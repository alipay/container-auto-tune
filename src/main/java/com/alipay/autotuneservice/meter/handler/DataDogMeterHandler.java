/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.handler;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.controller.model.meter.MeterMetricResult;
import com.alipay.autotuneservice.controller.model.meter.ValidateMeterResult;
import com.alipay.autotuneservice.meter.model.MeterType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author huangkaifei
 * @version : DataDogMeterHandler.java, v 0.1 2022年08月23日 9:55 PM huangkaifei Exp $
 */
@Slf4j
@Service("dataDogMeterHandler")
public class DataDogMeterHandler extends MeterHandler {

    @Override
    public Map<String, String> queryMetric(MeterMeta meterMeta, long startTime, long endTime, long step) {
        return null;
    }

    @Override
    public Boolean register(MeterMeta meterMeta) {
        // save register Info
        return saveOrUpdate(meterMeta);
    }

    @Override
    public ValidateMeterResult validateMeter(MeterMeta meterMeta) {
        log.info("DataDog validateMeter start. meterMeta={}", JSON.toJSONString(meterMeta));
        List<MeterMetricResult> result = Lists.newArrayList();
        result.add(MeterMetricResult.builder().metricName("QPS").metricResult("xxxx").build());
        result.add(MeterMetricResult.builder().metricName("RT").metricResult("10").build());
        result.add(MeterMetricResult.builder().metricName("ERROR").metricResult("20").build());
        return ValidateMeterResult.builder()
                .success(true)
                .message("DataDog connected successfully")
                .result(result)
                .build();
    }

    @Override
    public MeterType getMeterType() {
        return MeterType.DATADOG;
    }
}