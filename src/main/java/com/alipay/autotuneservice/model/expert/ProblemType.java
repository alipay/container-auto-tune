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
package com.alipay.autotuneservice.model.expert;

import com.alipay.autotuneservice.model.common.EvaluateTypeEnum;

/**
 * @author dutianze
 * @version ProblemType.java, v 0.1 2022年04月26日 16:19 dutianze
 */
public enum ProblemType {

    RT(EvaluateTypeEnum.RT),

    FGC_TIME(EvaluateTypeEnum.FGC_TIME),

    YGC_COUNT(EvaluateTypeEnum.YGC_COUNT),

    HEAP_MEMORY(EvaluateTypeEnum.HEAP_MEMORY),

    YGC_TIME(EvaluateTypeEnum.YGC_TIME),

    OLD_UTIL(EvaluateTypeEnum.OLD_UTIL),

    FGC_COUNT(EvaluateTypeEnum.FGC_COUNT),

    GC_TYPE(EvaluateTypeEnum.GC_TYPE),

    HEAP_META_IDLE(EvaluateTypeEnum.UNKNOWN),

    HEAP_OLD_IDLE(EvaluateTypeEnum.UNKNOWN);

    private final Double weight;

    ProblemType(EvaluateTypeEnum evaluateTypeEnum) {
        this.weight = evaluateTypeEnum.getWeight();
    }

    public Double getWeight() {
        return weight;
    }
}