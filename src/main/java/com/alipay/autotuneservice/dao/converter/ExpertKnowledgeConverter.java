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

import com.alipay.autotuneservice.dao.jooq.tables.records.ExpertKnowledgeRecord;
import com.alipay.autotuneservice.model.expert.ExpertJvmPlan;
import com.alipay.autotuneservice.model.expert.ExpertKnowledge;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.model.expert.ProblemType;
import com.alipay.autotuneservice.util.GsonUtil;

/**
 * @author dutianze
 * @version ExpertKnowledgeConverter.java, v 0.1 2022年04月29日 11:42 dutianze
 */
public class ExpertKnowledgeConverter implements
                                     EntityConverter<ExpertKnowledge, ExpertKnowledgeRecord> {

    @Override
    public ExpertKnowledgeRecord serialize(ExpertKnowledge entity) {
        if (entity == null) {
            return null;
        }
        ExpertKnowledgeRecord record = new ExpertKnowledgeRecord();
        record.setId(entity.getId());
        record.setGarbageCollector(entity.getGarbageCollector().name());
        record.setJdkVersion(entity.getJdkVersion());
        record.setDesc(entity.getDesc());
        record.setProblemTypeSet(GsonUtil.toJson(entity.getProblemTypes()));
        record.setExpertJvmPlans(GsonUtil.toJson(entity.getExpertJvmPlans()));
        record.setCreatedBy(entity.getCreatedBy());
        record.setCreatedTime(entity.getCreatedTime());
        record.setUpdatedTime(entity.getUpdatedTime());
        return record;
    }

    @Override
    public ExpertKnowledge deserialize(ExpertKnowledgeRecord record) {
        if (record == null) {
            return null;
        }
        ExpertKnowledge entity = new ExpertKnowledge();
        entity.setId(record.getId());
        entity.setGarbageCollector(GarbageCollector.valueOf(record.getGarbageCollector()));
        entity.setJdkVersion(record.getJdkVersion());
        entity.setDesc(record.getDesc());
        entity.setProblemTypes(GsonUtil.fromJsonSet(record.getProblemTypeSet(), ProblemType.class));
        entity.setExpertJvmPlans(GsonUtil.fromJsonList(record.getExpertJvmPlans(),
            ExpertJvmPlan.class));
        entity.setCreatedBy(record.getCreatedBy());
        entity.setCreatedTime(record.getCreatedTime());
        entity.setUpdatedTime(record.getUpdatedTime());
        return entity;
    }
}