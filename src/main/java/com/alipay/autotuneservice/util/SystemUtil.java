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
package com.alipay.autotuneservice.util;

import com.alipay.autotuneservice.configuration.ConstantsProperties;
import com.alipay.autotuneservice.model.common.CloudType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class SystemUtil {

    public static String generateDecisionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 获取当前环境所处的云环境
     */
    public static CloudType getCloudTypeFromEnv(Environment env) {
        String mockCloudType = env.getProperty("mockCloudType");
        if (StringUtils.isEmpty(mockCloudType)) {
            throw new UnsupportedOperationException("Can not find the cloud type");
        }
        if (StringUtils.contains(mockCloudType.toLowerCase(), CloudType.AWS.name().toLowerCase())) {
            return CloudType.AWS;
        }
        if (StringUtils
            .contains(mockCloudType.toLowerCase(), CloudType.ALIYUN.name().toLowerCase())) {
            return CloudType.ALIYUN;
        }
        if (StringUtils.contains(mockCloudType.toLowerCase(), CloudType.K8S.name().toLowerCase())) {
            return CloudType.K8S;
        }
        throw new UnsupportedOperationException("Can not find the cloud type");
    }

    /**
     * get server domain url
     * @return
     */
    public static String getDomainUrl() {
        try {
            ConstantsProperties constantsProperties = (ConstantsProperties) SpringFactoryUtils
                .getBean("constantsProperties");
            return constantsProperties.getDomainUrl();
        } catch (Exception e) {
            log.error("getDomainUrl occurs an error.", e);
            return "";
        }
    }

    /**
     * get server web url
     * @return
     */
    public static String getWebUrl() {
        try {
            ConstantsProperties constantsProperties = (ConstantsProperties) SpringFactoryUtils
                .getBean("constantsProperties");
            return constantsProperties.getWebHomeUrl();
        } catch (Exception e) {
            log.error("getWebUrl occurs an error.", e);
            return "";
        }
    }
}
