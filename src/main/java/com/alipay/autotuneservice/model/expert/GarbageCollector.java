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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dutianze
 * @version GarbageCollector.java, v 0.1 2022年04月26日 16:43 dutianze
 */
public enum GarbageCollector {

    /**
     * 串行收集器, 多用于client模式, 它的优点是简单高效，对于单个 CPU 环境来说，由于没有线程交互的开销，因此拥有最高的单线程收集效率。
     *
     * 收集器: Serial Garbage Collector
     * 描述: young Copy and old MarkSweepCompact
     * 启用参数: [-XX:+UseSerialGC]
     */
    SERIAL_GARBAGE_COLLECTOR(GarbageCollectorEnum.COPY, GarbageCollectorEnum.MARK_SWEEP_COMPACT,
            appDefaultJvm -> appDefaultJvm.contains("-XX:+UseSerialGC")),

    /**
     * 并行收集器, 目标是达到一个可控制的吞吐量, 它被称为”吞吐量优先”收集器, 在注重吞吐量以及 CPU 资源敏感的场合，都可以优先考虑
     *
     * 收集器: Parallel Garbage Collector
     * 描述: young PS Scavenge old PS MarkSweep with adaptive sizing
     * 启用参数: [-XX:+UseParallelGC -XX:+UseParallelOldGC] or [-XX:+UseParallelGC] or [-XX:+UseParallelOldGC]
     * 额外参数: 是否自适应大小 -XX:+UseAdaptiveSizePolicy -XX:-UseAdaptiveSizePolicy
     */
    PARALLEL_GARBAGE_COLLECTOR(GarbageCollectorEnum.PS_SCAVENGE, GarbageCollectorEnum.PS_MARK_SWEEP,
            appDefaultJvm -> appDefaultJvm.contains("-XX:+UseParallelGC")),

    /**
     * CMS收集器, 尽量缩短垃圾回收时间和用户线程的停顿时间, 主要场景在 互联网 B/S 架构上
     *
     * 收集器: CMS Garbage Collector
     * 描述： young ParNew old ConcurrentMarkSweep
     * 启用参数：[-XX:+UseConcMarkSweepGC]	or [-XX:+UseConcMarkSweepGC -XX:+UseParNewGC] (deprecated in Java 8 and removed in Java 9)
     */
    CMS_GARBAGE_COLLECTOR(GarbageCollectorEnum.PAR_NEW, GarbageCollectorEnum.CONCURRENT_MARK_SWEEP,
            appDefaultJvm -> appDefaultJvm.contains("-XX:+UseConcMarkSweepGC") && !appDefaultJvm.contains("-XX:-UseParNewGC")),

    /**
     * G1收集器, 是一款面向服务端应用的垃圾收集器, 在多 CPU 和大内存的场景下有很好的性能
     *
     * 收集器: G1 Garbage Collector
     * 描述: young G1 Young and old G1 Mixed
     * 启用参数: [-XX:+UseG1GC]
     */
    G1_GARBAGE_COLLECTOR(GarbageCollectorEnum.G1_YOUNG_GENERATION, GarbageCollectorEnum.G1_MIXED_GENERATION,
            appDefaultJvm -> appDefaultJvm.contains("-XX:+UseG1GC")),

    /**
     * 描述: deprecated in Java 8 and removed in Java 9 - for ParNew see the line below which is NOT deprecated.
     * 启用参数: [-XX:+UseParNewGC]
     */
    @Deprecated
    PARNEW_MARKSWEEPCOMPACT_COLLECTOR(GarbageCollectorEnum.PAR_NEW, GarbageCollectorEnum.MARK_SWEEP_COMPACT,
            appDefaultJvm -> appDefaultJvm.contains("-XX:+UseParNewGC") && !appDefaultJvm.contains("-XX:+UseConcMarkSweepGC")),

    /**
     * 描述: deprecated in Java 8 and removed in Java 9.
     * 启用参数: [-XX:+UseConcMarkSweepGC -XX:-UseParNewGC]
     */
    @Deprecated
    COPY_CONCURRENTMARKSWEEP_COLLECTOR(GarbageCollectorEnum.COPY, GarbageCollectorEnum.CONCURRENT_MARK_SWEEP,
            appDefaultJvm -> appDefaultJvm.contains("-XX:+UseConcMarkSweepGC") && appDefaultJvm.contains("-XX:-UseParNewGC")),

    ///**
    // * Z Garbage Collector .
    // * jdk < 15 : -XX:+UnlockExperimentalVMOptions -XX:+UseZGC
    // * jdk >= 15 : -XX:+UseZGC
    // */
    //Z_GARBAGE_COLLECTOR

    UNKNOWN(GarbageCollectorEnum.UNKNOWN, GarbageCollectorEnum.UNKNOWN, appDefaultJvm -> false);

    GarbageCollector(GarbageCollectorEnum young, GarbageCollectorEnum old, Function<String, Boolean> optCondition) {
        this.young = young;
        this.old = old;
        this.optCondition = optCondition;
    }

    private final GarbageCollectorEnum      young;
    private final GarbageCollectorEnum      old;
    private final Function<String, Boolean> optCondition;

    public GarbageCollectorEnum getYoung() {
        return young;
    }

    public GarbageCollectorEnum getOld() {
        return old;
    }

    public enum GarbageCollectorEnum {

        // young
        COPY("Copy"),
        PS_SCAVENGE("PS Scavenge"),
        PAR_NEW("ParNew"),
        G1_YOUNG_GENERATION("G1 Young Generation"),

        // old
        MARK_SWEEP_COMPACT("MarkSweepCompact"),
        PS_MARK_SWEEP("PS MarkSweep"),
        CONCURRENT_MARK_SWEEP("ConcurrentMarkSweep"),
        G1_MIXED_GENERATION("G1 Mixed Generation"),

        UNKNOWN("unknown");

        private final String value;

        GarbageCollectorEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private List<String> gcNames() {
        return Stream.of(this.young.value, this.old.value)
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    public Function<String, Boolean> getOptCondition() {
        return optCondition;
    }

    public static GarbageCollector matchGarbageCollector(List<String> gcNames) {
        List<String> sortedGcNames = gcNames.stream().sorted(String::compareTo).collect(Collectors.toList());
        for (GarbageCollector collector : GarbageCollector.values()) {
            if (collector.gcNames().equals(sortedGcNames)) {
                return collector;
            }
        }
        boolean g1 = gcNames.stream().anyMatch(name -> name.startsWith("G1"));
        if (g1) {
            return GarbageCollector.G1_GARBAGE_COLLECTOR;
        }
        return GarbageCollector.UNKNOWN;
    }

    public static GarbageCollector matchGarbageCollectorByJvmOpt(String appDefaultJvm) {
        for (GarbageCollector collector : GarbageCollector.values()) {
            if (collector.getOptCondition().apply(appDefaultJvm)) {
                return collector;
            }
        }
        return GarbageCollector.UNKNOWN;
    }
}