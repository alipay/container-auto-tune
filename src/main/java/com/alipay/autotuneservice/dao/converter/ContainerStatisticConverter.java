/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.jooq.tables.records.ContainerStatisticsRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;

/**
 * @author huangkaifei
 * @version : ContainerStatisticConverter.java, v 0.1 2022年10月31日 8:38 PM huangkaifei Exp $
 */
public class ContainerStatisticConverter implements EntityConverter<ContainerStatisticsRecord, ContainerStatistics> {

    @Override
    public ContainerStatistics serialize(ContainerStatisticsRecord entity) {
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(entity.getData(), new TypeReference<ContainerStatistics>() {});
    }

    @Override
    public ContainerStatisticsRecord deserialize(ContainerStatistics data) {
        if (data == null) {
            return null;
        }
        ContainerStatisticsRecord record = new ContainerStatisticsRecord();
        record.setAppId(data.getAppId());
        record.setPodName(data.getPodName());
        record.setGmtModified(data.getGmtCreated());
        record.setData(JSON.toJSONString(data));
        return record;
    }
}