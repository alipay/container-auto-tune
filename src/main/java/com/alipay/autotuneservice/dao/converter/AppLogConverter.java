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

import com.alipay.autotuneservice.dao.jooq.tables.records.AppLogRecord;
import com.alipay.autotuneservice.model.common.AppLog;
import com.alipay.autotuneservice.model.common.AppLogType;

/**
 * @author dutianze
 * @version AppLogConverter.java, v 0.1 2022年05月07日 14:31 dutianze
 */
public class AppLogConverter implements EntityConverter<AppLog, AppLogRecord> {

    @Override
    public AppLogRecord serialize(AppLog entity) {
        if (entity == null) {
            return null;
        }
        AppLogRecord appLogRecord = new AppLogRecord();
        appLogRecord.setId(entity.getId());
        appLogRecord.setAppId(entity.getAppId());
        appLogRecord.setLogType(entity.getAppLogType().name());
        appLogRecord.setS3Key(entity.getS3Key());
        appLogRecord.setHostName(entity.getHostName());
        appLogRecord.setCreatedTime(entity.getCreatedTime());
        appLogRecord.setUpdatedTime(entity.getUpdatedTime());
        appLogRecord.setFileName(entity.getFileName());
        return appLogRecord;
    }

    @Override
    public AppLog deserialize(AppLogRecord record) {
        if (record == null) {
            return null;
        }
        return AppLog.builder().withId(record.getId()).withAppId(record.getAppId())
            .withAppLogType(AppLogType.valueOf(record.getLogType())).withS3Key(record.getS3Key())
            .withHostName(record.getHostName()).withCreatedTime(record.getCreatedTime())
            .withUpdatedTime(record.getUpdatedTime()).withFileName(record.getFileName()).build();
    }
}