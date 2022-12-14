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
package com.alipay.autotuneservice.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.TuneParamTrialDataRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTrialDataRecord;
import com.alipay.autotuneservice.model.common.TrailTuneTaskStatus;
import com.alipay.autotuneservice.model.tune.trail.TrailTuneContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TuneParamTrialDataRepositoryImplTest {

    @Autowired
    private TuneParamTrialDataRepository repository;

    @Test
    void getTrialData() {
        Integer pipelineId = 0;
        TrailTuneTaskStatus status = TrailTuneTaskStatus.FINISH;
        TuningParamTrialDataRecord trialData = repository.getTrialData(pipelineId, status);
        System.out.println(JSON.toJSONString(TrailTuneContext.convert(trialData)));
    }

    @Test
    void updateStatus() {
        Integer pipelineId = 0;
        TrailTuneTaskStatus status = TrailTuneTaskStatus.FINISH;
        System.out.println(repository.updateTaskStatus(pipelineId));
    }
}