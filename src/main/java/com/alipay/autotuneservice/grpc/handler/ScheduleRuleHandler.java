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

import com.alipay.autotuneservice.model.rule.RuleModel;
import com.auto.tune.client.MetricsGrpcRequest;
import org.springframework.scheduling.support.CronExpression;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author dutianze
 * @version ScheduleRuleProcessor.java, v 0.1 2022年02月15日 13:53 dutianze
 */
public class ScheduleRuleHandler implements RuleHandler {

    private final RuleModel ruleModel;

    public ScheduleRuleHandler(RuleModel ruleModel) {
        this.ruleModel = ruleModel;
    }

    @Override
    public void process(MetricsGrpcRequest request, RuleProcessResponse response,
                        RuleChainHandler chainHandler) {
        String cron = ruleModel.getRuleParam().getCron();
        Date date = new Date();
        Date nextTriggerDate = getNextTriggerTime(cron, date);
        if (date.getTime() < nextTriggerDate.getTime()) {
            return;
        }
        response.addAction(ruleModel);
        chainHandler.process(request, response, chainHandler);
    }

    public Date getNextTriggerTime(String cron, Date date) {
        CronExpression expression = CronExpression.parse(cron);
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        ZonedDateTime next = expression.next(dateTime);
        return next == null ? null : Date.from(next.toInstant());
    }
}