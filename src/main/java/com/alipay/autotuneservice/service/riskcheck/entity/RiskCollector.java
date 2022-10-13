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

import java.util.*;
import java.util.stream.Collectors;

@Data
public class RiskCollector {

    /**
     * 指标检测结果
     */
    private Map<CheckType, TypeResult> riskType;

    /**
     * 有风险的pod
     */
    private List<String>               riskPod;

    /**
     * 检测过程中，程序出现异常的话，记录异常原因
     */
    private String                     errorMsg;

    public void collector(CheckType type, RiskCheckEnum result) {
        if (null == riskType) {
            riskType = new HashMap();
        }
        riskType.put(type, TypeResult.build(result, type.name()));
    }

    public void collector(String pod) {
        if (null == riskPod) {
            riskPod = new ArrayList<String>();
        }
        riskPod.add(pod);
    }

    public RiskCollector(Set<CheckType> checkTypes) {
        this.riskType = checkTypes.stream().collect(Collectors.toMap(k -> k, v -> new TypeResult(true, v.name())));
    }

    public RiskCollector(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public RiskCollector() {

    }
}
