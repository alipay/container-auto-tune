/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.ContainerProcessInfoRepository;
import com.alipay.autotuneservice.dao.converter.ContainerProcessInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.ContainerProcessInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerProcessInfo;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : ContainerProcessInfoRepositoryImpl.java, v 0.1 2022年11月01日 10:20 AM huangkaifei Exp $
 */
@Slf4j
@Service
public class ContainerProcessInfoRepositoryImpl extends BaseDao implements ContainerProcessInfoRepository {

    private final ContainerProcessInfoConverter                                   converter = new ContainerProcessInfoConverter();
    private final com.alipay.autotuneservice.dao.jooq.tables.ContainerProcessInfo TABLE     = Tables.CONTAINER_PROCESS_INFO;

    @Override
    public void insert(ContainerProcessInfo item) {
        if (item == null) {
            return;
        }
        mDSLContext.insertInto(TABLE)
                .set(TABLE.CONTAINER_ID, item.getContainerId())
                .set(TABLE.APP_ID, Long.valueOf(item.getAppId()))
                .set(TABLE.GMT_MODIFIED, item.getGmtCreated())
                .set(TABLE.POD_NAME, item.getPodName())
                .set(TABLE.DATA, JSON.toJSONString(item))
                .execute();
    }

    @NonNull
    @Override
    public List<ContainerProcessInfo> queryProcessInfos(String containerId) {
        if (StringUtils.isBlank(containerId)) {
            return Lists.newArrayList();
        }
        return mDSLContext.select()
                .from(TABLE)
                .where(TABLE.CONTAINER_ID.eq(containerId))
                .fetchInto(ContainerProcessInfoRecord.class)
                .stream().filter(Objects::nonNull)
                .map(converter::serialize)
                .collect(Collectors.toList());
    }
}