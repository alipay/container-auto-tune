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
package com.alipay.autotuneservice.model.pipeline;

import com.alipay.autotuneservice.util.EnhanceBeanUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * @author dutianze
 * @version TunePipeline.java, v 0.1 2022年03月29日 16:52 dutianze
 */
@Data
@Slf4j
public class TunePipeline implements Serializable {

    private Integer id;

    private Integer pipelineId;

    private String accessToken;

    private Integer appId;

    private MachineId machineId;

    /**
     * 当前状态
     */
    private Status status;

    /**
     * 当前阶段
     */
    private TuneStage stage;

    /**
     * 当前阶段
     */
    @NotNull
    private TunePipelinePhase currentPhase;

    /**
     * 前一个阶段
     */
    @Nullable
    private TunePipelinePhase prePhase;

    /**
     * 计划ID
     */
    private Integer tunePlanId;

    /**
     * 流程状态
     */
    private PipelineStatus pipelineStatus;

    public boolean canFlowToNext() {
        return this.status.equals(Status.RUNNING);
    }

    public void flowTo(TuneStage state) {
        if (this.stage.equals(state)) {
            return;
        }
        // 任务流转至新阶段
        this.stage = state;
        this.prePhase = currentPhase;
        this.currentPhase = new TunePipelinePhase(this, this.getContext());
    }

    public MachineId getMachineId() {
        return this.machineId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TunePipeline generateNewTunePipeline(TuneStage tuneStage) {
        TunePipeline testTunePipeline = new TunePipeline();
        testTunePipeline.setPipelineId(this.pipelineId);
        testTunePipeline.setAccessToken(this.accessToken);
        testTunePipeline.setAppId(this.appId);
        testTunePipeline.setStatus(Status.RUNNING);
        testTunePipeline.setMachineId(tuneStage.getMachineId());
        testTunePipeline.setStage(tuneStage);
        TunePipelinePhase testTunePhase = new TunePipelinePhase(testTunePipeline, this.getContext());
        log.info("getContext is: {}", this.getContext());
        testTunePipeline.setCurrentPhase(testTunePhase);
        testTunePipeline.setTunePlanId(this.tunePlanId);
        return testTunePipeline;
    }

    @JsonIgnore
    public TuneContext getContext() {
        if (this.currentPhase == null) {
            return null;
        }
        return this.currentPhase.getContext();
    }

    public void patchContext(TuneContext context) {
        if (context == null) {
            return;
        }
        TuneContext currentContext = this.getContext();
        EnhanceBeanUtils.copyPropertiesIgnoreNull(context, currentContext);
    }

    public boolean isAlive() {
        return Stream.of(Status.RUNNING, Status.WAIT).anyMatch(e -> e.equals(this.getStatus()));
    }

    public boolean isGray(){
        return StringUtils.isNotEmpty(this.getContext().getGrayJvm());
    }
}