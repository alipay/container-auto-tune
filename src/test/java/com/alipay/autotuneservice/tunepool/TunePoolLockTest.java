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
package com.alipay.autotuneservice.tunepool;

import com.alipay.autotuneservice.AutotuneServiceApplication;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * 一种依托连接来进行的争抢锁实现
 *
 * @author chenqu
 * @version : TunePoolLockTest.java, v 0.1 2022年04月01日 13:17 chenqu Exp $
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutotuneServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class TunePoolLockTest {

    @Autowired
    private RedisClient              redisClient;

    static {
        System.setProperty("xx", "xx");
    }

    @Test
    public void lock() {
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(0);
                    redisClient.tryLock("xx", 0, r -> {
                        try {
                            TimeUnit.SECONDS.sleep(0);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    log.error("xx", e);
                }
            }
        }).start();
        try {
            TimeUnit.SECONDS.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lockTest() {
        redisClient.setNx("xx", 0, 0, TimeUnit.SECONDS);
        System.out.println(redisClient.get("xx", Integer.class));
        ;
    }

}