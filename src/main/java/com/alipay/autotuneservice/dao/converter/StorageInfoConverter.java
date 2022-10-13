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

import com.alipay.autotuneservice.dao.jooq.tables.records.StorageInfoRecord;
import com.alipay.autotuneservice.model.common.StorageInfo;

/**
 * @author dutianze
 * @version StorageModelConverter.java, v 0.1 2022年04月19日 14:04 dutianze
 */
public class StorageInfoConverter implements EntityConverter<StorageInfo, StorageInfoRecord> {

    @Override
    public StorageInfoRecord serialize(StorageInfo entity) {
        if (entity == null) {
            return null;
        }
        StorageInfoRecord record = new StorageInfoRecord();
        record.setId(entity.getId());
        record.setS3Key(entity.getS3Key());
        record.setFileName(entity.getFileName());
        record.setCreatedTime(entity.getCreatedTime());
        record.setUpdatedTime(entity.getUpdatedTime());
        return record;
    }

    @Override
    public StorageInfo deserialize(StorageInfoRecord record) {
        if (record == null) {
            return null;
        }
        StorageInfo info = new StorageInfo();
        info.setId(record.getId());
        info.setS3Key(record.getS3Key());
        info.setFileName(record.getFileName());
        info.setCreatedTime(record.getCreatedTime());
        info.setUpdatedTime(record.getUpdatedTime());
        return info;
    }
}