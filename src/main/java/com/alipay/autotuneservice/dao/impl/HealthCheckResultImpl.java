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
import com.alipay.autotuneservice.dao.HealthCheckResultRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckResultRecord;
import com.alipay.autotuneservice.model.common.HealthCheckStatus;
import org.jooq.Record;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author huoyuqi
 * @version HealthCheckInfoImpl.java, v 0.1 2022年04月25日 7:19 下午 huoyuqi
 */
@Service
public class HealthCheckResultImpl extends BaseDao implements HealthCheckResultRepository {
    @Override
    public Integer insert(Integer appId, String accessToken, String createBy, LocalDateTime createTime, LocalDateTime updateTime,
                          String createMode, String status, String problem, String report, String reportDetail) {
        return Objects.requireNonNull(mDSLContext.insertInto(Tables.HEALTH_CHECK_RESULT)
                .set(Tables.HEALTH_CHECK_RESULT.APP_ID, appId)
                .set(Tables.HEALTH_CHECK_RESULT.ACCESS_TOKEN, accessToken)
                .set(Tables.HEALTH_CHECK_RESULT.CREATED_BY, createBy)
                .set(Tables.HEALTH_CHECK_RESULT.CREATED_TIME, createTime)
                .set(Tables.HEALTH_CHECK_RESULT.CREATE_MODE, createMode)
                .set(Tables.HEALTH_CHECK_RESULT.STATUS, status)
                .set(Tables.HEALTH_CHECK_RESULT.PROBLEAM, problem)
                .set(Tables.HEALTH_CHECK_RESULT.REPORT, report)
                .set(Tables.HEALTH_CHECK_RESULT.REPORT_DETAIL, reportDetail)
                .returning()
                .fetchOne()).getId();
    }

    @Override
    public HealthCheckResultRecord selectById(Integer healthCheckId) {
        return mDSLContext.select()
                .from(Tables.HEALTH_CHECK_RESULT)
                .where(Tables.HEALTH_CHECK_RESULT.ID.eq(healthCheckId))
                .fetchOneInto(HealthCheckResultRecord.class);
    }

    @Override
    public List<HealthCheckResultRecord> selectByIds(List<Integer> healthIds) {
        return mDSLContext.select()
                .from(Tables.HEALTH_CHECK_RESULT)
                .where(Tables.HEALTH_CHECK_RESULT.ID.in(healthIds))
                .orderBy(Tables.HEALTH_CHECK_RESULT.CREATED_TIME.desc())
                .fetchInto(HealthCheckResultRecord.class);
    }

    @Override
    public List<HealthCheckResultRecord> findByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.HEALTH_CHECK_RESULT)
                .where(Tables.HEALTH_CHECK_RESULT.APP_ID.eq(appId))
                .orderBy(Tables.HEALTH_CHECK_RESULT.CREATED_TIME.desc())
                .fetch()
                .into(HealthCheckResultRecord.class);
    }

    @Override
    public HealthCheckResultRecord findFirst() {
        Record record = mDSLContext.select()
                .from(Tables.HEALTH_CHECK_RESULT)
                .where(Tables.HEALTH_CHECK_RESULT.STATUS.eq(HealthCheckStatus.ENDING.name()))
                .orderBy(Tables.HEALTH_CHECK_RESULT.CREATED_TIME.desc())
                .limit(1)
                .fetchOne();
        return record==null ? null : record.into(HealthCheckResultRecord.class);
    }

    @Override
    public HealthCheckResultRecord findFirstByAppId(Integer appId) {
        Record record = mDSLContext.select()
                .from(Tables.HEALTH_CHECK_RESULT)
                .where(Tables.HEALTH_CHECK_RESULT.APP_ID.eq(appId))
                .and(Tables.HEALTH_CHECK_RESULT.STATUS.eq(HealthCheckStatus.ENDING.name()))
                .orderBy(Tables.HEALTH_CHECK_RESULT.CREATED_TIME.desc())
                .limit(1)
                .fetchOne();
        return record==null ? null : record.into(HealthCheckResultRecord.class);

    }

    @Override
    public HealthCheckResultRecord findFirstByAppIdAndStatus(Integer appId, String status) {
        Record record = mDSLContext.select()
                .from(Tables.HEALTH_CHECK_RESULT)
                .where(Tables.HEALTH_CHECK_RESULT.APP_ID.eq(appId))
                .and(Tables.HEALTH_CHECK_RESULT.STATUS.eq(status))
                .orderBy(Tables.HEALTH_CHECK_RESULT.CREATED_TIME.desc())
                .fetchOne();
        return record==null ? null : record.into(HealthCheckResultRecord.class);
    }

    @Override
    public List<HealthCheckResultRecord> findByAppIdAndStatus(Integer appId, String status) {
        return mDSLContext.select()
                .from(Tables.HEALTH_CHECK_RESULT)
                .where(Tables.HEALTH_CHECK_RESULT.APP_ID.eq(appId))
                .and(Tables.HEALTH_CHECK_RESULT.STATUS.eq(status))
                .orderBy(Tables.HEALTH_CHECK_RESULT.CREATED_TIME.desc())
                .fetch()
                .into(HealthCheckResultRecord.class);
    }

    @Override
    public void update(Integer id, LocalDateTime updateTime, String status, String problem, String report, String reportDetail) {
        UpdateQuery<HealthCheckResultRecord> updateQuery = mDSLContext.updateQuery(Tables.HEALTH_CHECK_RESULT);
        updateQuery.addValue(Tables.HEALTH_CHECK_RESULT.UPDATE_TIME, updateTime);
        updateQuery.addValue(Tables.HEALTH_CHECK_RESULT.STATUS,status);
        updateQuery.addValue(Tables.HEALTH_CHECK_RESULT.PROBLEAM, problem);
        updateQuery.addValue(Tables.HEALTH_CHECK_RESULT.REPORT, report);
        updateQuery.addValue(Tables.HEALTH_CHECK_RESULT.REPORT_DETAIL, reportDetail);
        updateQuery.addConditions(Tables.HEALTH_CHECK_RESULT.ID.eq(id));
        updateQuery.execute();

    }


}