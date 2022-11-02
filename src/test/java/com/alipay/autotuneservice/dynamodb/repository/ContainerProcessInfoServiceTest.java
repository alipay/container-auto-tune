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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dynamodb.bean.ContainerProcessInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class ContainerProcessInfoServiceTest {

    @Autowired
    private ContainerProcessInfoService processInfoRepository;

    @Test
    public void batchInsertProcessInfo() {

        // given
        List<ContainerProcessInfo> list = new ArrayList<>();
        String containerId = "xx";
        long pid = 0;
        String cmd = "xx";
        list.add(build(containerId, pid, cmd));
        pid = 0;
        cmd = "xx";
        list.add(build(containerId, pid, cmd));
        pid = 0;
        cmd = "xx";
        list.add(build(containerId, pid, cmd));

        // when
        processInfoRepository.batchInsertProcessInfo(list);
    }

    @Test
    public void batchInsertProcessInfo2() {

        // given
        List<ContainerProcessInfo> list = new ArrayList<>();
        String containerId = "xx";
        long pid = 0;
        String cmd = "xx";
        list.add(build(containerId, pid, cmd));
        pid = 0;
        cmd = "xxr";
        list.add(build(containerId, pid, cmd));
        pid = 0;
        cmd = "xx";
        list.add(build(containerId, pid, cmd));

        // when
        processInfoRepository.batchInsertProcessInfo(list);
    }

    @Test
    public void queryProcessInfos() {
        String containerId = "xx";
        List<ContainerProcessInfo> list = processInfoRepository.queryProcessInfos(containerId);
        List<ContainerProcessInfo> collect = list.stream().filter(t -> t.getGmtCreated() > 0l).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(collect));
        System.out.println(list.size());
    }

    @Test
    public void insertProcessInfo() {
        // given
        String containerId = "xx";
        long pid = 0;
        String cmd = "xx";
        ContainerProcessInfo processInfo = build(containerId, pid, cmd);
        processInfo.setGmtCreated(0l);
        processInfo.setPodName("xx");

        // then
        processInfoRepository.insertProcessInfo(processInfo);
    }

    @Test
    public void insertProcessInfo333() {
        // given

        ContainerProcessInfo processInfo = new ContainerProcessInfo();
        processInfo.setGmtCreated(0l);
        processInfo.setPodName("xx");
        processInfo.setContainerId("xx");

        // then
        processInfoRepository.insertProcessInfo(processInfo);
    }

    @Test
    public void insertProcessInfo1() {
        // given
        String str = "xx";
        ContainerProcessInfo processInfo = JSON.parseObject(str,
            new TypeReference<ContainerProcessInfo>() {
            });
        // then
        processInfoRepository.insertProcessInfo(processInfo);
    }

    private ContainerProcessInfo build(String containerId, long pid, String cmd) {
        ContainerProcessInfo processInfo = new ContainerProcessInfo();
        processInfo.setContainerId(containerId);
        long createTime = System.currentTimeMillis();
        processInfo.setGmtCreated(createTime);

        processInfo.setUSER("xx");
        processInfo.setPID(pid);
        processInfo.setCPU(0);
        processInfo.setMEM(0);
        processInfo.setVSZ(0);
        processInfo.setRSS(0);
        processInfo.setTTY("xx");
        processInfo.setSTAT("xx");
        processInfo.setSTART("xx");
        processInfo.setTIME("xx");
        processInfo.setCOMMAND(cmd);
        System.out.println(JSON.toJSONString(processInfo));
        return processInfo;
    }

    @Test
    public void findProcessInfo() {
        String containerId = "xx";
        long pid = 1;
        ContainerProcessInfo processInfo = processInfoRepository.findProcessInfo(containerId, pid);
        System.out.println(processInfo);
    }

    @Test
    public void updateProcessInfo() {
        String containerId = "xx";
        long pid = 1;
        String cmd = "xx";
        ContainerProcessInfo processInfo = build(containerId, pid, cmd);

    }

    @Test
    public void saveProcessInfos() {
        String str = "xx";
        String containerId = "xx";
        String podName = "xx";
        Integer appId = 0;
        processInfoRepository.saveProcessInfos(appId, podName, containerId, str);
    }

}