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
package com.alipay.autotuneservice.service.algorithmlab;

import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JvmOptionMeta {

    public static final Set<String> G1_OPTIONS = Sets.newHashSet("-XX:G1HeapRegionSize", "-XX:MaxGCPauseMillis", "-XX:+UseG1GC",
            "-XX:G1NewSizePercent", "-XX:G1MaxNewSizePercent", "-XX:InitiatingHeapOccupancyPercent",
            "-XX:G1MixedGCLiveThresholdPercent", "-XX:G1HeapWastePercent", "-XX:G1MixedGCCountTarget",
            "-XX:G1OldCSetRegionThresholdPercent",
            "-XX:G1ReservePercent", "-XX:G1AdaptiveIHOPNumInitialSamples", "-XX:+G1UseAdaptiveIHOP");

    public static final Set<String> CMS_OPTIONS = Sets.newHashSet("-XX:+CMSClassUnloadingEnabled", "-XX:CMSExpAvgFactor",
            "-XX:+UseConcMarkSweepGC", "-XX:+UseParNewGC",
            "-XX:CMSInitiatingOccupancyFraction", "-XX:CMSMaxAbortablePrecleanTime", "-XX:+UseCMSInitiatingOccupancyOnly",
            "-XX:+CMSScavengeBeforeRemark", "-XX:CMSTriggerRatio");

    public static final Map<String, TuneParamModel> JVM_DEFAULT_VALUES = new HashMap<String, TuneParamModel>(){{
        put("-Xss",TuneParamModel.builder().paramName("-Xss").paramVal("1024k").operator("").build());
        put("-XX:InitiatingHeapOccupancyPercent",TuneParamModel.builder().paramName("-XX:InitiatingHeapOccupancyPercent").paramVal("45").operator("=").build());
        put("-XX:NewRatio",TuneParamModel.builder().paramName("-XX:NewRatio").paramVal("2").operator("=").build());
        put("-XX:MaxTenuringThreshold",TuneParamModel.builder().paramName("-XX:MaxTenuringThreshold").paramVal("15").operator("=").build());
        put("-XX:CMSInitiatingOccupancyFraction",TuneParamModel.builder().paramName("-XX:CMSInitiatingOccupancyFraction").paramVal("92").operator("=").build());
        put("-XX:MaxGCPauseMillisC",TuneParamModel.builder().paramName("-XX:MaxGCPauseMillisC").paramVal("200").operator("=").build());
        put("-XX:G1ReservePercent",TuneParamModel.builder().paramName("-XX:G1ReservePercent").paramVal("10").operator("=").build());
    }};

}
