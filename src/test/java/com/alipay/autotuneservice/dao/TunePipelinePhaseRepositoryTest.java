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
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.model.pipeline.TunePipelinePhase;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author dutianze
 * @date 2022/4/11
 */
@SpringBootTest
class TunePipelinePhaseRepositoryTest {

    @Autowired
    private TunePipelinePhaseRepository tunePipelinePhaseRepository;
    @Autowired
    private DSLContext                  dslContext;

    @Test
    void findTest() {
        TunePipelinePhase tunePipelinePhase = tunePipelinePhaseRepository.find(1, dslContext);
        System.out.println(tunePipelinePhase);
    }

    @Test
    void save() {
        // insert
        TunePipelinePhase tunePipelinePhase = new TunePipelinePhase();
        tunePipelinePhase.setPipelineId(0);
        tunePipelinePhase.setStage(TuneStage.NONE);
        TunePipelinePhase saved = tunePipelinePhaseRepository.save(tunePipelinePhase, dslContext);
        System.out.println("xx " + saved);

        // update
        saved.setStage(TuneStage.HEALTHY_CHECK);
        TunePipelinePhase updated = tunePipelinePhaseRepository.save(saved, dslContext);
        System.out.println("xx: " + updated);
    }
}