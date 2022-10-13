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

import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelinePhaseRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TunePipelinePhase;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.util.GsonUtil;

/**
 * @author dutianze
 * @version TunePipelinePhaseConverter.java, v 0.1 2022年04月11日 11:26 dutianze
 */
public class TunePipelinePhaseConverter implements
                                       EntityConverter<TunePipelinePhase, TunePipelinePhaseRecord> {

    public TunePipelinePhaseRecord serialize(TunePipelinePhase phase) {
        if (phase == null) {
            return null;
        }

        TunePipelinePhaseRecord record = new TunePipelinePhaseRecord();
        record.setId(phase.getId());
        record.setStage(phase.getStage().name());
        record.setPipelineId(phase.getPipelineId());
        record.setPipelineBranchId(phase.getPipelineBranchId());
        record.setContext(GsonUtil.toJson(phase.getContext()));
        return record;
    }

    public TunePipelinePhase deserialize(TunePipelinePhaseRecord record) {
        if (record == null) {
            return null;
        }
        TunePipelinePhase tunePipeline = new TunePipelinePhase();
        tunePipeline.setId(record.getId());
        tunePipeline.setStage(TuneStage.valueOf(record.getStage()));
        tunePipeline.setPipelineId(record.getPipelineId());
        tunePipeline.setPipelineBranchId(record.getPipelineBranchId());
        tunePipeline.setContext(GsonUtil.fromJson(record.getContext(), TuneContext.class));
        return tunePipeline;
    }
}