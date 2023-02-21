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
package com.alipay.autotuneservice.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version EnvHandler.java, v 0.1 2022年04月29日 17:56 dutianze
 */
@Component
public class EnvHandler {

    private final Environment environment;

    public EnvHandler(Environment environment) {
        this.environment = environment;
    }

    public boolean isDev() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.stream(activeProfiles).anyMatch(e -> e.equalsIgnoreCase("dev"));
    }

    public boolean isEnvContain(String env) {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.stream(activeProfiles).anyMatch(r -> StringUtils.contains(r.toLowerCase(), env.toLowerCase()));
    }

    public List<String> getEnv() {
        return Arrays.stream(environment.getActiveProfiles()).collect(Collectors.toList());
    }
}