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
import com.alipay.autotuneservice.model.dto.assembler.ExpertEvalItemAssembler;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dutianze
 * @version CostStrategy.java, v 0.1 2022年06月20日 19:28 dutianze
 */
public class CostStrategy implements ExpertStrategy {

    @Override
    public ExpertEvalResult match(AppInfo appInfo, GarbageCollector garbageCollector,
                                  List<ProblemType> problemTypeList,
                                  List<ExpertKnowledge> expertKnowledgeLists) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(problemTypeList));
        Set<ExpertEvalItem> expertEvalItems = new HashSet<>();
        for (ProblemType e : problemTypeList) {
            switch (e) {
                case HEAP_META_IDLE: {
                    expertEvalItems.addAll(ExpertEvalItemAssembler.apply(
                        appInfo.getAppDefaultJvm(), this.metaPlans()));
                    break;
                }
                case HEAP_OLD_IDLE:
                    expertEvalItems.addAll(ExpertEvalItemAssembler.apply(
                        appInfo.getAppDefaultJvm(), this.heapOldPlans()));
                    break;
                default:
                    // ignore
            }
        }
        if (CollectionUtils.isEmpty(expertEvalItems)) {
            throw new ServerException(ResultCode.EXPERT_KNOWLEDGE_NOT_FOUND);
        }
        return ExpertEvalResult.of(ExpertEvalResultType.COST, new ArrayList<>(expertEvalItems));
    }

    @Override
    public boolean use(List<ProblemType> problemTypeList) {
        return problemTypeList.stream().anyMatch(e -> e.equals(ProblemType.HEAP_META_IDLE) || e.equals(ProblemType.HEAP_OLD_IDLE));
    }

    private List<ExpertJvmPlan> metaPlans() {
        List<ExpertJvmPlan> plans = new ArrayList<>();
        plans.add(new ExpertJvmPlan("-XX:MaxMetaspaceSize", ExpertEvalType.DOWN));
        plans.add(new ExpertJvmPlan("-XX:MetaspaceSize", ExpertEvalType.DOWN));
        return plans;
    }

    private List<ExpertJvmPlan> heapOldPlans() {
        List<ExpertJvmPlan> plans = new ArrayList<>();
        plans.add(new ExpertJvmPlan("-Xmx", ExpertEvalType.DOWN));
        plans.add(new ExpertJvmPlan("-Xms", ExpertEvalType.DOWN));
        return plans;
    }
}