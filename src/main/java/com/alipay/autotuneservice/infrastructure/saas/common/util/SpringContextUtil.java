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
package com.alipay.autotuneservice.infrastructure.saas.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author yiqi
 * @version 1.0
 * @description SpringContentUtil
 * @date 2022/7/8 15:19
 **/
public class SpringContextUtil {

    private static String   sdkEnv;
    private static String[] PROFILES;

    static {
        // idea启动
        String active = System.getProperty("spring.profiles.active");
        if (StringUtils.isEmpty(active)) {
            // jar包的方式启动
            String property = System.getProperty("sun.java.command");
            String[] items = property.split(" ");
            for (String item : items) {
                String args = "--spring.profiles.active";
                if (item.contains(args)) {
                    active = item.substring(item.indexOf(args) + args.length() + 1);
                    break;
                }
            }

            if (StringUtils.isEmpty(active)) {
                // 跑单测，没有 --spring.profiles.active，从默认的配置文件读
                try {
                    ResourceBundle application = ResourceBundle.getBundle("application");
                    active = application.getString("spring.profiles.active");
                } catch (MissingResourceException e) {
                    // 如果application.properties 无法正常读取，那就从application.yml读
                    Map<String, Object> map = SpringPropertiesCache.get("");
                    Map<String, Object> spring = (Map<String, Object>) map.get("spring");
                    Map<String, String> profiles = (Map<String, String>) spring.get("profiles");
                    active = profiles.get("active");
                }
            }

        }
        PROFILES = active.split(",");
    }

    /**
     * aws 测试环境 判断
     * @return boolean
     */
    public static boolean isAwsTest() {
        return Arrays.stream(PROFILES).anyMatch("aws-test"::equalsIgnoreCase);
    }

    /**
     * aliyun-test
     * @return boolean
     */
    public static boolean isAliyunTest() {
        return Arrays.stream(PROFILES).anyMatch("aliyun-test"::equalsIgnoreCase);
    }

    /**
     * aws-prod
     * @return boolean
     */
    public static boolean isAwsProd() {
        return Arrays.stream(PROFILES).anyMatch("aws-prod"::equalsIgnoreCase);
    }

    /**
     * aliyun-prod
     * @return boolean
     */
    public static boolean isAliyunProd() {
        return Arrays.stream(PROFILES).anyMatch("aliyun-prod"::equalsIgnoreCase);
    }

    public static void setSdkEnv(String env) {
        sdkEnv = env;
    }

    public static String getSdkEnv() {
        if (StringUtils.isNotBlank(sdkEnv)) {
            return sdkEnv;
        }
        if (isAwsTest()) {
            return "aws-test";
        } else if (isAwsProd()) {
            return "aws-prod";
        } else if (isAliyunTest()) {
            return "aliyun-test";
        } else if (isAliyunProd()) {
            return "aliyun-prod";
        }
        throw new IllegalArgumentException("当前环境不存在，请检查");
    }
}
