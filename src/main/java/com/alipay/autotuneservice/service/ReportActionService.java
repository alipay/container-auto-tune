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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.dynamodb.bean.ThreadPoolMonitorMetricData;
import com.alipay.autotuneservice.model.JavaInfo;
import com.alipay.autotuneservice.model.agent.ThreadPoolRequest;

import java.util.List;

/**
 * @author dutianze
 * @version AppInfoService.java, v 0.1 2022年03月10日 17:20 dutianze
 */
public interface ReportActionService {

    void doJavaConfigInit(String params);

    JavaInfo findJavaInfo(String hostName);

    List<String> findLibs(Integer appId, String hostName, String libContains);

    List<ThreadPoolMonitorMetricData> findThreadPool(Integer appId, String hostName);

    List<ThreadPoolMonitorMetricData> findThreadPoolByContains(Integer appId, String hostName, String poolNameContains);

    void fixThreadPool(Integer appId, ThreadPoolRequest threadPoolRequest);

    boolean arthasInstall(Integer appId, String hostName);

    String arthasCommand(Integer appId, String hostName, String command);

    boolean checkArthasInstall(Integer appId, String hostName);
}