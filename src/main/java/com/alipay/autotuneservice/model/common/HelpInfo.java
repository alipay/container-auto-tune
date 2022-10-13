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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dutianze
 * @version HelpInfo.java, v 0.1 2022年06月02日 11:45 dutianze
 */
@Data
public class HelpInfo implements Serializable {

    public int           id;

    /**
     * 第几个步骤
     */
    public int           step;

    /**
     * 标题
     */
    public String        title;

    /**
     * 文本
     */
    public String        message;

    /**
     * 类型
     */
    public HelpType      helpType;

    @JsonIgnore
    public String        createdBy;

    @JsonIgnore
    public String        updatedBy;

    @JsonIgnore
    public LocalDateTime createdTime;

    @JsonIgnore
    public LocalDateTime updatedTime;

    public enum HelpType {

        // 安装三合一脚本
        TWATCH,

        // 安装dockerfile
        DOCKER,

        // attach agent
        ATTACH_AGENT
    }
}