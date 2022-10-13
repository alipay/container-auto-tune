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
package com.alipay.autotuneservice.model.common;

/**
 * @author huoyuqi
 * @version EvaluateTypeEnum.java, v 0.1 2022年04月25日 10:24 上午 huoyuqi
 */
public enum EvaluateTypeEnum {
    /**
     * FGC_COUNT
     */
    FGC_COUNT("fgc_count", 0.18),

    /**
     * FGC_TIME
     */
    FGC_TIME("fgc_time", 0.18),

    /**
     * YGC_COUNT
     */
    YGC_COUNT("ygc_count", 0.1),

    /**
     * YGC_TIME
     */
    YGC_TIME("ygc_time", 0.1),

    /**
     * RT
     */
    RT("rt", 0.1),

    /**
     * HEAP_MEMORY
     */
    HEAP_MEMORY("heap_memory", 0.1),

    /**
     * OLD_UTIL
     */
    OLD_UTIL("old_util", 0.1),

    /**
     * GC_TYPE
     */
    GC_TYPE("gc_type", 0.1),

    /**
     * 成本控制项 meta
     */
    HEAP_META_IDLE("heap_meta_idle", 0.02),

    /**
     * 成本控制项 old
     */
    HEAP_OLD_IDLE("heap_old_idle", 0.02),

    /**
     * 未知类型枚举
     */
    UNKNOWN("unknown", 0.0);

    private final String code;
    private final Double weight;

    EvaluateTypeEnum(String code, Double weight) {
        this.code = code;
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public Double getWeight() {
        return weight;
    }
}