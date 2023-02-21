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

import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.model.tune.TuneChangeDefinition;

import java.util.List;

/**
 * @author huoyuqi
 * @version PodInfo.java, v 0.1 2022年04月18日 7:13 下午 huoyuqi
 */
public interface TuneLogInfo {

    void insertPodInfo(TuneLogInfoRecord record);

    void updateChangePodInfo(TuneLogInfoRecord record, List<TuneChangeDefinition> changeDefinitions);

    void updateRiskTraceId(Integer tuneId, String traceId);

    TuneLogInfoRecord findRecord(TuneLogInfoRecord record);

    List<TuneLogInfoRecord> findRecordByPipeline(Integer pipelineId, String action);

    List<TuneLogInfoRecord> findRecordByPipelineIds(List<Integer> pipelineIds, String action);
}