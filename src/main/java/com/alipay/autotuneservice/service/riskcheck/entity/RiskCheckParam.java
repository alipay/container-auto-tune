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
package com.alipay.autotuneservice.service.riskcheck.entity;

import com.google.common.collect.ImmutableSet;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class RiskCheckParam {

    /**
     * 应用id   --必填
     */
    private Integer        appID;

    /**
     * pod列表   --必填
     */
    private List<String>   podnames;

    /**
     * 应用名称 --非必填
     */
    private String         appName;

    /**
     * 检测次数 默认6次  --非必填
     */
    private Integer        checkTime   = 6;

    /**
     * 检测时间跨度 默认5分钟   --非必填
     */
    private Integer        checkOffset = 5;

    /**
     * 检测指标   --非必填
     */
    private Set<CheckType> checkTypes;

    public RiskCheckParam() {
        checkTypes = ImmutableSet.of(CheckType.FGC_COUNT, CheckType.FGC_TIME, CheckType.YGC_COUNT,
            CheckType.META_UTIL, CheckType.OLD_UTIL);
    }
}
