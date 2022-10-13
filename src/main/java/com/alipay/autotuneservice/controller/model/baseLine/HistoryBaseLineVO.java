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
package com.alipay.autotuneservice.controller.model.baseLine;

import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version AppBaseLineParamsVO.java, v 0.1 2022年08月09日 11:04 上午 huoyuqi
 */
@Data
public class HistoryBaseLineVO {

    /**
     * 应用id
     */
    Integer         appId;

    /**
     * 主流程id
     */
    Integer         pipelineId;

    /**
     * 应用状态  是否在流程中
     */
    TunePlanStatus  status;

    /**
     * 应用版本
     */
    String          version;

    /**
     * 调优计划名称
     */
    String          planName;

    /**
     * 变更人
     */
    String          createBy;

    /**
     * 变更时间
     */
    Long            time;

    /**
     * 变更jvmMarketId
     */
    Integer         currentJvmMarketId;

    /**
     * jvm参数
     */
    List<String>    jvm;

    /**
     * pod compare是否可点击
     */
    private Boolean compareOn;

    public HistoryBaseLineVO(Integer pipelineId, Integer appId, TunePlanStatus status,
                             String planName, String createBy, Long time, String version,
                             Integer jvmMarketId, List<String> jvm, Boolean compareOn) {
        this.pipelineId = pipelineId;
        this.appId = appId;
        this.status = status;
        this.planName = planName;
        this.createBy = createBy;
        this.time = time;
        this.version = version;
        this.currentJvmMarketId = jvmMarketId;
        this.jvm = jvm;
        this.compareOn = compareOn;
    }
}