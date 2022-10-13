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

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author huoyuqi
 * @version EffectTypeEnum.java, v 0.1 2022年05月05日 12:18 下午 huoyuqi
 */
public enum JvmParamTypeEnum {

    Xms("-Xms", ""), Xmx("-Xmx", ""), Xmn("-Xmn", ""), Xss("-Xss", ""), CMSInitiatingOccupancyFraction(
                                                                                                       "XX:CMSInitiatingOccupancyFraction",
                                                                                                       "="), ;

    private final String jvmParam;
    private final String append;

    JvmParamTypeEnum(String jvmParam, String append) {
        this.jvmParam = jvmParam;
        this.append = append;
    }

    public static JvmParamTypeEnum getInstance(String jvmParam) {
        Optional<JvmParamTypeEnum> optional = Arrays.stream(JvmParamTypeEnum.values())
                .filter(type -> StringUtils.equals(type.jvmParam, jvmParam))
                .findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public String getJvmParam() {
        return jvmParam;
    }

    public String getAppend() {
        return append;
    }
}