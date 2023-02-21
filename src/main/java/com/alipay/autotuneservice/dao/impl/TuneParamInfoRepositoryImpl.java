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
import com.alipay.autotuneservice.dao.TuneParamInfoRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneParamInfoRecord;
import com.alipay.autotuneservice.model.tune.params.TuneParamUpdateStatus;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.ObjectUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author huangkaifei
 * @version : TuneParamInfoRepositoryImpl.java, v 0.1 2022年05月18日 2:06 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class TuneParamInfoRepositoryImpl extends BaseDao implements TuneParamInfoRepository {

    @Override
    public TuneParamInfoRecord getById(Integer id) {
        return mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(Tables.TUNE_PARAM_INFO.ID.eq(id))
                .fetch()
                .into(TuneParamInfoRecord.class).get(0);
    }

    @Override
    public List<TuneParamInfoRecord> getByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(Tables.TUNE_PARAM_INFO.APP_ID.eq(appId))
                .groupBy(Tables.TUNE_PARAM_INFO.PIPELINE_ID)
                .fetchInto(TuneParamInfoRecord.class);
    }

    @Override
    public TuneParamInfoRecord findByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(Tables.TUNE_PARAM_INFO.APP_ID.eq(appId))
                .orderBy(Tables.TUNE_PARAM_INFO.ID.desc())
                .limit(1)
                .fetchOneInto(TuneParamInfoRecord.class);
    }

    @Override
    public TuneParamInfoRecord getByAppIdAndPipelineId(Integer appId, Integer pipelineId) {
        Condition condition = null;
        if (appId != null && appId > 0 ) {
            condition = Tables.TUNE_PARAM_INFO.APP_ID.eq(appId);
        }
        if (pipelineId != null && pipelineId > 0 ) {
            condition = Tables.TUNE_PARAM_INFO.PIPELINE_ID.eq(pipelineId);
        }
        if (condition == null) {
            return null;
        }
        return mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(condition)
                .orderBy(Tables.TUNE_PARAM_INFO.CREATE_TIME.desc())
                .limit(1)
                .fetchOneInto(TuneParamInfoRecord.class);
    }

    @Override
    public TuneParamInfoRecord getByAppIdAndPipelineIdAndStatus(Integer appId, Integer pipelineId, TuneParamUpdateStatus updateStatus) {
        log.info("getByAppIdAndPipelineIdAndStatus appId: {}, pipelineId: {}, updateStatus: {}", appId, pipelineId, updateStatus);
        Condition condition = Tables.TUNE_PARAM_INFO.APP_ID.eq(ObjectUtil.checkIntegerPositive(appId));
        condition = condition.and(Tables.TUNE_PARAM_INFO.PIPELINE_ID.eq(pipelineId));
        if (updateStatus != null) {
            condition = condition.and(Tables.TUNE_PARAM_INFO.UPDATE_STATUS.eq(updateStatus.name()));
        }
        return mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(condition)
                .orderBy(Tables.TUNE_PARAM_INFO.CREATE_TIME.desc())
                .limit(1)
                .fetchOneInto(TuneParamInfoRecord.class);
    }

    @Override
    public int insert(TuneParamInfoRecord record) {
        return mDSLContext.insertInto(Tables.TUNE_PARAM_INFO)
                .set(Tables.TUNE_PARAM_INFO.CREATE_TIME, LocalDateTime.now())
                .set(Tables.TUNE_PARAM_INFO.UPDATED_TIME, LocalDateTime.now())
                .set(Tables.TUNE_PARAM_INFO.APP_ID, record.getAppId())
                .set(Tables.TUNE_PARAM_INFO.PIPELINE_ID, record.getPipelineId())
                .set(Tables.TUNE_PARAM_INFO.JVM_MARKET_ID, record.getJvmMarketId())
                .set(Tables.TUNE_PARAM_INFO.DECISION_ID, record.getDecisionId())
                .set(Tables.TUNE_PARAM_INFO.ACCESS_TOKEN, record.getAccessToken())
                .set(Tables.TUNE_PARAM_INFO.UPDATE_PARAMS, record.getUpdateParams())
                .set(Tables.TUNE_PARAM_INFO.DEFAULT_PARAM, record.getDefaultParam())
                .set(Tables.TUNE_PARAM_INFO.CHANGED_TUNE_GROUP, record.getChangedTuneGroup())
                .set(Tables.TUNE_PARAM_INFO.UPDATE_STATUS, record.getUpdateStatus())
                .set(Tables.TUNE_PARAM_INFO.OPERATOR, record.getOperator())
                .execute();
    }

    @Override
    public TuneParamInfoRecord findByDecisionId(String decisionId) {
        List<TuneParamInfoRecord> tuneParamInfoRecords = mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(Tables.TUNE_PARAM_INFO.DECISION_ID.eq(decisionId))
                .fetchInto(TuneParamInfoRecord.class);
        return CollectionUtils.isEmpty(tuneParamInfoRecords) ? null : tuneParamInfoRecords.get(0);
    }

    @Override
    public TuneParamInfoRecord getUserModifiedJvmOption(Integer appId, Integer jvmMarketId) {
        Condition condition = null;
        if (appId != null && appId > 0 ) {
            condition = Tables.TUNE_PARAM_INFO.APP_ID.eq(appId);
        }
        if (jvmMarketId != null && jvmMarketId > 0) {
            condition = condition.and(Tables.TUNE_PARAM_INFO.JVM_MARKET_ID.eq(jvmMarketId));
        }
        if (condition == null) {
            return null;
        }
        return mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(condition)
                .orderBy(Tables.TUNE_PARAM_INFO.CREATE_TIME.desc())
                .limit(1)
                .fetchOneInto(TuneParamInfoRecord.class);
    }

    @Override
    public int update(TuneParamInfoRecord record) {
        Preconditions.checkNotNull(record);
        UpdateQuery<TuneParamInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_PARAM_INFO);
        if (record.getAppId() != null && record.getAppId() > 0) {
            updateQuery.addValue(Tables.TUNE_PARAM_INFO.APP_ID, record.getAppId());
        }
        if (record.getPipelineId() != null && record.getPipelineId() > 0) {
            updateQuery.addValue(Tables.TUNE_PARAM_INFO.PIPELINE_ID, record.getPipelineId());
        }
        if (record.getJvmMarketId() != null && record.getJvmMarketId() > 0) {
            updateQuery.addValue(Tables.TUNE_PARAM_INFO.JVM_MARKET_ID, record.getJvmMarketId());
        }
        if (StringUtils.isNotBlank(record.getDefaultParam())) {
            updateQuery.addValue(Tables.TUNE_PARAM_INFO.DEFAULT_PARAM, record.getDefaultParam());
        }
        if (StringUtils.isNotBlank(record.getUpdateParams())) {
            updateQuery.addValue(Tables.TUNE_PARAM_INFO.UPDATE_PARAMS, record.getUpdateParams());
        }
        if (StringUtils.isNotBlank(record.getChangedTuneGroup())) {
            updateQuery.addValue(Tables.TUNE_PARAM_INFO.CHANGED_TUNE_GROUP, record.getChangedTuneGroup());
        }
        if (StringUtils.isNotBlank(record.getUpdateStatus())) {
            updateQuery.addValue(Tables.TUNE_PARAM_INFO.UPDATE_STATUS, record.getUpdateStatus());
        }
        updateQuery.addValue(Tables.TUNE_PARAM_INFO.UPDATED_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.TUNE_PARAM_INFO.ID.eq(record.getId()));
        return updateQuery.execute();
    }

    @Override
    public TuneParamInfoRecord findTunableTuneParamRecord(Integer appId, Integer pipelineId) {
        Condition condition = Tables.TUNE_PARAM_INFO.APP_ID.eq(ObjectUtil.checkIntegerPositive(appId));
        condition = condition.and(Tables.TUNE_PARAM_INFO.PIPELINE_ID.eq(pipelineId));
        condition = condition.and(Tables.TUNE_PARAM_INFO.UPDATE_STATUS.ne(TuneParamUpdateStatus.END.name()));
        return mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(condition)
                .orderBy(Tables.TUNE_PARAM_INFO.ID.desc())
                .limit(1)
                .fetchOneInto(TuneParamInfoRecord.class);
    }

    @Override
    public TuneParamInfoRecord findTuneParamRecord(Integer appId, Integer pipelineId, TuneParamUpdateStatus status) {
        Condition condition = Tables.TUNE_PARAM_INFO.APP_ID.eq(ObjectUtil.checkIntegerPositive(appId));
        condition = condition.and(Tables.TUNE_PARAM_INFO.PIPELINE_ID.eq(pipelineId));
        condition = condition.and(Tables.TUNE_PARAM_INFO.UPDATE_STATUS.eq(status.name()));
        return mDSLContext.select()
                .from(Tables.TUNE_PARAM_INFO)
                .where(condition)
                .orderBy(Tables.TUNE_PARAM_INFO.UPDATED_TIME.desc())
                .limit(1)
                .fetchOneInto(TuneParamInfoRecord.class);
    }

    @Override
    public Integer updateJvmMarketId(Integer appId, Integer pipelineId, Integer jvmMarketId) {
        UpdateQuery<TuneParamInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_PARAM_INFO);
        updateQuery.addValue(Tables.TUNE_PARAM_INFO.JVM_MARKET_ID, jvmMarketId);
        updateQuery.addConditions(Tables.TUNE_PARAM_INFO.APP_ID.eq(appId),Tables.TUNE_PARAM_INFO.PIPELINE_ID.eq(pipelineId));
        return updateQuery.execute();
    }
}