/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller.model.tuneparam;

import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author huangkaifei
 * @version : SubmitTuneParamsRequest.java, v 0.1 2022年05月19日 12:32 PM huangkaifei Exp $
 */
@Data
@Builder
public class SubmitTuneParamsRequest {

    /***
     * app id
     */
    private Integer appId;

    /**
     * pipeline Id
     */
    private Integer pipelineId;

    /**
     * 默认的调优参数，仅后端使用
     */
    private List<TuneParamItem> defaultTuneParamItems;

    /**
     * 提交的调优参数
     */
    private List<TuneParamItem> tuneParamItems;

    /**
     * 提交的调优分组
     */
    private List<TuneConfig> tuneGroups;

    /**
     * 提交人
     */
    private String operator;

    /**
     * 判断灰度是否是二次提交
     */
    private Boolean flag;
}