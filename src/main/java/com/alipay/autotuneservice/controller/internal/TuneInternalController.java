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
package com.alipay.autotuneservice.controller.internal;

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.ExpertService;
import com.alipay.autotuneservice.service.algorithmlab.tune.req.ExpertRequest;
import com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.ExpertEvalResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dutianze
 * @version ExpertController.java, v 0.1 2022年04月28日 13:59 dutianze
 */
@Slf4j
@RestController
@RequestMapping("/api/expert")
public class TuneInternalController {

    @Autowired
    private ExpertService expertService;

    @NoLogin
    @PostMapping(path = "/analyze")
    public ServiceBaseResult<ExpertEvalResult> analyze(@RequestBody ExpertRequest expertRequest) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> expertService.eval(expertRequest.getAppId()));
    }
}