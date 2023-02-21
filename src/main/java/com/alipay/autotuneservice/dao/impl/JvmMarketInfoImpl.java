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
import com.alipay.autotuneservice.dao.BaseLineInfo;
import com.alipay.autotuneservice.dao.JvmMarketInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.BaseLineRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMarketInfoRecord;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.UpdateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : TunePlanRepository.java, v 0.1 2022年04月18日 15:41 chenqu Exp $
 */
@Service
@Slf4j
public class JvmMarketInfoImpl extends BaseDao implements JvmMarketInfo {

    @Autowired
    private BaseLineInfo baseLineInfo;

    @Override
    public JvmMarketInfoRecord getJvmInfo(Integer marketId) {
        List<JvmMarketInfoRecord> records = mDSLContext.select()
                .from(Tables.JVM_MARKET_INFO)
                .where(Tables.JVM_MARKET_INFO.ID.eq(marketId))
                .fetch()
                .into(JvmMarketInfoRecord.class);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.get(0);
    }

    @Override
    public Integer insert(String jvmConfig) {
        return mDSLContext.insertInto(Tables.JVM_MARKET_INFO)
                .set(Tables.JVM_MARKET_INFO.JVM_CONFIG, jvmConfig)
                .set(Tables.JVM_MARKET_INFO.CREATED_TIME, DateUtils.now())
                .returning()
                .fetchOne().getId();
    }

    @Override
    public Integer getOrInsertJvmByCMD(String jvmCmd, Integer appId, Integer pipelineId) {
        List<JvmMarketInfoRecord> records = mDSLContext.select()
                .from(Tables.JVM_MARKET_INFO)
                .where(Tables.JVM_MARKET_INFO.JVM_CONFIG.eq(jvmCmd))
                .fetch()
                .into(JvmMarketInfoRecord.class);
        if (CollectionUtils.isEmpty(records)) {
            Integer jvmMarketId = insert(jvmCmd);
            insertBaseLine(appId, pipelineId, jvmMarketId, jvmCmd);
            return jvmMarketId;
        }
        return records.get(0).getId();
    }

    @Override
    public void updateJvmConfig(Integer marketId, String jvmCmd) {
        UpdateQuery<JvmMarketInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.JVM_MARKET_INFO);
        updateQuery.addValue(Tables.JVM_MARKET_INFO.JVM_CONFIG, jvmCmd);
        updateQuery.addConditions(Tables.JVM_MARKET_INFO.ID.eq(marketId));
        updateQuery.execute();
    }

    @Override
    public List<JvmMarketInfoRecord> getJvmInfo(List<Integer> marketIds) {
        return mDSLContext.select()
                .from(Tables.JVM_MARKET_INFO)
                .where(Tables.JVM_MARKET_INFO.ID.in(marketIds))
                .orderBy(Tables.JVM_MARKET_INFO.ID)
                .fetchInto(JvmMarketInfoRecord.class);
    }

    private void insertBaseLine(Integer appId, Integer pipeLineId, Integer jvmMarketId, String jvmCmd) {
        try {
            Optional<JvmMarketInfoRecord> sameJvm = null;
            List<BaseLineRecord> records = baseLineInfo.getByAppIdAndPipelineId(appId, pipeLineId);
            if (CollectionUtils.isNotEmpty(records)) {
                List<Integer> jvmMarketIds = records.stream().map(BaseLineRecord::getJvmMarketId).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(jvmMarketIds)){
                    List<JvmMarketInfoRecord> recordList = getJvmInfo(jvmMarketIds);
                    if(CollectionUtils.isNotEmpty(recordList)){
                        sameJvm = recordList.stream().filter(r -> TruncationJvmMarketId(r.getJvmConfig()).equals(jvmCmd)).findFirst();
                    }
                }
            }
            BaseLineRecord baseLineRecord = baseLineInfo.getByAppId(appId);
            BaseLineRecord record = new BaseLineRecord();
            record.setPipelineId(pipeLineId);
            record.setJvmMarketId(jvmMarketId);
            Integer version = baseLineRecord == null ? 1 : baseLineRecord.getVersion() + 1;
            record.setVersion(version);
            record.setAppId(appId);
            if (null == sameJvm || !sameJvm.isPresent()) {
                baseLineInfo.insert(record);
            }
        } catch (Exception e) {
            log.error("insertBaseLine insert occurs an error, pipelineId: {}", pipeLineId);
        }

    }

    protected String TruncationJvmMarketId(String jvm) {
        if (jvm.contains(UserUtil.TUNE_JVM_APPEND)) {
            String[] arrays = jvm.split(" ");
            List<String> filterArrs = Arrays.stream(arrays).filter(arr -> !arr.contains(UserUtil.TUNE_JVM_APPEND)).collect(
                    Collectors.toList());
            jvm = String.join(" ", filterArrs);
        }
        return jvm;
    }

}