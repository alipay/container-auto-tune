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

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.dynamodb.bean.HealthCheckData;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.MultiCloudSdkFactory;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.aliyun.model.BodyTypeEnum;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.aliyun.model.Mail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenqu
 * @version : HealthCheckTest.java, v 0.1 2022年04月26日 13:15 chenqu Exp $
 */
@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@DisplayName("health check Unit Tests")
public class HealthCheckTest {

    @Autowired
    private AppHealthCheckService appHealthCheckService;

    /**
     * 检查提交是否正常
     */
    @Test
    public void submitHealtthCheck() {
        appHealthCheckService.submitHealthCheck(14839);
    }

    /**
     * 测试邮件
     */
    @Test
    public void emailTest() throws Exception {
        Mail mail = new Mail();
        List<String> a = new ArrayList<>();
        a.add("xx");
        a.add("xx");
        mail.setToAddress(a);
        mail.setContent("xx");
        mail.setSubject("xx");
        mail.setBodyTypeEnum(BodyTypeEnum.HTML);
        System.out.println("xx");
    }

}