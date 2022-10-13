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
package com.alipay.autotuneservice.dao;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.controller.model.HistoryTunePlanVo;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckInfoRecord;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.service.TunePlanService;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version HealthCheckInfoTest.java, v 0.1 2022年04月25日 7:30 下午 huoyuqi
 */
@Slf4j
@SpringBootTest
public class HealthCheckInfoTest {

    @Autowired
    private HealthCheckInfo    healthCheckInfo;

    @Autowired
    private TunePlanRepository repository;

    @Test
    void selectTest() {
        HealthCheckInfoRecord healthCheckInfoRecord = healthCheckInfo.selectByAccessTokenAndAppId(
            "xx", 0);
        log.info("------------------healthCheckInfoRecord.id is: {}", healthCheckInfoRecord.getId());
    }

    @Test
    void insert() {
        HealthCheckInfoRecord healthCheckInfoRecord = healthCheckInfo.selectByAccessTokenAndAppId("xx", 0);

        List<List<String>> arrayList = new ArrayList<>();
        List<String> problemList1 = Arrays.asList("RT", "YGCCOUNT", "YGCTIME", "FGCCOUNT", "FGCTIME", "HEAPMEMORY", "OLDUTIL", "GCTYPE");
        List<String> problemList = Arrays.asList("RT", "FGCCOUNT", "FGCTIME");
        problemList1.forEach(item -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (problemList.contains(item)) {
                System.out.println("hello");
            }
        });

    }

    @Test
    void search() {
        Date end = null;
        Date start = null;
        List<TunePlan> tunePlans = repository.findTunePlanByAppId(0);
    }
}