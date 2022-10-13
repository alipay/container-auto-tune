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
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.converter.TunePipelineConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelineRecord;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TunePipelinePhase;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version TunePipelineRepositoryImpl.java, v 0.1 2022年03月30日 18:07 dutianze
 */
@Slf4j
@Service
public class TunePipelineRepositoryImpl implements TunePipelineRepository {

    private static final TunePipelineConverter converter = new TunePipelineConverter();

    @Autowired
    private TunePipelinePhaseRepository        tunePipelinePhaseRepository;
    @Autowired
    protected DSLContext                       dslContext;

    @Override
    public TunePipeline saveOneWithTransaction(TunePipeline tunePipeline) {
        return dslContext.transactionResult(ctx -> {
            DSLContext innerDsl = DSL.using(ctx);
            return this.save(tunePipeline, innerDsl);
        });
    }

    @Override
    public void saveWithTransaction(TunePipeline... tunePipelines) {
        dslContext.transaction(ctx -> {
            DSLContext innerDsl = DSL.using(ctx);
            for (TunePipeline pipeline : tunePipelines) {
                this.save(pipeline, innerDsl);
            }
        });
    }

    @Override
    public TunePipeline findByPipelineId(Integer pipelineId) {
        List<TunePipelineRecord> pipelineRecords = this.selectByPipelineId(pipelineId);
        if (CollectionUtils.isEmpty(pipelineRecords)) {
            return null;
        }
        return converter.deserialize(pipelineRecords.get(0));
    }

    @Override
    public TunePipeline findByPipelineIdAndStatus(Integer pipelineId, Status status) {
        List<TunePipelineRecord> pipelineRecords = this.selectByPipelineId(pipelineId);
        if (CollectionUtils.isEmpty(pipelineRecords)) {
            return null;
        }
        Optional<TunePipelineRecord> recordOptional = pipelineRecords.stream()
                .filter(e -> e.getStatus().equals(status.name()))
                .findFirst();
        if (!recordOptional.isPresent()) {
            return null;
        }
        TunePipeline tunePipeline = converter.deserialize(recordOptional.get());
        this.attachTunePipelinePhase(tunePipeline, recordOptional.get(), dslContext);
        return tunePipeline;
    }

    @Override
    public List<TunePipeline> findByAppIdAndStatus(Integer appId, Status status) {
        Condition condition = Tables.TUNE_PIPELINE.APP_ID.eq(appId)
                .and(Tables.TUNE_PIPELINE.ID.eq(Tables.TUNE_PIPELINE.PIPELINE_ID));
        if (status != null) {
            condition = condition.and(Tables.TUNE_PIPELINE.STATUS.eq(status.name()));
        }
        List<TunePipelineRecord> records = dslContext.select()
                .from(Tables.TUNE_PIPELINE)
                .where(condition)
                .fetchInto(TunePipelineRecord.class);
        Map<Integer, TunePipelinePhase> phaseIdMapPhase = tunePipelinePhaseRepository.findToMap(records, dslContext);
        return records.stream().map(e -> converter.deserializeWithPhase(e, phaseIdMapPhase)).collect(Collectors.toList());
    }

    @Override
    public List<TunePipeline> findByAppId(Integer appId) {
        List<MachineId> machineIds = Arrays.asList(MachineId.TUNE_PIPELINE,MachineId.TUNE_JVM_PIPELINE);
        Condition condition = Tables.TUNE_PIPELINE.APP_ID.eq(appId)
                .and(Tables.TUNE_PIPELINE.ID.in(machineIds));
        List<TunePipelineRecord> records = dslContext.select()
                .from(Tables.TUNE_PIPELINE)
                .where(condition)
                .fetchInto(TunePipelineRecord.class);
        Map<Integer, TunePipelinePhase> phaseIdMapPhase = tunePipelinePhaseRepository.findToMap(records, dslContext);
        return records.stream().map(e -> converter.deserializeWithPhase(e, phaseIdMapPhase)).collect(Collectors.toList());
    }

    @Override
    public List<TunePipeline> findPipelineByStatus(Status status) {
        Condition condition = Tables.TUNE_PIPELINE.STATUS.eq(status.name());
        List<TunePipelineRecord> records = dslContext.select()
                .from(Tables.TUNE_PIPELINE)
                .where(condition)
                .fetchInto(TunePipelineRecord.class);
        Map<Integer, TunePipelinePhase> phaseIdMapPhase = tunePipelinePhaseRepository.findToMap(records, dslContext);
        return records.stream().map(e -> converter.deserializeWithPhase(e, phaseIdMapPhase)).collect(Collectors.toList());
    }

