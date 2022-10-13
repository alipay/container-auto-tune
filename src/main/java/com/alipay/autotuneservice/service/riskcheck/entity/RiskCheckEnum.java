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

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * 风险检测结果
 */
public enum RiskCheckEnum {
    HIGH_RISK, LOW_RISK, NORMAL,
    /**
     * 代表检测异常，如未获取的到pod数据
     */
    UNKNOW,
    /**
     * 代表未检测结束
     */
    EMPTY;

    private static final List<RiskCheckEnum> RISK = ImmutableList.of(HIGH_RISK, LOW_RISK);

    public Boolean existRisk() {
        return RISK.contains(this);
    }
}