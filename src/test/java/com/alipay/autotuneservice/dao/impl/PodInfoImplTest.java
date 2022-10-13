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

import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.service.AppInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class PodInfoImplTest {

    @Autowired
    private PodInfo        podInfo;

    @Autowired
    private AppInfoService appInfoService;

    @Test
    public void update() {
        List<PodInfoRecord> byAppId = podInfo.getByAppId(81);
        PodInfoRecord podInfoRecord = byAppId.get(0);
        podInfoRecord.setCpuCoreLimit(2);
        System.out.println(podInfoRecord);
    }

    @Test
    public void updatePodInstallTuneAgent() {
        try {
            PodInfoRecord podInfoRecord = new PodInfoRecord();
            podInfoRecord.setId(185936);
            podInfoRecord.setAgentInstall(0);
            podInfo.updatePodInstallTuneAgent(podInfoRecord);
        } catch (Exception e) {
        }
    }

    @Test
    public void getAppAgentInstallNums() {
        int appInstallTuneAgentNums = appInfoService.getAppInstallTuneAgentNums(912, null);
        System.out.println(appInstallTuneAgentNums);
    }

    @Test
    public void batchGetPodInstallTuneAgentNumsByAppIdTest(){
        List<Integer> appIds = Arrays.asList(11399,11403);
        List<PodInfoRecord> records = podInfo.batchGetPodInstallTuneAgentNumsByAppId(appIds);
        for (PodInfoRecord record : records) {
            System.out.println(record);
        }
        Map<Integer,Long> hashMap = new HashMap<>();
        appIds.forEach(appId-> hashMap.put(appId,records.stream().filter(record -> record.getAppId().equals(appId)).count()));
        hashMap.forEach((k,v)-> System.out.println("key is:"+k+" value is: "+v));
    }

    @Test
    public void getAllAlivePodByAccessToken() {
        List<PodInfoRecord> records = podInfo.getAllAlivePods();
        for (int i = 0; i < records.size(); i++) {
            System.out.println(records.get(i).getPodName());
        }
    }

}