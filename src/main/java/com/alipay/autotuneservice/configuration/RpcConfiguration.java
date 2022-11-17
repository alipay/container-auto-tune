/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.configuration;

import com.alipay.autotuneservice.base.cache.FIFOLocalCache;
import com.alipay.autotuneservice.base.cache.LocalCache;
import io.grpc.netty.NettyServerBuilder;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author dutianze
 * @version RpcConfiguration.java, v 0.1 2022年03月31日 18:04 dutianze
 */
@Slf4j
@Configuration
public class RpcConfiguration {

    @Autowired
    private Executor            grpcExecutor;
    @Autowired
    private ConstantsProperties constantProperties;

    @Bean
    public GrpcServerConfigurer keepAliveServerConfigurer() {
        return serverBuilder -> {
            if (serverBuilder instanceof NettyServerBuilder) {
                ((NettyServerBuilder) serverBuilder)
                        .keepAliveTime(30, TimeUnit.SECONDS)
                        .keepAliveTimeout(5, TimeUnit.SECONDS)
                        .executor(grpcExecutor)
                        .permitKeepAliveWithoutCalls(true);
            }
        };
    }

    @Bean
    public LocalCache<Object, Object> localCache() {
        return new FIFOLocalCache<>(1000);
    }
}