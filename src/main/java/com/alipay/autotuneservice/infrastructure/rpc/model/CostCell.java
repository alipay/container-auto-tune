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
package com.alipay.autotuneservice.infrastructure.rpc.model;

import com.alipay.autotuneservice.model.common.UserInfo;
import lombok.Getter;

/**
 * @author dutianze
 * @version CostCell.java, v 0.1 2022年05月18日 16:31 dutianze
 */
@Getter
public class CostCell {

    // 内部accessToken
    private final String accessToken;
    // 租户码
    private final String tenantCode;
    // 需要业务方计量记录绑定产品账户
    private final String productAccountId;

    private final String planCode;

    public CostCell(UserInfo userInfo) {
        this.accessToken = userInfo.getAccessToken();
        this.tenantCode = userInfo.getTenantCode();
        this.productAccountId = userInfo.getProductAccountId();
        this.planCode = userInfo.getPlanCode();
    }
}