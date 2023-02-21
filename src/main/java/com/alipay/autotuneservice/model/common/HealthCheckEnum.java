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
 * @version HealthCheckEnum.java, v 0.1 2022年04月28日 11:17 下午 huoyuqi
 */
public enum HealthCheckEnum {

    /**
     * RT
     */
    //    RT("响应时间", "响应时间正常", "检测响应时间过长"),
    RT("RT", "RT is normal", "RT takes a long time"),

    /**
     * YGC_COUNT
     */
    //    YGC_COUNT("YGC次数", "YGC次数正常", "检测YGC次数增涨过多"),
    YGC_COUNT("YGC_COUNT", "YGC count is normal", "Full gc frequency anomaly"),

    /**
     * YGC_TIME
     */
    //    YGC_TIME("YGC时间", "YGC耗时正常", "检测出YGC时间过长"),
    YGC_TIME("YGC_TIME", "YGC time is normal", "YGC takes a long time"),

    /**
     * FGC_COUNT
     */
    //    FGC_COUNT("FGC次数", "FGC次数正常", "检测出FGC次数增涨过多"),
    FGC_COUNT("FGC_COUNT", "FGC count is normal", "Full gc frequency anomaly"),

    /**
     * FGC_TIME
     */
    //    FGC_TIME("FGC时间", "FGC耗时正常", "检测出FGC耗时过长"),
    FGC_TIME("FGC_TIME", "FGC time is normal", "FGC takes a long time"),

    /**
     * HEAP_MEMORY
     */
    //    HEAP_MEMORY("堆内存", "内存检测正常", "检测出堆内存过多"),
    HEAP_MEMORY("HEAP_MEMORY", "heap mem usage is normal", "heap mem usage is high"),

    /**
     * OLD_UTIL
     */
    //    OLD_UTIL("old区使用率", "使用率正常", "检测出使用率过频"),
    OLD_UTIL("OLD_UTIL", "old usage is normal", "old usage usage is hig"),

    /**
     * GC_TYPE
     */
    //    GC_TYPE("垃圾回收器", "使用回收器合理", "使用回收器异常"),
    GC_TYPE("GC_TYPE", "Collector usage is reasonable", "Collector usage is unreasonable"),

    /**
     * 成本控制项 meta
     */
    //    HEAP_META_IDLE("meta空间使用", "使用meta空间合理", "使用meta空间不合理"),
    HEAP_META("HEAP_META", "metaspace usage is reasonable", "metaspace usage is unreasonable"),

    /**
     * 成本控制项 old
     */
    //    HEAP_OLD_IDLE("old 空间使用", "使用old空间合理", "使用old空间不合理"),
    HEAP_OLD("HEAP_OLD", "heap old  usage is reasonable", "heap old usage is unreasonable"),

    /**
     * 未知类型枚举
     */
    //    UNKNOWN("unknown", "未知", "未知");
    UNKNOWN("unknown", "unknown", "unknown");

    private final String checkItemName;

    public String getCheckItemName() {
        return checkItemName;
    }

    public String getNormal() {
        return normal;
    }

    public String getAbnormal() {
        return abnormal;
    }

    private final String normal;
    private final String abnormal;

    HealthCheckEnum(String checkItemName, String normal, String abnormal) {
        this.checkItemName = checkItemName;
        this.normal = normal;
        this.abnormal = abnormal;
    }

    public static List<String> healthCheckNames() {
        return Arrays.stream(HealthCheckEnum.values())
                .filter(healthCheckEnum -> healthCheckEnum != HealthCheckEnum.UNKNOWN)
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}