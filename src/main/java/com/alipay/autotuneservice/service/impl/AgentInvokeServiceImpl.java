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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.agent.twatch.AgentCallInvoke;
import com.alipay.autotuneservice.agent.twatch.DoInvokeRunner;
import com.alipay.autotuneservice.agent.twatch.constants.TwatchCmdConstants;
import com.alipay.autotuneservice.agent.twatch.core.CallMethodType;
import com.alipay.autotuneservice.agent.twatch.core.CallMethodType.POD;
import com.alipay.autotuneservice.agent.twatch.model.ExecCmdResult;
import com.alipay.autotuneservice.agent.twatch.model.PodHealthIndexEnum;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.dynamodb.repository.ContainerProcessInfoRepository;
import com.alipay.autotuneservice.dynamodb.repository.TwatchInfoRepository;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.statistics.StatisticsResponse;
import com.alipay.autotuneservice.service.AgentInvokeService;
import com.alipay.autotuneservice.util.AgentConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.agent.twatch.constants.TwatchCmdConstants.CHECK_AUTO_TUNE_AGENT_EXIST_REG;
import static com.alipay.autotuneservice.agent.twatch.constants.TwatchCmdConstants.CHECK_AUTO_TUNE_AGENT_INSTALL_CMD;

/**
 * 暴露使用TWatch agent的接口
 * 该接口提供异步Action执行, 方法名为asyncGETxxx, 对应的action根据方法名来获取, 返回sessionId, 使用getActionResult根据sessionId查询结果
 * 异步方法执行：
 * 1. 生成sessionId
 * 2. 根据方法名利用javaassit生成动态类
 * 3. 放入sessionId, action到缓存里
 */
@Service
@Slf4j
public class AgentInvokeServiceImpl implements AgentInvokeService {

    @Autowired
    private RedisClient                    redisClient;
    @Autowired
    private DoInvokeRunner                 doInvokeRunner;
    @Autowired
    private ContainerProcessInfoRepository processInfoRepository;
    @Autowired
    private TwatchInfoRepository           twatchInfoRepository;

    private String getSyncActionResult(String sessionId) {
        try {
            if (StringUtils.isEmpty(sessionId)) {
                return "";
            }
            int count = 0;
            Object res = null;
            while (count <= 20 && res == null) {
                count++;
                res = getAsyncActionResult(sessionId);
                TimeUnit.SECONDS.sleep(1);
            }
            return res == null ? "" : (String) res;
        } catch (Exception e) {
            log.error("asyncHandleResponse occurs an error.", e);
            return "";
        }
    }

    @Override
    public Object getAsyncActionResult(String sessionId) {
        return redisClient.get(AgentConstant.generateCallBackKey(sessionId), Object.class);
    }

    @Override
    public String getPodEnv(InvokeType type, String podName) {
        //根据podName换取containerId
        List<TwatchInfoDo> infoDos = doInvokeRunner.findInfoByPod(podName);
        if (CollectionUtils.isEmpty(infoDos)) {
            return "not found by podName=" + podName;
        }
        String sessionId = new AgentCallInvoke.BuildInvoke(CallMethodType.POD.GET_ENV)
            .args(CallMethodType.POD.GET_ENV.CONTAINERID.key(), infoDos.get(0).getContainerId())
            .build().fire();
        if (InvokeType.ASYNC == type) {
            return sessionId;
        }
        return getSyncActionResult(sessionId);
    }

    @Override
    public String execCmd(InvokeType type, String podName, String cmd) {
        log.info("execCmd, type:{}, podName:{}, cmd:{}", type, podName, cmd);
        if (StringUtils.isBlank(podName) || StringUtils.isBlank(cmd)) {
            log.warn("podName={} or cmd={} is invalid.", podName, cmd);
            return "";
        }
        // TODO 增加执行白名单判断,白名单里应只含包含查询类操作
        List<TwatchInfoDo> infoDos = twatchInfoRepository.findInfoByPod(podName);
        if (CollectionUtils.isEmpty(infoDos)) {
            log.warn("not found by podName={}", podName);
            return "";
        }
        String sessionId = new AgentCallInvoke.BuildInvoke(POD.EXEC_CMD)
            .args(CallMethodType.POD.EXEC_CMD.CONTAINERID.key(), infoDos.get(0).getContainerId())
            .args(POD.EXEC_CMD.CMDNAME.key(), cmd).build().fire();
        log.info("sessionId={}", sessionId);
        if (InvokeType.ASYNC == type) {
            return sessionId;
        }
        return getSyncActionResult(sessionId);
    }

