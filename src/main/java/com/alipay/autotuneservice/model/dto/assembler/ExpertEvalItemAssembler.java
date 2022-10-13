/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
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