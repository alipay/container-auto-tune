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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.ExpertKnowledgeRepository;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.dto.ExpertAnalyzeCommand;
import com.alipay.autotuneservice.model.dto.ExpertEvalResult;
import com.alipay.autotuneservice.model.dto.ExpertKnowledgeCommand;
import com.alipay.autotuneservice.model.dto.assembler.ExpertKnowledgeFactory;
import com.alipay.autotuneservice.model.exception.ResourceNotFoundException;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.model.expert.ExpertKnowledge;
import com.alipay.autotuneservice.model.expert.ExpertStrategy;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dutianze
 * @version ExpertServiceImpl.java, v 0.1 2022年04月26日 17:09 dutianze
 */
@Service
public class ExpertServiceImpl implements ExpertService {

    @Autowired
    private ExpertKnowledgeRepository expertKnowledgeRepository;
    @Autowired
    private AppInfoRepository         appInfoRepository;

    @Override
    public ExpertEvalResult eval(ExpertAnalyzeCommand cmd) {
        AppInfo appInfo = appInfoRepository.findById(cmd.getAppId());
        if (appInfo == null) {
            throw new ResourceNotFoundException(ResultCode.APP_NOT_FOUND);
        }
        GarbageCollector garbageCollector = cmd.findGarbageCollector(appInfo);
        ExpertStrategy expertStrategy = ExpertKnowledge.matchStrategy(cmd.getProblemTypeList());
        List<ExpertKnowledge> expertKnowledgeLists = expertKnowledgeRepository.loadData();
        ExpertEvalResult expertEvalResult = expertStrategy.match(appInfo, garbageCollector,
            cmd.getProblemTypeList(), expertKnowledgeLists);
        if (expertEvalResult == null) {
            throw new ServerException(ResultCode.EXPERT_KNOWLEDGE_NOT_FOUND);
        }
        return expertEvalResult;
    }

    @Override
    public ExpertKnowledge record(ExpertKnowledgeCommand cmd) {
        ExpertKnowledge expertKnowledge = ExpertKnowledgeFactory.newExpertKnowledge(cmd);
        return expertKnowledgeRepository.save(expertKnowledge);
    }
}