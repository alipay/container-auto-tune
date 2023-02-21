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
package com.alipay.autotuneservice.controller.model.meter;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 测试监控链接返回结果
 *
 * @author huangkaifei
 * @version : ValidateMeterResult.java, v 0.1 2022年08月23日 11:41 AM huangkaifei Exp $
 */
@Data
@Builder
public class ValidateMeterResult {

    private boolean                 success = false;
    private String                  message;
    /**
     * metric result
     */
    private List<MeterMetricResult> result;

    public static ValidateMeterResult failedResult(String errorMsg){
        return ValidateMeterResult.builder().success(false).message(errorMsg).build();
    }
}