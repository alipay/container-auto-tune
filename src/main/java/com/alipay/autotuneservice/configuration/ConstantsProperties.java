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
package com.alipay.autotuneservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author huangkaifei
 * @version : ConstantsProperties.java, v 0.1 2022年06月20日 5:06 PM huangkaifei Exp $
 */
@Data
@Configuration
@Primary
@ConfigurationProperties(prefix = "tmaster.configure")
@EnableConfigurationProperties({ ConstantsProperties.class })
public class ConstantsProperties {
    /**
     * Tmaestro service domain url
     */
    private String domainUrl;
    /**
     * Tmaestro web home url
     */
    private String webHomeUrl;
    private String productCode;
    /**
     * 调用saas的租户URL
     */
    private String saasTenantUrl;
    /**
     * saas factory url
     */
    private String saasUrl;

    /**
     *   域名apiUrl
     */
    private String domainApiUrl;
    /**
     *   grpc host
     */
    private String grpcHost;
    /**
     *   grpc host
     */
    private String grpcPort;

    /**
     * 算法url 健康检测
     */
    private String algorithmUrl;

    /**
     * saasfactory 接口鉴权
     */
    private String appId;

    /**
     * saasfactory 接口鉴权
     */
    private String secret;

    /**
     * 发送邮箱
     */
    private String fromEmail;

    /**
     * EmailSecret
     */
    private String emailSecret;

}