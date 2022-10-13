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
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.converter.TunePlanConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePlanRecord;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.EnhanceBeanUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.UpdateQuery;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : TunePlanRepository.java, v 0.1 2022年04月18日 15:41 chenqu Exp $
 */
@Service
public class TunePlanRepositoryImpl extends BaseDao implements TunePlanRepository {

    private final TunePlanConverter converter = new TunePlanConverter();

    @Override
    public TunePlan findRunningTunePlanById(Integer id) {
        TunePlan tunePlan = converter.deserialize(mDSLContext.select().from(Tables.TUNE_PLAN)
            .where(Tables.TUNE_PLAN.ID.eq(id)).limit(1).fetchOneInto(TunePlanRecord.class));
        if (tunePlan.getTunePlanStatus().isFinal()) {
            return null;
        }
        return tunePlan;
    }

    @Override
    public TunePlan findTunePlanById(Integer id) {
        return converter.deserialize(this.selectById(id));
    }

    @Override
    public List<TunePlan> findTunePlanByAppId(Integer appId) {
        List<TunePlanRecord> records = mDSLContext.select().from(Tables.TUNE_PLAN).where(Tables.TUNE_PLAN.APP_ID.eq(appId)).fetchInto(
                TunePlanRecord.class);
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public List<TunePlan> findByAppIdAndStatus(Integer appId, TunePlanStatus status, Long startTime, Long endTime) {
        Condition condition = Tables.TUNE_PLAN.APP_ID.eq(appId);
        if (status != null) {
            condition = condition.and(Tables.TUNE_PLAN.PLAN_STATUS.eq(status.name()));
        }
        if (startTime != null && startTime > 0) {
            condition = condition.and(Tables.TUNE_PLAN.CREATED_TIME.ge(DateUtils.asLocalData(startTime)));
        }
        if (endTime != null && endTime > 0) {
            condition = condition.and(Tables.TUNE_PLAN.CREATED_TIME.le(DateUtils.asLocalData(endTime)));
        }
        List<TunePlanRecord> records = mDSLContext.select().from(Tables.TUNE_PLAN).where(condition).fetchInto(TunePlanRecord.class);
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public TunePlan findLastTunePlanByAppId(Integer appId) {
        TunePlanRecord record = mDSLContext.select().from(Tables.TUNE_PLAN)
            .where(Tables.TUNE_PLAN.APP_ID.eq(appId)).orderBy(Tables.TUNE_PLAN.CREATED_TIME.desc())
            .limit(1).fetchOneInto(TunePlanRecord.class);
        return converter.deserialize(record);
    }

    @Override
    public List<TunePlan> batchFindLastTunePlanByAppId(List<Integer> appIds) {
        Condition condition = Tables.TUNE_PLAN.APP_ID.in(appIds)
                .and(Tables.TUNE_PLAN.PLAN_STATUS.eq(TunePlanStatus.END.name()));
        List<TunePlanRecord> records1 = mDSLContext.select()
                .from(Tables.TUNE_PLAN)
                .where(Tables.TUNE_PLAN.ID.in(
                        DSL.select(DSL.max(Tables.TUNE_PLAN.ID))
                                .from(Tables.TUNE_PLAN)
                                .where(condition)
                                .groupBy(Tables.TUNE_PLAN.APP_ID)
                ))
                .fetchInto(TunePlanRecord.class);
        return records1.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public List<TunePlan> batchFindTunePlanByPipelineId(List<Integer> pipelineIds) {
        List<TunePlanRecord> records = mDSLContext.select()
                .from(Tables.TUNE_PLAN)
                .where(Tables.TUNE_PLAN.ID.in(pipelineIds))
                .fetchInto(TunePlanRecord.class);
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public List<TunePlan> findByAppIdAndTime(Integer appId, Long start, Long end) {
        LocalDateTime startTime = start == null ? DateUtils.now().plusMonths(-1) : DateUtils.asLocalData(start);
        LocalDateTime endTime = end == null ? DateUtils.now() : DateUtils.asLocalData(end);
        List<TunePlanRecord> records = mDSLContext.select()
                .from(Tables.TUNE_PLAN)
                .where(Tables.TUNE_PLAN.CREATED_TIME.cast(LocalDateTime.class).between(startTime, endTime))
                .and(Tables.TUNE_PLAN.APP_ID.eq(appId))
                .and(Tables.TUNE_PLAN.PLAN_STATUS.eq(TunePlanStatus.END.name()))
                .orderBy(Tables.TUNE_PLAN.CREATED_TIME.desc())
                .fetchInto(TunePlanRecord.class);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public TunePlan findByAppIdDescTime(Integer appId, Long start, Long end) {
        LocalDateTime startTime = start == null ? DateUtils.now().plusMonths(-1) : DateUtils
            .asLocalData(start);
        LocalDateTime endTime = end == null ? DateUtils.now() : DateUtils.asLocalData(end);
        TunePlanRecord record = mDSLContext
            .select()
            .from(Tables.TUNE_PLAN)
            .where(
                Tables.TUNE_PLAN.CREATED_TIME.cast(LocalDateTime.class).between(startTime, endTime))
            .and(Tables.TUNE_PLAN.APP_ID.eq(appId))
            .and(Tables.TUNE_PLAN.PLAN_STATUS.eq(TunePlanStatus.END.name()))
            .orderBy(Tables.TUNE_PLAN.CREATED_TIME.desc()).limit(1)
            .fetchOneInto(TunePlanRecord.class);
        return converter.deserialize(record);
    }

    @Override
    public List<TunePlan> findByAppIdLimit(Integer appId, Long end) {
        LocalDateTime startTime = DateUtils.asLocalData(1234L);
        LocalDateTime endTime = end == null ? DateUtils.now() : DateUtils.asLocalData(end);
        List<TunePlanRecord> records = mDSLContext.select().from(Tables.TUNE_PLAN)
                .where(Tables.TUNE_PLAN.CREATED_TIME.cast(LocalDateTime.class).between(startTime, endTime))
                .and(Tables.TUNE_PLAN.APP_ID.eq(appId))
                .limit(30)
                .fetch()
                .sortDesc(Tables.TUNE_PLAN.CREATED_TIME).into(TunePlanRecord.class);
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public int updateEffectById(Integer id, String tuneEffect, Double income) {
        Preconditions.checkNotNull(id);
        UpdateQuery<TunePlanRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_PLAN);
        if (StringUtils.isNotEmpty(tuneEffect)) {
            updateQuery.addValue(Tables.TUNE_PLAN.TUNE_EFFECT, tuneEffect);
        }
        updateQuery.addValue(Tables.TUNE_PLAN.UPDATE_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.TUNE_PLAN.ID.eq(id));
        return updateQuery.execute();
    }

    @Override
    public int updateGrayPredictById(Integer id, String tuneEffect, Double income) {
        Preconditions.checkNotNull(id);
        UpdateQuery<TunePlanRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_PLAN);
        if (StringUtils.isNotEmpty(tuneEffect)) {
            updateQuery.addValue(Tables.TUNE_PLAN.PREDICT_EFFECT, tuneEffect);
        }
        updateQuery.addValue(Tables.TUNE_PLAN.UPDATE_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.TUNE_PLAN.ID.eq(id));
        return updateQuery.execute();
    }

    @Override
    public int updatePredictEffect(Integer id, String predictEffect) {
        Preconditions.checkNotNull(id);
        UpdateQuery<TunePlanRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_PLAN);
        if (StringUtils.isNotEmpty(predictEffect)) {
            updateQuery.addValue(Tables.TUNE_PLAN.PREDICT_EFFECT, predictEffect);
        }
        updateQuery.addValue(Tables.TUNE_PLAN.UPDATE_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.TUNE_PLAN.ID.eq(id));
        return updateQuery.execute();
    }

    @Override
    public void updateTuneStatusById(Integer id, TunePlanStatus tuneStatus) {
        if (id == null) {
            return;
        }
        UpdateQuery<TunePlanRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_PLAN);
        updateQuery.addValue(Tables.TUNE_PLAN.UPDATE_TIME, DateUtils.now());
        updateQuery.addValue(Tables.TUNE_PLAN.PLAN_STATUS, tuneStatus.name());
        updateQuery.addConditions(Tables.TUNE_PLAN.ID.eq(id));
        updateQuery.execute();
    }

    @Override
    public void updateStatusById(Integer id, TunePlanStatus tuneStatus) {
        if (id == null) {
            return;
        }
        UpdateQuery<TunePlanRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_PLAN);
        updateQuery.addValue(Tables.TUNE_PLAN.UPDATE_TIME, DateUtils.now());
        updateQuery.addValue(Tables.TUNE_PLAN.TUNE_STATUS, tuneStatus.name());
        updateQuery.addConditions(Tables.TUNE_PLAN.ID.eq(id));
        updateQuery.execute();
    }

    @Override
    public List<TunePlan> findByStatus() {
        LocalDateTime endTime = DateUtils.now();
        LocalDateTime startTime = DateUtils.now().plusDays(-7);
        List<TunePlanRecord> records = mDSLContext.select().from(Tables.TUNE_PLAN)
                .where(Tables.TUNE_PLAN.CREATED_TIME.cast(LocalDateTime.class).between(startTime, endTime))
                //.and(Tables.TUNE_PLAN.PLAN_STATUS.eq("END"))
                .fetch()
                .sortDesc(Tables.TUNE_PLAN.CREATED_TIME).into(TunePlanRecord.class);
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public TunePlan save(TunePlan tunePlan) {
        TunePlanRecord record = converter.serialize(tunePlan);
        record.setUpdateTime(DateUtils.now());
        // insert
        if (record.getId() == null) {
            record.setCreatedTime(DateUtils.now());
            Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record);
            return converter.deserialize(mDSLContext.insertInto(Tables.TUNE_PLAN).set(map)
                .returning().fetchOne());
        }
        // update
        Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record,
            Tables.TUNE_PLAN.ID);
        mDSLContext.update(Tables.TUNE_PLAN).set(map).where(Tables.TUNE_PLAN.ID.eq(record.getId()))
            .returning().execute();
        return converter.deserialize(this.selectById(record.getId()));
    }

    private TunePlanRecord selectById(Integer id) {
        return mDSLContext.select().from(Tables.TUNE_PLAN).where(Tables.TUNE_PLAN.ID.eq(id))
            .limit(1).fetchOneInto(TunePlanRecord.class);
    }

}