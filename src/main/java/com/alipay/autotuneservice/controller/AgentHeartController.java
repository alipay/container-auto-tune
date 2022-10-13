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
package com.alipay.autotuneservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.agent.twatch.hearbeat.HeartBeatResponse;
import com.alipay.autotuneservice.agent.twatch.model.AgentActionRequest;
import com.alipay.autotuneservice.agent.twatch.monitor.ContainerMetricRunner;
import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.agent.BoundUnionRequest;
import com.alipay.autotuneservice.model.agent.CallBackRequest;
import com.alipay.autotuneservice.model.agent.ContainerMetricRequest;
import com.alipay.autotuneservice.model.statistics.StatisticsResponse;
import com.alipay.autotuneservice.service.AgentHeartService;
import com.alipay.autotuneservice.service.AgentInvokeService;
import com.alipay.autotuneservice.service.CommandService;
import com.alipay.autotuneservice.service.impl.AgentInvokeServiceImpl;
import com.alipay.autotuneservice.service.impl.AgentInvokeServiceImpl.InvokeType;
import com.alipay.autotuneservice.util.SystemUtil;
import com.alipay.autotuneservice.util.TraceIdGenerator;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/heartbeat")
public class AgentHeartController {

    @Autowired
    private AgentHeartService     agentHeartService;
    @Autowired
    private AgentInvokeService    agentInvokeService;
    @Autowired
    private ContainerMetricRunner containerMetricRunner;
    @Autowired
    private CommandService        commandService;

    /**
     * 接收客户端心跳
     *
     * @param agentName
     * @param date
     * @return
     */
    @NoLogin
    @GetMapping("/receive")
    public ServiceBaseResult<HeartBeatResponse> receive(@RequestParam(value = "agentName") String agentName,
                                                        @RequestParam(value = "date", required = false) long date) {
        try {
            log.info("receive HeartBeat from agent = {},date={}", agentName, date);
            Preconditions.checkArgument(StringUtils.isNotBlank(agentName), "agentName不能为空.");
            //TODO 记录心跳的每一次时间
            //询问指定agentName是否有触发的动作
            Set<AgentActionRequest> requests = agentHeartService.askAction(agentName);
            if (CollectionUtils.isEmpty(requests)) {
                //直接返回
                return ServiceBaseResult.successResult();
            }
            log.info("receive HeartBeat from agent:{}, date:{}, requests size:{}", agentName, date,
                requests.size());
            //组织返回信息
            HeartBeatResponse heartBeatResponse = new HeartBeatResponse();
            heartBeatResponse.setAgentName(agentName);
            heartBeatResponse.setActionList(new ArrayList<>(requests));
            return ServiceBaseResult.successResult(heartBeatResponse);
        } catch (Exception e) {
            log.error("receive heartbeat occurs an error.", e);
            return ServiceBaseResult.failureResult(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                String.format("处理agent=%s心跳失败, errMsg=%s", agentName, e.getMessage()));
        }
    }

    /**
     * 回调处理
     *
     * @param request
     * @return
     */
    @NoLogin
    @PostMapping("/doCallBack")
    public ServiceBaseResult<Boolean> doCallBack(@RequestBody CallBackRequest request) {
        try {
            if (request == null) {
                throw new RuntimeException("request is required,please check!");
            }
            TraceIdGenerator.clear();
            MDC.put(TraceIdGenerator.TRACE_ID, request.getTraceId());
            log.info("doCallBack, sessionId:{}", request.getSessionId());
            request.checkEmpty();
            return ServiceBaseResult.successResult(agentHeartService.doCallBack(request));
        } catch (Exception e) {
            log.error("doCallBack an error.", e);
            return ServiceBaseResult.failureResult(
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                String.format("doCallBack失败-->request=[%s],errMsg=%s",
                    JSONObject.toJSONString(request), e.getMessage()));
        }
    }

