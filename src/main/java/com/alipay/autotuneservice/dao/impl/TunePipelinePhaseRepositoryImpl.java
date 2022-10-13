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

import com.alipay.autotuneservice.dao.TunePipelinePhaseRepository;
import com.alipay.autotuneservice.dao.converter.TunePipelinePhaseConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelinePhaseRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelineRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TunePipelinePhase;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.UpdateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dutianze
 * @version TunePipelinePhaseRepositoryImpl.java, v 0.1 2022年04月07日 17:59 dutianze
 */
@Slf4j
@Service
public class TunePipelinePhaseRepositoryImpl implements TunePipelinePhaseRepository {

    private static final TunePipelinePhaseConverter converter = new TunePipelinePhaseConverter();

    @Autowired
    protected DSLContext                            dslContext;

    @Override
    public TunePipelinePhase find(Integer pipelineId, DSLContext dslContext) {
        TunePipelinePhaseRecord record = this.selectById(pipelineId, dslContext);
        if (record == null) {
            return null;
        }
        return converter.deserialize(record);
    }

    @Override
    public TunePipelinePhase save(TunePipelinePhase tunePipelinePhase, DSLContext dslContext) {
        TunePipelinePhaseRecord record = converter.serialize(tunePipelinePhase);
        TunePipelinePhaseRecord selectedRecord = this.selectById(tunePipelinePhase.getId(),
            dslContext);
        Integer id;
        if (selectedRecord == null) {
            id = this.insert(record, dslContext);
        } else {
            id = tunePipelinePhase.getId();
            this.update(record, dslContext);
        }
        return this.find(id, dslContext);
    }

    @Override
    public Map<Integer, TunePipelinePhase> findToMap(List<TunePipelineRecord> pipelineRecords, DSLContext dslContext) {
        Set<Integer> phaseIds = pipelineRecords.stream()
                .flatMap(e -> Stream.of(e.getCurrentPhaseId(), e.getPrePhaseId())
                        .filter(Objects::nonNull)
                )
                .collect(Collectors.toSet());

        List<TunePipelinePhaseRecord> phaseRecords = dslContext.select()
                .from(Tables.TUNE_PIPELINE_PHASE)
                .where(Tables.TUNE_PIPELINE_PHASE.ID.in(phaseIds))
                .fetchInto(TunePipelinePhaseRecord.class);

        return phaseRecords.stream()
                .map(converter::deserialize)
                .collect(Collectors.toMap(TunePipelinePhase::getId, Function.identity(), (e, n) -> e));
    }

    @Override
    public void updateContext(Integer id, TuneContext context) {
        UpdateQuery<TunePipelinePhaseRecord> updateQuery = dslContext
            .updateQuery(Tables.TUNE_PIPELINE_PHASE);
        updateQuery.addValue(Tables.TUNE_PIPELINE_PHASE.CONTEXT, GsonUtil.toJson(context));
        updateQuery.addConditions(Tables.TUNE_PIPELINE_PHASE.ID.eq(id));
        updateQuery.execute();
    }

    private Integer insert(TunePipelinePhaseRecord record, DSLContext dslContext) {
        record.setUpdatedTime(DateUtils.now());
        record.setCreatedTime(DateUtils.now());
        TunePipelinePhaseRecord newRecord = dslContext
            .newRecord(Tables.TUNE_PIPELINE_PHASE, record);
        newRecord.store();
        return newRecord.getId();
    }

    private void update(TunePipelinePhaseRecord record, DSLContext dslContext) {
        record.setUpdatedTime(DateUtils.now());
        TunePipelinePhaseRecord updateRecord = dslContext.newRecord(Tables.TUNE_PIPELINE_PHASE);
        updateRecord.from(record);
        updateRecord.update();
    }

    private TunePipelinePhaseRecord selectById(Integer id, DSLContext dslContext) {
        Condition condition = Tables.TUNE_PIPELINE_PHASE.ID.eq(id);
        return dslContext.select().from(Tables.TUNE_PIPELINE_PHASE).where(condition).limit(1)
            .fetchOneInto(TunePipelinePhaseRecord.class);
    }
}