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
import com.alipay.autotuneservice.service.StorageInfoService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author huoyuqi
 * @version JvmProfilerController.java, v 0.1 2022年12月21日 3:31 下午 huoyuqi
 */
@RestController
@RequestMapping("/api/jvmProfiler")
public class JvmProfilerController {

    @Autowired
    private StorageInfoService storageInfoService;

    @GetMapping()
    public ServiceBaseResult<String> jvmProfiler(@RequestParam(value = "s3Key") String s3Key) {

        InputStream inputStream = storageInfoService.downloadFileFromAliS3(s3Key);
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> IOUtils.toString(inputStream, StandardCharsets.UTF_8));
    }
}