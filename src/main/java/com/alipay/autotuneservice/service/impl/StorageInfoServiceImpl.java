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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.configuration.ConstantsProperties;
import com.alipay.autotuneservice.dao.AppLogRepository;
import com.alipay.autotuneservice.dao.CommandInfoRepository;
import com.alipay.autotuneservice.grpc.GrpcCommon;
import com.alipay.autotuneservice.model.common.CloudType;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.common.FileContent;
import com.alipay.autotuneservice.model.common.IntegrateAgentEnum;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.util.SystemUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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
    private static final String LITE_TMAESTRO_ENTRY_TEMPLATE   = "lite-tmaestro-entry.flt";
    public static final  String TMAESTRO_ENTRY_FILE            = "tmaestro-entry.sh";

    private static final String TWATCH_DAEMON_SET_YAML_TEMPLATE = "twatch.flt";
    public static final  String TWATCH_DAEMON_SET_YAML_FILE     = "twatch.yml";

    private static final String TMAESTRO_ONBOARD_TEMPLATE = "tmaestro-onboard.flt";
    public static final  String TMAESTRO_ONBOARD_FILE     = "tmaestro-onboard";

    private static final String INSTALL_AUTO_TUNE_AGENT_TEMPLATE = "installTuneAgent.flt";
    private static final String INSTALL_AUTO_TUNE_AGENT_FILE     = "installTuneAgent.sh";

    private static final String AUTO_TUNE_AGENT_YAML_TEMPLATE = "autotuneAgentYaml.flt";
    private static final String AUTO_TUNE_AGENT_YAML_FILE     = "tmaster.yml";

    @Autowired
    private AppInfoService        appInfoService;
    @Autowired
    private AppLogRepository      appLogRepository;
    @Autowired
    private ConstantsProperties   constantProperties;
    @Autowired
    private Environment           env;
    @Autowired
    private CommandInfoRepository commandInfoRepository;

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

    private FileContent getFileContent(String accessToken, String dockerStartShellFile,
                                       String dockerStartShellTemplate) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(accessToken),
                    "the accessToken can not be empty.");
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", accessToken);
            map.put("tmaestro_server_url", constantProperties.getDomainUrl());
            return new FileContent(dockerStartShellFile, this.generateShellScript(
                    dockerStartShellTemplate, map));
        } catch (Exception e) {
            log.info("getFileContent for file={} occurs an error.", dockerStartShellFile, e);
            return null;
        }
    }

    @Override
    public FileContent generateTmaestroEntryShell(String accessToken, String region,
                                                  String clusterName) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(accessToken),
                    "the accessToken can not be empty.");
            Preconditions.checkArgument(StringUtils.isNotBlank(region),
                    "the region can not be empty.");
            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName),
                    "the clusterName can not be empty.");
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", accessToken);
            map.put("region", region);
            map.put("clusterName", clusterName);
            map.put("TMAESTRO_HOME_PAGE_URL", constantProperties.getWebHomeUrl());
            map.put("tmaestro_server_url", constantProperties.getDomainUrl());
            return new FileContent(TMAESTRO_ENTRY_FILE, this.generateShellScript(
                    getTmaestroEntryTemplate(), map));
        } catch (Exception e) {
            log.info("generateTmaestroEntryShell failed due to {}", e.getMessage());
            return null;
        }
    }

    private String getTmaestroEntryTemplate() {
        CloudType cloudType = SystemUtil.getCloudTypeFromEnv(env);
        switch (cloudType) {
            case K8S:
                return LITE_TMAESTRO_ENTRY_TEMPLATE;
            case ALIYUN:
                return ALIYUN_TMAESTRO_ENTRY_TEMPLATE;
            case AWS:
                return TMAESTRO_ENTRY_TEMPLATE;
        }
        throw new UnsupportedOperationException(String.format(
                " can not find template by cloudType=%s", cloudType));
    }

    @Override
    public FileContent generateDemonSetYaml(String accessToken) {
        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", accessToken);
        // TODO 获取稳定版本的twatch镜像ID
        String imageId = "514796603615.dkr.ecr.us-east-1.amazonaws.com/saasalpha:twatch_202206021727";
        map.put("imageId", imageId);
        return new FileContent(TWATCH_DAEMON_SET_YAML_FILE, this.generateShellScript(
                TWATCH_DAEMON_SET_YAML_TEMPLATE, map));
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
        map.put("accessToken", accessToken);
        map.put("host", constantProperties.getGrpcHost());
        map.put("port", constantProperties.getGrpcPort());
        return new FileContent(AUTO_TUNE_AGENT_YAML_FILE, this.generateShellScript(
                AUTO_TUNE_AGENT_YAML_TEMPLATE, map));
    }

    @Override
    public InputStream downloadFileFromAliS3(String key) {
        return null;
    }

    @Override
    public String uploadFileToS3(InputStream inputStream, String fileName) {
        return null;
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

    private String generateShellScript(String templateDir, Map<String, Object> dataModel) {
        try {
            Configuration config = new Configuration(Configuration.VERSION_2_3_29);
            config.setClassForTemplateLoading(StorageInfoServiceImpl.class, "/templates");
            config.setTemplateExceptionHandler((te, env, out) -> log.error("freeMarker print error", te));
            config.setDefaultEncoding(StandardCharsets.UTF_8.name());
            config.setInterpolationSyntax(Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);

            Template template = config.getTemplate(templateDir);
            template.getConfiguration().setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("freeMarker process template error", e);
        }
        throw new RuntimeException("generateShellScript fail, dataModel:" + dataModel);
    }
}