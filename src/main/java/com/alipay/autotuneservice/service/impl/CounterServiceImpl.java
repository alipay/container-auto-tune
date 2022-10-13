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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenqu
 * @version : CounterServiceImpl.java, v 0.1 2022年04月06日 16:53 chenqu Exp $
 */
@Service
public class CounterServiceImpl implements CounterService {

    private static final String TARGET_KEY = "targetCount";
    private static final String NOW_KEY    = "nowCount";
    private static final String TUNE_JVM   = "tuneJvm";

    @Autowired
    private RedisClient         redisClient;

    @Override
    public String tryAccess(Integer appId) {
        String appKey = String.valueOf(appId);
        long targetCount = redisClient.getIncrValue(generateTargetCount(appKey));
        if (targetCount <= 0) {
            return null;
        }
        long count = redisClient.incr(generateNowCount(appKey));
        if (count <= targetCount) {
            return redisClient.get(generateSaveJvmKey(appKey), String.class);
        }
        return null;
    }

    @Override
    public void reset(Integer appId, int count, String jvm) throws Exception {
        String appKey = String.valueOf(appId);
        redisClient.setIncrValue(generateTargetCount(appKey), count);
        redisClient.setIncrValue(generateNowCount(appKey), 0);
        redisClient.set(generateSaveJvmKey(appKey), jvm);
    }

    @Override
    public void delete(String key) {
        redisClient.del(generateTargetCount(key));
    }

    private String generateTargetCount(String key) {
        return String.format("%s_%s", TARGET_KEY, key);
    }

    private String generateNowCount(String key) {
        return String.format("%s_%s", NOW_KEY, key);
    }

    private String generateSaveJvmKey(String appName) {
        return String.format("%s_%s", TUNE_JVM, appName);
    }
}