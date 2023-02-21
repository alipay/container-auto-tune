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

import com.alipay.autotuneservice.grpc.GrpcCommon;
import com.alipay.autotuneservice.model.ArthasHtmlType;
import com.alipay.autotuneservice.model.common.FileContent;
import com.alipay.autotuneservice.model.notice.NoticeRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author huoyuqi
 * @version StorageInfoService.java, v 0.1 2023年02月02日 2:23 下午 huoyuqi
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
     *
     * @param accessToken
     */
    FileContent generateInstallAttachAutoTuneJar(String accessToken, Integer attachId);

    /**
     * autoTuneYaml
     *
     * @param accessToken
     */
    FileContent generateAutoTuneYaml(String accessToken);

    void saveDumpFile(GrpcCommon grpcCommon, Path filePath, String fileName, String sessionId) throws IOException;

    String uploadFileToS3(InputStream inputStream, String fileName);

    /**
     * download file from s3
     *
     * @param s3Key
     * @return
     */
    S3Object downloadFileFromS3(String s3Key);

    InputStream downloadFileFromAliS3(String s3Key);

    ResponseEntity<byte[]> downloadFileUrlFromAliS3(String key);

    /**
     * 消息通知
     *
     * @param noticeRequest
     * @return
     */
    FileContent generateTuneNotice(NoticeRequest noticeRequest);

    /**
     * 报警消息通知
     *
     * @param noticeRequest
     * @return
     */
    FileContent generateAlarmNotice(NoticeRequest noticeRequest);

    /**
     * get Content From S3
     *
     * @param s3Key
     * @return
     * @throws IOException
     */
    String getContentFromS3(String s3Key) throws IOException;

    /**
     * get the content of the arthas html by ArthasHtmlType.
     *
     * @param type ArthasHtmlType instance
     * @return
     */
    FileContent getArthasHtmlFileContent(ArthasHtmlType type);
}