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

import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.AppMonitorInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author huoyuqi
 * @version K8sAccessTokenInfoTest.java, v 0.1 2022年04月19日 2:21 下午 huoyuqi
 */
@SpringBootTest
public class K8sAccessTokenInfoTest {
    @Autowired
    private K8sAccessTokenInfo      k8sAccessTokenInfo;

    @Autowired
    private FillMetaDataMonitorTask fillDynamodbTask;

    @Test
    void select() {
        List<K8sAccessTokenInfoRecord> k8sAccessTokenInfoRecordList = k8sAccessTokenInfo.getK8sAccessTokenInfoRecord();
        k8sAccessTokenInfoRecordList.forEach(item->{
            System.out.println(item.getAccessToken());
        });
    }

    @Test
    void task() {
        fillDynamodbTask.doTask();
    }

    @Test
    void testInsert() {
        AppMonitorInfo appMonitorInfo = new AppMonitorInfo();
        appMonitorInfo.setAppName("xx");
        appMonitorInfo.setGmtCreated(System.currentTimeMillis());
        appMonitorInfo.setNameSpace("xx");
    }

}