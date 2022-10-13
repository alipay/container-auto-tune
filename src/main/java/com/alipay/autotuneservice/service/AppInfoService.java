/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.controller.model.AppVO;
import com.alipay.autotuneservice.controller.model.ClusterVO;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.grpc.GrpcCommon;
import com.alipay.autotuneservice.model.common.AppInstallInfo;
import com.alipay.autotuneservice.model.common.AppModel;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author dutianze
 * @version AppInfoService.java, v 0.1 2022年03月10日 17:20 dutianze
 */
public interface AppInfoService {

    List<AppModel> getByIds(Collection<Integer> ids);

    List<AppInfoRecord> getByNodeId(Integer id);

    AppModel findAppModel(String accessToken, String k8sNamespace, String appName);

    int insertCoreParam(String accessToken, String nameSpace, String hostname);

    void insertAppRecord(Integer userId, String accessToken, String nodeIds, String appName, String appAsName, String appDesc,
                         String status, String appDefaultJvm, String clusterName, String namespace);

    AppInfoRecord getByAppAndATAndNamespace(String appName, String accessToken, String namespace);

    AppInfoRecord getByAppAndATAndNamespace(String appName, String namespace);

    void updateNodeIds(Integer id, String nodeIds);

    void updateAppJvm(Integer id, String defaultJvm);

    void updateAppStatue(Integer id, AppStatus appStatus);

    List<AppInfoRecord> getAllAlivePodsByToken(String accessToken, String clusterName);

    List<AppVO> appList(String accessToken, String appName);

    /**
     * 获取所有的集群名+Region List
     */
    List<ClusterVO> clusterList(String accessToken);

    /**
     * 根据集群名(集群名+region)和token 进行查询
     */
    List<AppVO> appListByClusterAndRegion(String clusterName, String accessToken);

    Map<String, List<AppVO>> appListByClusterAndRegionAndApp(String clusterName, String accessToken, String appName);

    List<AppVO> getAllAliveApp();

    AppInfoRecord selectById(Integer id);

    AppInfoRecord findAndUpdateAppJvmDefault(String accessToken, String podName, String defaultJvm);

    String getRecommendJvmOpts(String accessToken, String podName, String defaultJvm);

    int getAppInstallTuneAgentNums(Integer appId, String appName);

    // String appName, String accessToken
    void patchAppTag(Integer id, AppTag appTag);

    void patchAppTag(GrpcCommon grpcCommon, AppTag appTag);

    void deleteApp(Integer id);

    AppInstallInfo findAppInstallInfo(Integer appId);

    /**
     * 查询app安装信息， 该接口耗时长
     *
     * @param appId
     * @return
     */
    AppInstallInfo findAppInstallInfoV1(Integer appId);

    /**
     * 根据appId更新时间
     * @param appId
     */
    void  updateAppTime(Integer appId);
}