    @Override
    public ExecCmdResult<String> execCmdV1(InvokeType type, String podName, String cmd) {
        ExecCmdResult<String> execCmdResult = new ExecCmdResult<>();
        if (StringUtils.isBlank(podName) || StringUtils.isBlank(cmd)) {
            return execCmdResult.setData(String.format("podName=%s or cmd=%s is invalid.", podName,
                cmd));
        }
        // TODO 增加执行白名单判断,白名单里应只含包含查询类操作
        List<TwatchInfoDo> infoDos = doInvokeRunner.findInfoByPod(podName);
        if (CollectionUtils.isEmpty(infoDos)) {
            execCmdResult.setData(String.format("not found by podName=%s", podName));
            return execCmdResult;
        }
        String sessionId = new AgentCallInvoke.BuildInvoke(POD.EXEC_CMD)
            .args(CallMethodType.POD.EXEC_CMD.CONTAINERID.key(), infoDos.get(0).getContainerId())
            .args(POD.EXEC_CMD.CMDNAME.key(), cmd).build().fire();
        log.info("execCmdV1, podName={}, sessionId={}", podName, sessionId);
        if (InvokeType.ASYNC == type) {
            return execCmdResult.setSuccess(true).setData(sessionId);
        }
        try {
            String syncActionResult = getSyncActionResult(sessionId);
            log.info("execCmdV1 syncActionResult={}", syncActionResult);
            return execCmdResult.setSuccess(true).setData(syncActionResult);
        } catch (Exception e) {
            log.error("execCmdV1 getSyncActionResult occurs an error.", e);
            return execCmdResult.setSuccess(false).setData(e.getMessage());
        }
    }

    @Override
    public String execStats(InvokeType type, String containerId) {
        if (StringUtils.isBlank(containerId)) {
            return "execStats input podName or containId is empty.";
        }
        log.info("execStats start. containerId={}", containerId);
        String sessionId = new AgentCallInvoke.BuildInvoke(POD.EXEC_STATS)
            .args(CallMethodType.PROCESS.LIST.CONTAINERID.key(), containerId).build().fire();
        if (InvokeType.ASYNC == type) {
            return sessionId;
        }
        return getSyncActionResult(sessionId);
    }

    @Override
    public Boolean checkPodIsInstallTuneAgent(String podName) {
        String containerId = getContainerByPod(podName);
        if (StringUtils.isBlank(containerId)) {
            return Boolean.FALSE;
        }
        String cmd = execCmd(InvokeType.SYNC, podName, CHECK_AUTO_TUNE_AGENT_INSTALL_CMD);
        log.info("checkPodIsInstallTuneAgent res={}", cmd);
        return Pattern.matches(CHECK_AUTO_TUNE_AGENT_EXIST_REG, cmd);
    }

    @Override
    public String getPodHealthIndex(String podName, PodHealthIndexEnum podHealthIndexEnum) {
        Map<String, String> map = getAllPodHealthIndexes(podName);
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        return map.get(podHealthIndexEnum.getHealthIndex());
    }

    @Override
    public Map<String, String> getAllPodHealthIndexes(String podName) {
        Map<String, String> map = redisClient.get(PodHealthIndexEnum.generatePodHealthIndexKey(podName), Map.class);
        if (MapUtils.isNotEmpty(map)) {
            return map;
        }
        ExecCmdResult<String> execCmdResult = execCmdV1(InvokeType.SYNC, podName, TwatchCmdConstants.CAT_AGENT_HEALTH_CHECK_FILE_CMD);
        log.info("getPodHealthIndex execCmdResult={}", execCmdResult);
        if (!execCmdResult.isSuccess() || StringUtils.isBlank(execCmdResult.getData())) {
            return Maps.newHashMap();
        }
        String response = JSON.parseObject(execCmdResult.getData(), new TypeReference<String>() {});
        log.info("getPodHealthIndex podName={}, result={}", podName, response);
        Map<String, String> podIndexValue = Arrays.asList(response.split("\n")).stream().map(item -> {
            try {
                String decodeItem = new String(Base64.getDecoder().decode(item));
                PodHealthIndexEnum healthIndexEnum = PodHealthIndexEnum.findIndex(decodeItem);
                return healthIndexEnum.getParseFunc().apply(decodeItem);
            } catch (Exception e) {
                return Lists.newArrayList();
            }
        })
                .filter(item -> CollectionUtils.isNotEmpty(item) && item.size() >= 2)
                .collect(Collectors.toMap(item -> (String) item.get(0), item -> (String) item.get(1), (e, u) -> e));
        if (MapUtils.isNotEmpty(podIndexValue)) {
            redisClient.setNx(PodHealthIndexEnum.generatePodHealthIndexKey(podName), podIndexValue, 30, TimeUnit.MINUTES);
        }
        return podIndexValue;
    }

