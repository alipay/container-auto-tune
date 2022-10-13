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

/**
 * @author huoyuqi
 * @version AppEnum.java, v 0.1 2022年06月13日 6:33 下午 huoyuqi
 */
public enum AppEnum {

    /**
     * 非Java应用
     */
    OTHER("OTHER"),

    /**
     * Java应用
     */
    JAVA("JAVA"),

    /**
     * 只安装agent
     */
    AGENT("AGENT"),

    /**
     * 安装agent和dockFile
     */
    DOCKERFILE("DOCKERFILE"),

    /**
     * 未知类型枚举
     */
    UNKNOWN("UNKNOWN");

    private final String code;

    AppEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}