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

import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TunePipelinePhase;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.List;

/**
 * @author dutianze
 * @date 2022/4/7
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TunePipelineRepositoryTest {

    @Autowired
    private TunePipelineRepository tunePipelineRepository;

    @Test
    void saveInsertTest() {
        TunePipeline tunePipeline = new TunePipeline();
        tunePipeline.setAccessToken("xx");
        tunePipeline.setAppId(1);
        tunePipeline.setMachineId(MachineId.TUNE_PIPELINE);
        tunePipeline.setStatus(Status.RUNNING);
        tunePipeline.setStage(TuneStage.NONE);

        TunePipelinePhase tunePipelinePhase = new TunePipelinePhase(tunePipeline, new TuneContext(
            "xx", 0));
        tunePipeline.setCurrentPhase(tunePipelinePhase);

        TunePipeline saved = tunePipelineRepository.saveOneWithTransaction(tunePipeline);
        System.out.println(saved);
    }

    @Test
    void saveUpdateTest() {
        TunePipeline tunePipeline = new TunePipeline();
        tunePipeline.setId(0);
        tunePipeline.setMachineId(MachineId.TUNE_PIPELINE);
        tunePipeline.setStatus(Status.EXCEPTION);
        tunePipeline.setStage(TuneStage.NONE);
        TunePipeline saved = tunePipelineRepository.saveOneWithTransaction(tunePipeline);
        System.out.println(saved);
    }

    @Test
    void findByAppIdAndStatus() {
        List<TunePipeline> pipelines = tunePipelineRepository.findByAppIdAndStatus(0, Status.RUNNING);
        pipelines.forEach(System.out::println);

        Assertions.assertThat(pipelines).isNotNull();
    }

    @Test
    void findPipelineByStatus() {
        List<TunePipeline> pipelines = tunePipelineRepository.findPipelineByStatus(Status.RUNNING);
        pipelines.forEach(System.out::println);

        Assertions.assertThat(pipelines).isNotNull();
    }

    @Test
    void findByPipelineId() {

        TunePipeline byPipelineId = tunePipelineRepository.findByPipelineId(0);
        System.out.println();

    }
}