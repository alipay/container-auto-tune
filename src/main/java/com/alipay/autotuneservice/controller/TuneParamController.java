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
import com.alipay.autotuneservice.controller.model.tuneparam.AppTuneParamsVO;
import com.alipay.autotuneservice.controller.model.tuneparam.SubmitTuneParamsRequest;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsRequest;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsVO;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.TuneParamService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 包含调优参数的查询,修改等操作
 *
 * @author huangkaifei
 * @version : TuneParamController.java, v 0.1 2022年05月17日 2:55 PM huangkaifei Exp $
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/tune-params")
public class TuneParamController {

    @Autowired
    private TuneParamService tuneParamService;

    /**
     * 查询应用调优计划的pipeline对应的调优参数
     *
     * @param appId      app id
     * @param pipelineId pipeline Id
     * @return 应用的调优参数
     */
    @GetMapping("/{appId}/{pipelineId}/list")
    public ServiceBaseResult<AppTuneParamsVO> listTuneParams(@PathVariable(value = "appId") Integer appId,
                                                             @PathVariable(value = "pipelineId") Integer pipelineId) {
        return ServiceBaseResult.invoker().makeResult(() -> {
            log.info("listTuneParams enter. Input appId={}, pipelineId={}", appId, pipelineId);
            return tuneParamService.getTuneParams(appId, pipelineId);
        });
    }

    /**
     * 提交调优参数
     *
     * @param appId
     * @param pipelineId
     * @param request
     * @return
     */
    @NoLogin
    @PostMapping("/{appId}/{pipelineId}/submit")
    public ServiceBaseResult<Boolean> submitTuneParams(@PathVariable(value = "appId") Integer appId,
                                                       @PathVariable(value = "pipelineId") Integer pipelineId,
                                                       @RequestBody SubmitTuneParamsRequest request) {
        return ServiceBaseResult.invoker().makeResult(() -> tuneParamService.submitTuneParam(appId, pipelineId, request));
    }

    @NoLogin
    @PostMapping("/updateTuneParams")
    public ServiceBaseResult<UpdateTuneParamsVO> updateTuneParams(@RequestBody UpdateTuneParamsRequest request) {
        return ServiceBaseResult.invoker().paramCheck(() -> {
            Preconditions.checkArgument(request != null, "UpdateTuneParamsRequest can not be null.");
        }).makeResult(() -> tuneParamService.updateTuneParams(request));
    }
}