/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.controller.model.tuneparam.AppTuneParamsVO;
import com.alipay.autotuneservice.controller.model.tuneparam.SubmitTuneParamsRequest;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamItem;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsRequest;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsVO;
import com.alipay.autotuneservice.model.tune.params.DecisionedTuneParam;
import com.alipay.autotuneservice.model.tune.params.TuneParamUpdateStatus;

import java.util.List;

/**
 * @author huangkaifei
 * @version : TuneParamService.java, v 0.1 2022年05月17日 3:20 PM huangkaifei Exp $
 */
public interface TuneParamService {
    /**
     * 获取调优参数
     *
     * @param appId
     * @param pipelineId
     * @return
     */
    AppTuneParamsVO getTuneParams(Integer appId, Integer pipelineId);

    /**
     * 提交调优参数
     *
     * @param appId
     * @param pipelineId
     * @param request
     * @return submitId
     */
    Boolean submitTuneParam(Integer appId, Integer pipelineId, SubmitTuneParamsRequest request);

    /**
     * 提交自动调优的调优参数
     *
     * @param appId
     * @param pipelineId
     * @param appDefaultJvm
     * @param recommendJvm
     * @return
     */
    Boolean submitAutoTuneParam(Integer appId, Integer pipelineId, String appDefaultJvm, String recommendJvm);

    /**
     * 灰度二次提交
     * @param appId
     * @param pipelineId
     * @param appDefaultJvm
     * @param recommendJvm
     * @return
     */
    Boolean submitGrayAutoTuneParam(Integer appId, Integer pipelineId, String appDefaultJvm, String recommendJvm);

    /**
     * query TuneParam Status
     *
     * @param decisionId
     * @return
     */
    TuneParamUpdateStatus queryTuneParamStatus(String decisionId);

    /**
     * query Tune Param Status by appId and pipelineId
     *
     * @param appId
     * @param pipelineId
     * @return
     */
    TuneParamUpdateStatus queryTuneParamStatus(Integer appId, Integer pipelineId);

    /**
     * 获取最终的调优参数，包含应用调优参数和调优分组
     *
     * @param appId
     * @param pipelineId
     * @return
     */
    DecisionedTuneParam getDecisionedTuneParams(Integer appId, Integer pipelineId);

    /**
     * 更新调优参数
     * note: 对应前端页面修改完参数后点击Sure的请求
     *
     * @param request
     * @return
     */
    UpdateTuneParamsVO updateTuneParams(UpdateTuneParamsRequest request);

    /**
     *  获取推荐jvm参数
     * @param appId
     * @param pipelineId
     * @param accessToken
     * @return
     */
    List<TuneParamItem> getAppRecommendTuneParamItem(Integer appId, Integer pipelineId, String accessToken);
}