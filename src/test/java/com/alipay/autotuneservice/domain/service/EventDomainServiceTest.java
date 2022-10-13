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
package com.alipay.autotuneservice.domain.service;

import com.alibaba.cola.statemachine.StateMachine;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.service.pipeline.TuneFlowService;
import org.junit.jupiter.api.Test;

/**
 * @author dutianze
 * @date 2022/3/30
 */
class EventDomainServiceTest {

    @Test
    void buildStateMachine() {
        TuneFlowService eventDomainService = new TuneFlowService();
        // main stateMachine
        StateMachine<TuneStage, TuneEventType, TunePipeline> main = eventDomainService
            .buildTuneStateMachine("xx");
        String mainUml = main.generatePlantUML();
        System.out.println(mainUml);

        // test stateMachine
        StateMachine<TuneStage, TuneEventType, TunePipeline> test = eventDomainService
            .buildTuneTestStateMachine("xx");
        String testUml = test.generatePlantUML();
        System.out.println(testUml);

        // batch stateMachine
        StateMachine<TuneStage, TuneEventType, TunePipeline> batch = eventDomainService
            .buildTuneBatchStateMachine("xx");
        String batchUml = batch.generatePlantUML();
        System.out.println(batchUml);
    }
}