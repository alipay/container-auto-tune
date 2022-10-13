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

import com.alipay.autotuneservice.dao.AppLogRepository;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.converter.AppLogConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppLogRecord;
import com.alipay.autotuneservice.model.common.AppLog;
import com.alipay.autotuneservice.model.common.AppLogType;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.EnhanceBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Field;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author dutianze
 * @version AppLogRepositoryImpl.java, v 0.1 2022年05月07日 14:22 dutianze
 */
@Slf4j
@Service
public class AppLogRepositoryImpl extends BaseDao implements AppLogRepository {

    private final AppLogConverter converter = new AppLogConverter();

    @Override
    public AppLog save(AppLog appLog) {
        AppLogRecord record = converter.serialize(appLog);
        record.setUpdatedTime(DateUtils.now());
        // insert
        if (record.getId() == null) {
            record.setCreatedTime(DateUtils.now());
            Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record);
            return converter.deserialize(mDSLContext.insertInto(Tables.APP_LOG).set(map)
                .returning().fetchOne());
        }
        // update
        Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record,
            Tables.APP_LOG.ID);
        mDSLContext.update(Tables.APP_LOG).set(map).where(Tables.APP_LOG.ID.eq(record.getId()))
            .returning().execute();
        return converter.deserialize(this.selectById(record.getId()));
    }

    @Override
    public AppLog findLastAppLog(Integer appId, AppLogType appLogType) {
        Condition condition = Tables.APP_LOG.APP_ID.eq(appId);
        condition.and(Tables.APP_LOG.LOG_TYPE.eq(appLogType.name()));
        return converter.deserialize(mDSLContext.select().from(Tables.APP_LOG).where(condition)
            .orderBy(Tables.APP_LOG.CREATED_TIME.desc()).limit(1).fetchOneInto(AppLogRecord.class));
    }

    private AppLogRecord selectById(Long id) {
        return mDSLContext.select().from(Tables.APP_LOG).where(Tables.APP_LOG.ID.eq(id)).limit(1)
            .fetchOneInto(AppLogRecord.class);
    }
}