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
package com.alipay.autotuneservice.dao.converter;

import com.alipay.autotuneservice.dao.jooq.tables.records.UserInfoRecord;
import com.alipay.autotuneservice.model.common.UserInfo;

/**
 * @author dutianze
 * @version UserInfoConverter.java, v 0.1 2022年04月14日 10:50 dutianze
 */
public class UserInfoConverter implements EntityConverter<UserInfo, UserInfoRecord> {

    @Override
    public UserInfoRecord serialize(UserInfo entity) {
        if (entity == null) {
            return null;
        }
        UserInfoRecord record = new UserInfoRecord();
        record.setId(entity.getId());
        record.setAccountId(entity.getAccountId());
        record.setAccessToken(entity.getAccessToken());
        record.setUserCompany(entity.getUserCompany());
        record.setUserName(entity.getUserName());
        record.setCreatedTime(entity.getCreatedTime());
        record.setUpdatedTime(entity.getUpdatedTime());
        record.setTenantCode(entity.getTenantCode());
        record.setProductAccountId(entity.getProductAccountId());
        record.setPlanCode(entity.getPlanCode());
        return record;
    }

    @Override
    public UserInfo deserialize(UserInfoRecord record) {
        if (record == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo(record.getAccountId(), record.getTenantCode(),
            record.getProductAccountId(), record.getPlanCode());
        userInfo.setId(record.getId());
        userInfo.setAccessToken(record.getAccessToken());
        userInfo.setUserCompany(record.getUserCompany());
        userInfo.setUserName(record.getUserName());
        userInfo.setCreatedTime(record.getCreatedTime());
        userInfo.setUpdatedTime(record.getUpdatedTime());
        return userInfo;
    }

}