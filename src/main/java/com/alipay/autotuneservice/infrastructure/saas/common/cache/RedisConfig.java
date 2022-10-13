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
package com.alipay.autotuneservice.infrastructure.saas.common.cache;

import com.alipay.autotuneservice.fake.FakeRedissonClient;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author huoyuqi
 * @version Redis.java, v 0.1 2022年02月09日 5:38 下午 huoyuqi
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.redis", name = "host")
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    /**
     * redisClient配置
     * @return RedisClient.class
     */
    @Bean
    public RedisClient aliyunJedisClient(RedissonClient redissonClient) {
        return new RedisClient(redissonClient);
    }

    /**
     * redisson配置
     * @return RedissonClient.class
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() throws Exception {
        try {
            String config = Objects.nonNull(redisProperties.getRedisson()) ? redisProperties
                .getRedisson().getConfig() : null;
            return RedisFactory.localRedisClient(redisProperties.getHost(),
                redisProperties.getPort(), redisProperties.getPassword(), config,
                redisProperties.isSsl());
        } catch (Exception e) {
            return new FakeRedissonClient();
        }
    }

}