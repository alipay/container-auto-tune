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
package com.alipay.autotuneservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class AgentInvokeServiceTest {

    @Test
    void execCmd() {
        String podStats = "xx";
        ServiceBaseResult<String> result = JSON.parseObject(podStats,
            new TypeReference<ServiceBaseResult<String>>() {
            });
        String data = "xx";
        if (StringUtils.isBlank(data)) {
            return;
        }

        System.out.println();
    }

    @Test
    void execCmdV1() {
    }
}