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
import com.alipay.autotuneservice.agent.twatch.model.ActionMethodRequest;
import com.alipay.autotuneservice.controller.model.diagnosis.FileVO;
import com.alipay.autotuneservice.gc.model.GcVO;
import com.alipay.autotuneservice.gc.service.GcLogAnalysisService;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.service.StorageInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huoyuqi
 * @version GcAnalysisController.java, v 0.1 2022年10月27日 4:58 下午 huoyuqi
 */

@Slf4j
@RestController
@RequestMapping("/api/gc")
public class GcAnalysisController {

    @Autowired
    private GcLogAnalysisService gcLogAnalysisService;

    @Autowired
    private StorageInfoService storageInfoService;

    @GetMapping("/gcFileAnalysis")
    public ServiceBaseResult<GcVO> gcFileAnalysis(@RequestParam(value = "startTime", required = false) Long startTime,
                                                  @RequestParam(value = "endTime", required = false) Long endTime,
                                                  @RequestParam(value = "fileName") String fileName,
                                                  @RequestParam(value = "s3Key") String s3Key) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> gcLogAnalysisService.gcFileAnalysis(startTime, endTime, fileName, s3Key));
    }

    @GetMapping("/getHistory")
    public ServiceBaseResult<List<FileVO>> getHistory(@RequestParam(value = "appName") String appName,
                                                      @RequestParam(value = "ruleAction", required = false) RuleAction ruleAction,
                                                      @RequestParam(value = "podName", required = false) String podName,
                                                      @RequestParam(value = "commandStatus", required = false) CommandStatus commandStatus,
                                                      @RequestParam(value = "startTime", required = false) Long startTime,
                                                      @RequestParam(value = "endTime", required = false) Long endTime) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> gcLogAnalysisService.getHistory(ruleAction, commandStatus, podName, startTime, endTime, appName));
    }

    @PostMapping("/upload")
    public ServiceBaseResult<Boolean> upload(@RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "taskName") String taskName,
                                             @RequestParam(value = "ruleAction") RuleAction ruleAction,
                                             @RequestParam(value = "appName") String appName,
                                             @RequestParam(value = "startTime", required = false) Long startTime,
                                             @RequestParam(value = "endTime", required = false) Long endTime) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> gcLogAnalysisService.upload(file, taskName, ruleAction, startTime, endTime, appName));
    }

    @GetMapping("/download")
    public ServiceBaseResult<ResponseEntity<byte[]>> download(@RequestParam(value = "s3Key") String s3Key) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> storageInfoService.downloadFileUrlFromAliS3(s3Key));
    }

    @PostMapping("/autoUpload")
    public ServiceBaseResult<Boolean> autoUpload(@RequestParam(value = "unicode") String unicode,
                                                 @RequestParam(value = "podName") String podName,
                                                 @RequestParam(value = "ruleAction") RuleAction ruleAction,
                                                 @RequestParam(value = "taskName") String taskName,
                                                 @RequestParam(value = "appName") String appName,
                                                 @RequestParam(value = "time", required = false) Long time) {
        Map<String, Object> context = new HashMap<>();
        if (time != null) {
            ActionMethodRequest actionMethodRequest = new ActionMethodRequest();
            actionMethodRequest.setMethodName("gcState");
            context.put("ActionMethodRequest", JSON.toJSONString(actionMethodRequest));
            context.put("jvmProfilerWaitTime", String.valueOf(time));
            context.put("STATUS", "INIT");
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> gcLogAnalysisService.autoUpload(unicode, podName, ruleAction, taskName, context, appName));
    }

    @PostMapping("/deleteFile/{id}")
    public ServiceBaseResult<Boolean> deleteFile(@PathVariable(value = "id") Long id) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> gcLogAnalysisService.delete(id));
    }

}