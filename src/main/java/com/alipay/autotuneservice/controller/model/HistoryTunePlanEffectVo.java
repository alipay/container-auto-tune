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

import lombok.Data;

/**
 * @author huangkaifei
 * @version : AgentActionRespnsone.java, v 0.1 2022年04月12日 8:42 AM huangkaifei Exp $
 */
@Data
public class HistoryTunePlanEffectVo {

    /**
     * 优化类型
     */
    private String effectType;

    /**
     * 调优次数
     */
    private Long   historyCount;

    /**
     * 上一个优化点时间
     */
    private Long   lastTuneTime  = 0L;

    /**
     * 上一个优化点描述
     */
    private Double lastPointDesc = 0.0;

    public HistoryTunePlanEffectVo() {

    }

    public HistoryTunePlanEffectVo(String effectType) {
        this.effectType = effectType;
    }

}