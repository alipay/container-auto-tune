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
package com.alipay.autotuneservice.infrastructure.rpc;

import com.alipay.autotuneservice.infrastructure.rpc.model.AccountResponse;
import com.alipay.autotuneservice.infrastructure.rpc.model.CostCell;
import com.alipay.autotuneservice.infrastructure.rpc.model.CostRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author dutianze
 * @version CostOrderHandler.java, v 0.1 2022年05月18日 16:31 dutianze
 */
@Slf4j
@Component
public class CostOrderHandler {

    @Autowired
    private SaasFactoryClient saasFactoryClient;
    @Value("${application.productCode}")
    private String            productCode;

    public void submit(CostCell costCell, int agentInstallCount) {
        log.info("submit, costCell:{}, agentInstallCount:{}", costCell, agentInstallCount);
        CostRequest costRequest = new CostRequest(costCell, productCode, agentInstallCount);
        AccountResponse<String> resp = saasFactoryClient.submit(costRequest);
        log.info("costClient submit, costRequest{}, resp:{}", costRequest, resp);
    }
}