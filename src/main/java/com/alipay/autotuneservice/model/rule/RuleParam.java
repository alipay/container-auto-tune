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
package com.alipay.autotuneservice.model.rule;

import lombok.Data;

/**
 * @author dutianze
 * @version RuleParam.java, v 0.1 2022年02月22日 17:39 dutianze
 */
@Data
public class RuleParam {

    private RuleAction ruleAction;

    /**
     * 自动 - 定时
     */
    // cron表达式
    private String  cron;
    // 回溯时间 单位: hour
    private Integer lookBackTime;

    /**
     * 自动 - 阈值
     */
    // 指标项
    private MetricItem compareMetricItem;
    // 操作符
    private Operator   compareOperator;
    // 指标值
    private Integer    compareMetricValue;
}