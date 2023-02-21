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
import com.alipay.autotuneservice.controller.model.AppType;
import com.alipay.autotuneservice.controller.model.AppVO;
import com.alipay.autotuneservice.controller.model.ClusterVO;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.CommandInfoRepository;
import com.alipay.autotuneservice.dao.JavaInfoRepository;
import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.ConfigInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JavaInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.grpc.GrpcCommon;
import com.alipay.autotuneservice.model.common.AppEnum;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.AppInstallInfo;
import com.alipay.autotuneservice.model.common.AppModel;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.common.PodAttach;
import com.alipay.autotuneservice.model.common.PodAttachStatus;
import com.alipay.autotuneservice.model.common.PodStatus;
import com.alipay.autotuneservice.model.common.ServerType;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.service.AgentInvokeService;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.ConfigInfoService;
import com.alipay.autotuneservice.service.PodAttachService;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.ObjectUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version ClusterServiceImpl.java, v 0.1 2022年03月10日 17:21 dutianze
 */
@Slf4j
@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoRepository     appInfoRepository;
    @Autowired
    private PodInfo               podInfo;
    @Autowired
    private TunePlanRepository    tunePlanRepository;
    @Autowired
    private K8sAccessTokenInfo    k8sAccessTokenInfo;
    @Autowired
    private ConfigInfoService     configInfoService;
    @Autowired
    private Executor              eventExecutor;
    @Autowired
    private AgentInvokeService    agentInvokeService;
    @Autowired
    private PodAttachService      podAttachService;
    @Autowired
    private JavaInfoRepository    javaInfoRepository;
    @Autowired
    private CommandInfoRepository commandInfoRepository;

    @Override
    public List<AppInfoRecord> getByNodeId(Integer id) {
        List<AppInfoRecord> clusterInfoRecords = appInfoRepository.getByNodeId(id);
        if (CollectionUtils.isEmpty(clusterInfoRecords)) {
            throw new RuntimeException(String.format("未找到指定应用=[%s]", id));
        }
        return clusterInfoRecords;
    }

    @Override
    public AppModel findAppModel(String accessToken, String k8sNamespace, String appName) {
        AppInfoRecord record = appInfoRepository.findAppModel(accessToken, k8sNamespace, appName);
        return ConvertUtils.convert2ClusterModel(record);
    }

    @Override
    public int insertCoreParam(String accessToken, String nameSpace, String appName) {
        AppInfoRecord record = new AppInfoRecord();
        record.setAccessToken(accessToken);
        record.setAppName(appName);
        record.setStatus(AppStatus.ALIVE.name());
        record.setCreatedTime(DateUtils.now());
        return appInfoRepository.insetAppInfo(record);
    }

    @Override
    public void insertAppRecord(Integer userId, String accessToken, String nodeIds, String appName, String appAsName, String appDesc,
                                String status, String appDefaultJvm, String clusterName, String namespace) {
        AppInfoRecord record = new AppInfoRecord();
        record.setUserId(userId);
        record.setAccessToken(accessToken);
        record.setNodeIds(nodeIds);
        record.setAppName(appName);
        record.setAppAsName(appAsName);
        record.setAppDesc(appDesc);
        record.setCreatedTime(DateUtils.now());
        record.setStatus(status);
        record.setAppDefaultJvm(appDefaultJvm);
        record.setClusterName(clusterName);
        record.setNamespace(namespace);
        appInfoRepository.insertAppInfoRecord(record);
    }

    @Override
    public AppInfoRecord getByAppAndATAndNamespace(String appName, String accessToken, String namespace) {
        return appInfoRepository.getByAppAndATAndNamespace(appName, accessToken, namespace);
    }

    @Override
    public AppInfoRecord getByAppAndATAndNamespace(String appName, String namespace) {
        return appInfoRepository.getByAppAndATAndNamespace(appName, namespace);
    }

    @Override
    public void updateAppJvm(Integer id, String defaultJvm) {
        log.info("updateAppJvm, id:{}, defaultJvm:{}", id, defaultJvm);
        AppInfoRecord record = new AppInfoRecord();
        record.setId(id);
        record.setAppDefaultJvm(defaultJvm);
        appInfoRepository.updateAppDefaultJvm(record);
    }

    @Override
    public List<AppInfoRecord> getAllAlivePodsByToken(String accessToken, String clusterName) {
        return appInfoRepository.getAppByTokenAndCluster(accessToken, clusterName);
    }

    @Override
    public List<AppVO> appList(String accessToken, String appName) {
        List<AppInfoRecord> records = StringUtils.isEmpty(appName) ? appInfoRepository.getAppListByTokenAndStatus(accessToken,
                AppStatus.ALIVE) : appInfoRepository.appList(accessToken, appName);
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return buildAppList(records);
    }

    @Override
    public AppVO findByIdAndToken(String accessToken, Integer id) {
        if (id != null && StringUtils.isNotEmpty(accessToken)) {
            AppInfoRecord record = appInfoRepository.findByIdAndToken(accessToken, id);
            if (record != null) {
                return buildAppList(Collections.singletonList(record)).get(0);
            }
        }
        return null;
    }

    @Override
    public List<ClusterVO> clusterList(String accessToken) {
        List<ClusterVO> clusterVOS = new ArrayList<>();
        List<AppInfoRecord> appInfoRecords = appInfoRepository.getAppByTokenAndCluster(accessToken, "agent-default");
        if (appInfoRecords != null) {
            clusterVOS.add(new ClusterVO("agent-default ", 0, "agent-default", ""));
        }
        List<K8sAccessTokenInfoRecord> records = k8sAccessTokenInfo.selectByToken(accessToken);
        if (CollectionUtils.isEmpty(records)) {
            return clusterVOS;
        }
        clusterVOS.addAll(records.stream().map(item -> {
            ClusterVO clusterVO = new ClusterVO();
            clusterVO.setClusterName(item.getClusterName() + " (" + item.getRegion() + ")");
            clusterVO.setCluster(item.getClusterName());
            clusterVO.setRegion(item.getRegion());
            clusterVO.setId(item.getId());
            return clusterVO;
        }).collect(Collectors.toList()));

        return clusterVOS;
    }

    @Override
    public List<AppVO> appListByClusterAndRegion(String cluster, String token) {
        List<AppInfoRecord> records = appInfoRepository.getAppByTokenAndCluster(token, cluster);
        if (CollectionUtils.isEmpty(records)) {
            throw new RuntimeException(String.format("not found cluster by cluster=[%s], token=[%s]", cluster, token));
        }
        return buildAppList(records);

    }

    @Override
    public Map<String, List<AppVO>> appListByClusterAndRegionAndApp(String clusterName, String accessToken, String appName,
                                                                    AppType appType) {
        log.info("appListByClusterAndRegionAndApp enter");
        // find by accessToken And clusterName
        List<AppInfoRecord> records = appInfoRepository.getAppByTokenAndCluster(accessToken, clusterName);
        if (CollectionUtils.isEmpty(records)) {
            log.info("getAppByTokenAndCluster return null, accessToken: {}, clusterName: {}", accessToken, clusterName);
            return Maps.newHashMap();
        }

        // filter appName
        if (StringUtils.isNotBlank(appName)) {
            records = records.stream().filter(a -> a.getAppName().contains(appName)).collect(Collectors.toList());
        }

        // convert records to appVos
        log.info("buildAppList start");
        List<AppVO> appVOS = this.buildAppList(records);

        //judge appType
        appVOS = appVOS.stream().filter(appVO -> judgeAppType(appVO.getAppType(), appType)).sorted(
                Comparator.comparing(AppVO::getAgentNum).reversed()).collect(Collectors.toList());

        // convert appVOS to appMap
        Map<String, List<AppVO>> appMap = appVOS.stream().collect(
                Collectors.groupingBy(item -> StringUtils.isEmpty(item.getClusterName()) ? "agent-Default" : item.getClusterName()));

        // get all cluster
        List<ClusterVO> clusterVOS = this.clusterList(accessToken);

        // match cluster to cluster + region
        Map<String, String> clusterMapClusterName = clusterVOS.stream().collect(
                Collectors.toMap(ClusterVO::getCluster, ClusterVO::getClusterName, (e, n) -> e));
        log.info("appMap is: {}", JSON.toJSONString(appMap));
        return appMap.entrySet().stream()
                .collect(Collectors.toMap(e -> clusterMapClusterName.getOrDefault(e.getKey(), "agent-default"),
                        Entry::getValue, (e, n) -> e));
    }

    private List<AppVO> buildAppList(List<AppInfoRecord> records) {
        List<Integer> appIds = records.stream().map(AppInfoRecord::getId).collect(Collectors.toList());
        CompletableFuture<Map<Integer, Long>> podInfoFuture = CompletableFuture.supplyAsync(
                () -> {
                    List<PodInfoRecord> podRecords = podInfo.batchGetPodInstallTuneAgentNumsByAppId(appIds);
                    Map<Integer, Long> podMap = new HashMap<>();
                    appIds.forEach(
                            appId -> podMap.put(appId, podRecords.stream().filter(record -> record.getAppId().equals(appId)).count()));
                    return podMap;
                }, eventExecutor);
        CompletableFuture<List<TunePlan>> tunePlaneFuture = CompletableFuture.supplyAsync(
                () -> tunePlanRepository.batchFindLastTunePlanByAppId(appIds), eventExecutor);
        CompletableFuture<List<ConfigInfoRecord>> configInfoFuture = CompletableFuture.supplyAsync(
                () -> configInfoService.batchAppConfigByAppIds(appIds), eventExecutor);

        AtomicReference<List<AppVO>> atomicReference = new AtomicReference<>(new ArrayList<>());
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(podInfoFuture, tunePlaneFuture, configInfoFuture)
                .thenAcceptAsync(it -> {
                    Map<Integer, Long> podMap = podInfoFuture.getNow(Maps.newHashMap());
                    List<TunePlan> tunePlans = tunePlaneFuture.getNow(Lists.newArrayList());
                    List<ConfigInfoRecord> configInfoRecords = configInfoFuture.getNow(Lists.newArrayList());
                    Map<Integer, TunePlan> planMap = new HashMap<>();
                    Map<Integer, Boolean> configMap = new HashMap<>();
                    appIds.forEach(appId -> {
                        planMap.put(appId, null);
                        configMap.put(appId, Boolean.FALSE);
                        if (CollectionUtils.isNotEmpty(tunePlans)) {
                            tunePlans.stream().filter(p -> p.getAppId().equals(appId)).forEach(plan -> planMap.put(appId, plan));
                        }
                        if (CollectionUtils.isNotEmpty(configInfoRecords)) {
                            configInfoRecords.stream().filter(c -> c.getAppId().equals(appId)).forEach(
                                    config -> configMap.put(appId, Boolean.TRUE));
                        }
                    });
                    atomicReference.set(records.parallelStream().map(record -> {
                        AppVO appVO = new AppVO();
                        String appAsName = String.format("%s (%s)", record.getAppName(), record.getNamespace());
                        appVO.setAppName(appAsName);
                        appVO.setAppNameV1(record.getAppName());
                        appVO.setAgentNum(podMap.get(record.getId()).intValue());
                        appVO.setId(record.getId());
                        appVO.setClusterName(record.getClusterName());
                        appVO.setTuneStatus(configMap.get(record.getId()));
                        appVO.setNamespace(record.getNamespace());
                        buildMetricVO(record.getId(), appVO, planMap);
                        AppTag tag = record.getAppTag() != null ? JSON.parseObject(record.getAppTag(), new TypeReference<AppTag>() {})
                                : null;
                        if (tag == null || !StringUtils.equals(String.valueOf(tag.getLang()), AppEnum.JAVA.name())) {
                            appVO.setAppEnum(AppEnum.OTHER);
                            return appVO;
                        }
                        appVO.setAppType("JAVA");
                        if (StringUtils.equals(String.valueOf(tag.getLang()), AppEnum.JAVA.name()) && tag.isInstallAgent() && tag
                                .isInstallDockFile()) {
                            appVO.setAppEnum(AppEnum.DOCKERFILE);
                            return appVO;
                        }
                        if (StringUtils.equals(String.valueOf(tag.getLang()), AppEnum.JAVA.name()) && tag.isInstallAgent()) {
                            appVO.setAppEnum(AppEnum.AGENT);
                            return appVO;
                        }
                        if (StringUtils.equals(String.valueOf(tag.getLang()), AppEnum.JAVA.name())) {
                            appVO.setAppEnum(AppEnum.JAVA);
                            return appVO;
                        }
                        return appVO;
                    }).collect(Collectors.toList()));
                }, eventExecutor);
        voidCompletableFuture.join();
        return atomicReference.get();
    }

    private void buildMetricVO(Integer appId, AppVO appVO, Map<Integer, TunePlan> planMap) {
        TunePlan tunePlan = planMap.get(appId);
        if (tunePlan != null && tunePlan.getTuneEffectVO() != null) {
            tunePlan.getTuneEffectVO().getTuneResultVOList().stream().filter(Objects::nonNull).forEach(item -> {
                switch (item.getEffectTypeEnum()) {
                    case "CPU":
                        appVO.setCpuNum(item.getReduce() != null ? (int) Math.round(item.getReduce()) : null);
                        break;
                    case "MEM":
                        appVO.setMemNum(item.getReduce() != null ? (int) Math.round(item.getReduce()) : null);
                        break;
                    case "FGC_COUNT":
                        appVO.setFgcCount(item.getReduce() != null ? (int) Math.round(item.getReduce()) : null);
                        break;
                    case "FGC_TIME":
                        appVO.setFgcTime(item.getReduce() != null ? (int) Math.round(item.getReduce()) : null);
                        break;
                    default:
                }
            });
        }
    }

    @Override
    public List<AppVO> getAllAliveApp() {
        List<AppVO> appListVO = new ArrayList<>();
        List<AppInfoRecord> appInfoRecordList = appInfoRepository.getAppListByStatus(AppStatus.ALIVE);
        if (CollectionUtils.isEmpty(appInfoRecordList)) {
            return null;
        }
        appInfoRecordList.forEach(record -> {
            AppVO appVO = new AppVO();
            appVO.setAppName(record.getAppName());
            appVO.setNamespace(record.getNamespace());
            appVO.setId(record.getId());
            appListVO.add(appVO);
        });
        return appListVO;
    }

    @Override
    public AppInfoRecord selectById(Integer id) {
        return appInfoRepository.getById(id);
    }

    @Override
    public void updateAppStatue(Integer id, AppStatus appStatus) {
        AppInfoRecord record = new AppInfoRecord();
        record.setId(id);
        record.setStatus(appStatus.name());
        appInfoRepository.updateNodeIds(record);
    }

    @Override
    public AppInfoRecord findAndUpdateAppJvmDefault(String accessToken, String podName, String defaultJvm) {
        if (StringUtils.isBlank(defaultJvm)) {
            return null;
        }
        try {
            // 从pod中查询APP_ID
            PodInfoRecord podInfoRecord = podInfo.getByPodAndAT(podName, accessToken);
            if (podInfoRecord == null || podInfoRecord.getAppId() == null) {
                return null;
            }
            Integer appId = podInfoRecord.getAppId();
            AppInfoRecord byId = appInfoRepository.getById(appId);
            byId.setAppDefaultJvm(defaultJvm);
            appInfoRepository.updateAppDefaultJvm(byId);
            return byId;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getRecommendJvmOpts(String accessToken, String podName, String defaultJvm) {
        AppInfoRecord appInfoRecord = findAndUpdateAppJvmDefault(accessToken, podName, defaultJvm);
        if (appInfoRecord == null) {
            return "";
        }
        // return getJvm(appInfoRecord.getId()); // TODO getJVM(appId) 实现后 取消注释
        return null;
    }

    @Override
    public int getAppInstallTuneAgentNums(Integer appId, @Nullable String appName) {
        return Optional.ofNullable(podInfo.getPodInstallTuneAgentNumsByAppId(appId))
                .orElse(Lists.newArrayList()).stream().collect(Collectors.toList()).size();
    }

    @Override
    public void patchAppTag(Integer id, AppTag appTag) {
        try {
            log.info("patchAppTag, id:{}, appTag:{}", id, appTag);
            AppInfo appInfo = appInfoRepository.findById(id);
            Preconditions.checkNotNull(appInfo, "appInfo is null");

            AppTag appTagFromDb = ObjectUtils.defaultIfNull(appInfo.getAppTag(), new AppTag());
            Optional.ofNullable(appTag.getLang())
                    .ifPresent(lang -> appTagFromDb.setLang(appTag.getLang()));
            Optional.ofNullable(appTag.getJavaVersion())
                    .ifPresent(lang -> appTagFromDb.setJavaVersion(appTag.getJavaVersion()));
            Optional.ofNullable(appTag.getCollector())
                    .ifPresent(lang -> appTagFromDb.setCollector(appTag.getCollector()));
            appInfoRepository.save(id, appTagFromDb);
        } catch (Exception e) {
            log.error("patchAppTag, id:{}, appTag:{}", id, appTag, e);
        }
    }

    @Override
    public void patchAppTag(GrpcCommon grpcCommon, AppTag appTag) {
        try {
            AppInfoRecord appInfoRecord = getByAppAndATAndNamespace(grpcCommon.getAppName(), grpcCommon.getAccessToken(),
                    grpcCommon.getNamespace());
            if (appInfoRecord == null) {
                log.warn("appInfoRecord is null,appName={},accessToken={},namespace={}", grpcCommon.getAppName(),
                        grpcCommon.getAccessToken(), grpcCommon.getNamespace());
                return;
            }
            appTag.resetJvmCollector(appInfoRecord);
            this.patchAppTag(appInfoRecord.getId(), appTag);
        } catch (Exception e) {
            log.error("patchAppTag, appName:{}, accessToken:{}, appTag:{}", grpcCommon.getAppName(), grpcCommon.getAccessToken(), appTag,
                    e);
        }
    }

    @Override
    public void deleteApp(Integer id) {
        appInfoRepository.deleteAppById(id);
    }

    @Override
    public AppInstallInfo findAppInstallInfo(Integer appId) {
        ObjectUtil.checkIntegerPositive(appId, "appId must be more than zero.");
        AppInstallInfo appInstallInfo = new AppInstallInfo();
        List<PodInfoRecord> podList = podInfo.getByAppId(appId);
        if (CollectionUtils.isEmpty(podList)) {
            log.warn("findAppInstallInfo can not find pods by appId={}", appId);
            return appInstallInfo;
        }

        Optional<PodInfoRecord> podInfoRecordOptional = podList.stream().filter(
                item -> Objects.nonNull(item) && Objects.nonNull(item.getAgentInstall()) && item.getAgentInstall() == 1).findAny();
        if (podInfoRecordOptional.isPresent()) {
            // agent与dockerfile均安装
            appInstallInfo.setInstallAutoAgent(true);
            appInstallInfo.setIntegrateDockerFile(true);
            return appInstallInfo;
        }
        List<Integer> podIdList = podList.stream().map(PodInfoRecord::getId).collect(Collectors.toList());
        List<PodAttach> podAttachList = podAttachService.findByPodIds(podIdList);
        long attachPodNums = Optional.ofNullable(podAttachList).orElse(Lists.newArrayList()).stream()
                .filter(item -> PodAttachStatus.INSTALLED == item.getStatus())
                .count();
        if (attachPodNums > 0) {
            appInstallInfo.setInstallAutoAgent(true);
        }
        return appInstallInfo;
    }

    @Override
    public AppInstallInfo findAppInstallInfoV1(Integer appId) {
        ObjectUtil.checkIntegerPositive(appId, "appId must be more than zero.");
        AppInstallInfo appInstallInfo = new AppInstallInfo();
        List<PodInfoRecord> podList = podInfo.getByAppId(appId);
        if (CollectionUtils.isEmpty(podList)) {
            log.warn("findAppInstallInfo can not find pods by appId={}", appId);
            return appInstallInfo;
        }
        int count = (int) podList.stream().filter(
                item -> Objects.nonNull(item) && Objects.nonNull(item.getAgentInstall()) && item.getAgentInstall() == 1).count();
        if (count > 0) {
            // agent与dockerfile均安装
            appInstallInfo.setInstallAutoAgent(true);
            appInstallInfo.setIntegrateDockerFile(true);
            appInstallInfo.setInstallTuneAgentNums(count);
            appInstallInfo.setAttachTuneAgentNums(count);
            return appInstallInfo;
        }
        List<Integer> podIdList = podList.stream().map(PodInfoRecord::getId).collect(Collectors.toList());
        List<PodAttach> podAttachList = podAttachService.findByPodIds(podIdList);
        long attachPodNums = Optional.ofNullable(podAttachList).orElse(Lists.newArrayList()).stream()
                .filter(item -> PodAttachStatus.INSTALLED == item.getStatus())
                .count();
        appInstallInfo.setInstallTuneAgentNums((int) attachPodNums);
        return appInstallInfo;
    }

    @Override
    public void updateAppTime(Integer appId) {
        try {
            AppInfo record = appInfoRepository.findById(appId);
            if (null == record || null == record.getAppTag()) {
                throw new RuntimeException("查询appInfo表中数据为空");
            }
            AppTag tag = record.getAppTag() != null ? record.getAppTag() : null;
            if (tag != null) {
                tag.setLastModifyTime(System.currentTimeMillis());
                record.setAppTag(tag);
                appInfoRepository.save(record.getId(), tag);
            }
        } catch (Exception e) {
            log.error("updateAppTime occurs an error appId:{}", appId);
        }
    }

    /**
     * 三种类型  1.空展示  2.appVO传入JAVA 前端传入JAVA 3.appVO传入null 前端传入OTHER
     *
     * @param type    appVO 传入类型 JAVA、null
     * @param appType 前端传入类型 JAVA、OTHER
     * @return
     */
    private Boolean judgeAppType(String type, AppType appType) {
        //
        if (null == appType) {
            return Boolean.TRUE;
        }
        if (appType.equals(AppType.JAVA) && StringUtils.equals(appType.name(), type)) {
            return Boolean.TRUE;
        }
        if (appType.equals(AppType.OTHER) && StringUtils.isEmpty(type)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Integer checkAppName(String appName, String namespace, String accessToken, String javaVersion, String jvm) {
        AppInfoRecord appInfoRecord = appInfoRepository.findAliveAppModel(accessToken, namespace, appName);
        if (appInfoRecord == null) {
            AppTag appTag = new AppTag();
            appTag.setInstallAgent(Boolean.TRUE);
            appTag.setLang(AppTag.Lang.JAVA);
            appTag.setInstallDockFile(Boolean.FALSE);
            appTag.setLastModifyTime(System.currentTimeMillis());
            appTag.setJavaVersion(javaVersion);
            //进行insert
            AppInfoRecord record = new AppInfoRecord();
            record.setAccessToken(accessToken);
            record.setAppName(appName);
            record.setNamespace(namespace);
            record.setClusterName("agent-default");
            record.setStatus(AppStatus.ALIVE.name());
            record.setCreatedTime(DateUtils.now());
            record.setAppDefaultJvm(
                    jvm.replace("java -jar -Dtmasteraccess_token=CONTAINER_AUTO_TUNE_TOKEN -javaagent:autoTuneAgent.jar ", ""));
            record.setAppTag(JSONObject.toJSONString(appTag));
            return appInfoRepository.insetAppInfo(record);
        }
        return appInfoRecord.getId();
    }

    @Override
    public void checkVm(GrpcCommon grpcCommon) {
        PodInfoRecord podInfoRecord = podInfo.getByPodAndAN(grpcCommon.getHostname(), grpcCommon.getNamespace());
        if (podInfoRecord != null) {
            if (StringUtils.isEmpty(podInfoRecord.getServerType()) || StringUtils.isEmpty(podInfoRecord.getUnicode())
                    || podInfoRecord.getAgentInstall() == null || podInfoRecord.getAgentInstall() <= 0) {
                podInfoRecord.setAgentInstall(1);
                podInfoRecord.setServerType(grpcCommon.getServerType().name());
                podInfoRecord.setUnicode(grpcCommon.getUnionCode());
                podInfo.updateServerTypeUnicode(podInfoRecord);
                return;
            }
            return;
        }
        log.info("serverType:{}", grpcCommon.getServerType());
        if (ServerType.VM == grpcCommon.getServerType()) {
            PodInfoRecord record = new PodInfoRecord();
            record.setAppId(grpcCommon.getAppId());
            record.setPodName(grpcCommon.getHostname());
            record.setIp(grpcCommon.getPodIp());
            record.setStatus(Status.RUNNING.name());
            record.setAccessToken(grpcCommon.getAccessToken());
            record.setK8sNamespace(grpcCommon.getNamespace());
            record.setCreatedTime(LocalDateTime.now());
            record.setPodStatus(PodStatus.ALIVE.name());
            record.setServerType(grpcCommon.getServerType().name());
            record.setUnicode(grpcCommon.getUnionCode());
            record.setAgentInstall(1);
            record.setPodJvm(grpcCommon.getJvmConfig());
            //todo unicode
            podInfo.insertPodInfo(record);
        }
    }

    @Override
    public void checkJavaInfo(GrpcCommon grpcCommon) {
        String appName = grpcCommon.getAppName();
        String hostName = grpcCommon.getHostname();
        //判断javaInfo是否存在
        JavaInfoRecord javaInfoRecord = javaInfoRepository.findInfo(appName, hostName);
        if (javaInfoRecord != null) {
            return;
        }
        //下发指令
        commandInfoRepository.sendCommand(grpcCommon.getUnionCode(), RuleAction.JAVA_INFO_RETRY, UUID.randomUUID().toString());
    }
}
