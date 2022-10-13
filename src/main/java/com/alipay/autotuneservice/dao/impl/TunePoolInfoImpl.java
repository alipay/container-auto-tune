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

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.TunePoolInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePoolInfoRecord;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.InsertQuery;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dutianze
 * @version RuleInfoImpl.java, v 0.1 2022年02月22日 19:20 dutianze
 */
@Service
public class TunePoolInfoImpl extends BaseDao implements TunePoolInfo {

    @Override
    public TunePoolInfoRecord getTunePool(String accessToken, int pipelineId, int appId) {
        return mDSLContext.select().from(Tables.TUNE_POOL_INFO)
            .where(Tables.TUNE_POOL_INFO.ACCESS_TOKEN.eq(accessToken))
            .and(Tables.TUNE_POOL_INFO.PIPELINE_ID.eq(pipelineId))
            .and(Tables.TUNE_POOL_INFO.APP_ID.eq(appId)).fetchOneInto(TunePoolInfoRecord.class);
    }

    @Override
    public int updateTunePool(TunePoolInfoRecord record) {
        UpdateQuery<TunePoolInfoRecord> updateQuery = mDSLContext
            .updateQuery(Tables.TUNE_POOL_INFO);
        if (StringUtils.isNotEmpty(record.getBatchPoolStatus())) {
            updateQuery.addValue(Tables.TUNE_POOL_INFO.BATCH_POOL_STATUS,
                record.getBatchPoolStatus());
        }
        if (StringUtils.isNotEmpty(record.getBatchPoolConfig())) {
            updateQuery.addValue(Tables.TUNE_POOL_INFO.BATCH_POOL_CONFIG,
                record.getBatchPoolConfig());
        }
        if (StringUtils.isNotEmpty(record.getExperimentPoolConfig())) {
            updateQuery.addValue(Tables.TUNE_POOL_INFO.EXPERIMENT_POOL_CONFIG,
                record.getExperimentPoolConfig());
        }
        if (StringUtils.isNotEmpty(record.getExperimentPoolStatus())) {
            updateQuery.addValue(Tables.TUNE_POOL_INFO.EXPERIMENT_POOL_STATUS,
                record.getExperimentPoolStatus());
        }
        updateQuery.addConditions(Tables.TUNE_POOL_INFO.APP_ID.eq(record.getAppId())
            .and(Tables.TUNE_POOL_INFO.PIPELINE_ID.eq(record.getPipelineId()))
            .and(Tables.TUNE_POOL_INFO.ACCESS_TOKEN.eq(record.getAccessToken())));
        return updateQuery.execute();
    }

    @Override
    public void createTunePool(TunePoolInfoRecord record) {
        if (StringUtils.isEmpty(record.getExperimentPoolStatus())) {
            record.setExperimentPoolStatus(TunePoolStatus.INIT.name());
        }
        if (StringUtils.isEmpty(record.getBatchPoolStatus())) {
            record.setBatchPoolStatus(TunePoolStatus.INIT.name());
        }
        if (StringUtils.isEmpty(record.getExperimentPoolConfig())) {
            record.setExperimentPoolConfig(JSONObject.toJSONString(ImmutableMap.of()));
        }
        if (StringUtils.isEmpty(record.getBatchPoolConfig())) {
            record.setBatchPoolConfig(JSONObject.toJSONString(ImmutableMap.of()));
        }
        record.setCreatedTime(LocalDateTime.now());
        InsertQuery<TunePoolInfoRecord> insertQuery = mDSLContext
            .insertQuery(Tables.TUNE_POOL_INFO);
        insertQuery.addRecord(record);
        insertQuery.execute();
    }

    @Override
    public List<TunePoolInfoRecord> findRunningPool() {
        Condition condition = Tables.TUNE_POOL_INFO.BATCH_POOL_STATUS.eq(
            TunePoolStatus.RUNNABLE.name()).or(
            Tables.TUNE_POOL_INFO.EXPERIMENT_POOL_STATUS.eq(TunePoolStatus.RUNNABLE.name()));
        return mDSLContext.select().from(Tables.TUNE_POOL_INFO).where(condition).fetch()
            .into(TunePoolInfoRecord.class);
    }
}