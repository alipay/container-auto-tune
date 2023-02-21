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

import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.model.common.HealthCheckEnum.*;
import static com.alipay.autotuneservice.model.expert.GarbageCollector.*;

/**
 * @author huoyuqi
 * @version HealthCheckEnum.java, v 0.1 2022年04月28日 11:17 下午 huoyuqi
 */
public enum RiskStatisticProEnum {

    G001(G1_GARBAGE_COLLECTOR, FGC_COUNT, "G1 A FGC occurred in the G1 collector","1", true, "G001==>G1 A FGC occurred in the G1 collector==>fgc_count_count==>1==>point==>FGC_COUNT"),
    G002(G1_GARBAGE_COLLECTOR, OLD_UTIL, "There is a risk of sudden increase in old area","0.3", true, "G002==>There is a risk of sudden increase in old area==>increase_rate==>30==>section==>OLD_UTIL"),
    G003(G1_GARBAGE_COLLECTOR, HEAP_MEMORY, "Heap mem usage is high","0.75", true, "G003==>Heap mem usage is high==>heap_memory==>75==>point==>HEAP_MEMORY"),
    G004(G1_GARBAGE_COLLECTOR, FGC_TIME, "FGC takes a long time","3", true, "G004==>FGC takes a long time==>fgc_time==>3==>point==>FGC_TIME"),
    G005(G1_GARBAGE_COLLECTOR, HEAP_META, "Matespace seems idle","0.45", false, "G005==>Matespace seems idle==>meta_util_mean==>45==>cost==>HEAP_META_IDLE"),
    G006(G1_GARBAGE_COLLECTOR, HEAP_OLD, "Heap seems idle","NAN", true, "G006==>Heap seems idle==>fgc_count==>3==>cost==>HEAP_OLD_IDLE"),
    C001(CMS_GARBAGE_COLLECTOR, FGC_COUNT, "Full gc frequency anomaly","8", true, "C001==>Full gc frequency anomaly==>fgc_count_p99==>8==>point==>FGC_COUNT"),
    C002(CMS_GARBAGE_COLLECTOR, OLD_UTIL, "There is a risk of sudden increase in old area","0.3", true, "C002==>There is a risk of sudden increase in old area==>increase_rate==>30==>section==>OLD_UTIL"),
    C003(CMS_GARBAGE_COLLECTOR, HEAP_MEMORY, "Heap mem usage is high","0.75", true, "C003==>Heap mem usage is high==>heap_memory==>75==>point==>HEAP_MEMORY"),
    C004(CMS_GARBAGE_COLLECTOR, FGC_TIME, "FGC takes a long time","3", true, "C004==>FGC takes a long time==>fgc_time==>3==>point==>FGC_TIME"),
    C005(CMS_GARBAGE_COLLECTOR, HEAP_META, "Matespace seems idle","0.45", true, "C005==>Matespace seems idle==>meta_util_mean==>45==>cost==>HEAP_META_IDLE"),
    C006(CMS_GARBAGE_COLLECTOR, HEAP_OLD, "Heap seems idle","NAN", true, "C006==>Heap seems idle==>fgc_count==>3==>cost==>HEAP_OLD_IDLE"),

    /**
     * 未知类型枚举
     */
    UNKNOWN(G1_GARBAGE_COLLECTOR, HealthCheckEnum.UNKNOWN, "unknown type","1", true, "unknown type==>unknown type");

    private final GarbageCollector garbageCollector;
    private final HealthCheckEnum healthCheckEnum;
    private final String desc;
    private final String threshold;

    public boolean isCompType() {
        return compType;
    }

    // true: check result > threshold  ==>  exception
    // false: check result < threshold ==>  exception
    private final boolean compType;
    private final String suggestion;

    public GarbageCollector getGarbageCollector() {
        return garbageCollector;
    }

    public HealthCheckEnum getHealthCheckEnum() {
        return healthCheckEnum;
    }

    public String getDesc() {
        return desc;
    }

    public String getThreshold() {
        return threshold;
    }

    public String getSuggestion() {
        return suggestion;
    }

    RiskStatisticProEnum(GarbageCollector garbageCollector, HealthCheckEnum healthCheckEnum,
                         String desc, String threshold, boolean compType, String suggestion) {
        this.garbageCollector = garbageCollector;
        this.healthCheckEnum = healthCheckEnum;
        this.desc = desc;
        this.threshold = threshold;
        this.compType = compType;
        this.suggestion = suggestion;
    }

    private static Map<String, RiskStatisticProEnum> cTypeMap = Maps.newHashMap();

    public static RiskStatisticProEnum enumFromCollectorAndType(GarbageCollector garbageCollector, HealthCheckEnum healthCheckEnum) {
        if(MapUtils.isEmpty(cTypeMap)){
            cTypeMap = Arrays.stream(RiskStatisticProEnum.values())
                    .filter(r -> r != RiskStatisticProEnum.UNKNOWN)
                    .collect(Collectors.toMap(k->k.getGarbageCollector().name()+"&"+k.getHealthCheckEnum().name(),v->v));
        }
        return cTypeMap.get(garbageCollector.name()+"&"+healthCheckEnum.name());
    }
}