    @Override
    public List<TunePipeline> findByPlanId(Integer planId) {
        Condition condition = Tables.TUNE_PIPELINE.TUNE_PLAN_ID.eq(planId);
        List<TunePipelineRecord> records = dslContext.select()
                .from(Tables.TUNE_PIPELINE)
                .where(condition)
                .fetchInto(TunePipelineRecord.class);
        Map<Integer, TunePipelinePhase> phaseIdMapPhase = tunePipelinePhaseRepository.findToMap(records, dslContext);
        return records.stream().map(e -> converter.deserializeWithPhase(e, phaseIdMapPhase)).collect(Collectors.toList());
    }

    @Override
    public TunePipeline findByMachineIdAndPipelineId(MachineId machineId, Integer pipelineId) {
        Condition condition = Tables.TUNE_PIPELINE.PIPELINE_ID.eq(pipelineId).and(
            Tables.TUNE_PIPELINE.MACHINE_ID.eq(machineId.name()));
        TunePipelineRecord tunePipelineRecord = dslContext.select().from(Tables.TUNE_PIPELINE)
            .where(condition).limit(1).fetchOneInto(TunePipelineRecord.class);
        if (tunePipelineRecord == null) {
            return null;
        }
        TunePipeline tunePipeline = converter.deserialize(tunePipelineRecord);
        this.attachTunePipelinePhase(tunePipeline, tunePipelineRecord, dslContext);
        return tunePipeline;
    }

    @Override
    public TunePipeline findByMachineIdAndPipelineId(Integer pipelineId) {
        List<MachineId> machineIds = Arrays.asList(MachineId.TUNE_PIPELINE,
            MachineId.TUNE_JVM_PIPELINE);
        Condition condition = Tables.TUNE_PIPELINE.PIPELINE_ID.eq(pipelineId).and(
            Tables.TUNE_PIPELINE.MACHINE_ID.in(machineIds));
        TunePipelineRecord tunePipelineRecord = dslContext.select().from(Tables.TUNE_PIPELINE)
            .where(condition).limit(1).fetchOneInto(TunePipelineRecord.class);
        if (tunePipelineRecord == null) {
            return null;
        }
        TunePipeline tunePipeline = converter.deserialize(tunePipelineRecord);
        this.attachTunePipelinePhase(tunePipeline, tunePipelineRecord, dslContext);
        return tunePipeline;
    }

    @Override
    public List<TunePipeline> batchFindMainPipelinesByPlanIds(MachineId machineId, List<Integer> planIds) {
        Condition condition = Tables.TUNE_PIPELINE.TUNE_PLAN_ID.in(planIds)
                .and(Tables.TUNE_PIPELINE.MACHINE_ID.eq(machineId.name()));
        List<TunePipelineRecord> pipelineRecords = dslContext.select()
                .from(Tables.TUNE_PIPELINE)
                .where(condition)
                .fetchInto(TunePipelineRecord.class);
        Map<Integer, TunePipelinePhase> phaseIdMapPhase = tunePipelinePhaseRepository.findToMap(pipelineRecords, dslContext);
        return pipelineRecords.stream()
                .map(e -> converter.deserializeWithPhase(e, phaseIdMapPhase))
                .collect(Collectors.toList());
    }

    @Override
    public List<TunePipeline> batchFindPipelinesByPlanIds(List<Integer> planIds) {
        List<MachineId> machineIds = Arrays.asList(MachineId.TUNE_PIPELINE,MachineId.TUNE_JVM_PIPELINE);
        Condition condition = Tables.TUNE_PIPELINE.TUNE_PLAN_ID.in(planIds)
                .and(Tables.TUNE_PIPELINE.MACHINE_ID.in(machineIds));
        List<TunePipelineRecord> pipelineRecords = dslContext.select()
                .from(Tables.TUNE_PIPELINE)
                .where(condition)
                .fetchInto(TunePipelineRecord.class);
        Map<Integer, TunePipelinePhase> phaseIdMapPhase = tunePipelinePhaseRepository.findToMap(pipelineRecords, dslContext);
        return pipelineRecords.stream()
                .map(e -> converter.deserializeWithPhase(e, phaseIdMapPhase))
                .collect(Collectors.toList());
    }

