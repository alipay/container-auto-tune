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
import com.alipay.autotuneservice.dao.CommandInfoRepository;
import com.alipay.autotuneservice.dao.JavaInfoRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JavaInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.ThreadPoolMonitorMetricData;
import com.alipay.autotuneservice.model.ArthasMessage;
import com.alipay.autotuneservice.model.JavaInfo;
import com.alipay.autotuneservice.model.agent.ThreadPoolRequest;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.ReportActionService;
import com.alipay.autotuneservice.service.chronicmap.ChronicleMapService;
import com.alipay.autotuneservice.util.AgentConstant;
import com.auto.tune.client.ResponseMessage;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author quchen
 * @version : ReportActionServiceImpl.java, v 0.1 2022年12月05日 12:25 quchen Exp $
 */
@Slf4j
@Service
public class ReportActionServiceImpl implements ReportActionService {

    @Autowired
    private JavaInfoRepository    javaInfoRepository;
    @Autowired
    private AppInfoService        appInfoService;
    @Autowired
    private ChronicleMapService   redisClient;
    @Autowired
    private PodInfo               podInfo;
    @Autowired
    private CommandInfoRepository commandInfoRepository;

    @Override
    public void doJavaConfigInit(String params) {
        log.info("doJavaConfigInit req:{}", params);
        //进行数据转化
        JavaInfo javaInfo = JSONObject.parseObject(params, JavaInfo.class);
        //存储
        javaInfoRepository.insert(javaInfo.toRecord());
    }

    @Override
    public JavaInfo findJavaInfo(String hostName) {
        JavaInfoRecord record = javaInfoRepository.findInfo(hostName);
        //转化
        if (record == null) {
            return new JavaInfo();
        }
        JavaInfo javaInfo = new JavaInfo();
        javaInfo.setAppName(record.getAppName());
        javaInfo.setNameSpace(record.getNamespace());
        javaInfo.setJvmHome(record.getJvmHome());
        javaInfo.setVersion(record.getVersion());
        javaInfo.setClassPath(record.getClassPath());
        javaInfo.setLibraryPath(record.getLibraryPath());
        javaInfo.setUserName(record.getUserName());
        javaInfo.setUserDir(record.getUserDir());
        javaInfo.setExcTime(record.getExecTime());
        javaInfo.setJarLibs(JSON.parseObject(record.getJavaLibs(), new TypeReference<Set<String>>() {}));
        javaInfo.setOsVersion(record.getOsVersion());
        javaInfo.setOsArch(record.getOsArch());
        javaInfo.setInputArguments(JSON.parseObject(record.getInputArguments(), new TypeReference<List<String>>() {}));
        javaInfo.setHostName(record.getHostName());
        return javaInfo;
    }

    @Override
    public List<String> findLibs(Integer appId, String hostName, String libContains) {
        JavaInfoRecord javaInfoRecord = javaInfoRepository.findInfo(getAppName(appId), hostName);
        if (javaInfoRecord == null) {
            return Lists.newArrayList();
        }
        //解析
        String libStr = javaInfoRecord.getJavaLibs();
        if (StringUtils.isEmpty(libStr)) {
            return Lists.newArrayList();
        }
        List<String> libs = JSON.parseObject(libStr, new TypeReference<List<String>>() {});
        if (StringUtils.isEmpty(libContains)) {
            return libs;
        }
        return libs.stream().filter(lib -> StringUtils.contains(lib, libContains)).collect(Collectors.toList());
    }

    @Override
    public List<ThreadPoolMonitorMetricData> findThreadPool(Integer appId, String hostName) {
        String appName = getAppName(appId);
        if (StringUtils.isEmpty(appName)) {
            return Lists.newArrayList();
        }
        // TODO
        //List<Object> threadPool = redisClient.lrange(AgentConstant.generateThreadPoolKey(appName, hostName));
        List<Object> threadPool = redisClient.lrange(AgentConstant.generateThreadPoolKey(appName, hostName));
        if (CollectionUtils.isEmpty(threadPool)) {
            return Lists.newArrayList();
        }
        //进行去重
        return threadPool.stream()
                .map(o -> (ThreadPoolMonitorMetricData) o)
                .collect(Collectors.toList());
    }

    @Override
    public List<ThreadPoolMonitorMetricData> findThreadPoolByContains(Integer appId, String hostName, String poolNameContains) {
        List<ThreadPoolMonitorMetricData> threadPool = findThreadPool(appId, hostName);
        threadPool.sort(Comparator.comparing(ThreadPoolMonitorMetricData::getTaskCount).reversed());
        if (StringUtils.isBlank(poolNameContains)) {
            return threadPool;
        }
        return threadPool.stream()
                .filter(item -> item.getThreadPoolName().contains(poolNameContains))
                .collect(Collectors.toList());
    }

