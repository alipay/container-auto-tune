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

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.ProxyService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author fangxueyang
 * @version ProxyController.java, v 0.1 2022年08月08日 17:57 hongshu
 */
@Slf4j
@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * query twatchInfo list by pod name
     */
    @NoLogin
    @RequestMapping("/twatchs")
    public ServiceBaseResult<List<TwatchInfoDo>> findTwatchsByPodName(@RequestParam(value = "podName") String podName) {
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> Preconditions.checkArgument(StringUtils.isNotEmpty(podName), "podName is empty"))
                .makeResult(() -> this.proxyService.findTwatchsByPodName(podName));
    }

    /**
     * query ContainerStatistics list by time range and containerId
     */
    @NoLogin
    @RequestMapping("/containerStatistics")
    public ServiceBaseResult<List<ContainerStatistics>> findCStatistic(@RequestParam(value = "containerId") String containerId,
                                                                       @RequestParam(value = "start") Long start,
                                                                       @RequestParam(value = "end") Long end) {
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> {
                    Preconditions.checkArgument(StringUtils.isNotEmpty(containerId), "containerId is empty");
                    Preconditions.checkArgument(start != null && start > 0, "Input param start must be positive.");
                    Preconditions.checkArgument(end != null && end > 0, "Input param end must be positive.");
                })
                .makeResult(() -> this.proxyService.findCStatistic(containerId, start, end));
    }

    /**
     * query JvmMonitorMetricData list by time range and podName
     */
    @NoLogin
    @RequestMapping("/jvmMonMetricRange")
    public ServiceBaseResult<List<JvmMonitorMetricData>> findJvmMonMetricRange(@RequestParam(value = "podName") String podName,
                                                                               @RequestParam(value = "start") Long start,
                                                                               @RequestParam(value = "end") Long end) {
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> {
                    Preconditions.checkArgument(StringUtils.isNotEmpty(podName), "podName is empty");
                    Preconditions.checkArgument(start != null && start > 0, "Input param start must be positive.");
                    Preconditions.checkArgument(end != null && end > 0, "Input param end must be positive.");
                })
                .makeResult(() -> Lists.newArrayList());
    }

    /**
     * query JvmMonitorMetricData list by time  and podName
     */
    @NoLogin
    @RequestMapping("/jvmMonMetricDay")
    public ServiceBaseResult<List<JvmMonitorMetricData>> findJvmMonMetricDay(@RequestParam(value = "podName") String podName,
                                                                             @RequestParam(value = "dt") Long dt) {
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> {
                    Preconditions.checkArgument(StringUtils.isNotEmpty(podName), "podName is empty");
                    Preconditions.checkArgument(dt != null && dt > 0, "Input param dt must be positive.");
                })
                .makeResult(() -> Lists.newArrayList());
    }

}
