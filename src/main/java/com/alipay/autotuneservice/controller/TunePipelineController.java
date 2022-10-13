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
package com.alipay.autotuneservice.controller;

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.configuration.ResourcePermission;
import com.alipay.autotuneservice.configuration.ResourcePermission.ResourceType;
import com.alipay.autotuneservice.controller.model.BatchGroupVO;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.dto.PipelineDTO;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.service.BatchGroupService;
import com.alipay.autotuneservice.service.pipeline.TunePipelineService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dutianze
 * @version TunePipelineController.java, v 0.1 2022年04月11日 17:53 dutianze
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/jvm/tune-pipeline")
public class TunePipelineController {

    @Autowired
    private TunePipelineService    tunePipelineService;
    @Autowired
    private TunePipelineRepository tunePipelineRepository;
    @Autowired
    private BatchGroupService      batchGroupService;

    @ResourcePermission(path = "id", type = ResourceType.PIPELINE_ID)
    @GetMapping("/detail/{id}")
    public ServiceBaseResult<PipelineDTO> findPipelineDetailByPipelineId(@PathVariable(value = "id") Integer id) {
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> Preconditions.checkArgument(id > 0, "id must more than 1"))
                .makeResult(() -> tunePipelineService.findMainPipelineByPipelineId(id));
    }

    /**
     * 获取分批调参信息
     */
    @ResourcePermission(path = "pipelineId", type = ResourceType.PIPELINE_ID)
    @GetMapping("/batchGroup/{pipelineId}")
    public ServiceBaseResult<BatchGroupVO> batchGroup(@PathVariable(value = "pipelineId") Integer pipelineId) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> {
                    TunePipeline pipeline = tunePipelineRepository.findByMachineIdAndPipelineId(MachineId.TUNE_BATCH_PIPELINE, pipelineId);
                    //基于 pipeline id 获取当前状态
                    if (pipeline == null) {
                        throw new RuntimeException("not found pipeline,id=" + pipelineId);
                    }
                    return batchGroupService.generateBatchGroup(pipeline);
                });
    }

    /**
     * 取消pipeline
     */
    @ResourcePermission(path = "pipelineId", type = ResourceType.PIPELINE_ID)
    @PostMapping("/cancel")
    public ServiceBaseResult<Boolean> cancel(@RequestParam(value = "pipelineId") Integer pipelineId) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> tunePipelineService.cancelPipeline(pipelineId));
    }
}