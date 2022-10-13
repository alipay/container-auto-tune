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
import com.alipay.autotuneservice.dao.PodAttachRepository;
import com.alipay.autotuneservice.dao.converter.PodAttachConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodAttachRecord;
import com.alipay.autotuneservice.model.common.PodAttach;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.EnhanceBeanUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version PodAttachRepositoryImpl.java, v 0.1 2022年06月17日 11:43 dutianze
 */
@Repository
public class PodAttachRepositoryImpl extends BaseDao implements PodAttachRepository {

    private static final PodAttachConverter converter = new PodAttachConverter();

    @Override
    public PodAttach save(PodAttach podAttach) {
        PodAttachRecord record = converter.serialize(podAttach);
        record.setUpdatedTime(DateUtils.now());
        // insert
        if (record.getId() == null) {
            record.setCreatedTime(DateUtils.now());
            Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record);
            return converter.deserialize(mDSLContext.insertInto(Tables.POD_ATTACH).set(map)
                .returning().fetchOne());
        }
        // update
        Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record,
            Tables.POD_ATTACH.ID);
        mDSLContext.update(Tables.POD_ATTACH).set(map)
            .where(Tables.POD_ATTACH.ID.eq(record.getId())).returning().execute();
        return converter.deserialize(this.selectById(record.getId()));
    }

    @Override
    public PodAttach findById(Integer id) {
        Condition condition = Tables.POD_ATTACH.ID.eq(id);
        return converter.deserialize(mDSLContext.select().from(Tables.POD_ATTACH).where(condition)
            .fetchOneInto(PodAttachRecord.class));
    }

    @Override
    public PodAttach findByPodId(Integer podId) {
        Condition condition = Tables.POD_ATTACH.POD_ID.eq(podId);
        return converter.deserialize(mDSLContext.select().from(Tables.POD_ATTACH).where(condition)
            .limit(1).fetchOneInto(PodAttachRecord.class));
    }

    @Override
    public List<PodAttach> findByPodIds(List<Integer> podIds) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(podIds), "podIds must not empty");
        Condition condition = Tables.POD_ATTACH.POD_ID.in(podIds);
        List<PodAttachRecord> podAttachRecords = mDSLContext.select()
                .from(Tables.POD_ATTACH)
                .where(condition)
                .fetchInto(PodAttachRecord.class);
        return podAttachRecords.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    private PodAttachRecord selectById(Integer id) {
        return mDSLContext.select().from(Tables.POD_ATTACH).where(Tables.POD_ATTACH.ID.eq(id))
            .fetchOneInto(PodAttachRecord.class);
    }
}