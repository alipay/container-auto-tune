/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.MeterMetricInfoRepository;
import com.alipay.autotuneservice.dao.converter.MeterMetricInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dynamodb.bean.MeterMetricInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author huangkaifei
 * @version : MeterMetricInfoRepositoryImpl.java, v 0.1 2022年11月01日 2:37 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class MeterMetricInfoRepositoryImpl extends BaseDao implements MeterMetricInfoRepository {

    private final com.alipay.autotuneservice.dao.jooq.tables.MeterMetricInfo TABLE     = Tables.METER_METRIC_INFO;
    private final MeterMetricInfoConverter                                   converter = new MeterMetricInfoConverter();

    @Override
    public void insert(MeterMetricInfo meterMetricInfo) {
        if (meterMetricInfo == null) {
            return;
        }
        mDSLContext.insertInto(TABLE)
                .set(TABLE.APP_ID, meterMetricInfo.getAppId())
                .set(TABLE.APP_NAME, meterMetricInfo.getAppName())
                .set(TABLE.GMT_CREATED, meterMetricInfo.getGmtCreated())
                .set(TABLE.DT, meterMetricInfo.getDt())
                .set(TABLE.DATA, meterMetricInfo.getData())
                .set(TABLE.METRIC_NAME, meterMetricInfo.getMetricName())
                .set(TABLE.METRIC_VENDOR, meterMetricInfo.getMeterVendor())
                .execute();
    }
}