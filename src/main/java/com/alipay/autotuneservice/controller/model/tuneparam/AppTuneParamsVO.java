/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller.model.tuneparam;

import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author huangkaifei
 * @version : AppTuneParamsVO.java, v 0.1 2022年05月04日 5:53 PM huangkaifei Exp $
 */
@Data
public class AppTuneParamsVO {

    private Integer             appId;
    private Integer             pipelineId;
    /**
     * 调优参数
     **/
    private List<TuneParamItem> tuneParamItems;
    /**
     * 新增参数数量
     */
    private Integer             newParamNum       = 0;
    /**
     * 删除参数数量
     */
    private Integer             delParamNum       = 0;
    /**
     * 相同参数数量
     */
    private Integer             sameParamNum      = 0;
    /**
     * 替换参数数量
     */
    private Integer             replaceParamNum   = 0;
    /**
     * 调优分组
     **/
    private List<TuneConfig>    tuneGroups;
    /**
     * 是否为自动调参
     **/
    private Boolean             autoTune          = false;
    /**
     * 调优参数是否完成
     */
    private boolean             tuneParamFinished = false;

    /**
     * 对比版本
     */
    private String compareVersion;

    /**
     * 灰度时间标
     */
    private Boolean grayCancelStatus = Boolean.FALSE;

    public void countParamNums() {
        if (CollectionUtils.isEmpty(tuneParamItems)) {
            return;
        }
        this.newParamNum = Math.toIntExact(tuneParamItems.stream().filter(Objects::nonNull)
                .filter(item -> item.getAttributeEnum() == TuneParamAttributeEnum.NEW).count());
        this.delParamNum = Math.toIntExact(tuneParamItems.stream().filter(Objects::nonNull)
                .filter(item -> item.getAttributeEnum() == TuneParamAttributeEnum.DELETE).count());
        this.replaceParamNum = Math.toIntExact(tuneParamItems.stream().filter(Objects::nonNull)
                .filter(item -> item.getAttributeEnum() == TuneParamAttributeEnum.REPLACE).count());
        this.sameParamNum = Math.toIntExact(tuneParamItems.stream().filter(Objects::nonNull)
                .filter(item -> item.getAttributeEnum() == TuneParamAttributeEnum.SAME).count());
    }
}