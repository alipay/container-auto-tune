/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller.model;

import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version TuneResultVO.java, v 0.1 2022年04月27日 4:20 下午 huoyuqi
 */
@Data
public class TuneEffectVO {

    /**
     * 健康检测项id
     */
    private Integer id;

    /**
     * 05效果验证时间
     */
    private Long verificationTime;

    /**
     * 剩余检测时间
     */
    private Long remainCheckTime;

    /**
     * 调整实体数量
     */
    private Integer podNum;

    /**
     * 参照检查开始时间
     */
    private long checkStartTime;

    /**
     * 参照检查结束时间
     */
    private long checkEndTime;

    /**
     * 参照观察开始时间
     */
    private Long observeStartTime;

    /**
     * 参照观察结束时间
     */
    private Long observeEndTime;

    /**
     * 评优得分
     */
    private Integer score;

    /**
     * 提升比例 6%
     */
    private Double promoteRate;

    /**
     * 当前pod数量
     */
    private Integer currentPodNum;

    /**
     * 优化后pod数量
     */
    private Integer tuneOptimizePodNum;

    /**
     * 预计总收益
     */
    private Double totalIncome;

    /**
     * 问题类型调参之后结果
     */
    private List<EffectTypeVO> tuneResultVOList;

    /**
     * 收益
     */
    private List<EffectIncomeVO> effectIncomeVOS;

    /**
     * 节省cpu数量
     */
    private Double tuneReduceCpu;

    /**
     * 节省mem的大小
     */
    private Double tuneReduceMem;

    /**
     * 节省pod 数量
     */
    private Double tuneReducePod;

    /**
     * 剩余时间
     */
    private Long effectTime;

    public TuneEffectVO() {

    }

    public TuneEffectVO(Integer score, Double promoteRate) {
        this.score = score;
        this.promoteRate = promoteRate;
    }

    public TuneEffectVO(Long effectTime) {
        this.effectTime = effectTime;
    }

    public Boolean checkFinishEvaluate() {
        return this.getObserveEndTime() != null && System.currentTimeMillis() > this.getObserveEndTime() && this.getScore() !=null;
        //return true;
    }

}