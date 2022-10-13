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

import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelineRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TunePipelinePhase;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Map;

/**
 * @author dutianze
 * @version TunePipelineRepository.java, v 0.1 2022年04月06日 18:00 dutianze
 */
public interface TunePipelinePhaseRepository {

    TunePipelinePhase find(Integer pipelineId, DSLContext dslContext);

    TunePipelinePhase save(TunePipelinePhase currentPhase, DSLContext dslContext);

    Map<Integer, TunePipelinePhase> findToMap(List<TunePipelineRecord> pipelineRecords,
                                              DSLContext dslContext);

    void updateContext(Integer id, TuneContext context);
}