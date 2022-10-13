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
package com.alipay.autotuneservice.model.envconfig;

import com.alipay.autotuneservice.fake.FakeRedissonClient;
import com.alipay.autotuneservice.model.common.EnvConfig;
import com.mongodb.connection.ClusterDescription;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author huangkaifei
 * @version : EnvConfigEnum.java, v 0.1 2022年08月19日 7:06 PM huangkaifei Exp $
 */
@Slf4j
public enum EnvConfigEnum {

    REDIS_CONFIG("redis") {
        @Override
        public boolean check(ConfigCheckContext context) {
            if (context == null) {
                return false;
            }
            if (context.getRedisClient().getRedissonClient() instanceof FakeRedissonClient) {
                return false;
            }
            return true;
        }

        @Override
        public void analyze(List<String> failedReason, List<String> suggests) {
            log.error("checkRedis connection failed.");
            failedReason.add("redis连接失败");
            suggests.add(String.format(
                "请在tmaestro-lte.yaml文件里检查redis配置：REDIS_HOST:%s, REDIS_PASSWORD:%s",
                EnvConfig.REDIS_HOST, "******"));
        }
    },
    MONGODB_CONFIG("MongoDB") {
        @Override
        public boolean check(ConfigCheckContext context) {
            try {
                if (context == null) {
                    return false;
                }
                ClusterDescription clusterDescription = context.getMongodb()
                    .getStandardMongoProvider().getMongoClient().getClusterDescription();
                return !StringUtils.equals(clusterDescription.getType().name(), "UNKNOWN");
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void analyze(List<String> failedReason, List<String> suggests) {
            log.error("checkMongoDB connection failed.");
            failedReason.add("mongoDB连接失败");
            suggests
                .add(String
                    .format(
                        "请在tmaestro-lte.yaml文件里检查mongoDB配置：MONGO_DOMAIN:%s, MONGO_USERNAME:%s, MONGO_PASSWORD:%s, MONGO_DATABASE:%s",
                        EnvConfig.MONGO_DOMAIN, EnvConfig.MONGO_USERNAME, "******",
                        EnvConfig.MONGO_DATABASE));
        }
    };

    private String name;

    EnvConfigEnum(String name) {
        this.name = name;
    }

    public abstract boolean check(ConfigCheckContext context);

    public abstract void analyze(List<String> failedReason, List<String> suggests);
}