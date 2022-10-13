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

import lombok.Data;

@Data
public class TypeResult {

    /**
     * 是否检测通过
     */
    private Boolean paas;
    /**
     * 失败原因
     */
    private String  errorMsg;

    private String  typeName;

    public TypeResult() {

    }

    public TypeResult(Boolean paas, String typeName) {
        this.paas = paas;
        this.typeName = typeName;
    }

    public TypeResult(Boolean paas, String errorMsg, String typeName) {
        this.paas = paas;
        this.errorMsg = errorMsg;
        this.typeName = typeName;
    }

    public static TypeResult build(RiskCheckEnum result, String typeName) {
        if (RiskCheckEnum.HIGH_RISK == result) {
            return new TypeResult(false, "当前值高于机器日常水位", typeName);
        }
        if (RiskCheckEnum.LOW_RISK == result) {
            return new TypeResult(false, "当前值低于机器日常水位", typeName);
        }
        if (RiskCheckEnum.UNKNOW == result) {
            return new TypeResult(false, "未获取到机器监控数据", typeName);
        }
        return new TypeResult(true, typeName);
    }
}