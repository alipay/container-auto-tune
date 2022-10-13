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

import com.alipay.autotuneservice.dao.UserInfoRepository;
import com.alipay.autotuneservice.infrastructure.rpc.model.CostCell;
import com.alipay.autotuneservice.model.common.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author dutianze
 * @date 2022/5/18
 */
@Slf4j
@SpringBootTest
class CostOrderHandlerTest {

    @Autowired
    private CostOrderHandler   costOrderHandler;
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Test
    void submit() {
        UserInfo userInfo = userInfoRepository.findByAccessToken("xx");
        CostCell costCell = new CostCell(userInfo);
        costOrderHandler.submit(costCell, 11);
    }
}