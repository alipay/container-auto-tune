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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.configuration.ConstantsProperties;
import com.alipay.autotuneservice.infrastructure.rpc.model.AccountResponse;
import com.alipay.autotuneservice.infrastructure.rpc.model.AccountUserInfo;
import com.alipay.autotuneservice.infrastructure.rpc.model.CostRequest;
import com.alipay.autotuneservice.infrastructure.rpc.model.UserInfoBasic;
import com.alipay.saascloud.factory.model.SaascloudApi;
import com.alipay.saascloud.factory.model.TenantAbilityVO;
import com.alipay.saascloud.factory.model.UserTenantVO;
import com.alipay.saascloud.factory.model.req.AuthReq;
import com.alipay.saascloud.factory.model.req.LoginTenantReq;
import com.alipay.saascloud.factory.model.req.SessionReq;
import com.alipay.saascloud.factory.model.req.metric.MetricTriggerRequest;
import com.alipay.saascloud.factory.support.Saasfactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dutianze
 * @version SaasFactoryClient.java, v 0.1 2022年03月31日 17:44 dutianze
 */
@Service
public class SaasFactoryClient {

    @Resource
    private ConstantsProperties constantsProperties;

    private AuthReq buildAuthReq() {
        return new AuthReq(constantsProperties.getAppId(), constantsProperties.getSecret());
    }

    public AccountResponse<UserInfoBasic> getUserInfoBasic(String authToken, String productCode) {
        SessionReq sessionReq = new SessionReq();
        sessionReq.setProductCode(productCode);
        sessionReq.setAuthToken(authToken);
        SaascloudApi<UserTenantVO> saascloudApi = Saasfactory.fetchTenant(sessionReq,
            buildAuthReq());
        String sourceJSON = JSON.toJSONString(saascloudApi);
        return JSON.parseObject(sourceJSON, new TypeReference<AccountResponse<UserInfoBasic>>() {
        });
    }

    public AccountResponse<AccountUserInfo> getUserInfo(String authToken, String tenantCode) {
        LoginTenantReq loginTenantReq = new LoginTenantReq();
        loginTenantReq.setTenantCode(tenantCode);
        loginTenantReq.setAuthToken(authToken);
        SaascloudApi<TenantAbilityVO> saascloudApi = Saasfactory.fetchTenantAbility(loginTenantReq,
            buildAuthReq());
        String sourceJSON = JSON.toJSONString(saascloudApi);
        return JSON.parseObject(sourceJSON, new TypeReference<AccountResponse<AccountUserInfo>>() {
        });
    }

    public AccountResponse<String> submit(CostRequest costRequest) {
        MetricTriggerRequest request = new MetricTriggerRequest();
        BeanUtils.copyProperties(costRequest, request);
        SaascloudApi<String> saascloudApi = Saasfactory.metricTrigger(request, buildAuthReq());
        String sourceJSON = JSON.toJSONString(saascloudApi);
        return JSON.parseObject(sourceJSON, new TypeReference<AccountResponse<String>>() {
        });
    }

}