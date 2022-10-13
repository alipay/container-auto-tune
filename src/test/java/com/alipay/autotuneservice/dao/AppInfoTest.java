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

import com.alipay.autotuneservice.service.ConfigInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author huoyuqi
 * @version AppInfoTest.java, v 0.1 2022年05月13日 2:08 下午 huoyuqi
 */
@SpringBootTest
public class AppInfoTest {
    @Autowired
    private AppInfoRepository appInfo;

    @Autowired
    private ConfigInfoService configInfoService;

    @Test
    void test() {
        appInfo.appList("xx", "xx").forEach(
                item -> System.out.println(item.getAppName()));
    }

    @Test
    void test1() {
        System.out.println(configInfoService.findAPPConfigByAPPID(124).getAutoTune());
    }

}