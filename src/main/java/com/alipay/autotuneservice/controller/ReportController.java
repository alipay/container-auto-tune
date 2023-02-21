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

import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.grpc.GrpcCommon;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.auto.tune.client.MetricsGrpcRequest;
import com.googlecode.protobuf.format.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenqu
 * @version : ReportController.java, v 0.1 2022年09月08日 14:17 chenqu Exp $
 */
@Slf4j
@RestController
@RequestMapping("/api/channel")
public class ReportController {

    @Autowired
    private JvmMonitorMetricRepository jvmMetriRepository;
    @Autowired
    private AppInfoService             appInfoService;

    @RequestMapping(value = "/report", method = RequestMethod.POST)
    public String report(@RequestBody String request) {
        try {
            MetricsGrpcRequest.Builder builder = MetricsGrpcRequest.newBuilder();
            JsonFormat.merge(request, builder);
            MetricsGrpcRequest grpcRequest = builder.build();
            log.info("receive request:{}", grpcRequest.toString());
            //初始化
            GrpcCommon grpcCommon = GrpcCommon.build(grpcRequest.getSystemCommon());
            // metrics
            jvmMetriRepository.insertGCData(ConvertUtils.convert2JvmMonitorMetricData(grpcRequest, appInfoService));
            // app tag
            AppTag appTag = AppTag.builder()
                    .withJavaVersion(grpcCommon.getJavaVersion())
                    .withCollector(GarbageCollector.matchGarbageCollector(grpcCommon.getCollectors()))
                    .build();
            appInfoService.patchAppTag(grpcCommon, appTag);
            return "receive metrics success";
        } catch (Exception e) {
            log.error("receive metrics error", e);
            return "receive metrics error";
        }
    }
}