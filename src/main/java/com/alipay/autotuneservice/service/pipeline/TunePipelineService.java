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
package com.alipay.autotuneservice.service.pipeline;

import com.alipay.autotuneservice.controller.model.TunePlanVO;
import com.alipay.autotuneservice.model.dto.PipelineDTO;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;

import java.util.List;

/**
 * @author dutianze
 * @version TunePipelineService.java, v 0.1 2022年04月11日 17:57 dutianze
 */
public interface TunePipelineService {

    /**
     * create pipeline need accessToken、appId
     */
    TunePipeline createPipeline(TuneContext context);

    /**
     * find all TunePipeline list by appId
     */
    default List<TunePipeline> findByAppId(Integer appId) {
        return findByAppIdAndStatus(appId, null);
    }

    /**
     * find all TunePipeline list by appId and pipeline status
     */
    List<TunePipeline> findByAppIdAndStatus(Integer appId, Status status);

    /**
     * find PipelineDTO by ID
     */
    PipelineDTO findMainPipelineByPipelineId(Integer pipelineId);

    /**
     * batch find PipelineDTOs by ID
     */
    List<PipelineDTO> batchFindMainPipelinesByPlanIds(List<Integer> planIds);

    /**
     * getAppTunePipeline
     *
     * @param pipelineId
     * @return
     */
    TunePlanVO getTunePipelineById(Integer pipelineId);

    /**
     * find By PlanId
     *
     * @param planId
     * @return
     */
    List<TunePipeline> findByPlanId(Integer planId);

    TunePlan findByPipelineId(Integer pipelineId);

    /**
     * 根据pipelineId检查tune plan是否为自动调参
     *
     * @param pipelineId
     * @return true： AUTO， false MANUAL
     */
    Boolean checkTunePlanIsAuto(Integer pipelineId);

    Boolean cancelPipeline(Integer pipelineId);
}