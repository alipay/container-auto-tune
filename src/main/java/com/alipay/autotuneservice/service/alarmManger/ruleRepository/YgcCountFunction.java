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

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmContext;
import com.alipay.autotuneservice.service.alarmManger.model.ResultModel;
import com.alipay.autotuneservice.service.alarmManger.model.RuleModel;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author huoyuqi
 * @version YgcCountFunction.java, v 0.1 2022年12月26日 3:11 下午 huoyuqi
 */
@Slf4j
public class YgcCountFunction extends BasicMetricCalcFunction {

    /**
     * 前置检查YGC_COUNT 次数计算
     */
    @Override
    public AviatorObject call(Map<String, Object> env) {
        try {
            log.info("YgcCountFunction enter");
            AlarmContext alarmContext = (AlarmContext) env.get("alarmContext");
            List<JvmMonitorMetricData> metricData = obtainMetrics(alarmContext);
            log.info("metricData is: {}", JSON.toJSONString(metricData));
            if (CollectionUtils.isEmpty(metricData)) {
                return new AviatorString(JSON.toJSONString(new ResultModel(false, "", false)));
            }

            double sum = metricData.stream().mapToDouble(JvmMonitorMetricData::getYgc).sum();
            double lastData = metricData.get(metricData.size() - 1).getYgc();
            RuleModel ruleModel = alarmContext.getRuleModel();
            String expression = String.format("%s%s%s", sum, ruleModel.getOperatorSymbol(), ruleModel.getData());
            ResultModel result = calcResult(ruleModel, lastData, expression);
            Optional.ofNullable(result).ifPresent(e -> {
                e.setResultMessage(e.getStatus() ? String.format("ygcCount 在最近%sS超过设置的阈值", ruleModel.getTime() / 1000) : "");
            });
            return new AviatorString(JSON.toJSONString(result));
        } catch (Exception e) {
            log.error("FgcCountFunction#call occurs an error", e);
            return new AviatorString(JSON.toJSONString(new ResultModel(false, "", false)));
        }

    }

    @Override
    public String getName() {
        return "YGC_COUNT";
    }
}