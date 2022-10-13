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
import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.controller.model.meter.ValidateMeterResult;
import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.dao.MeterMetaInfoRepository;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.meter.MeterService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huangkaifei
 * @version : MeterController.java, v 0.1 2022年08月22日 9:14 PM huangkaifei Exp $
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/meter")
public class MeterController {

    @Autowired
    private MeterMetaInfoRepository metaInfoRepository;

    @Autowired
    private MeterService            meterService;

    /**
     * 测试要注册监控连接
     *
     * @param request
     * @return
     */
    @PostMapping("/connection/validation")
    public ServiceBaseResult<ValidateMeterResult> validateMeter(@RequestBody MeterMeta request) {
        log.info("validateConnection request={}", request);
        return ServiceBaseResult.invoker().paramCheck(() -> {
            Preconditions.checkArgument(request != null, "request不能为空");
        }).makeResult(() -> {
            return meterService.validateMeter(request);
        });
    }

    /**
     * 注册监控信息
     * 对应前端确认连接
     *
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ServiceBaseResult<Boolean> register(@RequestBody MeterMeta request) {
        log.info("meter register, request={}", JSON.toJSONString(request));
        return ServiceBaseResult.invoker().paramCheck(() -> {
            Preconditions.checkArgument(StringUtils.isNotBlank(request.getMeterDomain()), "meterDomain can not be empty.");
            Preconditions.checkArgument(CollectionUtils.isNotEmpty(request.getMetricList()), "metricList can not be empty.");
            Preconditions.checkArgument(StringUtils.isNotBlank(request.getMeterName()), "Meter name can not be empty.");
            Preconditions.checkArgument(request.getAppId() != null, "AppId is invalid.");
        }).makeResult(() -> meterService.registerMeter(request));
    }

    /**
     * 查询应用注册的监控
     *
     * @param appId
     * @return
     */
    @GetMapping("/{appId}/list")
    public ServiceBaseResult<List<MeterMeta>> listAppMeters(@PathVariable Integer appId) {
        log.info("listAppMeters enter. appId={}", appId);
        return ServiceBaseResult.invoker().paramCheck(() -> {
            Preconditions.checkArgument(appId != null && appId > 0, "appId is invalid.");
        }).makeResult(() -> meterService.listMeters(appId));
    }

    /**
     * 删除应用注册的监控
     *
     * @param appId
     * @return
     */
    @PostMapping("/{appId}/{meterName}/delete")
    public ServiceBaseResult<Boolean> deleteMeter(@PathVariable Integer appId, @PathVariable String meterName) {
        log.info("deleteMeter enter. appId={}, meterName={}", appId, meterName);
        return ServiceBaseResult.invoker().paramCheck(() -> {
            Preconditions.checkArgument(appId != null && appId > 0, "appId is invalid.");
        }).makeResult(() -> meterService.deleteMeter(appId, meterName));
    }
}