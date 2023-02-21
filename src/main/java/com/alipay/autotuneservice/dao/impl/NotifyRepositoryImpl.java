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
import com.alipay.autotuneservice.dao.NotifyRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.NotifyRecord;
import com.alipay.autotuneservice.service.notifyGroup.model.NotifyStatus;
import com.alipay.autotuneservice.util.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author huoyuqi
 * @version NotifyRepositoryImpl.java, v 0.1 2022年12月28日 1:59 下午 huoyuqi
 */
@Repository
public class  NotifyRepositoryImpl extends BaseDao implements NotifyRepository {

    @Override
    public void insertNotify(String groupName, NotifyStatus status, String context) {
        mDSLContext.insertInto(Tables.NOTIFY)
                .set(Tables.NOTIFY.GROUP_NAME, groupName)
                .set(Tables.NOTIFY.STATUS, status.name())
                .set(Tables.NOTIFY.CONTEXT, context)
                .set(Tables.NOTIFY.ACCESS_TOKEN, UserUtil.getAccessToken())
                .set(Tables.NOTIFY.CREATE_BY, UserUtil.getUserName())
                .set(Tables.NOTIFY.CREATED_TIME, LocalDateTime.now())
                .returning()
                .fetch();
    }

    @Override
    public void updateNotify(Integer id, String groupName, NotifyStatus status, String context){
        UpdateQuery<NotifyRecord> updateQuery = mDSLContext.updateQuery(Tables.NOTIFY);
        if(StringUtils.isNotEmpty(groupName)){
            updateQuery.addValue(Tables.NOTIFY.GROUP_NAME, groupName);
        }
        if(!Objects.isNull(status)){
            updateQuery.addValue(Tables.NOTIFY.STATUS, status.name());
        }
        if(StringUtils.isNotEmpty(context)){
            updateQuery.addValue(Tables.NOTIFY.CONTEXT, context);
        }
        updateQuery.addValue(Tables.NOTIFY.UPDATED_TIME, LocalDateTime.now());
        updateQuery.addConditions(Tables.NOTIFY.ID.eq(id));
        updateQuery.execute();
    }

    @Override
    public NotifyRecord getById(Integer id){
        return mDSLContext.select()
                .from(Tables.NOTIFY)
                .where(Tables.NOTIFY.ID.eq(id))
                .fetchOneInto(NotifyRecord.class);
    }

    @Override
    public List<NotifyRecord> getByIds(List<Integer> ids) {
        return mDSLContext.select()
                .from(Tables.NOTIFY)
                .where(Tables.NOTIFY.ID.in(ids))
                .fetchInto(NotifyRecord.class);
    }

    @Override
    public List<NotifyRecord> getByAccessToken(String  token){
        return mDSLContext.select()
                .from(Tables.NOTIFY)
                .where(Tables.NOTIFY.ACCESS_TOKEN.eq(token))
                .fetchInto(NotifyRecord.class);
    }

    @Override
    public Boolean deleteById(Integer id) {
        Condition condition = Tables.NOTIFY.ID.eq(id)
                .and(Tables.NOTIFY.ACCESS_TOKEN.eq(UserUtil.getAccessToken()));
        try {
            mDSLContext.deleteFrom(Tables.NOTIFY).where(condition).execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
