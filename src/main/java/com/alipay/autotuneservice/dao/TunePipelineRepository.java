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
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;

import java.util.List;

/**
 * @author dutianze
 * @version TunePipelineRepository.java, v 0.1 2022年04月06日 18:00 dutianze
 */
public interface TunePipelineRepository {

    TunePipeline saveOneWithTransaction(TunePipeline tunePipeline);

    void saveWithTransaction(TunePipeline... tunePipeline);

    TunePipeline findByPipelineIdAndStatus(Integer pipelineId, Status status);

    /**
     * 只提供查询tunePlan, 用于其他用途，数据会缺失
     *
     * see {@link  TunePipelineRepository#findByMachineIdAndPipelineId}
     */
    TunePipeline findByPipelineId(Integer pipelineId);

    List<TunePipeline> findByAppIdAndStatus(Integer appId, Status status);

    List<TunePipeline> findByAppId(Integer appId);

    List<TunePipeline> findPipelineByStatus(Status status);

    List<TunePipeline> findByPlanId(Integer planId);

    TunePipeline findByMachineIdAndPipelineId(MachineId machineId, Integer pipelineId);

    TunePipeline findByMachineIdAndPipelineId(Integer pipelineId);

    List<TunePipeline> batchFindMainPipelinesByPlanIds(MachineId machineId, List<Integer> planIds);

    List<TunePipeline> batchFindPipelinesByPlanIds(List<Integer> planIds);

    List<TunePipeline> batchFindPipelinesByPipelines(List<Integer> pipelineIds);

    TunePipelineRecord findById(Integer pipelineId);
}