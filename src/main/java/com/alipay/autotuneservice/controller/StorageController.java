/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.controller;

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.dao.StorageRepository;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.FileContent;
import com.alipay.autotuneservice.model.common.StorageInfo;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author dutianze
 * @version StorageController.java, v 0.1 2022年04月18日 16:53 dutianze
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/storage")
public class StorageController {

    @Autowired
    private StorageRepository  storageRepository;
    @Autowired
    private StorageInfoService storageInfoService;

    @NoLogin
    @PostMapping("/upload")
    public ServiceBaseResult<StorageInfo> uploadFile(@RequestParam(value = "file", required = false) MultipartFile file) {
        log.info("uploadLog, file:{}", file.getOriginalFilename());
        try {
            String s3Key = storageInfoService.uploadFileToS3(file.getInputStream(),
                    file.getOriginalFilename());
            StorageInfo storageInfo = new StorageInfo(s3Key, file.getOriginalFilename());
            StorageInfo savedStorage = storageRepository.save(storageInfo);
            return ServiceBaseResult.successResult(savedStorage);
        } catch (Exception e) {
            log.error("uploadFile error", e);
            return ServiceBaseResult.failureResult("upload file error");

        }
    }

    @RequestMapping(path = "/download/{fileName}", method = RequestMethod.GET)
    public ResponseEntity<StreamingResponseBody> download(HttpServletResponse response,
                                                          @PathVariable(name = "fileName") String fileName) {
        StorageInfo storageInfo = storageRepository.findByFileName(fileName);
        InputStream inputStream = wrapDownloadFile(fileName);
        return ResponseEntity.ok(this.createResponseStream(response, inputStream,
                storageInfo.getFileName()));
    }

    private InputStream wrapDownloadFile(String fileName) {
        throw new UnsupportedOperationException();
    }

    public InputStream downloadFileFromLocal(String fileName) {
        try {
            File file = new File(String.format("storage/%s", fileName));
            if (!file.exists()) {
                log.info("downloadFileFromLocal fileName={} not exits.", fileName);
                return null;
            }
            return new FileInputStream(file);
        } catch (Exception e) {
            log.error("downloadFileFromLocal occurs an error.", e);
            return null;
        }
    }

    /**
     * 下载docker启动脚本
     */
    @GetMapping(path = "/config_java_opts.sh")
    public ResponseEntity<StreamingResponseBody> downloadDockerStartScript(HttpServletResponse response,
                                                                           @RequestParam(value = "accessToken") String accessToken,
                                                                           @RequestParam(value = "type", required = false) String type) {
        log.info("download config_java_opts.sh, type={}", type);
        String inputAccessToken = StringUtils.isBlank(accessToken) ? UserUtil.getAccessToken()
                : accessToken;
        FileContent fileContent = storageInfoService.generateIntegrateAgentScript(inputAccessToken,
                type);
        return ResponseEntity.ok(this.createResponseStream(response,
                fileContent.getAsInputStream(), fileContent.getLength(), fileContent.getFileName()));
    }

    /**
     * 下载tmaestro-entry.sh文件
     */
    @NoLogin
    @GetMapping(path = "/tmaestro-entry.sh")
    public ResponseEntity<StreamingResponseBody> downloadTMaestroEntry2File(HttpServletResponse response,
                                                                            @RequestParam(value = "accessToken") String accessToken,
                                                                            @RequestParam(value = "region") String region,
                                                                            @RequestParam(value = "clusterName") String clusterName) {
        String inputAccessToken = StringUtils.isBlank(accessToken) ? UserUtil.getAccessToken()
                : accessToken;
        FileContent fileContent = storageInfoService.generateTmaestroEntryShell(inputAccessToken,
                region, clusterName);
        if (fileContent == null) {
            return ResponseEntity.internalServerError().body(null);
        }
        return ResponseEntity.ok(this.createResponseStream(response,
                fileContent.getAsInputStream(), fileContent.getLength(), fileContent.getFileName()));
    }

    /**
     * 下载installTuneAgent.sh文件
     */
    @NoLogin
    @GetMapping(path = "/installTuneAgent.sh")
    public ResponseEntity<StreamingResponseBody> downloadAttachAgentFile(HttpServletResponse response,
                                                                         @RequestParam(value = "accessToken", required = false)
                                                                         String accessToken,
                                                                         @RequestParam(value = "attachId", required = false)
                                                                         Integer attachId) {
        log.info("downloadAttachAgentFile, accessToken:{}, attachId:{}", accessToken, attachId);
        FileContent fileContent = storageInfoService.generateInstallAttachAutoTuneJar(accessToken,
                attachId);
        if (fileContent == null) {
            return ResponseEntity.internalServerError().body(null);
        }
        return ResponseEntity.ok(this.createResponseStream(response,
                fileContent.getAsInputStream(), fileContent.getLength(), fileContent.getFileName()));
    }

    /**
     * 下载tmaster.yaml文件
     */
    @NoLogin
    @GetMapping(path = "/tmaster.yml")
    public ResponseEntity<StreamingResponseBody> downloadTmasterYaml(HttpServletResponse response,
                                                                     @RequestParam(required = false, defaultValue = "")
                                                                     String accessToken) {
        FileContent fileContent = storageInfoService.generateAutoTuneYaml(accessToken);
        if (fileContent == null) {
            return ResponseEntity.internalServerError().body(null);
        }
        return ResponseEntity.ok(this.createResponseStream(response,
                fileContent.getAsInputStream(), fileContent.getLength(), fileContent.getFileName()));
    }

    private StreamingResponseBody createResponseStream(HttpServletResponse response, InputStream inputStream, int fileLength,
                                                       String filename) {
        return outputStream -> {
            try {
                response.setContentLength(fileLength);
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                int BUFFER_SIZE = 1024;
                int bytesRead;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } finally {
                if (response != null) {
                    response.getOutputStream().close();
                }
                inputStream.close();
            }
        };
    }

    private StreamingResponseBody createResponseStream(HttpServletResponse response, InputStream inputStream,
                                                       String filename) {
        return outputStream -> {
            try {
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                int BUFFER_SIZE = 1024;
                int bytesRead;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } finally {
                if (response != null) {
                    response.getOutputStream().close();
                }
                inputStream.close();
            }
        };
    }
}