    /**
     * 绑定关系
     *
     * @param boundUnionRequest
     * @return
     */
    @NoLogin
    @PostMapping("/boundUnion")
    public ServiceBaseResult<Boolean> boundUnion(@RequestBody BoundUnionRequest boundUnionRequest) {
        try {
            log.info("boundUnion request boundUnionRequest isEmpty={}", boundUnionRequest == null);
            Preconditions.checkArgument(boundUnionRequest != null, "boundUnionRequest不能为空.");
            Preconditions.checkArgument(CollectionUtils.isNotEmpty(boundUnionRequest.getInfoDos()),
                "infoDos不能为空.");
            agentHeartService.boundUnion(boundUnionRequest.getInfoDos());
            return ServiceBaseResult.successResult();
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @PostMapping("/bound/containers/metric")
    public ServiceBaseResult<Boolean> boundContainerMetric(@RequestBody ContainerMetricRequest request) {
        try {
            Preconditions.checkArgument(request != null, "request不能为空.");
            containerMetricRunner.dispatchContainerMetric(request);
            return ServiceBaseResult.successResult();
        } catch (Exception e) {
            log.error("boundContainerMetric occurs an error.", e);
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/getPod")
    public ServiceBaseResult<String> getPod(@RequestParam(value = "podName") String podName) {
        try {
            String domainUrl = SystemUtil.getDomainUrl();
            System.out.println(domainUrl);
            String result = agentInvokeService.getPodEnv(AgentInvokeServiceImpl.InvokeType.SYNC,
                podName);
            return ServiceBaseResult.successResult(result);
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/getProcess")
    public ServiceBaseResult<String> getProcess(@RequestParam(value = "podName") String podName) {
        try {
            String result = agentInvokeService.getProcessByPod(
                AgentInvokeServiceImpl.InvokeType.SYNC, podName);
            return ServiceBaseResult.successResult(result);
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/execCmd")
    public ServiceBaseResult<String> execCmd(@RequestParam(value = "podName") String podName,
                                             @RequestParam(value = "cmd") String cmd,
                                             @RequestParam(value = "sync", required = false, defaultValue = "true") Boolean sync) {
        try {
            TraceIdGenerator.generateAndSet();
            log.info("execCmd start. podName={}, cmd={}, sync={}", podName, cmd, sync);
            InvokeType invokeType = sync ? InvokeType.SYNC : InvokeType.ASYNC;
            String res = agentInvokeService.execCmd(invokeType, podName, cmd);
            return ServiceBaseResult.successResult(res);
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/getActionResult")
    public ServiceBaseResult<String> getActionResult(@RequestParam(value = "sessionId") String sessionId) {
        try {
            log.info("getActionResult start. sessionId={}", sessionId);
            Object res = agentInvokeService.getAsyncActionResult(sessionId);
            return ServiceBaseResult.successResult(JSON.toJSONString(res));
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/podStats")
    public ServiceBaseResult<StatisticsResponse> getPodStats(@RequestParam(value = "podName") String podName) {
        try {
            StatisticsResponse podStats = agentInvokeService.getPodStats(podName);
            return ServiceBaseResult.successResult(podStats);
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/checkAgentInstall")
    public ServiceBaseResult<Boolean> checkAgentInstall(@RequestParam(value = "podName") String podName) {
        try {
            return ServiceBaseResult.successResult(agentInvokeService
                .checkPodIsInstallTuneAgent(podName));
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/checkPodHealthIndex")
    public ServiceBaseResult<String> checkPodHealthIndex(@RequestParam(value = "podName") String podName) {
        try {
            TraceIdGenerator.generateAndSet();
            Map<String, String> podHealthIndexes = agentInvokeService
                .getAllPodHealthIndexes(podName);
            return ServiceBaseResult.successResult(JSON.toJSONString(podHealthIndexes));
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/getPodJvm")
    public ServiceBaseResult<String> getPodJvm(@RequestParam(value = "podName") String podName) {
        try {
            return ServiceBaseResult.successResult(commandService.getPodJvm(podName));
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }
}
