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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version EffectTypeEnum.java, v 0.1 2022年05月05日 12:18 下午 huoyuqi
 */
public enum EffectTypeEnum {

    /**
     * 相应时间RT
     */
    RT("RT"),

    /**
     * QPS
     */
    QPS("QPS"),

    /**
     * MEM
     */
    MEM("MEMORY"),

    /**
     * FGC_COUNT
     */
    FGC_COUNT("FGC_COUNT"),

    /**
     * FGC_TIME
     */
    FGC_TIME("FGC_TIME"),

    /**
     * YGC_COUNT
     */
    YGC_COUNT("YGC_COUNT"),

    /**
     * YGC_TIME
     */
    YGC_TIME("YGC_TIME"),

    /**
     * CPU
     */
    CPU("CPU"),

    /**
     * 未知类型枚举
     */
    UNKNOWN("UNKNOWN");

    private final String code;

    EffectTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static List<String> effectTypeNames() {
        return Arrays.stream(EffectTypeEnum.values())
                .filter(effectTypeEnum -> effectTypeEnum != EffectTypeEnum.UNKNOWN)
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public static EffectTypeEnum findByType(String type) {
        for (EffectTypeEnum effectTypeEnum : values()) {
            if (effectTypeEnum.name().equals(type)) {
                return effectTypeEnum;
            }
        }
        return EffectTypeEnum.UNKNOWN;
    }
}