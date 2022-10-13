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
package com.alipay.autotuneservice.controller.model;

import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.service.riskcheck.entity.CheckResponse;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author dutianze
 * @version PipelineDTO.java, v 0.1 2022年05月13日 15:49 dutianze
 */
@Data
public class BatchGroupVO {

    /**
     * 流程id
     */
    private Integer           pipelineId;
    private TuneStage         tuneStage      = TuneStage.NONE;
    private List<GroupDetail> batchGroup     = Lists.newLinkedList();
    private PipelineStatus    pipelineStatus = PipelineStatus.NONE;
    private String            showDesc       = "";
    /**
     * 当前百分比
     */
    private double            tuneRate;

    public BatchGroupVO(Integer pipelineId) {
        this.pipelineId = pipelineId;
    }

    @Data
    public static class GroupDetail {
        private Integer         batchNo;
        private Integer         targetRestartNo;
        private Integer         nowRestartNo;
        private GroupStatus     groupStatus;
        private CheckResponse   checkResponse;
        private List<PodDetail> successPodNames = Lists.newArrayList();
        private long            restartBeginTime;
        private long            restartEndTime;
        /**
         * 风险识别id
         */
        private String          riskTraceId;
    }

    public enum GroupStatus {
        CHECKING, FINISH, WAITING, PAUSE, ERROR, INTERRUPT;
    }

    public enum PipelineStatus {
        NONE, FINISH, RUNNING, ERROR, PAUSE;
    }

    @Data
    public static class PodDetail {

        private String successPodName;
        private long   createTime;
        private long   finishTime;

    }
}