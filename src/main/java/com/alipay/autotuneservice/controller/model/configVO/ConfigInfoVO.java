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
package com.alipay.autotuneservice.controller.model.configVO;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
public class ConfigInfoVO {

    /**
     * 集群id
     */
    private int                           appId;

    /**
     * 自动调优开关
     */
    private Boolean                       autoTune;

    /**
     * 调优时间段
     */
    private Map<WeekEnum, List<TimeHHmm>> tunePrimaryTime;

    /**
     * 调节时间标识符，与{@link #tunePrimaryTime} 结合使用
     * true 代表 tunePrimaryTime 为可调节时间
     * false 代表 tunePrimaryTime 为不可调节时间
     */
    private Boolean                       tuneTimeTag;

    /**
     * true 自动执行
     * false 人工执行
     */
    private Boolean                       autoDispatch;

    /**
     * 调优分组
     */
    private List<TuneConfig>              tuneGroupConfig = Lists.newArrayList();

    /**
     * 智能防控开关
     */
    private Boolean                       riskSwitch;

    /**
     * 高级设置
     */
    private List<RiskIndictor>            advancedSetup;

    /**
     * 当前应用所在aws的时区
     */
    private String                        timeZone;

    /**
     * 操作时间
     */
    private String                        operateTime;

    public static List<TuneConfig> defaultTuneConfig() {
        return new ArrayList<TuneConfig>() {
            {
                add(new TuneConfig(1, 0.1));
                add(new TuneConfig(2, 0.3));
                add(new TuneConfig(3, 0.6));
                add(new TuneConfig(4, 1.0));
            }
        };
    }

    public static List<RiskIndictor> defaultAdvancedSetup() {
        return Arrays.asList(new RiskIndictor("old_used", null, null, false), new RiskIndictor(
            "old_util", null, null, false), new RiskIndictor("fgc_count", null, null, false),
            new RiskIndictor("fgc_time", null, null, false), new RiskIndictor("meta_used", null,
                null, false), new RiskIndictor("meta_util", null, null, false));
    }
}
