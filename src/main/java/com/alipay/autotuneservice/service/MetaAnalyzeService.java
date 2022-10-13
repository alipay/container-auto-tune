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

import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient;
import com.auto.tune.client.SystemCommonGrpc;

/**
 * @author chenqu
 * @version : MetaAnalyzeService.java, v 0.1 2022年03月21日 15:40 chenqu Exp $
 */
public interface MetaAnalyzeService {

    @Deprecated
    void analyze(SystemCommonGrpc systemCommonGrpc);

    K8sClient createEksClient(String accessToken, String clusterName);

    void refreshCache(String accessToken, String clusterName);

}