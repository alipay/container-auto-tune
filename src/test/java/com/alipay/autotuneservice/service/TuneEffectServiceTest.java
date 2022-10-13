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

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.schedule.TuneEffectTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author huoyuqi
 * @version TuneEffectServiceTest.java, v 0.1 2022年05月11日 11:40 上午 huoyuqi
 */
@SpringBootTest
public class TuneEffectServiceTest {

    @Autowired
    private TuneEffectService tuneEffectService;
    @Autowired
    private TuneEffectTask    tuneEffectTask;

    @Test
    void tuneProcessEffectTest() {
        System.out.println(JSON.toJSONString(tuneEffectService.tuneProcessEffect(63)));
    }

    @Test
    void tuneEffectTest() {
        tuneEffectService.triggerTuneEffect(378);
    }

    @Test
    void tuneEffectTask() {
        tuneEffectTask.invoke();
    }

}