    @Override
    public List<TunePipeline> batchFindPipelinesByPipelines(List<Integer> pipelineIds) {
        List<TunePipelineRecord> pipelineRecords = dslContext.select()
                .from(Tables.TUNE_PIPELINE)
                .where(Tables.TUNE_PIPELINE.ID.in(pipelineIds))
                .fetchInto(TunePipelineRecord.class);
        Map<Integer, TunePipelinePhase> phaseIdMapPhase = tunePipelinePhaseRepository.findToMap(pipelineRecords, dslContext);
        return pipelineRecords.stream().map(e -> converter.deserializeWithPhase(e, phaseIdMapPhase)).collect(Collectors.toList());
    }

    @Override
    public TunePipelineRecord findById(Integer pipelineId) {
        Condition condition = Tables.TUNE_PIPELINE.ID.eq(pipelineId);
        return dslContext.select().from(Tables.TUNE_PIPELINE).where(condition)
            .fetchOneInto(TunePipelineRecord.class);
    }

    private void attachTunePipelinePhase(TunePipeline tunePipeline, TunePipelineRecord record,
                                         DSLContext innerDsl) {
        TunePipelinePhase currentPhase = tunePipelinePhaseRepository.find(
            record.getCurrentPhaseId(), innerDsl);
        tunePipeline.setCurrentPhase(currentPhase);
        TunePipelinePhase prePhase = tunePipelinePhaseRepository.find(record.getPrePhaseId(),
            innerDsl);
        tunePipeline.setPrePhase(prePhase);
    }

    private List<TunePipelineRecord> selectByPipelineId(Integer pipelineId) {
        Condition condition = Tables.TUNE_PIPELINE.PIPELINE_ID.eq(pipelineId);
        return dslContext.select().from(Tables.TUNE_PIPELINE).where(condition)
            .orderBy(Tables.TUNE_PIPELINE.ID.desc()).fetchInto(TunePipelineRecord.class);
    }

    private TunePipeline save(TunePipeline tunePipeline, DSLContext innerDsl) {
        TunePipelineRecord record = converter.serialize(tunePipeline);
        Integer id;
        if (tunePipeline.getId() == null) {
            // create
            TunePipelineRecord createdRecord = this.insert(record, innerDsl);
            id = createdRecord.getId();
            // create current phase
            TunePipelinePhase currentPhase = tunePipeline.getCurrentPhase();
            currentPhase.setPipelineId(createdRecord.getPipelineId());
            currentPhase.setPipelineBranchId(createdRecord.getId());
            TunePipelinePhase createdPhase = tunePipelinePhaseRepository.save(currentPhase,
                innerDsl);
            // update phase
            createdRecord.setCurrentPhaseId(createdPhase.getId());
            this.update(createdRecord, innerDsl);
        } else {
            id = tunePipeline.getId();
            TunePipelinePhase phase = tunePipelinePhaseRepository.save(
                tunePipeline.getCurrentPhase(), innerDsl);
            record.setCurrentPhaseId(phase.getId());
            this.update(record, innerDsl);
        }
        return this.findById(id, innerDsl);
    }

    private TunePipelineRecord insert(TunePipelineRecord record, DSLContext innerDsl) {
        record.setUpdatedTime(DateUtils.now());
        record.setCreatedTime(DateUtils.now());
        TunePipelineRecord newRecord = innerDsl.newRecord(Tables.TUNE_PIPELINE, record);
        newRecord.store();
        if (newRecord.getPipelineId() == null) {
            newRecord.setPipelineId(newRecord.getId());
            newRecord.store();
        }
        return newRecord;
    }

    private void update(TunePipelineRecord record, DSLContext innerDsl) {
        TunePipelineRecord updateRecord = innerDsl.newRecord(Tables.TUNE_PIPELINE);
        updateRecord.from(record);
        updateRecord.setUpdatedTime(DateUtils.now());
        updateRecord.update();
    }

    private TunePipelineRecord selectById(Integer id, DSLContext innerDsl) {
        Condition condition = Tables.TUNE_PIPELINE.ID.eq(id);
        return innerDsl.select().from(Tables.TUNE_PIPELINE).where(condition).limit(1)
            .fetchOneInto(TunePipelineRecord.class);
    }

    private TunePipeline findById(Integer id, DSLContext innerDsl) {
        TunePipelineRecord record = this.selectById(id, innerDsl);
        if (record == null) {
            return null;
        }
        TunePipeline tunePipeline = converter.deserialize(record);
        this.attachTunePipelinePhase(tunePipeline, record, innerDsl);
        return tunePipeline;
    }
}