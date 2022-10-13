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
package com.alipay.autotuneservice.configuration.redis;

import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dutianze
 * @date 2022/4/24
 */
@SpringBootTest
class RedisClientTest {

    @Autowired
    private RedisClient redisClient;

    @Test
    void setVal() {
        redisClient.set("xx", "xx");
        System.out.println(redisClient.get("xx"));
    }

    @Test
    void setNx() {
        boolean nx1 = redisClient.setNx("xx", 1, 30, TimeUnit.SECONDS);
        Assertions.assertThat(nx1).isEqualTo(true);

        boolean nx2 = redisClient.setNx("xx", 1, 30, TimeUnit.SECONDS);
        Assertions.assertThat(nx2).isEqualTo(false);
    }

    @Test
    void setEx() {
        redisClient.setEx("xx", 1, 30, TimeUnit.SECONDS);
        Integer result = redisClient.get("xx", Integer.class);
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void put() {
        Integer result = redisClient.get("xx", Integer.class);
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void incrTest() {
        redisClient.del("xx");

        for (int i = 0; i < 10; i++) {
            long pre = redisClient.getIncrValue("xx");
            long incr = redisClient.incr("xx");
            Assertions.assertThat(pre).isEqualTo(incr - 1);
        }

        redisClient.setIncrValue("xx", 6);
        long incrValue = redisClient.getIncrValue("xx");
        Assertions.assertThat(incrValue).isEqualTo(6);
    }

    @Test
    void lrange() {
        List<Object> lrange = redisClient.lrange("xx");
        Assertions.assertThat(lrange).isNotNull();
    }
}