/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.schedule;

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.controller.model.meter.MeterMetricResult;
import com.alipay.autotuneservice.dynamodb.bean.MeterMetricInfo;
import com.alipay.autotuneservice.dynamodb.repository.MeterMetricInfoRepository;
import com.alipay.autotuneservice.meter.MeterService;
import com.alipay.autotuneservice.meter.config.MeterConfigFactory;
import com.alipay.autotuneservice.util.DateUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : MonitorMeterMetricTask.java, v 0.1 2022年08月26日 10:38 AM huangkaifei Exp $
 */
@Slf4j
@Component
public class MonitorMeterMetricTask {

    @Autowired
    private MeterService meterService;

    @Autowired
    private MeterConfigFactory meterConfigFactory;

    @Autowired
    private MeterMetricInfoRepository metricInfoRepository;

    //@Scheduled(fixedRate = 60 * 1000) // 待接入后放开,防止占表空间
    public void execute() {
        log.info("MonitorMeterMetricTask start.");
        // get meter meta
        Set<MeterMeta> meterMatas = meterConfigFactory.getMeterMatas();
        // fetch and store meter data by AppId
        meterMatas.parallelStream().forEach(this::fetchAndStoreMeterData);
    }

    private void fetchAndStoreMeterData(MeterMeta meterMeta) {
        Integer appId = meterMeta.getAppId();
        long end = System.currentTimeMillis();
        long start = end - 60 * 1000;
        long step = 0;
        // fetch
        Map<String, String>  res = meterService.queryMeterMetric(meterMeta, start, end, step);
        List<MeterMetricResult> collect = Optional.ofNullable(res)
                .orElse(Maps.newHashMap())
                .entrySet().stream()
                .map(entry -> MeterMetricResult.builder().metricName(entry.getKey()).metricResult(entry.getValue()).build())
                .collect(Collectors.toList());
        // store
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }
        collect.stream().parallel().forEach(item -> {
            MeterMetricInfo build = MeterMetricInfo.builder()
                    .metricName(item.getMetricName())
                    .metricVal(item.getMetricResult())
                    .gmtCreated(DateUtils.nowTimestamp())
                    .dt(DateUtils.getNowDt())
                    .appId(appId)
                    .build();
            metricInfoRepository.insert(build);
        });

    }
}