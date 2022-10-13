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
import com.alipay.autotuneservice.model.dto.ExpertEvalType;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version ExpertKnowledge.java, v 0.1 2022年04月26日 16:17 dutianze
 */
@Data
public class ExpertKnowledge {

    /**
     * id
     */
    private Integer                     id;

    /**
     * gc
     */
    private GarbageCollector            garbageCollector;

    /**
     * jdkVersion
     */
    private String                      jdkVersion;

    /**
     * 问题描述
     */
    private String                      desc;

    /**
     * problem set
     */
    private Set<ProblemType>            problemTypes;

    /**
     * 调整方案
     */
    private List<ExpertJvmPlan>         expertJvmPlans;

    /**
     * 创建人
     */
    private String                      createdBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.CUSTOM_FORMATTER)
    private LocalDateTime               createdTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtils.CUSTOM_FORMATTER)
    private LocalDateTime               updatedTime;

    private static List<ExpertStrategy> expertStrategies = ImmutableList
                                                             .of(new ChangeGCStrategy(),
                                                                 new CostStrategy(),
                                                                 new PrefStrategy());

    public static ExpertStrategy matchStrategy(final List<ProblemType> problemTypeList) {
        Optional<ExpertStrategy> expertStrategy = expertStrategies.stream().filter(e -> e.use(problemTypeList)).findFirst();
        if (expertStrategy.isPresent()) {
            return expertStrategy.get();
        }
        throw new ServerException(ResultCode.EXPERT_KNOWLEDGE_NOT_FOUND);
    }
}