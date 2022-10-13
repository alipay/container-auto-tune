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
package com.alipay.autotuneservice.model.dto;

import com.alipay.autotuneservice.model.expert.ExpertJvmPlan;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.model.expert.ProblemType;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author dutianze
 * @version ExpertKnowledgeCommand.java, v 0.1 2022年04月29日 11:23 dutianze
 */
@Data
public class ExpertKnowledgeCommand {

    /**
     * gc
     */
    private GarbageCollector    garbageCollector;

    /**
     * jdkVersion
     */
    private String              jdkVersion;

    /**
     * 问题描述
     */
    private String              desc;

    /**
     * problem set
     */
    private Set<ProblemType>    problemTypes;

    /**
     * 调整方案
     */
    private List<ExpertJvmPlan> expertJvmPlans;
}