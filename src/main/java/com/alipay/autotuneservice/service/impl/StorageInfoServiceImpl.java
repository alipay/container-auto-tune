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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.configuration.ConstantsProperties;
import com.alipay.autotuneservice.configuration.EnvHandler;
import com.alipay.autotuneservice.dao.AppLogRepository;
import com.alipay.autotuneservice.dao.CommandInfoRepository;
import com.alipay.autotuneservice.grpc.GrpcCommon;
import com.alipay.autotuneservice.model.ArthasHtmlType;
import com.alipay.autotuneservice.model.common.CloudType;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.common.FileContent;
import com.alipay.autotuneservice.model.common.IntegrateAgentEnum;
import com.alipay.autotuneservice.model.notice.NoticeContentEnum;
import com.alipay.autotuneservice.model.notice.NoticeRequest;
import com.alipay.autotuneservice.model.notice.NoticeType;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.util.SystemUtil;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author dutianze
 * @version StorageInfoServiceImpl.java, v 0.1 2022年04月19日 15:46 dutianze
 */
@Slf4j
@Service
public class StorageInfoServiceImpl implements StorageInfoService {

    private static final String DOCKER_START_SHELL_TEMPLATE = "config_java_opts.flt";
    public static final  String DOCKER_START_SHELL_FILE     = "config_java_opts.sh";

    private static final String TMAESTRO_ENTRY_TEMPLATE        = "tmaestro-entry.flt";
    private static final String ALIYUN_TMAESTRO_ENTRY_TEMPLATE = "aliyun-tmaestro-entry.flt";
    public static final  String TMAESTRO_ENTRY_FILE            = "tmaestro-entry.sh";

    private static final String TWATCH_DAEMON_SET_YAML_TEMPLATE = "twatch.flt";
    public static final  String TWATCH_DAEMON_SET_YAML_FILE     = "twatch.yml";

    private static final String TMAESTRO_ONBOARD_TEMPLATE = "tmaestro-onboard.flt";
    public static final  String TMAESTRO_ONBOARD_FILE     = "tmaestro-onboard";

    private static final String INSTALL_AUTO_TUNE_AGENT_TEMPLATE = "installTuneAgent.flt";
    private static final String INSTALL_AUTO_TUNE_AGENT_FILE     = "installTuneAgent.sh";

    private static final String AUTO_TUNE_AGENT_YAML_TEMPLATE = "autotuneAgentYaml.flt";
    private static final String AUTO_TUNE_AGENT_YAML_FILE     = "tmaster.yml";

    private static final String TUNE_NOTICE_TEMPLATE = "tune-notice.flt";
    public static final  String TUNE_NOTICE_FILE     = "tune-notice";

    private static final String TUNE_NOTICE_SUBMIT_TEMPLATE = "tune-notice-submit.flt";
    public static final  String TUNE_NOTICE_SUBMIT_FILE     = "tune-notice-submit";

    private static final String TUNE_NOTICE_ALARM_TEMPLATE = "tune-notice-alarm.flt";
    public static final  String TUNE_NOTICE_ALARM_FILE     = "tune-notice-alarm";

    private static final String TUNE_EMAIL_TEMPLATE = "tune-email.html";
    public static final  String TUNE_EMAIL_FILE     = "tune-email";

    private static final String TUNE_EMAIL_SUBMIT_TEMPLATE = "tune-email-submit.html";
    public static final  String TUNE_EMAIL_SUBMIT_FILE     = "tune-email-submit";

    private static final String TUNE_EMAIL_ALARM_TEMPLATE = "tune-email-alarm.html";
    public static final  String TUNE_EMAIL_ALARM_FILE     = "tune-email-alarm";

    @Autowired
    private AppLogRepository appLogRepository;

    @Autowired
    private CommandInfoRepository commandInfoRepository;
    @Autowired
    private ConstantsProperties   constantProperties;
    @Autowired
    private EnvHandler            envHandler;

    @Override
    public FileContent generateTMaestroOnBoard(String accessToken) {
        return getFileContent(accessToken, TMAESTRO_ONBOARD_FILE, TMAESTRO_ONBOARD_TEMPLATE);
    }

    @Override
    public FileContent generateDockerStartShell(String accessToken) {
        return getFileContent(accessToken, DOCKER_START_SHELL_FILE, DOCKER_START_SHELL_TEMPLATE);
    }

    @Override
    public FileContent generateIntegrateAgentScript(String accessToken, String type) {
        IntegrateAgentEnum agentEnum = IntegrateAgentEnum.find(type);
        return getFileContent(accessToken, agentEnum.getShellScript(), agentEnum.getTemplateFile());
    }

