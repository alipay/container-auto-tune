/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;

import java.util.List;

/**
 * @author huoyuqi
 * @version PodInfo.java, v 0.1 2022年04月18日 7:13 下午 huoyuqi
 */
public interface PodInfo {

    void insertPodInfo(PodInfoRecord podInfoRecord);

    List<PodInfoRecord> getAllPods();

    List<PodInfoRecord> getByAppId(Integer appId);

    List<PodInfoRecord> getAllAlivePods();

    List<PodInfoRecord> getByAllPodByAppId(Integer appId);

    List<PodInfoRecord> getAllAlivePodsByToken(String accessToken, String clusterName);

    List<PodInfoRecord> findByAppIds(List<Integer> appIds);

    int update(PodInfoRecord record);

    PodInfoRecord getById(Integer id);

    int updatePodInfoResourceFields(PodInfoRecord record);

    PodInfoRecord getByPodAndAT(String podName, String accessToken);

    String findOneRunningPodNameByAppId(Integer appId);

    List<PodInfoRecord> findByAccessToken(String accessToken);

    int updatePodInstallTuneAgent(PodInfoRecord record);

    List<PodInfoRecord> getPodInstallTuneAgentNumsByAppId(Integer appId);

    List<PodInfoRecord> batchGetPodInstallTuneAgentNumsByAppId(List<Integer> appId);

    List<PodInfoRecord> getAllAlivePodsByApp(Integer appId);

    void deletePod(Integer appId);

    PodInfoRecord findById(Integer id);

    List<PodInfoRecord> getDHostNameAlivePods(String dHostName);
}