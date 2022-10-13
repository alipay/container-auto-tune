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
import com.alipay.autotuneservice.dao.UserInfoRepository;
import com.alipay.autotuneservice.dao.converter.UserInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.UserInfoRecord;
import com.alipay.autotuneservice.model.common.UserInfo;
import com.alipay.autotuneservice.util.DateUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version UserInfoRepositoryImpl.java, v 0.1 2022年03月07日 20:41 dutianze
 */
@Service
public class UserInfoRepositoryImpl extends BaseDao implements UserInfoRepository {

    private static final UserInfoConverter converter = new UserInfoConverter();

    public UserInfo findByAccessToken(String accessToken) {
        UserInfoRecord record = mDSLContext.select().from(Tables.USER_INFO)
            .where(Tables.USER_INFO.ACCESS_TOKEN.eq(accessToken)).limit(1)
            .fetchOneInto(UserInfoRecord.class);
        return converter.deserialize(record);
    }

    @Override
    public UserInfo findByAccountIdAndTenantCode(String accountId, String tenantCode) {
        UserInfoRecord record = mDSLContext
            .select()
            .from(Tables.USER_INFO)
            .where(
                Tables.USER_INFO.ACCOUNT_ID.eq(accountId).and(
                    Tables.USER_INFO.TENANT_CODE.eq(tenantCode))).limit(1)
            .fetchOneInto(UserInfoRecord.class);
        return converter.deserialize(record);
    }

    @Override
    public UserInfo findFirstByTenantCode(String tenantCode) {
        UserInfoRecord record = mDSLContext.select().from(Tables.USER_INFO)
            .where(Tables.USER_INFO.TENANT_CODE.eq(tenantCode)).limit(1)
            .fetchOneInto(UserInfoRecord.class);
        return converter.deserialize(record);
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        UserInfoRecord record = mDSLContext.newRecord(Tables.USER_INFO,
            converter.serialize(userInfo));
        record.setUpdatedTime(DateUtils.now());

        if (record.getId() == null) {
            record.setCreatedTime(DateUtils.now());
            record.insert();
        } else {
            record.update();
        }
        return converter.deserialize(record);
    }

    @Override
    public List<UserInfo> findAll() {
        List<UserInfoRecord> userInfoRecords = mDSLContext.select()
                .from(Tables.USER_INFO)
                .fetchInto(UserInfoRecord.class);
        return userInfoRecords.stream().map(converter::deserialize).collect(Collectors.toList());
    }
}