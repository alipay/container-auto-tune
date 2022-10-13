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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.model.common.PodAttach;
import com.alipay.autotuneservice.model.common.PodAttachStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dutianze
 * @date 2022/6/17
 */
@SpringBootTest
class PodAttachServiceTest {

    @Autowired
    private PodAttachService podAttachService;

    static {
        System.setProperty("xx", "xx");
    }

    @Test
    void attachAgent() {
        podAttachService.attachAgent(0, 0);
    }

    @Test
    void updateStatus() {
        podAttachService.updateStatus(0, PodAttachStatus.INSTALLED);
    }

    @Test
    void findByPodId() {
        PodAttach podAttach = podAttachService.findByPodId(0);
        System.out.println(podAttach);
        Assertions.assertNotNull(podAttach);

    }

    @Test
    void findByPodIds() {
        List<PodAttach> podAttaches = podAttachService.findByPodIds(Stream.of(0, 0).collect(Collectors.toList()));
        podAttaches.forEach(System.out::println);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(podAttaches));
    }
}