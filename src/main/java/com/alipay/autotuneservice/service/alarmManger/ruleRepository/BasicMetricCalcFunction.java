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
package com.alipay.autotuneservice.service.alarmManger.ruleRepository;

import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmContext;
import com.alipay.autotuneservice.service.alarmManger.model.ResultModel;
import com.alipay.autotuneservice.service.alarmManger.model.RuleModel;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author huoyuqi
 * @version BasicFunction.java, v 0.1 2022年12月27日 8:36 下午 huoyuqi
 */
@Slf4j
public abstract class BasicMetricCalcFunction extends AbstractFunction {

    protected List<JvmMonitorMetricData> obtainMetrics(AlarmContext alarmContext) {
        RuleModel ruleModel = alarmContext.getRuleModel();
        Long currentTime = alarmContext.getCurrentTime();
        JvmMonitorMetricRepository jvmMetricDataRepository = alarmContext.getJvmMonitorMetricRepository();
        log.info("jvmMetricDataRepository is: {}, podName: {}, startTime: {}, currentTime: {}", jvmMetricDataRepository,
                alarmContext.getPodInfoRecord().getPodName(), currentTime - ruleModel.getTime() - 60 * 1000L, currentTime - 60 * 1000L);
        return jvmMetricDataRepository.getPodJvmMetric(alarmContext.getPodInfoRecord().getPodName(),
                currentTime - ruleModel.getTime() - 60 * 1000L, currentTime - 1000L);

    }

    protected ResultModel calcResult(RuleModel ruleModel, double lastData, String expression) {

        //满足规则下执行相应条件
        boolean flag = AviatorEvaluator.execute(expression).equals(true);
        if (lastData > 0 && flag) {
            return new ResultModel(true, null, true);
        }
        return new ResultModel(false, "", false);
    }

}