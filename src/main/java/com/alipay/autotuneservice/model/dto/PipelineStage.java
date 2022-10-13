/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.model.dto;

import com.alipay.autotuneservice.model.pipeline.TuneStage;

/**
 * @author dutianze
 * @version PipelineStage.java, v 0.1 2022年05月13日 16:23 dutianze
 */
public enum PipelineStage implements Comparable<PipelineStage> {

    /**
     * 健康检测阶段
     */
    HEALTHY(0),

    /**
     * 实验参数
     */
    PREDICT(1),

    /**
     * 确认参数
     */
    PARAMETER(2),

    /**
     * 调参进程
     */
    PROCESS(3),

    /**
     * 效果验证
     */
    EFFECT(4);

    private final int index;

    PipelineStage(Integer index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static PipelineStage of(TuneStage tuneStage) {
        switch (tuneStage) {
            case HEALTHY_CHECK:
                return HEALTHY;
            case ADJUSTMENT_PARAMETER:
                return PREDICT;
            case TUNING_PROCESS:
                return PROCESS;
            case VERIFY_EFFECT:
            case CLOSED:
                return EFFECT;
        }
        return HEALTHY;
    }


    public static PipelineStage with(TuneStage tuneStage) {
        switch (tuneStage) {
            case HEALTHY_CHECK:
                return HEALTHY;
            case GRAY_JVM:
                return PREDICT;
            case TUNING_PROCESS:
                return PROCESS;
            case VERIFY_EFFECT:
            case CLOSED:
                return EFFECT;
        }
        return HEALTHY;
    }


}