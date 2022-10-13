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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.infrastructure.rpc.model.TenantItem;
import com.alipay.autotuneservice.infrastructure.rpc.model.UserInfoBasic;
import com.alipay.autotuneservice.model.common.UserInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

/**
 * @author dutianze
 * @date 2022/4/1
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserInfoService userService;

    @Test
    void getByAccountId() throws InterruptedException {
        UserInfoBasic userInfoBasic = new UserInfoBasic();
        userInfoBasic.setUserEmail("xx");

        TenantItem tenantItem = new TenantItem();
        tenantItem.setTenantCode("xx");
        tenantItem.setProductAccountId("xx");
        tenantItem.setPlanCode("xx");
        userInfoBasic.setTenantItems(Collections.singletonList(tenantItem));

        UserInfo userModel = userService.registerByAccountId("xx", userInfoBasic);
        Assertions.assertThat(userModel).isNotNull();
    }
}