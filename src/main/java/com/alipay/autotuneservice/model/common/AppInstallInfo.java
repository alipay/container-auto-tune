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

import lombok.Data;

/**
 * @author huangkaifei
 * @version : AppInstallInfo.java, v 0.1 2022年06月14日 11:20 AM huangkaifei Exp $
 */
@Data
public class AppInstallInfo {
    /**
     * 是否安装autoTuneAgent
     */
    private boolean installAutoAgent      = false;

    /**
     * 是否集成TMaestro dockerfile
     */
    private boolean integrateDockerFile   = false;

    /**
     * 安装tuneAgent数量
     */
    private Integer installTuneAgentNums  = -1;

    /**
     * attach tuneAgent数量
     */
    private Integer attachTuneAgentNums   = -1;

    /**
     * 安装dockerfile数量
     */
    private Integer installDockerfileNums = -1;
}