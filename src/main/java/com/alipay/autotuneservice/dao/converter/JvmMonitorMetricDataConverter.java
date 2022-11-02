/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMonitorMetricDataRecord;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataConverter.java, v 0.1 2022年10月31日 8:36 PM huangkaifei Exp $
 */
public class JvmMonitorMetricDataConverter implements EntityConverter<JvmMonitorMetricDataRecord, JvmMonitorMetricData>{
    @Override
    public JvmMonitorMetricData serialize(JvmMonitorMetricDataRecord entity) {
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(entity.getData(), new TypeReference<JvmMonitorMetricData>(){});
    }

    @Override
    public JvmMonitorMetricDataRecord deserialize(JvmMonitorMetricData data) {
        if (data == null) {
            return null;
        }
        JvmMonitorMetricDataRecord record = new JvmMonitorMetricDataRecord();
        record.setAppName(data.getApp());
        record.setPodName(data.getPod());
        record.setGmtModified(data.getPeriod());
        record.setData(JSON.toJSONString(data));
        return record;
    }
}