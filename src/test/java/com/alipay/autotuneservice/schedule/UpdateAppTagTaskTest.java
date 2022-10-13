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
package com.alipay.autotuneservice.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.common.AppTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author huoyuqi
 * @version UpdateAppTagTaskTest.java, v 0.1 2022年06月13日 9:32 下午 huoyuqi
 */

@SpringBootTest
public class UpdateAppTagTaskTest {

    @Autowired
    private UpdateAppTagTask  updateAppTagTask;

    @Autowired
    private AppInfoRepository appInfoRepository;

    @Test
    void updateAppTagTaskTest() {
        updateAppTagTask.doTask();
    }

    @Test
    void updateAppTag() {
        AppInfoRecord record = appInfoRepository.getById(0);
        AppTag tag = record.getAppTag() != null ? JSON.parseObject(record.getAppTag(),
            new TypeReference<AppTag>() {
            }) : null;
        System.out.println(JSONObject.toJSONString(tag));
        if (tag != null) {
            tag.setInstallAgent(Boolean.TRUE);
            tag.setInstallDockFile(Boolean.FALSE);
            record.setAppTag(JSONObject.toJSONString(tag));
            appInfoRepository.updateAppTag(record);
        }
    }

}