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

import lombok.Data;

import java.io.Serializable;

/**
 * @author dutianze
 * @version TunePhase.java, v 0.1 2022年03月30日 16:30 dutianze
 */
@Data
public class TunePipelinePhase implements Serializable {

    private Integer     id;
    private TuneStage   stage;
    private Integer     pipelineId;
    private Integer     pipelineBranchId;
    private TuneContext context;

    public TunePipelinePhase() {
    }

    public TunePipelinePhase(TunePipeline tunePipeline, TuneContext context) {
        this.stage = tunePipeline.getStage();
        this.pipelineId = tunePipeline.getPipelineId();
        this.pipelineBranchId = tunePipeline.getId();
        this.context = context;
    }
}