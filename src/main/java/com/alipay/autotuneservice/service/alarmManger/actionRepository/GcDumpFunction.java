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
package com.alipay.autotuneservice.service.alarmManger.actionRepository;

import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.gc.service.GcLogAnalysisService;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmContext;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huoyuqi
 * @version GcDumpFunction.java, v 0.1 2022年12月27日 11:39 上午 huoyuqi
 */
@Slf4j
public class GcDumpFunction extends BasicActionFunction {

    @Override
    public AviatorObject call(Map<String, Object> env) {
        log.info("GcDumpFunction enter");
        AlarmContext alarmContext = (AlarmContext) env.get("alarmContext");
        GcLogAnalysisService gcLogAnalysisService = alarmContext.getGcLogAnalysisService();
        PodInfoRecord pod = alarmContext.getPodInfoRecord();
        String appName = alarmContext.getAppName();
        String taskName = String.format("%sGc日志分析%s", appName, constructDate());
        gcLogAnalysisService.autoUpload(pod.getUnicode(), pod.getPodName(), RuleAction.GC_DUMP, taskName, new HashMap<>(), appName);
        return new AviatorString("gc_dump完成");
    }

    @Override
    public String getName() {
        return "GC_DUMP";
    }

}