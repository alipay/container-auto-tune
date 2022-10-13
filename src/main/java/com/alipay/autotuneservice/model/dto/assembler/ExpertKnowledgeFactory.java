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

import com.alipay.autotuneservice.model.dto.ExpertKnowledgeCommand;
import com.alipay.autotuneservice.model.expert.ExpertKnowledge;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.UserUtil;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author dutianze
 * @version ExpertKnowledgeFactory.java, v 0.1 2022年04月29日 11:39 dutianze
 */
public class ExpertKnowledgeFactory {

    public static ExpertKnowledge newExpertKnowledge(ExpertKnowledgeCommand cmd) {
        ExpertKnowledge expertKnowledge = new ExpertKnowledge();
        expertKnowledge.setGarbageCollector(cmd.getGarbageCollector());
        expertKnowledge.setJdkVersion(cmd.getJdkVersion());
        expertKnowledge.setDesc(cmd.getDesc());
        expertKnowledge.setProblemTypes(cmd.getProblemTypes());
        expertKnowledge.setExpertJvmPlans(cmd.getExpertJvmPlans());
        expertKnowledge.setCreatedBy(ObjectUtils.defaultIfNull(UserUtil.getUserName(), "未知"));
        expertKnowledge.setCreatedTime(DateUtils.now());
        expertKnowledge.setUpdatedTime(DateUtils.now());
        return expertKnowledge;
    }
}