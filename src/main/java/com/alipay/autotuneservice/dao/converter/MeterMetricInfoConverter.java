/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.converter;

import com.alipay.autotuneservice.dao.jooq.tables.records.MeterMetricInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.MeterMetricInfo;

/**
 * @author huangkaifei
 * @version : MeterMetricInfoConverter.java, v 0.1 2022年11月01日 2:38 PM huangkaifei Exp $
 */
public class MeterMetricInfoConverter implements EntityConverter<MeterMetricInfoRecord, MeterMetricInfo> {

    @Override
    public MeterMetricInfo serialize(MeterMetricInfoRecord entity) {
        if (entity == null) {
            return null;
        }
        return MeterMetricInfo.builder()
                .metricName(entity.getMetricName())
                .dt(entity.getDt())
                .appId(entity.getAppId())
                .appName(entity.getAppName())
                .gmtCreated(entity.getGmtCreated())
                .meterVendor(entity.getMetricVendor())
                .data(entity.getData())
                .build();
    }

    @Override
    public MeterMetricInfoRecord deserialize(MeterMetricInfo data) {
        if (data == null) {
            return null;
        }
        MeterMetricInfoRecord record = new MeterMetricInfoRecord();
        record.setMetricName(data.getMetricName());
        record.setMetricVendor(data.getMeterVendor());
        record.setAppId(data.getAppId());
        record.setAppName(data.getAppName());
        record.setDt(data.getDt());
        record.setData(data.getData());
        record.setGmtCreated(data.getGmtCreated());
        return record;
    }
}