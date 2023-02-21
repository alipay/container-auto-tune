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

import com.alipay.autotuneservice.controller.model.diagnosis.FileType;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.StorageRepository;
import com.alipay.autotuneservice.dao.converter.StorageInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.StorageInfoRecord;
import com.alipay.autotuneservice.model.common.StorageInfo;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.EnhanceBeanUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version StorageRepositoryImpl.java, v 0.1 2022年04月18日 16:57 dutianze
 */
@Service
public class StorageRepositoryImpl extends BaseDao implements StorageRepository {

    private static final StorageInfoConverter converter = new StorageInfoConverter();

    @Override
    public StorageInfo findByFileName(String fileName) {
        Condition condition = Tables.STORAGE_INFO.FILE_NAME.eq(fileName);
        return converter.deserialize(mDSLContext.select()
                .from(Tables.STORAGE_INFO)
                .where(condition)
                .orderBy(Tables.STORAGE_INFO.UPDATED_TIME.desc())
                .limit(1)
                .fetchOneInto(StorageInfoRecord.class));
    }

    @Override
    public StorageInfo save(StorageInfo storageInfo) {
        StorageInfoRecord record = converter.serialize(storageInfo);
        // insert
        if (record.getId() == null) {
            record.setCreatedTime(DateUtils.now());
            Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record);
            return converter.deserialize(mDSLContext.insertInto(Tables.STORAGE_INFO)
                    .set(map)
                    .returning()
                    .fetchOne());
        }
        // update
        Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record, Tables.STORAGE_INFO.ID);
        mDSLContext.update(Tables.STORAGE_INFO)
                .set(map)
                .where(Tables.STORAGE_INFO.ID.eq(record.getId())).returning()
                .execute();
        return converter.deserialize(this.selectById(record.getId()));
    }

    @Override
    public StorageInfo findById(Long id) {
        Condition condition = Tables.STORAGE_INFO.ID.eq(id);
        return converter.deserialize(mDSLContext.select()
                .from(Tables.STORAGE_INFO)
                .where(condition)
                .orderBy(Tables.STORAGE_INFO.UPDATED_TIME.desc())
                .limit(1)
                .fetchOneInto(StorageInfoRecord.class));
    }

    @Override
    public StorageInfo findByNameAndToken(String fileName, String token) {
        Condition condition = Tables.STORAGE_INFO.FILE_NAME.eq(fileName)
                .and(Tables.STORAGE_INFO.ACCESS_TOKEN.eq(token));
        return converter.deserialize(mDSLContext.select()
                .from(Tables.STORAGE_INFO)
                .where(condition)
                .orderBy(Tables.STORAGE_INFO.UPDATED_TIME.desc())
                .limit(1)
                .fetchOneInto(StorageInfoRecord.class));
    }

    @Override
    public List<StorageInfo> findByFileTypeAndToken(FileType fileType, String token) {
        Condition condition = Tables.STORAGE_INFO.TYPE.eq(fileType.name())
                .and(Tables.STORAGE_INFO.ACCESS_TOKEN.eq(token));

        List<StorageInfoRecord> records = mDSLContext.select()
                .from(Tables.STORAGE_INFO)
                .where(condition)
                .orderBy(Tables.STORAGE_INFO.UPDATED_TIME.desc())
                .fetchInto(StorageInfoRecord.class);
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public boolean deleteByNameAndToken(String fileName, Long id) {
        Condition condition = Tables.STORAGE_INFO.FILE_NAME.eq(fileName)
                .and(Tables.STORAGE_INFO.ID.eq(id));
        try{
            mDSLContext.deleteFrom(Tables.STORAGE_INFO).where(condition).execute();
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    private StorageInfoRecord selectById(Long id) {
        return mDSLContext.select()
                .from(Tables.STORAGE_INFO)
                .where(Tables.STORAGE_INFO.ID.eq(id))
                .limit(1)
                .fetchOneInto(StorageInfoRecord.class);
    }

}