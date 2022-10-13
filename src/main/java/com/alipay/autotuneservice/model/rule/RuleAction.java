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

import com.alipay.autotuneservice.grpc.handler.ActionParam;
import com.alipay.autotuneservice.model.report.ReportType;

import java.util.function.BiConsumer;

/**
 * @author dutianze
 * @version RuleAction.java, v 0.1 2022年02月22日 17:42 dutianze
 */
public enum RuleAction {

    /**
     * java garbage collector
     */
    GC_DUMP(ReportType.GC, "com.auto.tune.core.step.impl.DumpGCLogStep", (r1, r2) -> {

    }),

    /**
     * java thread stack traces
     */
    THREAD_DUMP(ReportType.THREAD, "com.auto.tune.core.step.impl.DumpThreadStep"),

    /**
     * java shared object memory maps
     */
    HEAP_DUMP(ReportType.HEAP, "com.auto.tune.core.step.impl.DumpHeapStep"),

    /**
     * 更新agent配置、环境变量
     */
    UPDATE_CONFIG(ReportType.UNKNOWN, "com.auto.tune.core.step.impl.UpdateConfigStep"),
    ;

    private final ReportType                         reportType;
    private final String                             stepClassName;
    private final BiConsumer<ActionParam, RuleParam> consumer;

    RuleAction(ReportType reportType, String stepClassName) {
        this(reportType, stepClassName, (a, r) -> {
        });
    }

    RuleAction(ReportType reportType, String stepClassName,
               BiConsumer<ActionParam, RuleParam> consumer) {
        this.reportType = reportType;
        this.stepClassName = stepClassName;
        this.consumer = consumer;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public String getStepClassName() {
        return stepClassName;
    }

    public BiConsumer<ActionParam, RuleParam> getConsumer() {
        return consumer;
    }

    public void setAndCalcParams(ActionParam actionParam, RuleParam ruleParam) {
        this.consumer.accept(actionParam, ruleParam);
    }
}