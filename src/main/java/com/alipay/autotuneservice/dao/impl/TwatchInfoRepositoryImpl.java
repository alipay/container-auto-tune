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

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.TwatchInfoRepository;
import com.alipay.autotuneservice.dao.converter.TwatchInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.TwatchInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.TwatchInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : TwatchInfoRepositoryImpl.java, v 0.1 2022年10月31日 5:20 PM huangkaifei Exp $
 */
@Service
public class TwatchInfoRepositoryImpl extends BaseDao implements TwatchInfoRepository {

    private final TwatchInfoConverter converter = new TwatchInfoConverter();
    private final TwatchInfo          TABLE     = Tables.TWATCH_INFO;

    @Override
    public void insert(TwatchInfoDo twatchInfoDo) {

        mDSLContext.insertInto(TABLE)
                .set(TABLE.CONTAINER_ID, twatchInfoDo.getContainerId())
                .set(TABLE.CONTAINER_NAME, twatchInfoDo.getContainerName())
                .set(TABLE.POD_NAME, twatchInfoDo.getPodName())
                .set(TABLE.AGENT_NAME, twatchInfoDo.getAgentName())
                .set(TABLE.NODE_NAME, twatchInfoDo.getNodeName())
                .set(TABLE.NODE_IP, twatchInfoDo.getNodeIp())
                .set(TABLE.NAMESPACE, twatchInfoDo.getNameSpace())
                .set(TABLE.GMT_MODIFIED, twatchInfoDo.getGmtModified())
                .set(TABLE.DT_PERIOD, twatchInfoDo.getDtPeriod())
                .execute();
    }

    @Override
    public List<TwatchInfoDo> findByContainerId(String containerId) {
        List<TwatchInfoRecord> result = mDSLContext.select()
                .from(TABLE)
                .where(TABLE.CONTAINER_ID.eq(containerId))
                .fetchInto(TwatchInfoRecord.class);
        return Optional.of(result).orElse(Lists.newArrayList()).stream()
                .map(converter::serialize).collect(Collectors.toList());
    }

    @Override
    public List<TwatchInfoDo> findInfoByPod(String podName) {
        List<TwatchInfoRecord> result = mDSLContext.select()
                .from(TABLE)
                .where(TABLE.POD_NAME.eq(podName))
                .fetchInto(TwatchInfoRecord.class);
        return Optional.of(result).orElse(Lists.newArrayList()).stream()
                .map(converter::serialize).collect(Collectors.toList());
    }

    @Override
    public List<TwatchInfoDo> findInfoByAgent(String agentName) {
        List<TwatchInfoRecord> result = mDSLContext.select()
                .from(TABLE)
                .where(TABLE.AGENT_NAME.eq(agentName))
                .fetchInto(TwatchInfoRecord.class);
        return Optional.of(result).orElse(Lists.newArrayList()).stream()
                .map(converter::serialize).collect(Collectors.toList());
    }

    @Override
    public List<TwatchInfoDo> listAll() {
        return  mDSLContext.select()
                .from(TABLE)
                .orderBy(TABLE.GMT_MODIFIED)
                .limit(200)
                .fetchInto(TwatchInfoRecord.class)
                .stream()
                .map(converter::serialize)
                .collect(Collectors.toList());
    }
}