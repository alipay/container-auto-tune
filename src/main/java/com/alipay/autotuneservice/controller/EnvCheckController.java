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
import com.alipay.autotuneservice.controller.model.ConfigCheckVO;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangkaifei
 * @version : EnvCheckController.java, v 0.1 2022年08月18日 5:23 PM huangkaifei Exp $
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/env")
public class EnvCheckController {

    /**
     *  validate
     *
     * @return
     */
    @GetMapping("/config/validation")
    public ServiceBaseResult<ConfigCheckVO> validate() {
        log.info("start to validate env config.");
        return ServiceBaseResult.successResult();
    }
}