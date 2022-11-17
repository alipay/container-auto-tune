/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dynamodb.repository;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.MeterMetricInfoRepository;
import com.alipay.autotuneservice.dynamodb.bean.MeterMetricInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huangkaifei
 * @version : MeterMetricInfoService.java, v 0.1 2022年10月08日 2:17 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class MeterMetricInfoService {

    private static final String METER_METRIC_INFO_TABLE = "meter_metric_info";

    @Autowired
    private MeterMetricInfoRepository meterMetricRepository;

    public void insert(MeterMetricInfo meterMetricInfo) {
        try {
            meterMetricRepository.insert(meterMetricInfo);
        } catch (Exception e) {
            log.error("insert meterMetricInfo={} occurs an error", JSON.toJSONString(meterMetricInfo), e);
        }
    }
}