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
import com.alipay.autotuneservice.controller.model.TuneEffectVO;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.service.TuneEffectService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huoyuqi
 * @version AppTuneEffectController.java, v 0.1 2022年04月28日 10:18 上午 huoyuqi
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api")
public class AppTuneEffectController {

    @Autowired
    private TuneEffectService tuneEffectService;

    @GetMapping("/tuneEffect/{id}")
    public ServiceBaseResult<TuneEffectVO> tuneEffect(@PathVariable(value = "id") Integer pipelineId) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> {
                    Preconditions.checkArgument(pipelineId > 0, "pipelineId can not be less than 0");
                })
                .makeResult(() -> {
                    log.info("tuneEffect enter pipelineId: {}", pipelineId);
                    return tuneEffectService.tuneEffect(pipelineId);
                });
    }

    @GetMapping("/triggerTuneEffect/{id}")
    public ServiceBaseResult<String> triggerTuneEffect(@PathVariable(value = "id") Integer pipelineId) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> {
                    Preconditions.checkArgument(pipelineId > 0, "pipelineId can not be less than 0");
                })
                .makeResult(() -> {
                    log.info("triggerTuneEffect enter pipelineId: {}", pipelineId);
                    tuneEffectService.triggerTuneEffect(pipelineId);
                    return null;
                });
    }
}