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

import com.alipay.autotuneservice.configuration.Cached;
import com.alipay.autotuneservice.configuration.ConstantsProperties;
import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.dao.HelpInfoRepository;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.FileContent;
import com.alipay.autotuneservice.model.common.HelpInfo;
import com.alipay.autotuneservice.model.common.HelpInfo.HelpType;
import com.alipay.autotuneservice.model.common.ShellEnvEnum;
import com.alipay.autotuneservice.model.dto.InstallScriptDTO;
import com.alipay.autotuneservice.model.dto.StartupScriptDTO;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author dutianze
 * @version HelpController.java, v 0.1 2022年02月16日 11:00 dutianze
 */
@NoLogin
@RestController
@RequestMapping("/api/jvm/help")
public class HelpController {

    @Autowired
    private ConstantsProperties constantProperties;

    @Autowired
    private HelpInfoRepository helpInfoRepository;
    @Autowired
    private StorageInfoService storageInfoService;

    @GetMapping("/install-steps")
    @Cached
    public ServiceBaseResult<List<HelpInfo>> installSteps(@RequestParam(value = "helpType") HelpType helpType) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> helpInfoRepository.findByHelpType(helpType));
    }

    @GetMapping(path = "/install/script/twatch")
    @Cached
    public ServiceBaseResult<InstallScriptDTO> twatchInstallScript(@RequestParam(value = "region") String region,
                                                                   @RequestParam(value = "clusterName") String clusterName) {
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> {
                    Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clusterName cant be blank");
                    Preconditions.checkArgument(StringUtils.isNotBlank(region), "region cant be blank");
                })
                .makeResult(() -> {
                    String accessToken = UserUtil.getAccessToken();
                    Preconditions.checkNotNull(accessToken);

                    InstallScriptDTO installScriptDTO = new InstallScriptDTO();
                    // content
                    FileContent fileContent = storageInfoService.generateTmaestroEntryShell(accessToken, region, clusterName);
                    installScriptDTO.setContent(fileContent.getContent());

                    Map<String, String> parameters = ImmutableMap.of("region", region, "clusterName", clusterName);
                    String url = this.buildUrl(parameters, fileContent.getFileName());
                    // curl
                    installScriptDTO.setCurl(fileContent.curl(url, ShellEnvEnum.BASH));
                    // wget
                    installScriptDTO.setWget(fileContent.wget(url, ShellEnvEnum.BASH));
                    return installScriptDTO;
                });
    }

    @GetMapping(path = "/install/script/docker")
    public ServiceBaseResult<StartupScriptDTO> dockerStartupScript() {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> {
                    String accessToken = UserUtil.getAccessToken();
                    Preconditions.checkNotNull(accessToken);

                    StartupScriptDTO startupScriptDTO = new StartupScriptDTO();
                    // content
                    FileContent dockerBootstrapContent = storageInfoService.generateTMaestroOnBoard(accessToken);
                    startupScriptDTO.setDockerBootstrapContent(dockerBootstrapContent.getContent());

                    FileContent javaStartupScript = storageInfoService.generateDockerStartShell(accessToken);
                    startupScriptDTO.setJavaStartupScript(javaStartupScript.getContent());

                    Map<String, String> parameters = ImmutableMap.of("accessToken", accessToken);

                    // java startup
                    String url = this.buildUrl(parameters, javaStartupScript.getFileName());
                    // java startup script curl
                    startupScriptDTO.setJavaStartupScriptCurl(javaStartupScript.curl(url, ShellEnvEnum.BASH));
                    // java startup script wget
                    startupScriptDTO.setJavaStartupScriptWget(javaStartupScript.wget(url, ShellEnvEnum.BASH));

                    // javaAgent
                    FileContent javaAgentFileContent = new FileContent("autoTuneAgent.jar", "");
                    String javaAgentDownloadUrl = this.buildUrl(parameters, javaAgentFileContent.getFileName());
                    // javaAgent download curl
                    String javaAgentCurl = javaAgentFileContent.curl(javaAgentDownloadUrl, ShellEnvEnum.BASH);
                    startupScriptDTO.setJavaAgentDownloadCurl(javaAgentCurl);
                    // javaAgent download wget
                    String javaAgentWget = javaAgentFileContent.wget(javaAgentDownloadUrl, ShellEnvEnum.BASH);
                    startupScriptDTO.setJavaAgentDownloadWget(javaAgentWget);

                    return startupScriptDTO;
                });
    }

    /**
     * @see StorageController#downloadAttachAgentFile
     */
    @GetMapping(path = "/install/script/attachAutoTuneAgent")
    @Cached
    public ServiceBaseResult<InstallScriptDTO> installAttachAutoTuneScript() {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> {
                    InstallScriptDTO installScriptDTO = new InstallScriptDTO();
                    // content
                    FileContent fileContent = storageInfoService.generateInstallAttachAutoTuneJar(null, null);
                    installScriptDTO.setContent(fileContent.getContent());

                    String url = this.buildAttachAgentUrl(fileContent.getFileName());
                    // curl
                    installScriptDTO.setCurl(fileContent.curl(url, ShellEnvEnum.SH));
                    // wget
                    installScriptDTO.setWget(fileContent.wget(url, ShellEnvEnum.SH));
                    return installScriptDTO;
                });
    }

    private String buildUrl(Map<String, String> parameters, String fileName) throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(constantProperties.getDomainApiUrl());
        builder.setPath("/api/storage/" + fileName);
        builder.addParameter("accessToken",
                ObjectUtils.defaultIfNull(UserUtil.getAccessToken(), "unknown"));
        parameters.forEach(builder::addParameter);
        return builder.build().toURL().toString();
    }

    private String buildAttachAgentUrl(String fileName) throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(constantProperties.getDomainApiUrl());
        builder.setPath("/api/storage/" + fileName);
        return builder.build().toURL().toString();
    }
}