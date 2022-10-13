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
package com.alipay.autotuneservice.service.risk;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.service.riskcheck.RiskCheckService;
import com.alipay.autotuneservice.service.riskcheck.entity.CheckResponse;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCheckParam;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RiskCheckServiceTest {

    @Autowired
    private RiskCheckService riskCheckService;

    @Test
    void submitRiskCheckJob() {
        RiskCheckParam riskCheckParam = new RiskCheckParam();
        riskCheckParam.setAppID(81);
        riskCheckParam.setCheckOffset(2);
        String[] f = { "xx", "xx" };
        riskCheckParam.setPodnames(Lists.newArrayList(f));
        String trace = riskCheckService.submitRiskCheckJob(riskCheckParam);

    }

    @Test
    void getRiskCheckResult() {
        CheckResponse response = riskCheckService.getRiskCheckResult("xx");
        System.out.println(JSON.toJSON(response));
    }

}
