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
package com.alipay.autotuneservice.model.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author huangkaifei
 * @version : EnvConfig.java, v 0.1 2022年08月19日 5:49 PM huangkaifei Exp $
 */
public class EnvConfig {

    // ******* Mongo ********* //
    public static final String MONGO_DOMAIN = System.getenv("MONGO_DOMAIN");
    public static final String MONGO_USERNAME = System.getenv("MONGO_USERNAME");
    public static final String MONGO_PASSWORD = System.getenv("MONGO_PASSWORD");
    public static final String MONGO_DATABASE = System.getenv("MONGO_DATABASE");

    // ******* redis ********* //
    public static final String REDIS_HOST = System.getenv("REDIS_HOST");


    public static String buildConfigCacheKey(){
        String key = String.format("MONGO_%s_%S", MONGO_USERNAME, MONGO_PASSWORD);
        return Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }
}