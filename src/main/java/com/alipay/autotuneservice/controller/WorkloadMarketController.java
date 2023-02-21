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

import com.alipay.autotuneservice.controller.model.WorkLoadMarketVO;
import com.alipay.autotuneservice.dynamodb.bean.ThreadPoolMonitorMetricData;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.agent.ThreadPoolRequest;
import com.alipay.autotuneservice.model.constants.Constant;
import com.alipay.autotuneservice.service.ReportActionService;
import com.alipay.autotuneservice.service.WorkloadService;
import com.alipay.autotuneservice.service.chronicmap.ChronicleMapService;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version WorkloadMarketController.java, v 0.1 2022年10月26日 5:05 下午 huoyuqi
 */

@Slf4j
@RestController
@RequestMapping("/api/workloadMarket")
public class WorkloadMarketController {

    @Autowired
    private WorkloadService     workloadService;
    @Autowired
    private ReportActionService reportActionService;
    @Autowired
    private ChronicleMapService redisClient;

    @GetMapping
    public ServiceBaseResult<WorkLoadMarketVO> workloadMarket(@RequestParam(value = "start", required = false) Long start,
                                                              @RequestParam(value = "end", required = false) Long end) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> workloadService.getWorkLoadMarket(start, end));
    }

    @GetMapping("/{appId}/libs")
    public ServiceBaseResult<List<String>> libs(@PathVariable(value = "appId") Integer appId,
                                                @RequestParam(value = "hostName") String hostName,
                                                @RequestParam(value = "libContains", required = false) String libContains) {

        String key = String.format("libs_%s_%s", appId, hostName);
        Object obj = redisClient.get(key);
        List<String> libs;
        if (obj == null) {
            libs = reportActionService.findLibs(appId, hostName, null);
            if (CollectionUtils.isEmpty(libs)) {
                return ServiceBaseResult.invoker().makeResult(Lists::newArrayList);
            }
            redisClient.set(key, libs, 60 * 5);
        } else {
            libs = (List<String>) obj;
        }
        if (CollectionUtils.isNotEmpty(libs)) {
            Collections.sort(libs, String.CASE_INSENSITIVE_ORDER);
        }
        if (StringUtils.isEmpty(libContains)) {
            return ServiceBaseResult.invoker().makeResult(() -> libs);
        }
        return ServiceBaseResult.invoker().makeResult(
                () -> libs.stream().filter(lib -> StringUtils.contains(lib, libContains)).sorted().collect(Collectors.toList()));
    }

    @GetMapping("/{appId}/threadpool")
    public ServiceBaseResult<Map<String, Object>> threadpool(@PathVariable(value = "appId") Integer appId,
                                                             @RequestParam(value = "hostName") String hostName,
                                                             @RequestParam(value = "poolNameContains", required = false)
                                                                     String poolNameContains) {
        List<ThreadPoolMonitorMetricData> metricData = reportActionService.findThreadPoolByContains(appId, hostName, poolNameContains);
        log.info("metricData is:{}", metricData);
        return ServiceBaseResult.invoker()
                .makeResult(() -> ImmutableMap.of("totalNum", metricData.size(), "metricData", metricData));
    }

    @PostMapping("/{appId}/fixThreadpool")
    public ServiceBaseResult<Boolean> fixThreadpool(@PathVariable(value = "appId") Integer appId,
                                                    @RequestBody ThreadPoolRequest threadPoolRequest) {
        try {
            reportActionService.fixThreadPool(appId, threadPoolRequest);
            return ServiceBaseResult.invoker().makeResult(() -> Boolean.TRUE);
        } catch (Exception e) {
            log.error("fixThreadpool is error", e);
            return ServiceBaseResult.invoker().makeResult(() -> Boolean.FALSE);
        }
    }

    @GetMapping("/{appId}/arthas/install")
    public ServiceBaseResult<Boolean> install(@PathVariable(value = "appId") Integer appId,
                                              @RequestParam(value = "hostName") String hostName) {
        return ServiceBaseResult.invoker().makeResult(() -> reportActionService.arthasInstall(appId, hostName));
    }

    @GetMapping("/{appId}/arthas/command")
    public ServiceBaseResult<String> commandArthas(@PathVariable(value = "appId") Integer appId,
                                                   @RequestParam(value = "hostName") String hostName,
                                                   @RequestParam(value = "command") String command) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> {
                    Optional<String> optional = Constant.ARTHAS_CMD_WHITE_SET.stream().filter(command::contains).findAny();
                    if (!optional.isPresent()) {
                        throw new IllegalArgumentException(
                                String.format("The command: %s not is invalid, please check it and retry later.", command));
                    }
                })
                .makeResult(() -> reportActionService.arthasCommand(appId, hostName, command));
    }

    @GetMapping("/{appId}/checkInstall")
    public ServiceBaseResult<Boolean> checkArthasInstall(@PathVariable(value = "appId") Integer appId,
                                                         @RequestParam(value = "hostName") String hostName) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> {
                    log.info("checkArthasInstall start. appId={}, hostName={}", appId, hostName);
                    Preconditions.checkArgument(StringUtils.isNotBlank(hostName), "hostName is blank.");
                })
                .makeResult(() -> reportActionService.checkArthasInstall(appId, hostName));
    }
}