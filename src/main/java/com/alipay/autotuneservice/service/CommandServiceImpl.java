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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.PodProcessInfo;
import com.alipay.autotuneservice.service.impl.AgentInvokeServiceImpl.InvokeType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.alipay.autotuneservice.agent.twatch.constants.TwatchCmdConstants.CAT_FINAL_JVM_OPTS_CMD;

/**
 * @author huangkaifei
 * @version : CommandServiceImpl.java, v 0.1 2022年07月05日 2:41 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class CommandServiceImpl implements CommandService {

    private static final List<String> FAILED_ERROR_FLAGS = ImmutableList.of("No such file",
                                                             "OCI runtime exec failed");

    @Autowired
    private AgentInvokeService        agentInvokeService;
    @Autowired
    private PodService                podService;

    @Override
    public String execCmd(String podName, String cmd) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(podName), "podName不能为空");
            Preconditions.checkArgument(StringUtils.isNotBlank(cmd), "cmd不能为空");
            String result = agentInvokeService.execCmd(InvokeType.SYNC, podName, cmd);
            return checkResultFailed(result) ? "" : JSON.parseObject(result,
                new TypeReference<String>() {
                });
        } catch (Exception e) {
            log.error("CommandServiceImpl#execCmd - occurs an error.", e);
            return "";
        }
    }

    @Override
    public String getPodJvm(String podName) {
        String res = execCmd(podName, CAT_FINAL_JVM_OPTS_CMD);
        if (StringUtils.isNotEmpty(res)) {
            return res;
        }
        List<PodProcessInfo> podJavaProcess = podService.getPodJavaProcess(podName);
        if (CollectionUtils.isEmpty(podJavaProcess)) {
            log.info("getPodJvm - getPodJavaProcess res is empty for podName={}", podName);
            return "";
        }
        res = podJavaProcess.get(0).getCommand();
        log.info("getPodJvm - podName={} res={}", podName, res);
        return res;
    }

    private boolean checkResultFailed(String result) {
        if (StringUtils.isBlank(result)) {
            return true;
        }
        for (String flag : FAILED_ERROR_FLAGS) {
            if (result.contains(flag)) {
                return true;
            }
        }
        return false;
    }
}