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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.PodProcessInfo;
import com.alipay.autotuneservice.model.agent.ContainerMetric;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvertUtilsTest {

    @Test
    void convert2ContainerProcessInfos() {

        ConvertUtils.convert2ContainerProcessInfos(1, getContainerProcess());
    }

    @Test
    public void test() {
        String str = "xx";
    }

    private ContainerMetric getContainerProcess() {
        String str = "xx";

        ContainerMetric containerMetric = JSON.parseObject(str,
            new TypeReference<ContainerMetric>() {
            });
        return containerMetric;

    }

    @Test
    void convert2PodProcessInfo() {
        String str = "xx";
        List<PodProcessInfo> podProcessInfos = ConvertUtils.convert2PodProcessInfo(str);
        System.out.println(podProcessInfos);
    }

    @Test
    void convert2PodProcessInfoListV2() {
        String res = "xx";
        List<PodProcessInfo> podProcessInfos = ConvertUtils.convert2PodProcessInfoListV2(res);
        System.out.println(podProcessInfos);
    }
}