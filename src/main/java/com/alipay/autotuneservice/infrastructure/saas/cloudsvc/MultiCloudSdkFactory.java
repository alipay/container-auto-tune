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
package com.alipay.autotuneservice.infrastructure.saas.cloudsvc;

import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.MailProvider;
import com.alipay.autotuneservice.infrastructure.saas.common.constant.CloudTypeConstant;
import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringContextUtil;
import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringPropertiesCache;
import org.apache.http.util.Asserts;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yiqi
 * @date 2022/05/30
 */
public interface MultiCloudSdkFactory {

    String                            DEFAULT_KEY = "default";

    Map<String, MultiCloudSdkFactory> INSTANCES   = new HashMap<>();

    /**
     * 获取实例对象，根据 application.yml中 cloud.type的配置，生成的工厂对象
     * @return MultiCloudSdkFactory的具体实现类
     */
    static MultiCloudSdkFactory getInstance() {
        return getInstance(DEFAULT_KEY, DEFAULT_KEY);
    }

    /**
     * 获取实例对象，根据 application.yml中 cloud.type的配置，生成的工厂对象
     * @param accessKey accessKey
     * @param accessSecret accessSecret
     * @return MultiCloudSdkFactory的具体实现类
     */
    static MultiCloudSdkFactory getInstance(String accessKey, String accessSecret) {
        return init(accessKey, accessSecret);
    }

    /**
     * 邮件服务
     * @return MailProvider
     */
    MailProvider mailProvider();

    /**
     * 初始化近端包工厂类
     * @param accessKey accessKey
     * @param accessSecret accessSecret
     * @return MultiCloudSdkFactory的具体实现类
     */
    @SuppressWarnings("unchecked")
    static MultiCloudSdkFactory init(String accessKey, String accessSecret) {
        String key = accessKey + "_" + accessSecret;
        MultiCloudSdkFactory instance = INSTANCES.get(key);
        if (Objects.isNull(instance)) {
            String include = SpringContextUtil.getSdkEnv();
            Map<String, Object> map = SpringPropertiesCache.get(include);
            String type = SpringContextUtil.getSdkEnv();
            Asserts.notNull(type, "application.yml not config [spring.profiles.active], please check it");
            if (type.equals(CloudTypeConstant.ALI_YUN_TEST) || type.equals(CloudTypeConstant.ALI_YUN_PROD)) {
                Object aliyun = map.get("aliyun");
                Asserts.notNull(aliyun, String.format("cloud.type is %s, but not find %s config in application.yml", "aliyun", "aliyun"));
                instance = AliyunFactory.getInstance(accessKey, accessSecret);
                INSTANCES.put(key, instance);
                return instance;
            } else{
                throw new IllegalArgumentException("cloud.type is not support, please check it");
            }
        }
        return instance;
    }
}
