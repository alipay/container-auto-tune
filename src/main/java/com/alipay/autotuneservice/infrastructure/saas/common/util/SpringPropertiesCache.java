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
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiqi
 * @version 1.0
 * @description Spring配置文件缓存
 * @date 2022/7/11 13:05
 **/
public class SpringPropertiesCache {

    private static final Map<String, Map<String, Object>> PROFILE_CACHE = new ConcurrentHashMap<>();

    private static final Yaml                             PARSER        = new Yaml();

    private static String[]                               CONFIG_PATHS  = new String[] { "",
            "config/"                                                  };

    public static synchronized Map<String, Object> get(String profile) {
        if (PROFILE_CACHE.get(profile) == null) {
            profile = StringUtils.isBlank(profile) ? "" : "-" + profile;
            // 按照Spring的逻辑，循环该指定目录下（classpath:,classpath:/config）的配置文件
            boolean flag = readConfig(profile, profile);
            if (flag) {
                // 如果都没配置，默认读取 application.yml里的内容，如果这里面还没有，那没救了
                readConfig(profile, "");
            }
        }
        return PROFILE_CACHE.get(profile);
    }

    @SuppressWarnings("unchecked")
    private static boolean readConfig(String profile, String fileName) {
        boolean flag = true;
        for (String path : CONFIG_PATHS) {
            try (InputStream resourceAsStream = SpringPropertiesCache.class.getClassLoader()
                .getResourceAsStream(String.format("%sapplication%s.yml", path, fileName))) {
                if (resourceAsStream != null) {
                    Map map = PARSER.loadAs(resourceAsStream, Map.class);
                    // 如果已经存在了缓存，那么，两者进行合并，后来的覆盖新来的
                    if (PROFILE_CACHE.containsKey(profile)) {
                        Map<String, Object> existCache = PROFILE_CACHE.get(profile);
                        existCache.putAll(map);
                    } else {
                        PROFILE_CACHE.put(profile, map);
                    }
                    // 读取到了想读取的配置文件，将标记设置为 false
                    flag = false;
                }
            } catch (IOException e) {
                throw new RuntimeException(String.format("load application%s.yaml fail", profile));
            }
        }
        return flag;
    }
}
