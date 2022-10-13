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

import java.util.List;

/**
 * @author huangkaifei
 * @version : TMaestroInstructionVO.java, v 0.1 2022年06月02日 9:22 AM huangkaifei Exp $
 */
@Data
public class TMaestroOnboardInstructionVO {

    /**
     * tmaestro入口接入步骤
     *
     * 负责集群向tmaestro注册, twatch, metric-server安装
     */
    List<InstallInstructionStep> tmaestroEntrySteps;

    /**
     * tmaestro 调参agent安装步骤
     *
     */
    List<InstallInstructionStep> tmaestroTuneAgentInstallSteps;
}