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
import com.alipay.autotuneservice.model.dto.ExpertEvalResult;
import com.alipay.autotuneservice.model.dto.ExpertEvalResultType;
import com.alipay.autotuneservice.model.dto.assembler.ExpertEvalItemAssembler;
import com.alipay.autotuneservice.model.exception.ServerException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version PrefStrategy.java, v 0.1 2022年06月20日 19:28 dutianze
 */
public class PrefStrategy implements ExpertStrategy {

    private final ExpertEvalItemAssembler assembler = new ExpertEvalItemAssembler();

    @Override
    public ExpertEvalResult match(AppInfo appInfo,
                                  GarbageCollector garbageCollector,
                                  List<ProblemType> problemTypeList,
                                  List<ExpertKnowledge> expertKnowledgeLists) {
        Map<Double, List<ExpertKnowledge>> rateRank = expertKnowledgeLists
                .stream()
                .filter(e -> {
                    if (garbageCollector.equals(GarbageCollector.UNKNOWN)) {
                        return true;
                    }
                    return e.getGarbageCollector().equals(garbageCollector);
                })
                .collect(Collectors.groupingBy(
                        e -> e.getProblemTypes()
                                .stream()
                                .filter(problemTypeList::contains)
                                .mapToDouble(ProblemType::getWeight)
                                .sum()));
        Optional<Double> maxOptional = rateRank.keySet().stream().max(Double::compareTo);
        ExpertKnowledge expertKnowledge = maxOptional.map(aDouble -> rateRank.get(aDouble).get(0)).orElse(null);
        if (expertKnowledge != null) {
            ExpertEvalResult result = ExpertEvalResult.of(ExpertEvalResultType.PERF, assembler.apply(expertKnowledge, appInfo));
            if(CollectionUtils.isEmpty(result.getEvalList())){
                throw new ServerException(ResultCode.EXPERT_KNOWLEDGE_NOT_RECOMMEND);
            }
            return result;
        }
        throw new ServerException(ResultCode.EXPERT_KNOWLEDGE_NOT_FOUND);
    }

    @Override
    public boolean use(List<ProblemType> problemTypeList) {
        return problemTypeList.stream().noneMatch(e ->
                e.equals(ProblemType.GC_TYPE) || e.equals(ProblemType.HEAP_META_IDLE) || e.equals(ProblemType.HEAP_OLD_IDLE));
    }
}