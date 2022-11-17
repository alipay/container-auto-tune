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
package com.alipay.autotuneservice.agent.twatch.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author huangkaifei
 * @version : PodHealthIndexEnum.java, v 0.1 2022年05月10日 11:13 AM huangkaifei Exp $
 */
public enum PodHealthIndexEnum {

    /**
     * autoTuneAgent.jars是否安装
     *
     * 解析内容： IS_TUNE_AGENT_INSTALL=true
     */
    IS_TUNE_AGENT_INSTALL("IS_TUNE_AGENT_INSTALL", "^IS_TUNE_AGENT_INSTALL=", SpiltFunc.SPLIT_EQUAL_SIGN_FUNC),
    /**
     * 检查应用使用推荐JVM参数是否正常启动
     *
     * 解析内容： USE_RECOMMEND_JVM_START_SUCCESS=true
     */
    USE_RECOMMEND_JVM_START_SUCCESS("USE_RECOMMEND_JVM_START_SUCCESS", "^USE_RECOMMEND_JVM_START_SUCCESS=",
            SpiltFunc.SPLIT_EQUAL_SIGN_FUNC),
    ;

    private String                         healthIndex;
    private String                         regxPattern;
    private Function<String, List<String>> parseFunc;

    PodHealthIndexEnum(String healthIndex, String regxPattern, Function<String, List<String>> parseFunc) {
        this.healthIndex = healthIndex;
        this.regxPattern = regxPattern;
        this.parseFunc = parseFunc;
    }

    public static PodHealthIndexEnum findIndex(String inputHealthIndex) {
        Preconditions.checkArgument(StringUtils.isNotBlank(inputHealthIndex), "inputHealthIndex must be not empty.");
        for (PodHealthIndexEnum podHealthIndexEnum : values()) {
            if (Pattern.compile(podHealthIndexEnum.regxPattern).matcher(inputHealthIndex).find()) {
                return podHealthIndexEnum;
            }
        }
        throw new UnsupportedOperationException(String.format("Can not find PodHealthIndexEnum by inputHealthIndex=%s", inputHealthIndex));
    }

    public Function<String, List<String>> getParseFunc() {
        return this.parseFunc;
    }

    public String getHealthIndex() {
        return healthIndex;
    }

    public String getRegxPattern() {
        return regxPattern;
    }

    public static String generatePodHealthIndexKey(String podName) {
        return String.format("POD_HEALTH_INDEX_%s", podName);
    }

    static class SpiltFunc {
        private static Function<String, List<String>> SPLIT_EQUAL_SIGN_FUNC = (str) -> {
            String[] split = str.split("=");
            if (split.length < 2) {
                return Lists.newArrayList();
            }
            return Lists.newArrayList(split[0], split[1]);
        };
    }
}