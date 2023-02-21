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
package com.alipay.autotuneservice.controller.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangkaifei
 * @version : AccessTokenModel.java, v 0.1 2022年03月21日 下午3:11 huangkaifei Exp $
 */

@Data
@Builder
public class K8sAccessTokenModel {
    private String accessToken;
    @Deprecated
    private String accessKeyId;
    @Deprecated
    private String secretAccessKey;
    @Deprecated
    private String clusterId;
    private String clusterName;
    private String region;
    @Deprecated
    private String endpoint;
    @Deprecated
    private String cer;
    private String s3Key;
    private String clusterStatus;
}