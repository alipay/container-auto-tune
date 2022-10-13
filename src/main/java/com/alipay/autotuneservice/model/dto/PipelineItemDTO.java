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
package com.alipay.autotuneservice.model.dto;

import lombok.Data;

/**
 * @author dutianze
 * @version PipelineDTO.java, v 0.1 2022年05月13日 15:49 dutianze
 */
@Data
public class PipelineItemDTO {

    /**
     * item序列号
     */
    private final int           index;

    /**
     * 阶段枚举
     */
    private final PipelineStage stage;

    /**
     * 阶段状态
     */
    private PipelineStatus      status;

    public PipelineItemDTO(PipelineStage stage) {
        this.index = stage.getIndex();
        this.stage = stage;
    }

    public void changeStatus(PipelineStage stage) {
        if (this.stage.getIndex() < stage.getIndex()) {
            this.status = PipelineStatus.FINISH;
        } else if (this.stage.getIndex() == stage.getIndex()) {
            this.status = PipelineStatus.RUNNING;
        } else {
            this.status = PipelineStatus.WAIT;
        }
    }
}