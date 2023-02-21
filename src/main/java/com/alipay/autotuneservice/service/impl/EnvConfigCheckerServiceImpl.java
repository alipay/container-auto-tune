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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.controller.model.ConfigCheckVO;
import com.alipay.autotuneservice.service.EnvConfigCheckerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author huangkaifei
 * @version : EnvConfigCheckerServiceImpl.java, v 0.1 2022年08月19日 8:26 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class EnvConfigCheckerServiceImpl implements EnvConfigCheckerService {

    @Override
    public ConfigCheckVO validateEnvConfig() {
        return null;
    }
}