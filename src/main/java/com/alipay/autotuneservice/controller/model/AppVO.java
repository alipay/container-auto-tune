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
package com.alipay.autotuneservice.controller.model;

import com.alipay.autotuneservice.model.common.AppEnum;
import lombok.Data;

/**
 * @author huoyuqi
 * @version AppVO.java, v 0.1 2022年04月19日 7:30 下午 huoyuqi
 */
@Data
public class AppVO {

    /**
     * 应用名称
     */
    private String  appName;

    /**
     * 应用id
     */
    private Integer id;

    /**
     * 单机数量
     */
    private Integer agentNum;

    /**
     * node数量
     */
    private Integer nodeNum;

    /**
     * pod 数量
     */
    private Integer podNum;

    /**
     * 是否检测
     */
    private Boolean checkStatus;

    /**
     * 是否自动优化
     */
    private Boolean tuneStatus;

    /**
     * 减少多少CPU
     */
    private Integer cpuNum;

    /**
     * 减少多少MEM
     */
    private Integer memNum;

    /**
     * 减少多少FGC_COUNT
     */
    private Integer fgcCount;

    /**
     * 减少多少FGC_TIME
     */
    private Integer fgcTime;

    /**
     * 应用类型是 java、go等
     */
    private String  appType;

    /**
     * app的install类型 分为四种 非java类型 Java类型 安装agent 安装agent和dockerFile
     */
    private AppEnum appEnum;

    private String  clusterName;
}