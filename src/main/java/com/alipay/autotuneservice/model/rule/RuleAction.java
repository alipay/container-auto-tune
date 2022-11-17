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
package com.alipay.autotuneservice.model.rule;

import com.alipay.autotuneservice.model.report.ReportType;
import com.alipay.autotuneservice.model.report.ResultType;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dutianze
 * @version RuleAction.java, v 0.1 2022年02月22日 17:42 dutianze
 */
public enum RuleAction {

    /**
     * java garbage collector
     */
    GC_DUMP(ReportType.GC, ResultType.STREAM, "com.auto.tune.core.step.impl.action.DumpGCLogStep"),

    /**
     * java thread stack traces
     */
    THREAD_DUMP(ReportType.THREAD, ResultType.STREAM, "com.auto.tune.core.step.impl.action.DumpThreadStep"),

    /**
     * java shared object memory maps
     */
    HEAP_DUMP(ReportType.HEAP, ResultType.STREAM, "com.auto.tune.core.step.impl.action.DumpHeapStep"),

    /**
     * 更新agent配置、环境变量
     */
    ENV_CONFIG_STEP(ReportType.UNKNOWN, ResultType.BOOLEAN, "com.auto.tune.core.step.impl.EnvConfigStep"),

    /**
     * 线程操作指令
     */
    THREAD_OPERATION(ReportType.THREAD, ResultType.STRING, "com.auto.tune.core.step.impl.action.ThreadStep"),

    /**
     * GC操作指令
     */
    GC_OPERATION(ReportType.GC, ResultType.STRING, "com.auto.tune.core.step.impl.action.GCStep"),

    /**
     * java shared object memory maps
     */
    JVM_PROFILER(ReportType.HEAP, ResultType.STREAM, "com.auto.tune.core.step.impl.action.JvmProfilerStep"),

    /**
     * java garbage collector
     */
    UNKNOWN(ReportType.UNKNOWN, ResultType.STRING, "");

    private final ReportType reportType;
    private final ResultType resultType;
    private final String     stepClassName;

    RuleAction(ReportType reportType, ResultType resultType, String stepClassName) {
        this.reportType = reportType;
        this.resultType = resultType;
        this.stepClassName = stepClassName;
    }

    public static RuleAction valueOfType(String actionName) {
        if (StringUtils.isBlank(actionName)) {
            return RuleAction.UNKNOWN;
        }
        for (RuleAction ruleAction : RuleAction.values()) {
            if (ruleAction.name().equals(actionName)) {
                return ruleAction;
            }
        }
        return RuleAction.UNKNOWN;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public String getStepClassName() {
        return stepClassName;
    }

    public ResultType getResultType() {
        return resultType;
    }
}