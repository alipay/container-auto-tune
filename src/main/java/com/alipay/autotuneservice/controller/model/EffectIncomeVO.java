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
package com.alipay.autotuneservice.controller.model;

import lombok.Data;

/**
 * @author huoyuqi
 * @version EffectIncomeVO.java, v 0.1 2022年05月09日 2:12 下午 huoyuqi
 */
@Data
public class EffectIncomeVO {

    /**
     * 收益类型  包含cpu、内存
     */
    private String incomeType;

    /**
     * 调优前参照结果
     */
    private Double referResult;

    /**
     * 调优后结果
     */
    private Double observeResult;

    /**
     * 收益
     */
    private Double income;

    public EffectIncomeVO() {

    }

    public EffectIncomeVO(String incomeType, Double referResult, Double observeResult, Double income) {
        this.incomeType = incomeType;
        this.referResult = referResult;
        this.observeResult = observeResult;
        this.income = income;
    }
}