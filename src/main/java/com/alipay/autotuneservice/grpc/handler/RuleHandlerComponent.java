/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.grpc.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.CommandInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.CommandInfoRecord;
import com.alipay.autotuneservice.grpc.GrpcCommon;
import com.alipay.autotuneservice.model.common.CommandInfo;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.util.AgentConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version RuleHandlerComponent.java, v 0.1 2022年02月22日 20:37 dutianze
 */
@Slf4j
@Component
public class RuleHandlerComponent {

    @Autowired
    private CommandInfoRepository commandInfoRepository;

    public List<ActionParam> checkRuleFlag(GrpcCommon grpcCommon) {
        //根据unionCode，查询指令
        String unionCode = grpcCommon.getUnionCode();
        if (StringUtils.isEmpty(unionCode)) {
            return Lists.newArrayList();
        }
        List<CommandInfo> commandInfos = commandInfoRepository.getByUnionCode(unionCode, CommandStatus.INIT);
        if (CollectionUtils.isEmpty(commandInfos)) {
            return Lists.newArrayList();
        }
        return commandInfos.stream().map(commandInfo -> {
                    ActionParam actionParam = new ActionParam(RuleAction.valueOfType(commandInfo.getRuleAction()));
                    if (StringUtils.isNotEmpty(commandInfo.getContext())) {
                        actionParam.setParams(JSON.parseObject(commandInfo.getContext(),
                                new TypeReference<Map<String, String>>() {}));
                    }
                    actionParam.setSessionId(commandInfo.getSessionId());
                    actionParam.setId(commandInfo.getId());
                    return actionParam;
                })
                .filter(actionParam -> actionParam.getRuleAction() != RuleAction.UNKNOWN)
                .filter(actionParam -> {
                    //更新状态
                    return updateStatus(actionParam.getId(), CommandStatus.PENDING);
                })
                .collect(Collectors.toList());
    }

    private boolean updateStatus(long id, CommandStatus commandStatus) {
        return commandInfoRepository.uStatus(id, commandStatus);
    }

    public boolean updateResult(String sessionId, Map<String, String> resultObj, CommandStatus commandStatus) {
        return commandInfoRepository.uResult(sessionId, resultObj, commandStatus);
    }

    public boolean updateBySessionId(String sessionId, Map<String, String> tagsMap) {
        CommandInfoRecord commandInfoRecord = commandInfoRepository.getBySessionId(sessionId);
        Map<String, String> tmp = Maps.newHashMap();
        if (commandInfoRecord != null) {
            String context = commandInfoRecord.getContext();
            if (StringUtils.isNotEmpty(context)) {
                tmp.putAll(JSON.parseObject(context, new TypeReference<Map<String, String>>() {}));
            }
        }
        tmp.putAll(tagsMap);
        return commandInfoRepository.uStatusBySessionId(sessionId, CommandStatus.valueOf(tmp.get(AgentConstant.STATUS)), tmp);
    }
}