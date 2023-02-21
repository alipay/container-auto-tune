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

import com.alipay.autotuneservice.dao.AlarmRepository;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.AlarmRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.NotifyRecord;
import com.alipay.autotuneservice.util.UserUtil;
import org.jooq.Condition;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author huoyuqi
 * @version AlarmRepositoryImpl.java, v 0.1 2022年12月29日 10:38 上午 huoyuqi
 */
@Repository
public class AlarmRepositoryImpl extends BaseDao implements AlarmRepository {

    @Override
    public void insertAlarm(AlarmRecord record) {

        mDSLContext.insertInto(Tables.ALARM)
                .set(Tables.ALARM.ALARM_NAME, record.getAlarmName())
                .set(Tables.ALARM.APP_NAME, record.getAppName())
                .set(Tables.ALARM.APP_ID, record.getAppId())
                .set(Tables.ALARM.STATUS, record.getStatus())
                .set(Tables.ALARM.ALARM_RULE, record.getAlarmRule())
                .set(Tables.ALARM.RULE_ACTION, record.getRuleAction())
                .set(Tables.ALARM.ALARM_NOTICE, record.getAlarmNotice())
                .set(Tables.ALARM.CONTEXT, record.getContext())
                .set(Tables.ALARM.CREATE_BY, UserUtil.getUserName())
                .set(Tables.ALARM.CREATED_TIME, LocalDateTime.now())
                .set(Tables.ALARM.COMBINATION_TYPE, record.getCombinationType())
                .returning()
                .fetch();
    }

    @Override
    public void updateAlarm(AlarmRecord record) {
        UpdateQuery<AlarmRecord> updateQuery = mDSLContext.updateQuery(Tables.ALARM);
        updateQuery.addValue(Tables.ALARM.ALARM_NAME, record.getAlarmName());
        updateQuery.addValue(Tables.ALARM.STATUS, record.getStatus());
        updateQuery.addValue(Tables.ALARM.COMBINATION_TYPE, record.getCombinationType());
        updateQuery.addValue(Tables.ALARM.ALARM_RULE, record.getAlarmRule());
        updateQuery.addValue(Tables.ALARM.RULE_ACTION, record.getRuleAction());
        updateQuery.addValue(Tables.ALARM.ALARM_NOTICE, record.getAlarmNotice());
        updateQuery.addValue(Tables.ALARM.CONTEXT, record.getContext());
        updateQuery.addValue(Tables.ALARM.UPDATED_TIME, LocalDateTime.now());
        updateQuery.addConditions(Tables.ALARM.ID.eq(record.getId()));
        updateQuery.execute();
    }

    @Override
    public List<AlarmRecord> getByAppId(Integer id) {
        return mDSLContext.select()
                .from(Tables.ALARM)
                .where(Tables.ALARM.APP_ID.eq(id))
                .fetchInto(AlarmRecord.class);
    }

    @Override
    public Boolean deleteByAlarmId(Integer alarmId) {
        Condition condition = Tables.ALARM.ID.eq(alarmId)
                .and(Tables.ALARM.ACCESS_TOKEN.eq(UserUtil.getAccessToken()));
        try {
            mDSLContext.deleteFrom(Tables.ALARM).where(condition).execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public AlarmRecord getByAlarmId(Integer id) {
        return mDSLContext.select()
                .from(Tables.ALARM)
                .where(Tables.ALARM.ID.eq(id))
                .fetchOneInto(AlarmRecord.class);
    }

    public List<NotifyRecord> getByIds(List<Integer> ids) {
        return mDSLContext.select()
                .from(Tables.NOTIFY)
                .where(Tables.NOTIFY.ID.in(ids))
                .fetchInto(NotifyRecord.class);
    }

}