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
package com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend;

import com.alipay.autotuneservice.service.algorithmlab.JvmOptionMeta;
import com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongshu
 * @version TuneLab.java, v 0.1 2022年10月31日 17:57 hongshu
 */
@Slf4j
@Data
public class TuneTrendExpert {

    // 通用经验
    public static Map<ProblemMetricEnum,List<ExpertJvmPlan>> COMMON_TUNE_TREND =
            new HashMap<ProblemMetricEnum,List<ExpertJvmPlan>>(){{
                put(ProblemMetricEnum.Xmx_Xms_DIFF, Arrays.asList(new ExpertJvmPlan("-Xms", ExpertEvalType.DELETE),
                        new ExpertJvmPlan("-Xms", ExpertEvalType.APPEND)));
                put(ProblemMetricEnum.MetaSpaceSize_MaxMetaSpaceSize_DIFF, Arrays.asList(new ExpertJvmPlan("-XX:MetaspaceSize", ExpertEvalType.DELETE),
                        new ExpertJvmPlan("-XX:MetaspaceSize", ExpertEvalType.APPEND)));
                put(ProblemMetricEnum.MaxNewSize_NewSize_DIFF, Arrays.asList(new ExpertJvmPlan("-XX:NewSize", ExpertEvalType.DELETE),
                        new ExpertJvmPlan("-XX:NewSize", ExpertEvalType.APPEND)));
                put(ProblemMetricEnum.YGC_TIME_GREAT, Arrays.asList(new ExpertJvmPlan("-XX:ParallelGCThreads", ExpertEvalType.UP),
                        new ExpertJvmPlan("-Xms", ExpertEvalType.DOWN),
                        new ExpertJvmPlan("-Xmx", ExpertEvalType.DOWN),
                        new ExpertJvmPlan("-XX:MaxNewSize", ExpertEvalType.DOWN)));
                put(ProblemMetricEnum.FGC_COUNT_HIGH, Arrays.asList(
                        new ExpertJvmPlan("-Xms", ExpertEvalType.UP),
                        new ExpertJvmPlan("-Xmx", ExpertEvalType.UP)));
                put(ProblemMetricEnum.FGC_TIME_GREAT, Arrays.asList(
                        new ExpertJvmPlan("-Xms", ExpertEvalType.DOWN),
                        new ExpertJvmPlan("-Xmx", ExpertEvalType.DOWN)));
                put(ProblemMetricEnum.META_UTIL_HIGH, Arrays.asList(
                        new ExpertJvmPlan("-XX:MetaspaceSize", ExpertEvalType.UP),
                        new ExpertJvmPlan("-XX:MaxMetaspaceSize", ExpertEvalType.UP)));
                put(ProblemMetricEnum.META_UTIL_LOW, Arrays.asList(
                        new ExpertJvmPlan("-XX:MetaspaceSize", ExpertEvalType.DOWN),
                        new ExpertJvmPlan("-XX:MaxMetaspaceSize", ExpertEvalType.DOWN)));
                put(ProblemMetricEnum.OLD_UTIL_HIGH, Arrays.asList(
                        new ExpertJvmPlan("-Xms", ExpertEvalType.UP),
                        new ExpertJvmPlan("-Xmx", ExpertEvalType.UP)));
                put(ProblemMetricEnum.OLD_UTIL_LOW, Arrays.asList(
                        new ExpertJvmPlan("-Xms", ExpertEvalType.DOWN),
                        new ExpertJvmPlan("-Xmx", ExpertEvalType.DOWN)));
                put(ProblemMetricEnum.HEAP_UTIL_LOW, Arrays.asList(
                        new ExpertJvmPlan("-Xms", ExpertEvalType.DOWN),
                        new ExpertJvmPlan("-Xmx", ExpertEvalType.DOWN)));
                put(ProblemMetricEnum.ReservedCodeCacheSize_SMALL, Collections.singletonList(
                        new ExpertJvmPlan("-XX:ReservedCodeCacheSize", ExpertEvalType.UP)));
            }};
    // g1
    public static Map<ProblemMetricEnum,List<ExpertJvmPlan>> G1_TUNE_TREND  = new HashMap<ProblemMetricEnum,List<ExpertJvmPlan>>(){{
        put(ProblemMetricEnum.FGC_COUNT_HIGH, Collections.singletonList(
                new ExpertJvmPlan("-XX:MaxNewSize", ExpertEvalType.DOWN)));
        put(ProblemMetricEnum.FGC_TIME_GREAT, Arrays.asList(
                new ExpertJvmPlan("-XX:InitiatingHeapOccupancyPercent", ExpertEvalType.DOWN),
                new ExpertJvmPlan("-XX:G1MixedGCCountTarget", ExpertEvalType.DOWN)));
        put(ProblemMetricEnum.OLD_UTIL_HIGH, Collections.singletonList(
                new ExpertJvmPlan("-XX:MaxNewSize", ExpertEvalType.DOWN)));
        List<ExpertJvmPlan> expertJvmPlans = new ArrayList <> ();
        JvmOptionMeta.G1_OPTIONS.forEach(opt->{
            expertJvmPlans.add(new ExpertJvmPlan(opt, ExpertEvalType.DELETE));
        });
        JvmOptionMeta.CMS_OPTIONS.forEach(opt->{
            expertJvmPlans.add(new ExpertJvmPlan(opt, ExpertEvalType.APPEND));
        });
        put(ProblemMetricEnum.GC_TYPE_UNREASONABLE,expertJvmPlans);
    }};

    // cms
    public static Map<ProblemMetricEnum,List<ExpertJvmPlan>> CMS_TUNE_TREND =
            new HashMap<ProblemMetricEnum,List<ExpertJvmPlan>>(){{
                put(ProblemMetricEnum.FGC_COUNT_HIGH, Collections.singletonList(
                        new ExpertJvmPlan("-Xmn", ExpertEvalType.DOWN)));
                put(ProblemMetricEnum.FGC_TIME_GREAT, Arrays.asList(
                        new ExpertJvmPlan("-XX:CMSInitiatingOccupancyFraction", ExpertEvalType.DOWN)));
                put(ProblemMetricEnum.OLD_UTIL_HIGH, Collections.singletonList(
                        new ExpertJvmPlan("-Xmn", ExpertEvalType.DOWN)));

                List<ExpertJvmPlan> expertJvmPlans = new ArrayList <> ();
                JvmOptionMeta.CMS_OPTIONS.forEach(opt->{
                    expertJvmPlans.add(new ExpertJvmPlan(opt, ExpertEvalType.DELETE));
                });
                JvmOptionMeta.G1_OPTIONS.forEach(opt->{
                    expertJvmPlans.add(new ExpertJvmPlan(opt, ExpertEvalType.APPEND));
                });
                put(ProblemMetricEnum.GC_TYPE_UNREASONABLE,expertJvmPlans);
            }};
}
