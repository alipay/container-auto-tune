/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.JvmMonitorMetricDataRepository;
import com.alipay.autotuneservice.dao.converter.JvmMonitorMetricDataConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMonitorMetricDataRecord;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataRepositoryImpl.java, v 0.1 2022年10月31日 9:10 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class JvmMonitorMetricDataRepositoryImpl extends BaseDao implements JvmMonitorMetricDataRepository {

    private final com.alipay.autotuneservice.dao.jooq.tables.JvmMonitorMetricData TABLE = Tables.JVM_MONITOR_METRIC_DATA;
    private final JvmMonitorMetricDataConverter converter = new JvmMonitorMetricDataConverter();

    @Override
    public List<JvmMonitorMetricData> queryByPodNameAndDt(String podName, long dt) {
        Preconditions.checkArgument(StringUtils.isNotBlank(podName), "podName can not be empty.");
        List<JvmMonitorMetricDataRecord> result = mDSLContext.select()
                .from(TABLE)
                .where(TABLE.POD_NAME.eq(podName))
                .and(TABLE.GMT_MODIFIED.eq(dt))
                .fetchInto(JvmMonitorMetricDataRecord.class);
        return Optional.of(result).orElse(Lists.newArrayList()).stream().map(converter::serialize).collect(Collectors.toList());
    }

    @Override
    public void insert(JvmMonitorMetricData data) {
        Preconditions.checkNotNull(data);
        mDSLContext.insertInto(TABLE)
                .set(TABLE.APP_NAME, data.getApp())
                .set(TABLE.POD_NAME, data.getPod())
                .set(TABLE.GMT_MODIFIED, data.getPeriod())
                .set(TABLE.DATA, JSON.toJSONString(data))
                .execute();
    }

    @Override
    public List<JvmMonitorMetricData> getPodJvmMetric(String podName, long start, long end) {
        Preconditions.checkArgument(StringUtils.isNotBlank(podName), "podName can not be empty.");
        List<JvmMonitorMetricDataRecord> result = mDSLContext.select()
                .from(TABLE)
                .where(TABLE.POD_NAME.eq(podName))
                .and(TABLE.GMT_MODIFIED.gt(start))
                .and(TABLE.GMT_MODIFIED.lt(end))
                .fetchInto(JvmMonitorMetricDataRecord.class);
        return Optional.of(result).orElse(Lists.newArrayList()).stream().map(converter::serialize).collect(Collectors.toList());
    }
}