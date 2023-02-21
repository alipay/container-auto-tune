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

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 应用安装调参Agent请求
 *
 * @author huangkaifei
 * @version : InstallAgentRequest.java, v 0.1 2022年10月11日 7:52 PM huangkaifei Exp $
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallAgentRequest {

    /**
     * 应用名称, 必填
     */
    private String appName = "";
    /**
     * 应用所在的集群名称, 必填
     */
    private String clusterName = "";
    /**
     * 应用所在的namespace, 必填
     */
    private String namespace = "";
    /**
     * 应用的Java进程默认启动参数, 必填
     */
    private String appPreExecCmd = "";
    /**
     * 应用的Java进程默认启动参数, 必填
     */
    private String appDefaultJvm = "";
    /**
     * 应用启动的java进程的jar名称, 必填
     */
    private String appJarName = "";
    /**
     * The arguments passed to the main function.
     */
    private String appJarArguments = "";

    public void check() {
        Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clusterName can not empty.");
        Preconditions.checkArgument(StringUtils.isNotBlank(namespace), "namespace can not empty.");
        Preconditions.checkArgument(StringUtils.isNotBlank(appName), "appName can not empty.");
        Preconditions.checkArgument(StringUtils.isNotBlank(appJarName), "appJarName can not empty.");
    }
}