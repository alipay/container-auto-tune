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
package com.alipay.autotuneservice.grpc.handler;

import com.alipay.autotuneservice.grpc.GrpcCommon;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.model.rule.RuleModel;
import com.alipay.autotuneservice.model.rule.RuleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dutianze
 * @version RuleHandlerComponent.java, v 0.1 2022年02月22日 20:37 dutianze
 */
@Slf4j
@Component
public class RuleHandlerComponent {

    @Autowired
    private RedisClient         redisClient;

    private static final String RULE_CACHE_KEY_PREFIX = "autotune_rule_flag";

    public List<ActionParam> checkRuleFlag(GrpcCommon grpcCommon) {
        // accessToken + appName 相同app只有一台发送日志文件
        String cacheKey = this.buildCacheKey(grpcCommon);
        boolean allowSend = redisClient.setNx(cacheKey, "lock", 1, TimeUnit.DAYS);
        List<ActionParam> actionParams = new ArrayList<>();
        if (!allowSend) {
            return actionParams;
        }
        ActionParam actionParam = new ActionParam(RuleAction.GC_DUMP);
        actionParams.add(actionParam);
        return actionParams;
    }

    private RuleHandler build(RuleModel ruleModel) {
        RuleType ruleType = ruleModel.getRuleType();
        switch (ruleType) {
            case MANUAL_TRIGGER:
                break;
            case AUTO_TIMING:
                return new ScheduleRuleHandler(ruleModel);
            case AUTO_THRESHOLD:
                return new ThresholdRuleHandler(ruleModel);
        }
        log.error("RuleHandlerComponent build RuleHandler error, ruleModel:{}", ruleModel);
        throw new RuntimeException("RuleHandlerComponent build RuleHandler error");
    }

    private String buildCacheKey(GrpcCommon grpcCommon) {
        return String.join("_", RULE_CACHE_KEY_PREFIX, grpcCommon.getAccessToken(),
            grpcCommon.getNamespace(), grpcCommon.getAppName());
    }
}