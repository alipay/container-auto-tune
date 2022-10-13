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

import lombok.Data;

/**
 * 集群报告, 属于 PRD 1.0 的内容
 *
 * @author t-rex
 * @version AppInfoVO.java, v 0.1 2022年02月14日 3:51 下午 t-rex
 */
@Data
public class AppInfoVO {

    private String jvmBaseLine;
    // 集群id
    private int    appId;

    // 集群名字
    private String appName;

    /**
     * 单个应用的 jvm 概要
     */
    private double ygcCount;
    private double fgcCount;
    private double gcTimes;
    private int    reportCount;
    private int    nodeCount;

}