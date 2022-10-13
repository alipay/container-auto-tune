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

import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.dto.ExpertEvalItem;
import com.alipay.autotuneservice.model.dto.ExpertEvalResult;
import com.alipay.autotuneservice.model.dto.ExpertEvalResultType;
import com.alipay.autotuneservice.model.dto.ExpertEvalType;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version ChangeGCStrategy.java, v 0.1 2022年06月20日 19:32 dutianze
 */
public class ChangeGCStrategy implements ExpertStrategy {

    private static final Set<String> G1_OPTIONS  = Sets.newHashSet("-XX:G1HeapRegionSize",
                                                     "-XX:MaxGCPauseMillis", "-XX:+UseG1GC",
                                                     "-XX:G1NewSizePercent",
                                                     "-XX:G1MaxNewSizePercent",
                                                     "-XX:InitiatingHeapOccupancyPercent",
                                                     "-XX:G1MixedGCLiveThresholdPercent",
                                                     "-XX:G1HeapWastePercent",
                                                     "-XX:G1MixedGCCountTarget",
                                                     "-XX:G1OldCSetRegionThresholdPercent",
                                                     "-XX:G1ReservePercent",
                                                     "-XX:G1AdaptiveIHOPNumInitialSamples",
                                                     "-XX:+G1UseAdaptiveIHOP");

    private static final Set<String> CMS_OPTIONS = Sets.newHashSet("-XX:+CMSClassUnloadingEnabled",
                                                     "-XX:CMSExpAvgFactor",
                                                     "-XX:+UseConcMarkSweepGC", "-XX:+UseParNewGC",
                                                     "-XX:CMSInitiatingOccupancyFraction",
                                                     "-XX:CMSMaxAbortablePrecleanTime",
                                                     "-XX:+UseCMSInitiatingOccupancyOnly",
                                                     "-XX:+CMSScavengeBeforeRemark",
                                                     "-XX:CMSTriggerRatio");

    @Override
    public ExpertEvalResult match(AppInfo appInfo, GarbageCollector garbageCollector,
                                  List<ProblemType> problemTypeList,
                                  List<ExpertKnowledge> expertKnowledgeLists) {
        List<ExpertEvalItem> expertEvalItems = null;
        if (GarbageCollector.CMS_GARBAGE_COLLECTOR.equals(garbageCollector)) {
            expertEvalItems = g1Gc();
            List<ExpertEvalItem> invalidOptions = deleteInvalidOptions(appInfo.getAppDefaultJvm(),
                CMS_OPTIONS);
            expertEvalItems.addAll(invalidOptions);
        }
        if (GarbageCollector.G1_GARBAGE_COLLECTOR.equals(garbageCollector)) {
            expertEvalItems = cmsGc();
            List<ExpertEvalItem> invalidOptions = deleteInvalidOptions(appInfo.getAppDefaultJvm(),
                G1_OPTIONS);
            expertEvalItems.addAll(invalidOptions);
        }
        if (CollectionUtils.isEmpty(expertEvalItems)) {
            throw new ServerException(ResultCode.EXPERT_KNOWLEDGE_NOT_FOUND);
        }
        return ExpertEvalResult.of(ExpertEvalResultType.PERF, expertEvalItems);
    }

    @Override
    public boolean use(List<ProblemType> problemTypeList) {
        return problemTypeList.stream().anyMatch(ProblemType.GC_TYPE::equals);
    }

    public static List<ExpertEvalItem> g1Gc() {
        List<ExpertEvalItem> result = new ArrayList<>();
        ExpertEvalItem item = new ExpertEvalItem(null, "no-value", "-XX:+UseG1GC",
            ExpertEvalType.APPEND);
        result.add(item);
        return result;
    }

    public static List<ExpertEvalItem> cmsGc() {
        List<ExpertEvalItem> result = new ArrayList<>();
        ExpertEvalItem item = new ExpertEvalItem(null, "no-value", "-XX:+UseConcMarkSweepGC",
            ExpertEvalType.APPEND);
        result.add(item);
        return result;
    }

    private static List<ExpertEvalItem> deleteInvalidOptions(String jvmDefault,
                                                             Set<String> invalidJvmOptions) {
        List<ExpertEvalItem> result = new ArrayList<>();
        Set<String> params = Arrays.stream(ArrayUtils.nullToEmpty(jvmDefault.split(" "))).collect(
            Collectors.toSet());
        for (String param : params) {
            for (String invalidJvmOption : invalidJvmOptions) {
                if (param.contains(invalidJvmOption)) {
                    result.add(new ExpertEvalItem(null, null, invalidJvmOption,
                        ExpertEvalType.DELETE));
                    break;
                }
            }
        }
        return result;
    }
}