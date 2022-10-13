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
import com.alipay.autotuneservice.infrastructure.rpc.model.AccountUserInfo;
import com.alipay.autotuneservice.infrastructure.rpc.model.UserInfoBasic;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author dutianze
 * @date 2022/3/31
 */
@Slf4j
@SpringBootTest
class AccountAuthClientTest {

    @Autowired
    private SaasFactoryClient   accountAuthClient;

    private final static String productCode = "xx";
    private final static String tenantName  = "xx";

    @Test
    void tenantListTest() {
        AccountResponse<UserInfoBasic> response = accountAuthClient.getUserInfoBasic("xx",
            productCode);
        log.info("xx", response);
        Assertions.assertThat(response.isSuccess()).isEqualTo(true);
    }

    @Test
    void getUserInfoTest() {
        AccountResponse<AccountUserInfo> response = accountAuthClient.getUserInfo("xx", tenantName);
        log.info("xx", response);
        Assertions.assertThat(response.isSuccess()).isEqualTo(true);
    }
}