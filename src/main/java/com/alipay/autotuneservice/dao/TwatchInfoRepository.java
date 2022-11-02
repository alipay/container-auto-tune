/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;

import java.util.List;

/**
 * @author huangkaifei
 * @version : TwatchInfoService.java, v 0.1 2022年10月31日 5:13 PM huangkaifei Exp $
 */
public interface TwatchInfoRepository {

    void insert(TwatchInfoDo twatchInfoDo);

    List<TwatchInfoDo> findByContainerId(String containerId);

    List<TwatchInfoDo> findInfoByPod(String podName);

    List<TwatchInfoDo> findInfoByAgent(String agentName);

    List<TwatchInfoDo> listAll();
}