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
package com.alipay.autotuneservice.service.algorithmlab.template;

import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.service.algorithmlab.TuneParamModel;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hongshu
 * @version TuneLab.java, v 0.1 2022年11月17日 21:15 hongshu
 */
public class TemplateEssentialParams {

    // 必须填充参数 用于调参模版初始化参数合并
    public static final Map<GarbageCollector,Set<String>> TEMP_ESSENTIA_PARAMS = new HashMap<GarbageCollector,Set<String>>(){{
        put(GarbageCollector.G1_GARBAGE_COLLECTOR,Sets.newHashSet("-XX:+UseG1GC",
                "-verbose:gc", "-XX:+PrintGCDetails","-XX:+PrintGCDateStamps", "-XX:+PrintGCTimeStamps",
                "-Xmx", "-Xms", "-XX:InitiatingHeapOccupancyPercent"));
        put(GarbageCollector.CMS_GARBAGE_COLLECTOR,Sets.newHashSet("-XX:+UseConcMarkSweepGC",
                "-XX:+UseParNewGC","-XX:+CMSClassUnloadingEnabled",
                "-verbose:gc", "-XX:+PrintGCDetails","-XX:+PrintGCDateStamps", "-XX:+PrintGCTimeStamps",
                "-Xmx", "-Xms", "-XX:InitiatingHeapOccupancyPercent"));
    }};

    // 参数默认值
    private static final Map<String, TuneParamModel>  COMMON_BASE_VALUE = new HashMap<String, TuneParamModel>(){{
        put("-verbose:gc",TuneParamModel.builder().paramName("-verbose:gc").paramVal("").operator("").build());
        put("-XX:+PrintGCDetails",TuneParamModel.builder().paramName("-XX:+PrintGCDetails").paramVal("").operator("").build());
        put("-XX:+PrintGCDateStamps",TuneParamModel.builder().paramName("-XX:+PrintGCDateStamps").paramVal("").operator("").build());
        put("-XX:+PrintGCTimeStamps",TuneParamModel.builder().paramName("-XX:+PrintGCTimeStamps").paramVal("").operator("").build());
        put("-XX:InitiatingHeapOccupancyPercent",TuneParamModel.builder().paramName("-XX:InitiatingHeapOccupancyPercent").paramVal("65").operator("=").build());
    }};

    private static final Map<String, TuneParamModel>  CMS_BASE_VALUE = new HashMap<String, TuneParamModel>(){{
        putAll(COMMON_BASE_VALUE);
        put("-XX:+UseConcMarkSweepGC",TuneParamModel.builder().paramName("-XX:+UseConcMarkSweepGC").paramVal("").operator("").build());
        put("-XX:+UseParNewGC",TuneParamModel.builder().paramName("-XX:+UseParNewGC").paramVal("").operator("").build());
        put("-XX:+CMSClassUnloadingEnabled",TuneParamModel.builder().paramName("-XX:+CMSClassUnloadingEnabled").paramVal("").operator("").build());
    }};

    private static final Map<String, TuneParamModel>  G1_BASE_VALUE = new HashMap<String, TuneParamModel>(){{
        putAll(COMMON_BASE_VALUE);
        put("-XX:+UseG1GC",TuneParamModel.builder().paramName("-XX:+UseG1GC").paramVal("").operator("").build());
    }};


    public static final Map<GarbageCollector,Map<String, TuneParamModel>> TEMP_ESSENTIA_VALUES_BASE = new HashMap<GarbageCollector,Map<String, TuneParamModel>>(){{
        put(GarbageCollector.CMS_GARBAGE_COLLECTOR,CMS_BASE_VALUE);
        put(GarbageCollector.G1_GARBAGE_COLLECTOR,G1_BASE_VALUE);
    }};

    // 个性化数据默认值  内存规格(g) + "_" + 参数
    // CPU规格
    public static final List<String> CPU_SPECS = Arrays.asList("2","4","6","8","16","32");
    // 内存规格
    public static final List<Integer> MEM_SPECS = Arrays.asList(4,8,12,16,32);
    public static final Map<GarbageCollector,Map<String, TuneParamModel>> TEMP_ESSENTIA_VALUES = new HashMap<GarbageCollector,Map<String, TuneParamModel>>(){{
        put(GarbageCollector.CMS_GARBAGE_COLLECTOR,new HashMap<String, TuneParamModel>(){{
            put("4_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("2g").operator("").build());
            put("4_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("2g").operator("").build());
            put("8_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("5g").operator("").build());
            put("8_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("5g").operator("").build());
            put("12_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("8g").operator("").build());
            put("12_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("8g").operator("").build());
            put("16_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("12g").operator("").build());
            put("16_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("12g").operator("").build());
            put("32_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("24g").operator("").build());
            put("32_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("24g").operator("").build());
        }});
        put(GarbageCollector.G1_GARBAGE_COLLECTOR,new HashMap<String, TuneParamModel>(){{
            put("4_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("2g").operator("").build());
            put("4_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("2g").operator("").build());
            put("8_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("5g").operator("").build());
            put("8_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("5g").operator("").build());
            put("12_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("8g").operator("").build());
            put("12_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("8g").operator("").build());
            put("16_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("12g").operator("").build());
            put("16_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("12g").operator("").build());
            put("32_-Xmx",TuneParamModel.builder().paramName("-Xmx").paramVal("24g").operator("").build());
            put("32_-Xms",TuneParamModel.builder().paramName("-Xms").paramVal("24g").operator("").build());
        }});
    }};
}