    @Override
    public void fixThreadPool(Integer appId, ThreadPoolRequest threadPoolRequest) {
        //下发指令
        log.info("fixThreadPool appId={}, threadPoolRequest={}", appId, JSON.toJSONString(threadPoolRequest));
        commandInfoRepository.sendCommand(getUnionCode(threadPoolRequest.getHostName(), appId), RuleAction.FIX_THREAD_POOL,
                UUID.randomUUID().toString(),
                ImmutableMap.of("threadPoolReq", threadPoolRequest));
    }

    @Override
    public boolean arthasInstall(Integer appId, String hostName) {
        String uuid = UUID.randomUUID().toString();
        //下发指令
        commandInfoRepository.sendCommand(getUnionCode(hostName, appId), RuleAction.ARTHAS_INSTALL, uuid);
        return Boolean.TRUE;
    }

    @Override
    public String arthasCommand(Integer appId, String hostName, String command) {
        String uuid = UUID.randomUUID().toString();
        if (AgentConstant.ARTHAS_STREAM_POOL.containsKey(hostName)) {
            try {
                AgentConstant.ARTHAS_STREAM_POOL.get(hostName).onNext(ResponseMessage.newBuilder().setSessionId(uuid).setRspMsg(command)
                        .build());
            } catch (Exception e) {
                AgentConstant.ARTHAS_STREAM_POOL.remove(hostName);
                return String.format("%s not found stream, please retry install", hostName);
            }
        } else {
            // TODO
            //redisClient.publish(new ArthasMessage(uuid, appId, hostName, command));
            AgentConstant.ARTHAS_MAP.put(uuid, new ArthasMessage(uuid, appId, hostName, command));
        }
        String result = getSyncActionResult(uuid, 100);
        log.info("arthas command:{},result:{}", command, result);
        if (StringUtils.isEmpty(result)) {
            boolean install = checkArthasInstall(appId, hostName);
            if (!install) {
                return "disconnect..please refresh terminal";
            }
            return "result empty";
        }
        return wrapArthasCommandResp(command, result);
    }

    private String wrapArthasCommandResp(String command, String result) {
        if (StringUtils.equals("broken pipe,retry connection!", result)) {
            return result;
        }
        // 过滤arthas返回值第一行带命令的换行
        String res = result.replaceFirst(command, "").trim();
        log.info("first result : {}", res);
        //res = res.replaceFirst("\\[1m", "");
        // 过滤arthas返回值里命令行名称[arthas@
        if (StringUtils.contains(res, "[arthas@")) {
            String data = res.split("\\[arthas@")[0];
            if (StringUtils.isBlank(data)) {
                log.info("second result : {}", res);
                data = StringUtils.split(res, "]$", 2)[1];
                log.info("second split result : {}", data);
                return data.split("\\[arthas@")[0];
            }
            if (data.contains("[1m")) {
                data = data.replace("[1m", "");
            }
            return data;
        }
        return res;
    }

    @Override
    public boolean checkArthasInstall(Integer appId, String hostName) {
        //判断是否有安装Arthas
        try {
            if (AgentConstant.ARTHAS_STREAM_POOL.containsKey(hostName)) {
                return Boolean.TRUE;
            }
            String uuid = UUID.randomUUID().toString();
            // TODO
            //redisClient.publish(new ArthasMessage(uuid, appId, hostName, AgentConstant.CHECK_ARTHAS_INSTALL));
            AgentConstant.ARTHAS_MAP.put(uuid, new ArthasMessage(uuid, appId, hostName, AgentConstant.CHECK_ARTHAS_INSTALL));

            String result = getSyncActionResult(uuid, 5);
            if (StringUtils.isEmpty(result)) {
                return Boolean.FALSE;
            }
            return Boolean.parseBoolean(result);
        } catch (Exception e) {
            log.error("checkArthasInstall is error", e);
            return Boolean.FALSE;
        }
    }

    private String getSyncActionResult(String sessionId, long retryTime) {
        try {
            if (StringUtils.isEmpty(sessionId)) {
                return "";
            }
            int count = 0;
            Object res = null;
            while (count <= retryTime && res == null) {
                count++;
                // TODO
                res = redisClient.get(AgentConstant.generateArthasCallBackKey(sessionId), Object.class);
                log.info("res: {}", res);
                TimeUnit.MILLISECONDS.sleep(100);
            }
            if (res != null) {
                //TODO
                redisClient.del(AgentConstant.generateArthasCallBackKey(sessionId));

            }
            return res == null ? "" : (String) res;
        } catch (Exception e) {
            log.error("asyncHandleResponse occurs an error.", e);
            return "";
        }
    }

    private String getAppName(Integer appId) {
        AppInfoRecord appInfoRecord = appInfoService.selectById(appId);
        if (appInfoRecord == null) {
            return "";
        }
        return appInfoRecord.getAppName();
    }

    private String getUnionCode(String hostName, Integer appId) {
        //获取union_code
        PodInfoRecord podInfoRecord = podInfo.getByPodAndAID(hostName, appId);
        if (StringUtils.isEmpty(podInfoRecord.getUnicode())) {
            throw new RuntimeException("unicode is empty,please check");
        }
        return podInfoRecord.getUnicode();
    }
}