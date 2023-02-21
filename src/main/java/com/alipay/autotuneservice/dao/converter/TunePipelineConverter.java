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

import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelineRecord;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.PipelineStatus;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TunePipelinePhase;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author dutianze
 * @version TunePipelineConverter.java, v 0.1 2022年04月07日 17:45 dutianze
 */
@Slf4j
public class TunePipelineConverter implements EntityConverter<TunePipeline, TunePipelineRecord> {

    @Override
    public TunePipelineRecord serialize(TunePipeline tunePipeline) {
        if (tunePipeline == null) {
            return null;
        }
        TunePipelineRecord record = new TunePipelineRecord();
        record.setId(tunePipeline.getId());
        record.setPipelineId(tunePipeline.getPipelineId());
        record.setAccessToken(tunePipeline.getAccessToken());
        record.setAppId(tunePipeline.getAppId());
        record.setMachineId(tunePipeline.getMachineId().name());
        record.setStatus(tunePipeline.getStatus().name());
        record.setStage(tunePipeline.getStage().name());
        record.setTunePlanId(tunePipeline.getTunePlanId());
        PipelineStatus pipelineStatus = null;
        log.info("TunePipelineConverter tunePipeline.getContext: {}", tunePipeline.getContext());
        if (tunePipeline.getContext() != null && tunePipeline.getContext().getPipelineStatus() != null) {
            pipelineStatus = tunePipeline.getContext().getPipelineStatus();
            record.setType(pipelineStatus.name());
        }
        if (tunePipeline.getCurrentPhase() != null) {
            record.setCurrentPhaseId(tunePipeline.getCurrentPhase().getId());
        }
        if (tunePipeline.getPrePhase() != null) {
            record.setPrePhaseId(tunePipeline.getPrePhase().getId());
        }
        return record;
    }

    @Override
    public TunePipeline deserialize(TunePipelineRecord record) {
        if (record == null) {
            return null;
        }
        TunePipeline tunePipeline = new TunePipeline();
        try{
            tunePipeline.setId(record.getId());
            tunePipeline.setPipelineId(record.getPipelineId());
            tunePipeline.setAccessToken(record.getAccessToken());
            tunePipeline.setAppId(record.getAppId());
            tunePipeline.setMachineId(MachineId.valueOf(record.getMachineId()));
            tunePipeline.setStatus(Status.valueOf(record.getStatus()));
            tunePipeline.setStage(TuneStage.valueOf(record.getStage()));
            tunePipeline.setTunePlanId(record.getTunePlanId());
            tunePipeline.setPipelineStatus(PipelineStatus.valueOf(record.getType()));
        }catch (Exception e){
            log.error("deserialize occurs an error");
        }
        return tunePipeline;
    }

    public TunePipeline deserializeWithPhase(TunePipelineRecord pipelineRecord,
                                             Map<Integer, TunePipelinePhase> tunePipelinePhaseMap) {
        TunePipeline tunePipeline = this.deserialize(pipelineRecord);
        TunePipelinePhase prePhase = tunePipelinePhaseMap.get(pipelineRecord.getPrePhaseId());
        tunePipeline.setPrePhase(prePhase);
        TunePipelinePhase currentPhase = tunePipelinePhaseMap.get(pipelineRecord.getCurrentPhaseId());
        tunePipeline.setCurrentPhase(currentPhase);
        return tunePipeline;
    }

}