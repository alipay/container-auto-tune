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

import com.alipay.autotuneservice.agent.twatch.model.ExecCmdResult;
import com.alipay.autotuneservice.agent.twatch.model.PodHealthIndexEnum;
import com.alipay.autotuneservice.model.statistics.StatisticsResponse;
import com.alipay.autotuneservice.service.impl.AgentInvokeServiceImpl;
import com.alipay.autotuneservice.service.impl.AgentInvokeServiceImpl.InvokeType;

import java.util.Map;

/**
 * 暴露使用TWatch agent的接口
 * 该接口提供异步Action执行, 方法名为asyncGETxxx, 对应的action根据方法名来获取, 返回sessionId, 使用getActionResult根据sessionId查询结果
 * 异步方法执行：
 * 1. 生成sessionId
 * 2. 根据方法名利用javaassit生成动态类
 * 3. 放入sessionId, action到缓存里
 */
public interface AgentInvokeService {

    /**
     * 同步获取动作执行结果
     *
     * @return
     */
    Object getAsyncActionResult(String sessionId);

    /**
     * 异步获取pod的ENV
     *
     * @param podName
     * @return
     */
    String getPodEnv(AgentInvokeServiceImpl.InvokeType type, String podName);

    /**
     * getProcessByPod
     *
     * @param type
     * @param podName
     * @return
     */
    String getProcessByPod(InvokeType type, String podName);

    /**
     * 查询pod的CPU和内存等实时监控信息
     *
     * @param podName
     * @return
     */
    StatisticsResponse getPodStats(String podName);

    /**
     * 获取容器进程信息
     *
     * @param podName
     * @param containerId
     * @return
     */
    String listProcess(InvokeType type, String podName, String containerId);

    /**
     * execCmd
     *
     * @param type
     * @param podName
     * @param cmd
     * @return
     */
    String execCmd(InvokeType type, String podName, String cmd);

    ExecCmdResult execCmdV1(InvokeType type, String podName, String cmd);

    /**
     * 在pod的容器执行stats
     *
     * @param type
     * @param podName
     * @return
     */
    String execStats(InvokeType type, String podName);

    /**
     * check whether autoTuneAgent.jar is installed
     *
     * @param podName
     * @return
     */
    Boolean checkPodIsInstallTuneAgent(String podName);

    /**
     * Get pod health index
     *
     *  e.g getPodHealthIndexes(pod, PodHealthIndexEnum.USE_RECOMMEND_JVM_START_SUCCESS)
     *
     * @param podName
     * @param podHealthIndexEnum
     * @return
     */
    String getPodHealthIndex(String podName, PodHealthIndexEnum podHealthIndexEnum);

    /**
     * Get pod all health index
     *
     * @param podName
     * @return
     */
    Map<String, String> getAllPodHealthIndexes(String podName);
}
