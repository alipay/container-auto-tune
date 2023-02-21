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
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.PodProcessInfo;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.JvmMarketInfo;
import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMarketInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.dynamodb.repository.TwatchInfoService;
import com.alipay.autotuneservice.model.common.PodStatus;
import com.alipay.autotuneservice.model.tunepool.PoolType;
import com.alipay.autotuneservice.service.AgentInvokeService;
import com.alipay.autotuneservice.service.CounterService;
import com.alipay.autotuneservice.service.PodService;
import com.alipay.autotuneservice.service.impl.AgentInvokeServiceImpl.InvokeType;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.ObjectUtil;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huoyuqi
 * @version PodServiceImpl.java, v 0.1 2022年04月06日 10:38 上午 huoyuqi
 */
@Slf4j
@Service
public class PodServiceImpl implements PodService {
    private static final String JVMMARKETID = "-DJVMMARKETD";
    private static final String DATE_CMD    = "date -R";

    @Autowired
    private AppInfoRepository  appInfoRepository;
    @Autowired
    private K8sAccessTokenInfo k8sAccessTokenInfo;
    @Autowired
    private CounterService     counterService;
    @Autowired
    private AgentInvokeService agentInvokeService;
    @Autowired
    private PodInfo            podInfo;
    @Autowired
    private JvmMarketInfo      jvmMarketInfo;
    @Autowired
    private TwatchInfoService  twatchInfoRepository;

    @Override
    public Integer getAppPodNum(Integer appId) {
        try {
            List<PodInfoRecord> records = podInfo.getByAppId(appId);
            log.info("PodServiceImpl#getAppPodNum podNum is: {}", records.size());
            return records.size();
        } catch (Exception e) {
            log.warn("PodServiceImpl#getAppPodNum 执行过程抛出异常 e:{}" + e.getMessage());
            return -1;
        }
    }

    @Override
    public Integer getPodNumByIdAndJvm(Integer appId, String jvmMarketId) {
        try {
            List<PodInfoRecord> podInfoRecordList = podInfo.getByAppId(appId);
            if (CollectionUtils.isEmpty(podInfoRecordList)) {
                return 0;
            }
            AtomicInteger count = new AtomicInteger();
            podInfoRecordList.stream().filter(item -> StringUtils.isNotEmpty(item.getPodJvm()) && item.getPodJvm().contains(JVMMARKETID))
                    .map(PodInfoRecord::getPodJvm).forEach(item2 -> {
                        String tempJvmMarket = item2.substring(item2.indexOf(JVMMARKETID));
                        tempJvmMarket.substring(12, tempJvmMarket.indexOf(" "));
                        if (StringUtils.equals(tempJvmMarket, jvmMarketId)) {
                            count.set(count.get() + 1);
                        }
                    });
            return count.get();
        } catch (Exception e) {
            return -1;
        }

    }

    @Override
    public Integer getAppRunningPodNum(Integer appId) {
        return -1;
    }

    @Override
    public Integer getAppPodNumByJvmId(Integer appId, Integer jvmId) {
        List<PodInfoRecord> records = podInfo.getByAppId(appId);
        Stream<PodInfoRecord> stream = records.stream()
                .filter(record -> !StringUtils.isEmpty(record.getPodJvm()));
        if (jvmId == null || jvmId <= 0) {
            records = stream.filter(record -> !StringUtils.contains(record.getPodJvm(), UserUtil.getTuneJvmConfig(null)))
                    .collect(Collectors.toList());
        } else {
            records = stream.filter(record -> StringUtils.contains(record.getPodJvm(), UserUtil.getTuneJvmConfig(jvmId)))
                    .collect(Collectors.toList());
        }
        log.info("PodServiceImpl#getAppPodNumByEnv podNumByEnv is: {}", records.size());
        return records.size();
    }

