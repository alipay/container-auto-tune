/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dynamodb.repository;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dynamodb.bean.MeterMetricInfo;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author huangkaifei
 * @version : MeterMetricInfoRepository.java, v 0.1 2022年10月08日 2:17 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class MeterMetricInfoRepository {

    private static final String METER_METRIC_INFO_TABLE = "meter_metric_info";

    @Autowired
    private NosqlService nosqlService;

    public List<MeterMetricInfo> findByAppNameAndGmt(String appName, long start, long end) {
        try {
            return nosqlService.queryRange(METER_METRIC_INFO_TABLE, "appName", appName, "gmtCreated", start, end, MeterMetricInfo.class);
        } catch (Exception e) {
            log.error("findByAppId for appName={} occurs an error", appName, e);
            return Lists.newArrayList();
        }
    }


    public void insert(MeterMetricInfo meterMetricInfo) {
        try {
            nosqlService.insert(Objects.requireNonNull(meterMetricInfo, "meterMetricInfo Can not be null."), METER_METRIC_INFO_TABLE);
        } catch (Exception e) {
            log.error("insert meterMetricInfo={} occurs an error", JSON.toJSONString(meterMetricInfo), e);
        }
    }
}