    @Override
    public StatisticsResponse getPodStats(String podName) {
        try {
            if (StringUtils.isBlank(podName)) {
                log.info("getPodStats podName is empty.");
                return null;
            }
            log.info("getPodStats start. podName={}", podName);
            List<TwatchInfoDo> infoDos = doInvokeRunner.findInfoByPod(podName);
            if (CollectionUtils.isEmpty(infoDos)) {
                log.info("getPodStats can not find containers for podName={}", podName);
                return null;
            }
            String sessionId = new AgentCallInvoke.BuildInvoke(POD.EXEC_STATS)
                .args(CallMethodType.PROCESS.LIST.CONTAINERID.key(),
                    infoDos.get(0).getContainerId()).build().fire();
            String response = getSyncActionResult(sessionId);
            String s = response.replaceAll("\\\\", "");
            log.info("getPodStats response={}", s);
            return JSONObject.parseObject(s, new TypeReference<StatisticsResponse>() {
            });
        } catch (Exception e) {
            log.info("");
            return null;
        }
    }

    @Override
    public String listProcess(InvokeType type, String podName, String containerId) {
        if (StringUtils.isBlank(podName) || StringUtils.isBlank(containerId)) {
            return "listProcess input podName or containId is empty.";
        }
        String key = buildListProcessUUID(podName);
        if (redisClient.get(key) != null) {
            String result = redisClient.get(buildListProcessUUID(podName), String.class);
            log.info("listProcess - get pod={} process={} from cache", podName, result);
            return result;
        }
        log.info("listProcess start. podName={}, containerId={}", podName, containerId);
        String sessionId = new AgentCallInvoke.BuildInvoke(CallMethodType.PROCESS.LIST)
            .args(CallMethodType.PROCESS.LIST.CONTAINERID.key(), containerId).build().fire();
        if (InvokeType.ASYNC == type) {
            return sessionId;
        }
        String result = getSyncActionResult(sessionId);
        log.info("listProcess end. sessionId={}, podName={}, res={}", sessionId, podName, result);
        if (StringUtils.isNotBlank(result)) {
            redisClient.setEx(key, result, 3, TimeUnit.MINUTES);
        }
        return result;
    }

    private String buildListProcessUUID(String podName) {
        return String.format("LIST_POD_PROCESS_%s", podName);
    }

    @Override
    public String getProcessByPod(InvokeType type, String podName) {
        if (StringUtils.isBlank(podName)) {
            log.warn("getProcessByPod input podName or containId is empty.");
            return "";
        }
        List<TwatchInfoDo> infoByPod = doInvokeRunner.findInfoByPod(podName);
        if (CollectionUtils.isEmpty(infoByPod)) {
            log.warn("no container for pod={}", podName);
            return "";
        }
        log.info("getProcessByPod for podName={}, containers={}", podName,
            JSON.toJSONString(infoByPod));
        String containerId = infoByPod.get(0).getContainerId();
        return listProcess(type, podName, containerId);
    }

    public static enum InvokeType {
        SYNC, ASYNC;
    }

    private String getContainerByPod(String podName) {
        if (StringUtils.isBlank(podName)) {
            return "";
        }
        List<TwatchInfoDo> infoByPod = doInvokeRunner.findInfoByPod(podName);
        if (CollectionUtils.isEmpty(infoByPod)) {
            return "";
        }
        log.info("getContainerByPod for podName={}, containers={}", podName,
            JSON.toJSONString(infoByPod));
        return infoByPod.get(0).getContainerId();
    }
}
