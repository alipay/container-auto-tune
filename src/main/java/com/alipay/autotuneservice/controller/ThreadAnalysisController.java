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

import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.thread.ThreadLogAnalysisService;
import com.alipay.autotuneservice.thread.model.ThreadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author huoyuqi
 * @version ThreadAnalysisController.java, v 0.1 2022年12月01日 6:30 下午 huoyuqi
 */
@Slf4j
@RestController
@RequestMapping("/api/thread")
public class ThreadAnalysisController {

    @Autowired
    private ThreadLogAnalysisService threadLogAnalysisService;

    @PostMapping("/analysis")
    public ServiceBaseResult<ThreadVO> analysis(@RequestParam("file") MultipartFile file) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> threadLogAnalysisService.threadAnalysis(file));
    }

    @GetMapping("/fileAnalysis")
    public ServiceBaseResult<ThreadVO> fileAnalysis(@RequestParam(value = "fileName") String fileName,
                                                    @RequestParam(value = "s3Key") String s3Key) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> threadLogAnalysisService.threadFileAnalysis(fileName, s3Key));

    }
}