/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.handler;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.controller.model.meter.MeterMetricResult;
import com.alipay.autotuneservice.controller.model.meter.ValidateMeterResult;
import com.alipay.autotuneservice.dao.MeterMetaInfoRepository;
import com.alipay.autotuneservice.meter.model.MeterMetric;
import com.alipay.autotuneservice.meter.model.MeterType;
import com.alipay.autotuneservice.util.MeterUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : PrometheusMeterHandler.java, v 0.1 2022年08月23日 3:47 PM huangkaifei Exp $
 */
@Slf4j
@Service("prometheusMeterHandler")
public class PrometheusMeterHandler extends MeterHandler {

    private static final String PROMETHEUS_QUERY = "/api/v1/query?query=";

    @Autowired
    private MeterMetaInfoRepository metaInfoRepository;

    @Override
    public Map<String, String> queryMetric(MeterMeta meterMeta, long startTime, long endTime, long step) {
        Preconditions.checkArgument(meterMeta != null, "meterMeta can not be null.");
        String meterDomain = meterMeta.getMeterDomain();
        Preconditions.checkArgument(StringUtils.isNotBlank(meterDomain), "meterDomain is empty");
        List<MeterMetric> metricList = meterMeta.getMetricList();
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(metricList), "metricList is empty");
        log.info("queryMetric, input meterMeta={}", JSON.toJSONString(meterMeta));

        final String requestPrefix = String.format("%s%s", meterDomain, PROMETHEUS_QUERY);
        return metricList.stream()
                .filter(item -> StringUtils.isNotBlank(item.getMetricName()) && StringUtils.isNotBlank(item.getMetricPath()))
                .collect(Collectors.toMap(MeterMetric::getMetricName, val -> {
                    String metricData = MeterUtil.getMetricData(requestPrefix, val.getMetricPath());
                    return JSON.toJSONString(MeterUtil.parsePrometheusData(metricData));
                }));
    }

    @Override
    public Boolean register(MeterMeta meterMeta) {
        // save register Info
        return saveOrUpdate(meterMeta);
    }

    @Override
    public ValidateMeterResult validateMeter(MeterMeta meterMeta) {
        log.info("Prometheus validateMeter start. meterMeta={}", JSON.toJSONString(meterMeta));
        try {
            Map<String, String> metricNameValueMap = queryMetric(meterMeta, 0l, 0l, 0l);
            List<MeterMetricResult> collect = Optional.ofNullable(metricNameValueMap)
                    .orElse(Maps.newHashMap())
                    .entrySet()
                    .stream()
                    .map(entry -> MeterMetricResult.builder().metricName(entry.getKey()).metricResult(entry.getValue()).build()).collect(
                    Collectors.toList());

            if (CollectionUtils.isEmpty(collect)) {
                return ValidateMeterResult.failedResult(String.format("failed to fetch meterData for %s", getMeterType()));
            }
            return ValidateMeterResult.builder().success(true).message("prometheus connected successfully.").result(collect).build();
        } catch (Exception e) {
            log.error("{} validateMeter occurs an error.", getMeterType(), e);
            return ValidateMeterResult.failedResult(
                    String.format("validateMeter=%s failed, errorMsg=%s.", JSON.toJSONString(meterMeta), e.getMessage()));
        }
    }

    @Override
    public MeterType getMeterType() {
        return MeterType.PROMETHEUS;
    }
}