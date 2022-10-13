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

import lombok.Data;

@Data
public class RiskIndictor {

    /**
     * 指标名称
     */
    private String  indictor;

    /**
     * 最小值
     */
    private Double  min;

    /**
     * 最大值
     */
    private Double  max;

    /**
     * 开关
     */
    private Boolean onOFF;

    public RiskIndictor() {

    }

    public RiskIndictor(String indictor, Double min, Double max, Boolean onOFF) {
        this.indictor = indictor;
        this.min = min;
        this.max = max;
        this.onOFF = onOFF;
    }
}
