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
import com.alipay.autotuneservice.controller.model.tuneprediction.AppTunePredictVO;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.TuneEffectService;
import com.alipay.autotuneservice.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangkaifei
 * @version : AppTunePredictController.java, v 0.1 2022年05月13日 4:29 PM huangkaifei Exp $
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/tune/evaluation")
public class AppTunePredictController {
    @Autowired
    private TuneEffectService tuneEffectService;

    /**
     * 查询预期评估结果
     *
     * @param appId
     * @param pipelineId
     * @return
     */
    @NoLogin
    @RequestMapping(value = "/{appId}/{pipelineId}/predict", method = RequestMethod.GET)
    public ServiceBaseResult<AppTunePredictVO> queryPredictTuneEffect(@PathVariable Integer appId, @PathVariable Integer pipelineId) {
        return ServiceBaseResult.invoker().paramCheck(() -> {
            ObjectUtil.checkIntegerPositive(appId, "appId must be positive.");
            ObjectUtil.checkIntegerPositive(pipelineId, "pipelineId must be positive.");
        }).makeResult(() -> tuneEffectService.predictTuneEffect(appId, pipelineId));
    }

    @GetMapping("/{appId}/{pipelineId}/grayPredict")
    public ServiceBaseResult<AppTunePredictVO> queryGrayPredictTuneEffect(@PathVariable Integer appId, @PathVariable Integer pipelineId) {
        return ServiceBaseResult.invoker().paramCheck(() -> {
            ObjectUtil.checkIntegerPositive(appId, "appId must be positive.");
            ObjectUtil.checkIntegerPositive(pipelineId, "pipelineId must be positive.");
        }).makeResult(() -> tuneEffectService.grayEffect(appId, pipelineId));
    }
}