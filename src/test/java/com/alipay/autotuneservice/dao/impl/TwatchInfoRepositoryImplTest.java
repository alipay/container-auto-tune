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
package com.alipay.autotuneservice.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.TwatchInfoRepository;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TwatchInfoRepositoryImplTest {

    @Autowired
    private TwatchInfoRepository twatchInfoRepository;

    //@Test
    void insert() {
        TwatchInfoDo twatchInfoDo = build();
        twatchInfoRepository.insert(twatchInfoDo);
    }

    private TwatchInfoDo build(){
        String str = "{\n"
                + "  \"containerId\": \"xxxx\",\n"
                + "  \"nameSpace\": \"tmaster\",\n"
                + "  \"containerName\": \"test-container\",\n"
                + "  \"agentName\": \"test-agent\",\n"
                + "  \"gmtModified\": 1667213287856,\n"
                + "  \"podName\": \"test=pod\",\n"
                + "  \"dtPeriod\": 1667213287856,\n"
                + "  \"nodeName\": \"test-node\",\n"
                + "  \"nodeIp\": \"xxx\",\n"
                + "  \"imageId\": \"test-iamge\",\n"
                + "  \"labels\": \"\",\n"
                + "  \"type\": \"container\",\n"
                + "  \"containerStarted\": 1667213235,\n"
                + "  \"command\": \"java -jar test.jar\"\n"
                + "}";

        return JSON.parseObject(str, new TypeReference<TwatchInfoDo>() {});
    }
}