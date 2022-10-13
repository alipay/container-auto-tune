/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.controller.model.AppInfoVO;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;

import java.util.Collection;
import java.util.List;

/**
 * @author t-rex
 * @version AppInfoRepository.java, v 0.1 2022年02月17日 8:25 下午 t-rex
 */
public interface AppInfoRepository {

    List<AppInfoRecord> getByAccessToken(String accessToken, int pageNum, int pageSize);

    AppInfoVO getByClusterId(int clusterId);

    List<AppInfoRecord> getByClusterName(String clusterName);

    List<AppInfoRecord> getByIds(Collection<Integer> ids);

    List<AppInfoRecord> getByNodeId(Integer id);

    List<AppInfoRecord> getAppListByStatus(AppStatus appStatus);

    List<AppInfoRecord> getReportedApp();

    List<AppInfo> findAppByAccessTokenAndStatusAndTag(String accessToken, AppStatus appStatus, AppTag appTag);

    List<AppInfoRecord> getAppListByTokenAndStatus(String accessToken, AppStatus status);

    List<AppInfo> findByAccessTokenAndStatus(String accessToken, AppStatus status);

    AppInfoRecord findAppModel(String accessToken, String k8sNamespace, String appName);

    int findAppInstallAgentNums(String appId);

    String getAppName(Integer id);

    AppInfoRecord getById(Integer id);

    int insetAppInfo(AppInfoRecord record);

    void insertAppInfoRecord(AppInfoRecord record);

    AppInfoRecord getByAppAndAT(String appName, String accessToken);

    AppInfoRecord getByAppAndATAndNamespace(String appName, String accessToken, String namesapce);

    AppInfo findByAppAndATAndNamespace(String appName, String accessToken, String namespace);

    AppInfoRecord getByAppAndATAndNamespace(String appName, String namesapce);

    int updateNodeIds(AppInfoRecord record);

    int updateAppDefaultJvm(AppInfoRecord record);

    int updateAppTag(AppInfoRecord record);

    List<AppInfoRecord> appList(String accessToken, String appName);

    List<AppInfoRecord> getAppByTokenAndCluster(String accessToken, String cluster);

    AppInfo findById(Integer id);

    void save(Integer id, AppTag appTag);

    void deleteAppById(Integer id);
}