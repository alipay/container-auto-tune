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

import com.alipay.autotuneservice.controller.model.K8sAccessTokenModel;
import com.alipay.autotuneservice.infrastructure.rpc.model.UserInfoBasic;
import com.alipay.autotuneservice.model.common.UserInfo;

import javax.annotation.Nullable;

/**
 * @author dutianze
 * @version UserInfoService.java, v 0.1 2022年03月07日 20:45 dutianze
 */
public interface UserInfoService {

    UserInfo registerByAccountId(@Nullable String tenantCode, UserInfoBasic userInfoBasic)
                                                                                          throws InterruptedException;

    boolean saveAccessTokenInfo(K8sAccessTokenModel k8sAccessTokenModel);

    boolean checkTokenValidity(String accessToken);

    K8sAccessTokenModel getK8sTokeInfo(String accessToken, String clusterName);
}