    @Override
    public boolean changePod(Integer appId, Integer jvmMarketId, Integer num, PoolType poolType,
                             Function<List<PodInfoRecord>, Boolean> callBackFunc,
                             BiConsumer<PodInfoRecord, String> doChangeCallback, Consumer<List<String>> deletePods, Boolean isGray) {
        String appName = null;
        try {
            JvmMarketInfoRecord record = jvmMarketInfo.getJvmInfo(jvmMarketId);
            if (jvmMarketId != 0 && record == null) {
                throw new RuntimeException(String.format("the jvmMarketId=[%s] not found", jvmMarketId));
            }
            AppInfoRecord appInfoRecord = appInfoRepository.getById(appId);
            //组织jvm参数
            appName = appInfoRecord.getAppName();
            if (num <= 0) {
                return rollback(appName, appId, jvmMarketId, callBackFunc, doChangeCallback);
            }

            if (jvmMarketId == 0) {
                counterService.reset(appInfoRecord.getId(), num, appInfoRecord.getAppDefaultJvm());
            } else {
                counterService.reset(appInfoRecord.getId(), num, record.getJvmConfig());
            }
            List<PodInfoRecord> targetPods = Lists.newArrayList();
            List<PodInfoRecord> podInfoRecords = podInfo.getByAppId(appId);
            //获取调节的pod
            switch (poolType) {
                case EXPERIMENT:
                    List<PodInfoRecord> records = podInfoRecords.stream()
                            .filter(podRecord -> !StringUtils.isEmpty(podRecord.getPodJvm())).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(records)) {
                        records = podInfoRecords;
                    }
                    List<PodInfoRecord> jvmRecords = filterRecord(records,
                            (r) -> StringUtils.contains(r.getPodJvm(), UserUtil.getTuneJvmConfig(jvmMarketId)));
                    if (isGray) {
                        //  正常执行和回滚有有jvmMarketId都采用下面  收集不含有jvmMarketId的pod
                        jvmRecords = filterRecord(records,
                                (r) -> !StringUtils.contains(r.getPodJvm(), UserUtil.getTuneJvmConfig(jvmMarketId)));
                        //回滚情况下  defaultJvm无jvmMarketId 回滚含有jvmMarketId
                        if (jvmMarketId == 0 && !appInfoRecord.getAppDefaultJvm().contains("-DJvmMarketId=")) {
                            jvmRecords = filterRecord(records,
                                    (r) -> StringUtils.contains(r.getPodJvm(), UserUtil.getTuneJvmConfig(jvmMarketId)));
                        }
                    }
                    targetPods.addAll(jvmRecords);
                    if (targetPods.size() < num) {
                        List<PodInfoRecord> jvmDefaultRecords = filterRecord(records,
                                (r) -> StringUtils.contains(r.getPodJvm(), UserUtil.getTuneJvmConfig(null)));
                        targetPods.addAll(jvmDefaultRecords);
                    }
                    if (targetPods.size() < num) {
                        List<PodInfoRecord> defaultRecords = records.stream()
                                .filter(podInfoRecord -> !StringUtils.contains(podInfoRecord.getPodJvm(), UserUtil.getTuneJvmConfig(null)))
                                .filter(podInfoRecord -> !StringUtils
                                        .contains(podInfoRecord.getPodJvm(), UserUtil.getTuneJvmConfig(jvmMarketId)))
                                .collect(Collectors.toList());
                        targetPods.addAll(defaultRecords.subList(0, num - targetPods.size()));
                    }
                    break;
                case BATCH:
                    podInfoRecords = podInfoRecords.stream().filter(
                                    podRecord -> !StringUtils.contains(podRecord.getPodJvm(), UserUtil.getTuneJvmConfig(jvmMarketId)))
                            .collect(Collectors.toList());
                    targetPods.addAll(podInfoRecords);
                    break;
                default:
                    break;
            }
            if (targetPods.size() < num) {
                throw new RuntimeException(
                        String.format("delete num is error,please check-->targetNum=[%s],num=[%s]", targetPods.size(), num));
            }
            targetPods = targetPods.subList(0, num);
            //函数回调
            deletePods.accept(targetPods.stream().map(PodInfoRecord::getPodName).collect(Collectors.toList()));
            return callBackFunc.apply(targetPods);
        } catch (Exception e) {
            if (appName != null) {
                counterService.delete(appName);
            }
            log.warn("PodServiceImpl#changePod 执行过程抛出异常 e:{}", e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public void insertPod(Integer appId, Integer nodeId, String podName, String ip, String status, String podJvm, String env,
                          String podDeployType, String podTemplate, String podTags, String accessToken, String clusterName,
                          String k8sNamespace, String dHostName, String nodeIP, String nodeName) {
        PodInfoRecord record = new PodInfoRecord();
        record.setAppId(appId);
        record.setNodeId(nodeId);
        record.setPodName(podName);
        record.setIp(ip);
        record.setDHostname(dHostName);
        record.setStatus(status);
        if (StringUtils.isNotEmpty(podJvm)) {
            record.setPodJvm(podJvm);
        }
        record.setEnv(env);
        record.setPodDeployType(podDeployType);
        record.setPodTemplate(podTemplate);
        record.setPodTags(podTags);
        record.setAccessToken(accessToken);
        record.setClusterName(clusterName);
        record.setK8sNamespace(k8sNamespace);
        record.setCreatedTime(DateUtils.now());
        record.setNodeIp(nodeIP);
        record.setNodeName(nodeName);
        record.setPodStatus(StringUtils.equals(status, "Running") ? PodStatus.ALIVE.name() : PodStatus.INVALID.name());
        podInfo.insertPodInfo(record);
    }

    @Override
    public void updatePodStatue(Integer id, PodStatus status) {
        PodInfoRecord record = new PodInfoRecord();
        record.setId(id);
        record.setPodStatus(status.name());
        podInfo.update(record);
    }

    @Override
    public int getByPodNameAndAt(String podName, String accessToken) {
        PodInfoRecord podInfoRecord = podInfo.getByPodAndAT(podName, accessToken);
        if (podInfoRecord == null) {
            return -1;
        }
        return podInfoRecord.getId();
    }

    /**
     * "Tue, 19 Apr 2022 12:27:55 +0000";
     */
    @Override
    public String getPodDate(String podName) {
        try {
            if (StringUtils.isEmpty(podName)) {
                return "podName is empty, pls check.";
            }
            String response = agentInvokeService.execCmd(InvokeType.SYNC, podName, DATE_CMD);
            return JSON.parseObject(response, new TypeReference<String>() {});
        } catch (Exception e) {
            log.error("getPodDate podName={} occurs an error.", podName, e);
            return null;
        }
    }

    @Override
    public void deletePod(Integer appId) {
        podInfo.deletePod(appId);
    }

    @Override
    public List<PodProcessInfo> getPodProcessInfos(Integer podId) {
        ObjectUtil.checkIntegerPositive(podId, "Input pod must be positive");
        PodInfoRecord podInfoRecord = podInfo.getById(podId);
        if (podInfoRecord == null) {
            return Lists.newArrayList();
        }
        TwatchInfoDo twatchInfoDo = twatchInfoRepository.findOneByPod(podInfoRecord.getPodName());
        if (twatchInfoDo == null) {
            log.info("getPodProcessInfos can not find container by podName={}", podInfoRecord.getPodName());
            return Lists.newArrayList();
        }
        return getPodProcess(twatchInfoDo.getPodName());
    }

    private List<PodProcessInfo> getPodProcess(String podName) {
        String res = agentInvokeService.execCmd(InvokeType.SYNC, podName, "ps -ef");
        log.info("getPodProcess res={}", res);
        return ConvertUtils.convert2PodProcessInfo(res);
    }

    @Override
    public List<PodProcessInfo> getPodJavaProcess(String podName) {
        String res = agentInvokeService.getProcessByPod(InvokeType.SYNC, podName);
        log.info("getPodProcess res={} for podName={}", res, podName);
        return Lists.newArrayList();
    }

    /**
     * 根据podName 转换成相应的appName
     *
     * @param appName pod名称
     * @return 返回appName
     */
    private String convertAppName(String appName) {
        try {
            return appName.substring(0, StringUtils.lastOrdinalIndexOf(appName, "-", 2));
        } catch (Exception e) {
            return "";
        }
    }

    private List<PodInfoRecord> filterRecord(List<PodInfoRecord> records, Function<PodInfoRecord, Boolean> func) {
        return records.stream().filter(func::apply).collect(Collectors.toList());
    }

    private boolean rollback(String appName, Integer appId, Integer jvmMarketId, Function<List<PodInfoRecord>, Boolean> callBackFunc,
                             BiConsumer<PodInfoRecord, String> doChangeCallback)
            throws Exception {
        //随机取一个jvm参数的pod，进行删除
        List<PodInfoRecord> podInfoRecords = podInfo.getByAppId(appId);
        podInfoRecords = podInfoRecords.stream().filter(r ->
                        StringUtils.contains(r.getPodJvm(), UserUtil.getTuneJvmConfig(jvmMarketId))
                                || StringUtils.contains(r.getPodJvm(), UserUtil.getTuneJvmConfig(null)))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(podInfoRecords)) {
            return Boolean.TRUE;
        }
        PodInfoRecord record = podInfoRecords.get(0);
        counterService.reset(appId, 0, "");
        //删除
        return callBackFunc.apply(ImmutableList.of(record));
    }
}
