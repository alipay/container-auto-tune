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

import com.alipay.autotuneservice.dao.RiskCheckControlRepository;
import com.alipay.autotuneservice.dao.RiskCheckTaskRepository;
import com.alipay.autotuneservice.service.riskcheck.RiskCheckTimeDriver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class RiskCheckTimeDriverTest {

    @Autowired
    private RiskCheckTimeDriver        riskCheckTimeDriver;

    @Autowired
    private RiskCheckTaskRepository    riskCheckTaskRepository;

    @Autowired
    private RiskCheckControlRepository riskCheckControlRepository;

    @Test
    void submitRiskCheckJob() {

        riskCheckTimeDriver.executeTask();
    }

    @Test
    void handleJobStatus() {

        riskCheckTimeDriver.handleJobStatus();
    }

    @Test
    void delete() {

        LocalDateTime now = LocalDateTime.now();
        riskCheckControlRepository.delete(now.minusMinutes(1));
        riskCheckTaskRepository.delete(now.minusMinutes(1));
    }

}
