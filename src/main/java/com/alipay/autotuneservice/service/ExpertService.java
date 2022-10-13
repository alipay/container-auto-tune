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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.model.dto.ExpertAnalyzeCommand;
import com.alipay.autotuneservice.model.dto.ExpertEvalResult;
import com.alipay.autotuneservice.model.dto.ExpertKnowledgeCommand;
import com.alipay.autotuneservice.model.expert.ExpertKnowledge;

/**
 * @author dutianze
 * @version ExpertService.java, v 0.1 2022年04月26日 17:06 dutianze
 */
public interface ExpertService {

    /**
     * 评估问题
     */
    ExpertEvalResult eval(ExpertAnalyzeCommand cmd);

    /**
     * 记录经验
     */
    ExpertKnowledge record(ExpertKnowledgeCommand cmd);
}