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

import com.alipay.autotuneservice.controller.model.monitor.AppBasicInfoVO;
import com.alipay.autotuneservice.controller.model.monitor.AppIndicatorVO;
import com.alipay.autotuneservice.controller.model.monitor.PodIndicatorVO;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.monitor.MonitorService;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huoyuqi
 * @version MonitorControl.java, v 0.1 2022年10月17日 8:14 下午 huoyuqi
 */

@Slf4j
@RestController
@RequestMapping("/api/monitor")
public class MonitorControl {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private AppInfoRepository appInfoRepository;

    @GetMapping("/appIndicator")
    public ServiceBaseResult<AppIndicatorVO> appIndicator(@RequestParam(value = "appId") Integer appId,
                                                          @RequestParam(value = "type", required = false) String type,
                                                          @RequestParam(value = "startTime", required = false) Long startTime,
                                                          @RequestParam(value = "endTime", required = false) Long endTime) {
        log.info("appIndicator enter. appId：{}", appId);
        return ServiceBaseResult.invoker().paramCheck(() -> {
            Preconditions.checkArgument(null != appId && appId > 0, "appId is invalid.");
        }).makeResult(() -> {
                    AppInfoRecord record =  appInfoRepository.findByIdAndToken(UserUtil.getAccessToken(), appId);
                    if (record == null) {
                        return null;
                    }
                    return monitorService.getAppIndicators(appId, type, startTime, endTime);
                });
    }

    @GetMapping("/podIndicator")
    public ServiceBaseResult<PodIndicatorVO> podIndicator(@RequestParam(value = "podId") Integer podId,
                                                          @RequestParam(value = "podName") String podName,
                                                          @RequestParam(value = "type", required = false) String type,
                                                          @RequestParam(value = "startTime", required = false) Long startTime,
                                                          @RequestParam(value = "endTime", required = false) Long endTime) {
        log.info("podIndicator enter. podId: {}", podId);
        return ServiceBaseResult.invoker().paramCheck(() -> {
            Preconditions.checkArgument(null != podId && podId > 0, "podId is invalid.");
            Preconditions.checkArgument(StringUtils.isNotEmpty(podName), "podName is invalid.");
        }).makeResult(() -> monitorService.getPodIndicators(podId, podName, type, startTime, endTime));
    }

    @GetMapping("/basicInfo")
    public ServiceBaseResult<AppBasicInfoVO> basicInfo(@RequestParam(value = "appId") Integer appId) {
        log.info("basicInfo enter. appId: {}", appId);
        return ServiceBaseResult.invoker().paramCheck(() -> {
            Preconditions.checkArgument(null != appId && appId > 0, "appId is invalid.");
        }).makeResult(() -> {
            AppInfoRecord record =  appInfoRepository.findByIdAndToken(UserUtil.getAccessToken(), appId);
            if (record == null) {
                return null;
            }
            return monitorService.getAppBasicInfo(appId);
        });
    }

}