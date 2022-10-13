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

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.JvmOptsConfig;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmOptsConfigRecord;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.CounterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

/**
 * @author dutianze
 * @version JvmOptsController.java, v 0.1 2022年03月16日 14:26 dutianze
 */
@NoLogin
@Slf4j
@RestController
@RequestMapping("/api/tmaster/v1/jvm-opts-configs")
public class JvmOptsController {

    @Autowired
    private JvmOptsConfig     jvmOptsConfig;
    @Autowired
    private AppInfoService    appInfoService;
    @Autowired
    private CounterService    counterService;
    @Autowired
    private AppInfoRepository appInfoRepository;

    @GetMapping("/fetch/{accessToken}/{podName}")
    public String fetchJavaOpts(@PathVariable(value = "accessToken") String accessToken,
                                @PathVariable(value = "podName") String podName,
                                @RequestParam(value = "namespace", required = false, defaultValue = "") String namespace,
                                @RequestParam(value = "javaOptsDefault") String base64JvmOpt) {
        // base64 decode
        String decodeDefaultJvm = new String(Base64.getDecoder().decode(base64JvmOpt));
        log.info(
            "fetchJavaOpts, accessToken:{}, podName:{}, namespace={}, base64JvmOpt:{}, javaOptsDefault={}",
            accessToken, podName, namespace, base64JvmOpt, decodeDefaultJvm);
        //根据podName获取jvm参数
        try {
            //podName --> 转换为appName
            String appName = podName.substring(0, StringUtils.lastOrdinalIndexOf(podName, "-", 2));
            AppInfo appInfo = appInfoRepository.findByAppAndATAndNamespace(appName, accessToken,
                namespace);
            if (appInfo == null) {
                log.info(
                    "Can not find app record by appName={}, accessToken and namespace for pod={}",
                    appName, podName);
                return "";
            }
            log.info("fetchJavaOpts appInfo={} from DB", appInfo);
            String appDefaultJvm = appInfo.getAppDefaultJvm();
            if (StringUtils.isBlank(appDefaultJvm)) {
                //update Jvm
                log.info("fetchJavaOpts - start update appDefaultJVM.updateDefaultJvm={}",
                    decodeDefaultJvm);
                appInfoService.updateAppJvm(appInfo.getId(), decodeDefaultJvm);
            }
            String recommendJvm = counterService.tryAccess(appInfo.getId());
            if (StringUtils.isBlank(recommendJvm)) {
                recommendJvm = appDefaultJvm;
            }
            log.info("fetchJavaOpts - recommendJvm={} for pod={}", recommendJvm, podName);
            return recommendJvm;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/getAppJvm")
    public String getAppJvmMeta(@RequestParam(value = "apps") String apps) {
        return "";
    }

    @PostMapping()
    public ServiceBaseResult<Long> createConfig(@RequestParam(value = "jvmOpt") String jvmOpt) {
        log.info("createConfig, jvmOpt:{}", jvmOpt);
        JvmOptsConfigRecord record = new JvmOptsConfigRecord();
        record.setJvmOpt(jvmOpt);
        jvmOptsConfig.insert(record);
        return ServiceBaseResult.successResult();
    }

    @PatchMapping("{id}")
    public ServiceBaseResult<String> updateConfig(@PathVariable(value = "id") Long id,
                                                  @RequestParam(value = "jvmOpt") String jvmOpt) {
        log.info("updateConfig, id:{}, jvmOpt:{}", id, jvmOpt);
        JvmOptsConfigRecord record = new JvmOptsConfigRecord();
        record.setId(id);
        record.setJvmOpt(jvmOpt);
        jvmOptsConfig.updateById(record);
        return ServiceBaseResult.successResult();
    }
}