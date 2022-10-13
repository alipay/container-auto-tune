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

import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version baseLineVO.java, v 0.1 2022年08月15日 4:04 下午 huoyuqi
 */
@Data
public class BaseLineVO {

    /**
     * 调节状态
     */
    private Boolean           status;

    /**
     * 正在调节
     */
    private BaseLineContentVO newVersion;

    /**
     * 当前版本
     */
    private BaseLineContentVO previousVersion;

    /**
     * 主流程pipelineId
     */
    private Integer           pipelineId;

    /**
     * 应用id
     */
    private Integer           appId;

    /**
     * 流程计划
     */
    private String            planName;

    public BaseLineVO(Boolean status, BaseLineContentVO newVersion,
                      BaseLineContentVO previousVersion, Integer appId, Integer pipelineId,
                      String planName) {
        this.status = status;
        this.newVersion = newVersion;
        this.previousVersion = previousVersion;
        this.appId = appId;
        this.pipelineId = pipelineId;
        this.planName = planName;
    }
}