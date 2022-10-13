/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.controller.model.PodProcessInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.common.PodStatus;
import com.alipay.autotuneservice.model.tunepool.PoolType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author chenqu
 * @version : PodService.java, v 0.1 2022年04月05日 20:29 chenqu Exp $
 */
public interface PodService {

    /**
     * 获取应用的pod数
     *
     * @param appId
     * @return
     */
    public Integer getAppPodNum(Integer appId);

    /**
     * 根据appId 和 jvmMarkeId 获取pod数量
     *
     * @param appId       应用id
     * @param JvmMarketId jvm调参版本：-1为默认不调节的应用数
     * @return 数量
     */
    public Integer getPodNumByIdAndJvm(Integer appId, String JvmMarketId);

    /**
     * 获取应用的pod数
     *
     * @param appId
     * @return
     */
    public Integer getAppRunningPodNum(Integer appId);

    /**
     * 获取指定环境变量的应用数
     *
     * @param appId
     * @return
     */
    public Integer getAppPodNumByJvmId(Integer appId, Integer jvmId);

    /**
     * 获取指定环境变量的应用数
     *
     * @param appId
     * @param num
     */
    public boolean changePod(Integer appId, Integer jvmMarketId, Integer num, PoolType poolType,
                             Function<List<PodInfoRecord>, Boolean> callBackFunc,
                             BiConsumer<PodInfoRecord, String> doChangeCallback, Consumer<List<String>> deletePods, Boolean isGray);

    public void insertPod(Integer appId, Integer nodeId, String podName, String ip, String status, String podJvm, String env,
                          String podDeployType, String podTemplate, String podTags, String accessToken, String clusterName,
                          String k8sNamespace, String dHostName, String nodeIP, String nodeName);

    void updatePodStatue(Integer id, PodStatus status);

    int getByPodNameAndAt(String podName, String status);

    /**
     * Get pod date
     *
     * @param podName
     * @return
     */
    String getPodDate(String podName);

    /**
     * 删除关联appId中所有的pod
     *
     * @param appId
     */
    void deletePod(Integer appId);

    /**
     * 获取pod进程信息
     *
     * @param podId
     * @return
     */
    List<PodProcessInfo> getPodProcessInfos(Integer podId);

    /**
     * get pod java process
     *
     * @param podName
     * @return
     */
    List<PodProcessInfo> getPodJavaProcess(String podName);
}