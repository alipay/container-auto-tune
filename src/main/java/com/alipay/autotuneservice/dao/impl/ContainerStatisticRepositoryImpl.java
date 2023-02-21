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
import com.alipay.autotuneservice.dao.ContainerStatisticRepository;
import com.alipay.autotuneservice.dao.converter.ContainerStatisticConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.ContainerStatisticsRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : ContainerStatisticRepositoryImpl.java, v 0.1 2022年11月01日 10:18 AM huangkaifei Exp $
 */
@Slf4j
@Service
public class ContainerStatisticRepositoryImpl extends BaseDao implements ContainerStatisticRepository {

    private static final com.alipay.autotuneservice.dao.jooq.tables.ContainerStatistics TABLE = Tables.CONTAINER_STATISTICS;
    private final ContainerStatisticConverter converter = new ContainerStatisticConverter();


    @Override
    public void insert(ContainerStatistics statistics) {
        if (statistics == null) {
            return;
        }
        mDSLContext.insertInto(TABLE)
                .set(TABLE.CONTAINER_ID, statistics.getContainerId())
                .set(TABLE.GMT_MODIFIED, statistics.getGmtCreated())
                .set(TABLE.APP_ID, statistics.getAppId())
                .set(TABLE.POD_NAME, statistics.getPodName())
                .set(TABLE.DATA, JSON.toJSONString(statistics))
                .execute();
    }

    @Override
    public List<ContainerStatistics> queryRange(String containerId, long start, long end) {
        if (StringUtils.isBlank(containerId) || start > end) {
            return Lists.newArrayList();
        }
        return mDSLContext.select()
                .from(TABLE)
                .where(TABLE.CONTAINER_ID.eq(containerId))
                .fetchInto(ContainerStatisticsRecord.class)
                .stream()
                .map(converter::serialize)
                .collect(Collectors.toList());
    }
}