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

import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.util.GsonUtil;

/**
 * @author dutianze
 * @version AppInfoConverter.java, v 0.1 2022年05月16日 17:21 dutianze
 */
public class AppInfoConverter implements EntityConverter<AppInfo, AppInfoRecord> {

    @Override
    public AppInfoRecord serialize(AppInfo entity) {
        if (entity == null) {
            return null;
        }
        AppInfoRecord appInfoRecord = new AppInfoRecord();
        appInfoRecord.setId(entity.getId());
        appInfoRecord.setUserId(entity.getUserId());
        appInfoRecord.setAccessToken(entity.getAccessToken());
        appInfoRecord.setNodeIds(GsonUtil.toJson(entity.getNodeIds()));
        appInfoRecord.setAppName(entity.getAppName());
        appInfoRecord.setAppAsName(entity.getAppAsName());
        appInfoRecord.setAppDesc(entity.getAppDesc());
        appInfoRecord.setCreatedTime(entity.getCreatedTime());
        appInfoRecord.setUpdatedTime(entity.getUpdatedTime());
        appInfoRecord.setStatus(entity.getStatus().name());
        appInfoRecord.setAppDefaultJvm(entity.getAppDefaultJvm());
        appInfoRecord.setAppTag(GsonUtil.toJson(entity.getAppTag()));
        appInfoRecord.setNamespace(entity.getNamespace());
        return appInfoRecord;
    }

    @Override
    public AppInfo deserialize(AppInfoRecord record) {
        if (record == null) {
            return null;
        }
        AppInfo appInfo = new AppInfo();
        appInfo.setId(record.getId());
        appInfo.setUserId(record.getUserId());
        appInfo.setAccessToken(record.getAccessToken());
        appInfo.setNodeIds(GsonUtil.fromJsonList(record.getNodeIds(), Integer.class));
        appInfo.setAppName(record.getAppName());
        appInfo.setAppAsName(record.getAppAsName());
        appInfo.setAppDesc(record.getAppDesc());
        appInfo.setCreatedTime(record.getCreatedTime());
        appInfo.setUpdatedTime(record.getUpdatedTime());
        appInfo.setStatus(AppStatus.valueOf(record.getStatus()));
        appInfo.setAppDefaultJvm(record.getAppDefaultJvm());
        appInfo.setAppTag(GsonUtil.fromJson(record.getAppTag(), AppTag.class));
        appInfo.setNamespace(record.getNamespace());
        return appInfo;
    }
}