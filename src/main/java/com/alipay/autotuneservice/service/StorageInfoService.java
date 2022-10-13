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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.model.common.FileContent;
import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;

/**
 * @author dutianze
 * @version StorageInfoService.java, v 0.1 2022年04月19日 15:43 dutianze
 */
public interface StorageInfoService {

    /**
     * docker file 附加脚本
     */
    FileContent generateTMaestroOnBoard(String accessToken);

    /**
     * java启动脚本
     */
    FileContent generateDockerStartShell(String accessToken);

    /**
     * 产生集成agent的脚本
     *
     * @param accessToken
     * @param type
     * @return
     */
    FileContent generateIntegrateAgentScript(String accessToken, String type);

    /**
     * 一键安装twatch脚本
     */
    FileContent generateTmaestroEntryShell(String accessToken, String region, String clusterName);

    /**
     * twatch daemon set 脚本
     */
    FileContent generateDemonSetYaml(String accessToken);

    /**
     * 一键attach autoTune.jar
     * @param accessToken
     */
    FileContent generateInstallAttachAutoTuneJar(String accessToken, Integer attachId);

    /**
     * autoTuneYaml
     * @param accessToken
     */
    FileContent generateAutoTuneYaml(String accessToken);

    //void saveAppLogFile(GrpcCommon grpcCommon, Path filePath, String fileName, AppLogType appLogType) throws FileNotFoundException;
    //
    String uploadFileToS3(InputStream inputStream, String fileName);

    /**
     * download file from s3
     * @param s3Key
     * @return
     */
    S3Object downloadFileFromS3(String s3Key);

    InputStream downloadFileFromAliS3(String s3Key);
}