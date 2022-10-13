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
import com.alipay.autotuneservice.dao.HealthCheckInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckInfoRecord;
import com.alipay.autotuneservice.model.common.HealthCheckStatus;
import com.alipay.autotuneservice.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huoyuqi
 * @version HealthCheckInfoImpl.java, v 0.1 2022年04月25日 7:19 下午 huoyuqi
 */
@Service
public class HealthCheckInfoImpl extends BaseDao implements HealthCheckInfo {
    @Override
    public HealthCheckInfoRecord selectByAccessTokenAndAppId(String accessToken, Integer appId) {
        return mDSLContext.select().from(Tables.HEALTH_CHECK_INFO)
            .where(Tables.HEALTH_CHECK_INFO.ACCESS_TOKEN.eq(accessToken))
            .and(Tables.HEALTH_CHECK_INFO.APP_ID.eq(appId))
            .orderBy(Tables.HEALTH_CHECK_INFO.CREATED_TIME.desc()).limit(1)
            .fetchOneInto(HealthCheckInfoRecord.class);
    }

    @Override
    public Integer insert(Integer appId, String accessToken, String createBy, String status,
                          String problemPoint, Integer grade, String enChangePoint, String result) {
        return mDSLContext.insertInto(Tables.HEALTH_CHECK_INFO)
            .set(Tables.HEALTH_CHECK_INFO.APP_ID, appId)
            .set(Tables.HEALTH_CHECK_INFO.ACCESS_TOKEN, accessToken)
            .set(Tables.HEALTH_CHECK_INFO.CREATED_BY, createBy)
            .set(Tables.HEALTH_CHECK_INFO.STATUS, status)
            .set(Tables.HEALTH_CHECK_INFO.PROBLEAM_POINT, problemPoint)
            .set(Tables.HEALTH_CHECK_INFO.GRADE, grade.toString())
            .set(Tables.HEALTH_CHECK_INFO.PROBLEAM_POINT, problemPoint)
            .set(Tables.HEALTH_CHECK_INFO.ENCHANGE_POINT, enChangePoint)
            .set(Tables.HEALTH_CHECK_INFO.CREATED_TIME, DateUtils.now())
            .set(Tables.HEALTH_CHECK_INFO.ALGO_PROBLEAM, result).returning().fetchOne().getId();
    }

    @Override
    public List<HealthCheckInfoRecord> findByAppId(Integer appId) {
        return mDSLContext.select().from(Tables.HEALTH_CHECK_INFO)
            .where(Tables.HEALTH_CHECK_INFO.APP_ID.eq(appId))
            .orderBy(Tables.HEALTH_CHECK_INFO.CREATED_TIME.desc()).fetch()
            .into(HealthCheckInfoRecord.class);
    }

    @Override
    public void update(Integer id, String grade, String status, String problemPoint) {
        UpdateQuery<HealthCheckInfoRecord> updateQuery = mDSLContext
            .updateQuery(Tables.HEALTH_CHECK_INFO);
        if (StringUtils.isNotEmpty(grade)) {
            updateQuery.addValue(Tables.HEALTH_CHECK_INFO.GRADE, grade);
        }
        if (StringUtils.isNotEmpty(problemPoint)) {
            updateQuery.addValue(Tables.HEALTH_CHECK_INFO.PROBLEAM_POINT, problemPoint);
        }
        updateQuery.addValue(Tables.HEALTH_CHECK_INFO.STATUS, status);
        updateQuery.addConditions(Tables.HEALTH_CHECK_INFO.ID.eq(id));
        updateQuery.execute();
    }

    @Override
    public HealthCheckInfoRecord selectById(Integer healthCheckId) {
        return mDSLContext.select().from(Tables.HEALTH_CHECK_INFO)
            .where(Tables.HEALTH_CHECK_INFO.ID.eq(healthCheckId))
            .fetchOneInto(HealthCheckInfoRecord.class);
    }

    @Override
    public List<HealthCheckInfoRecord> batchGetHealthIdsByHealthIds(List<Integer> healthIds) {
        return mDSLContext.select().from(Tables.HEALTH_CHECK_INFO)
            .where(Tables.HEALTH_CHECK_INFO.ID.in(healthIds))
            .orderBy(Tables.HEALTH_CHECK_INFO.CREATED_TIME.desc())
            .fetchInto(HealthCheckInfoRecord.class);
    }

    @Override
    public HealthCheckInfoRecord selectRecentRunning() {
        return mDSLContext.select().from(Tables.HEALTH_CHECK_INFO)
            .where(Tables.HEALTH_CHECK_INFO.STATUS.eq(HealthCheckStatus.RUNNING.name()))
            .orderBy(Tables.HEALTH_CHECK_INFO.CREATED_TIME.desc())
            .fetchOneInto(HealthCheckInfoRecord.class);
    }
}