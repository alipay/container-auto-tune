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
package com.alipay.autotuneservice.agent.twatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.model.agent.BoundUnionRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

import static com.alipay.autotuneservice.util.AgentConstant.TWATCH_TABLE;

@Slf4j
public class HeartBeatServiceTest {

    @Test
    public void test() {
        String str = "xx";
        BoundUnionRequest boundUnionRequest = JSON.parseObject(str, new TypeReference<BoundUnionRequest>() {});
        boundUnionRequest.getInfoDos().forEach(item -> {
            log.info("insert do agentName={} podName={}, containerId={}", item.getAgentName(), item.getPodName(), item.getContainerId());
        });
    }

    @Test
    public void decode() {
        String str = "xx";
        System.out.println(new String(
                Base64.getDecoder().decode(str)
        ));

        String str1 = "xx";
        Arrays.asList(str1.split("\\n")).stream().forEach(item -> {
            System.out.println(item);
        });

    }

    @Test
    public void test2() {
        String str = "IS_TUNE_AGENT_INSTALL2=true";
        String pattern = "^IS_TUNE_AGENT_INSTALL=";
        System.out.println(Pattern.compile(pattern).matcher(str).find());

    }
}