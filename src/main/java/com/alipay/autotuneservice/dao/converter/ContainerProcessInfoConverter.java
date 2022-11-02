/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.jooq.tables.records.ContainerProcessInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerProcessInfo;

/**
 * @author huangkaifei
 * @version : ContainerProcessInfoConverter.java, v 0.1 2022年10月31日 8:37 PM huangkaifei Exp $
 */
public class ContainerProcessInfoConverter implements EntityConverter<ContainerProcessInfoRecord, ContainerProcessInfo>{

    @Override
    public ContainerProcessInfo serialize(ContainerProcessInfoRecord entity) {
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(entity.getData(), new TypeReference<ContainerProcessInfo>(){});
    }

    @Override
    public ContainerProcessInfoRecord deserialize(ContainerProcessInfo data) {
        if (data == null) {
            return null;
        }
        ContainerProcessInfoRecord record = new ContainerProcessInfoRecord();
        record.setAppId(Long.valueOf(data.getAppId()));
        record.setPodName(data.getPodName());
        record.setGmtModified(data.getGmtCreated());
        record.setData(JSON.toJSONString(data));
        return record;
    }
}