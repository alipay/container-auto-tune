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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.agent.twatch.model.PodHealthIndexEnum;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class AgentInvokeServiceImplTest {

    @Autowired
    private RedisClient            redisClient;

    @Autowired
    private AgentInvokeServiceImpl agentInvokeService;

    @Test
    public void getPodHealthIndex() {
        String asyncActionResult = "xx";
        String s1 = JSON.parseObject(asyncActionResult, new TypeReference<String>() {});

        Map<String, String> collect = Arrays.asList(s1.split("\n")).stream().map(item -> {
                    try {
                        String decodeItem = new String(Base64.getDecoder().decode(item));
                        PodHealthIndexEnum healthIndexEnum = PodHealthIndexEnum.findIndex(decodeItem);
                        return healthIndexEnum.getParseFunc().apply(decodeItem);
                    } catch (Exception e) {
                        return Lists.newArrayList();
                    }
                })
                .filter(item -> CollectionUtils.isNotEmpty(item) && item.size() >= 2)
                .collect(Collectors.toMap(item -> (String) item.get(0), item -> (String) item.get(1), (e, u) -> e));

        System.out.println(collect);
    }
}