    private FileContent getFileContent(String accessToken, String dockerStartShellFile, String dockerStartShellTemplate) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(accessToken), "the accessToken can not be empty.");
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", accessToken);
            map.put("tmaestro_server_url", constantProperties.getDomainUrl());
            return new FileContent(dockerStartShellFile, this.generateShellScript(dockerStartShellTemplate, map));
        } catch (Exception e) {
            log.info("getFileContent for file={} occurs an error.", dockerStartShellFile, e);
            return null;
        }
    }

    @Override
    public FileContent generateTuneNotice(NoticeRequest noticeRequest) {
        try {

            Map<String, Object> map = new HashMap<>();
            map.put("title", noticeRequest.getNoticeContentEnum().getTitle());
            map.put("appName", noticeRequest.getAppName());
            map.put("planName", noticeRequest.getPlanName());
            map.put("date", noticeRequest.getDate());
            map.put("content", noticeRequest.getNoticeContentEnum().getContent());
            map.put("tmaestroWebUrl", noticeRequest.getPlanWebUrl());
            map.put("buttonType", noticeRequest.getNoticeButtonType().getType());
            if (NoticeType.WECHAT.equals(noticeRequest.getNoticeType())) {
                if (NoticeContentEnum.SUBMIT.equals(noticeRequest.getNoticeContentEnum())) {
                    return new FileContent(TUNE_NOTICE_SUBMIT_FILE, this.generateShellScript(TUNE_NOTICE_SUBMIT_TEMPLATE, map));
                }
                return new FileContent(TUNE_NOTICE_FILE, this.generateShellScript(TUNE_NOTICE_TEMPLATE, map));
            }
            if (NoticeType.EMAIL.equals(noticeRequest.getNoticeType())) {
                if (NoticeContentEnum.SUBMIT.equals(noticeRequest.getNoticeContentEnum())) {
                    return new FileContent(TUNE_EMAIL_SUBMIT_FILE, this.generateShellScript(TUNE_EMAIL_SUBMIT_TEMPLATE, map));
                }
                return new FileContent(TUNE_EMAIL_FILE, this.generateShellScript(TUNE_EMAIL_TEMPLATE, map));
            }
            return null;

        } catch (Exception e) {
            log.info("generateTmaestroEntryShell failed due to {}", e.getMessage());
            return null;
        }
    }

    @Override
    public FileContent generateAlarmNotice(NoticeRequest noticeRequest) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("title", "AlarmNotice");
            map.put("appName", noticeRequest.getAppName());
            map.put("date", noticeRequest.getDate());
            map.put("content", noticeRequest.getNoticeMessage());
            map.put("tmaestroWebUrl", noticeRequest.getPlanWebUrl());
            map.put("buttonType", noticeRequest.getNoticeButtonType().getType());
            if (NoticeType.WECHAT.equals(noticeRequest.getNoticeType())) {
                return new FileContent(TUNE_NOTICE_FILE, this.generateShellScript(TUNE_NOTICE_TEMPLATE, map));
            }
            if (NoticeType.EMAIL.equals(noticeRequest.getNoticeType())) {
                return new FileContent(TUNE_EMAIL_ALARM_FILE, this.generateShellScript(TUNE_EMAIL_ALARM_TEMPLATE, map));
            }
            return null;

        } catch (Exception e) {
            log.info("generateTmaestroEntryShell failed due to {}", e.getMessage());
            return null;
        }
    }

    @Override
    public FileContent generateTmaestroEntryShell(String accessToken, String region, String clusterName) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(accessToken), "the accessToken can not be empty.");
            Preconditions.checkArgument(StringUtils.isNotBlank(region), "the region can not be empty.");
            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "the clusterName can not be empty.");
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", accessToken);
            map.put("region", region);
            map.put("clusterName", clusterName);
            map.put("TMAESTRO_HOME_PAGE_URL", constantProperties.getWebHomeUrl());
            map.put("tmaestro_server_url", constantProperties.getDomainUrl());
            map.put("TWATCH_FILE", getTwatchFileName());
            return new FileContent(TMAESTRO_ENTRY_FILE, this.generateShellScript(getTmaestroEntryTemplate(), map));
        } catch (Exception e) {
            log.info("generateTmaestroEntryShell failed due to {}", e.getMessage());
            return null;
        }
    }

    private String getTmaestroEntryTemplate() {
        CloudType cloudType = SystemUtil.getCloudTypeFromEnv(envHandler);
        if (CloudType.AWS == cloudType) {
            return TMAESTRO_ENTRY_TEMPLATE;
        }
        if (CloudType.ALIYUN == cloudType) {
            return ALIYUN_TMAESTRO_ENTRY_TEMPLATE;
        }
        throw new UnsupportedOperationException(String.format(" can not find template by cloudType=%s", cloudType));
    }

    @Override
    public FileContent generateDemonSetYaml(String accessToken) {
       return null;
    }

    @Override
    public FileContent generateInstallAttachAutoTuneJar(final String accessToken, final Integer attachId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tmaestro_url", constantProperties.getDomainUrl());
        map.put("accessToken", accessToken);
        Optional.ofNullable(accessToken).ifPresent(e -> map.put("accessToken", accessToken));
        Optional.ofNullable(attachId).ifPresent(e -> map.put("attachId", attachId));
        return new FileContent(INSTALL_AUTO_TUNE_AGENT_FILE, this.generateShellScript(INSTALL_AUTO_TUNE_AGENT_TEMPLATE, map));
    }

    @Override
    public FileContent generateAutoTuneYaml(String accessToken) {
        Map<String, Object> map = new HashMap<>();
        map.put("tmaestro_server_host", constantProperties.getDomainUrl());
        map.put("tmaestro_server_port", 9001);
        map.put("accessToken", accessToken);
        map.put("host", constantProperties.getGrpcHost());
        //map.put("host", constantProperties.getDomainApiUrl()); // grpc换成http
        return new FileContent(AUTO_TUNE_AGENT_YAML_FILE, this.generateShellScript(AUTO_TUNE_AGENT_YAML_TEMPLATE, map));
    }

    @Override
    public void saveDumpFile(GrpcCommon grpcCommon, Path filePath, String fileName, String sessionId) throws IOException {
        Preconditions.checkNotNull(filePath);
        Preconditions.checkNotNull(fileName);
        //存储媒介
        String s3Key = this.uploadFileToS3(Files.newInputStream(filePath.toFile().toPath()), fileName);
        Map<String, String> resultObj = ImmutableMap.of("filePath", filePath.toFile().getPath(), "fileName", fileName, "s3Key", s3Key);
        commandInfoRepository.uResult(sessionId, resultObj, CommandStatus.FINISH);
    }

    @Override
    public InputStream downloadFileFromAliS3(String key) {
        //todo 应该从本地文件下载 从本地直接读取
        try {
            return new FileInputStream(key);
        } catch (Exception e) {
            log.error("downloadFileFromAliS3 occurs an error", e);
            return null;
        }
    }

    @Override
    public ResponseEntity<byte[]> downloadFileUrlFromAliS3(String key) {
        //todo 应该从本地文件下载
        try {
            File file = new File(key);
            HttpHeaders headers = new HttpHeaders();
            // 下载显示的文件名，解决中文名称乱码问题
            String downloadFielName = new String(key.getBytes("UTF-8"), "iso-8859-1");
            // 通知浏览器以attachment
            headers.setContentDispositionFormData("attachment", downloadFielName);
            // application/octet-stream ： 二进制流数据（最常见的文件下载）。
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String uploadFileToS3(InputStream inputStream, String fileName) {
        if (inputStream != null) {
            Path tempFile = null;
            try {
                String destination = fileName;
                try {
                    //上传到的目标地址
                    File uploadFile = new File(destination);
                    FileUtils.copyInputStreamToFile(inputStream, uploadFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }
                return fileName;
            } catch (Exception e) {
                log.error("uploadLog upload file fail", e);
                throw new RuntimeException("uploadFileToS3 fail " + e.getMessage());
            }
        }
        throw new RuntimeException("uploadFileToS3 fail");
    }

    @Override
    public S3Object downloadFileFromS3(String s3Key) {
        return null;
    }

    @Override
    public String getContentFromS3(String s3Key) throws IOException {
        InputStream ins = new FileInputStream(s3Key);
        return IOUtils.toString(ins, StandardCharsets.UTF_8);
    }

    @Override
    public FileContent getArthasHtmlFileContent(ArthasHtmlType type) {
        if (type == null) {
            log.error("getArthasHtmlFileContent - input type is null.");
            return new FileContent("", "");
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("tmaestro_server_url", constantProperties.getDomainUrl());
            return new FileContent(type.getHtmlFileName(), this.generateShellScript(type.getHtmlTemplateName(), map));
        } catch (Exception e) {
            log.error("getArthasHtmlFileContent occurs an error.", e);
            return new FileContent("", String.format("Getting file content occurs error, due to %s", e.getMessage()));
        }
    }

    private String generateShellScript(String templateDir, Map<String, Object> dataModel) {
        try {
            Configuration config = new Configuration(Configuration.VERSION_2_3_29);
            config.setClassForTemplateLoading(StorageInfoServiceImpl.class, "/templates");
            config.setTemplateExceptionHandler((te, env, out) -> log.error("freeMarker print error", te));
            config.setDefaultEncoding(StandardCharsets.UTF_8.name());
            config.setInterpolationSyntax(Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);

            Template template = config.getTemplate(templateDir);
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("freeMarker process template error", e);
        }
        throw new RuntimeException("generateShellScript fail, dataModel:" + dataModel);
    }

    private String getTwatchFileName() {
        return envHandler.isEnvContain("aliyun-test") ? "twatch-aliyun-test.yaml" : "twatch-aliyun-prod.yaml";
    }
}