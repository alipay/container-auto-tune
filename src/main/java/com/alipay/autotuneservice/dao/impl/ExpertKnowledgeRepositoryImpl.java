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
import com.alipay.autotuneservice.dao.ExpertKnowledgeRepository;
import com.alipay.autotuneservice.dao.converter.ExpertKnowledgeConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.ExpertKnowledgeRecord;
import com.alipay.autotuneservice.model.expert.ExpertKnowledge;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.EnhanceBeanUtils;
import org.jooq.Field;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version ExpertKnowledgeRepositoryImpl.java, v 0.1 2022年04月26日 17:14 dutianze
 */
@Service
public class ExpertKnowledgeRepositoryImpl extends BaseDao implements ExpertKnowledgeRepository {

    private static final ExpertKnowledgeConverter converter = new ExpertKnowledgeConverter();

    @Override
    public List<ExpertKnowledge> loadData() {
        List<ExpertKnowledgeRecord> records = mDSLContext.select()
                .from(Tables.EXPERT_KNOWLEDGE)
                .fetch()
                .into(ExpertKnowledgeRecord.class);
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public ExpertKnowledge save(ExpertKnowledge expertKnowledge) {
        ExpertKnowledgeRecord record = converter.serialize(expertKnowledge);
        record.setUpdatedTime(DateUtils.now());
        // insert
        if (record.getId() == null) {
            record.setCreatedTime(DateUtils.now());
            Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record);
            return converter.deserialize(mDSLContext.insertInto(Tables.EXPERT_KNOWLEDGE).set(map)
                .returning().fetchOne());
        }
        // update
        Map<Field<?>, Object> map = EnhanceBeanUtils.parseRecordNonNullValueIntoMap(record,
            Tables.EXPERT_KNOWLEDGE.ID);
        mDSLContext.update(Tables.EXPERT_KNOWLEDGE).set(map)
            .where(Tables.EXPERT_KNOWLEDGE.ID.eq(record.getId())).returning().execute();
        return converter.deserialize(this.selectById(record.getId()));
    }

    private ExpertKnowledgeRecord selectById(Integer id) {
        return mDSLContext.select().from(Tables.EXPERT_KNOWLEDGE)
            .where(Tables.EXPERT_KNOWLEDGE.ID.eq(id)).limit(1)
            .fetchOneInto(ExpertKnowledgeRecord.class);
    }
}