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
package com.alipay.autotuneservice.model.dto.assembler;

import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.dto.ExpertEvalItem;
import com.alipay.autotuneservice.model.dto.ExpertEvalType;
import com.alipay.autotuneservice.model.expert.ExpertJvmPlan;
import com.alipay.autotuneservice.model.expert.ExpertKnowledge;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author dutianze
 * @version ExpertEvalItemAssembler.java, v 0.1 2022年04月28日 14:32 dutianze
 */
@Component
public class ExpertEvalItemAssembler implements BiFunction<ExpertKnowledge, AppInfo, List<ExpertEvalItem>> {

    @Override
    public List<ExpertEvalItem> apply(@Nonnull ExpertKnowledge expertKnowledge, @Nonnull AppInfo appInfo) {
        return apply(appInfo.getAppDefaultJvm(), expertKnowledge.getExpertJvmPlans());
    }

    public static List<ExpertEvalItem> apply(String appDefaultJvm, List<ExpertJvmPlan> expertJvmPlans) {
        //todo 注意是否有专家经验
        String[] jvmOpts = ArrayUtils.nullToEmpty(appDefaultJvm.split(" "));
        // build
        List<ExpertEvalItem> expertEvalItems = new ArrayList<>();
        for (ExpertJvmPlan tunePlan : expertJvmPlans) {
            String target = tunePlan.extractTarget(jvmOpts);
            String value = tunePlan.extractTargetValue(target);
            ExpertEvalItem expertEvalItem = new ExpertEvalItem(target, value, tunePlan.getJvmOpts(), tunePlan.getExpertEvalType());
            if (ExpertEvalType.isUpdate(tunePlan.getExpertEvalType()) && StringUtils.isEmpty(value)) {
                continue;
            }
            expertEvalItems.add(expertEvalItem);
        }
        return expertEvalItems;
    }
}