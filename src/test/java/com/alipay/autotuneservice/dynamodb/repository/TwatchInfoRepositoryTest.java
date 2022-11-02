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
package com.alipay.autotuneservice.dynamodb.repository;

import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TwatchInfoRepositoryTest {

    @Autowired
    private TwatchInfoService repository;

    @Test
    void findInfoByPod() {
        List<TwatchInfoDo> infoByPod = repository.findInfoByPod("xx");
        System.out.println(infoByPod);
    }

    @Test
    void findInfoByContainerId() {
        List<TwatchInfoDo> infoByPod = repository.findInfoByContainerId("xx");
        System.out.println(infoByPod);
    }
}