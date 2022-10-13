/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.model.dto;

import com.alipay.autotuneservice.model.pipeline.PipelineStatus;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author dutianze
 * @version PipelineDTO.java, v 0.1 2022年05月13日 15:49 dutianze
 */
@Data
@Slf4j
public class PipelineDTO {

    /**
     * 流程id
     */
    private final Integer pipelineId;

    /**
     * 计划ID
     */
    private final Integer tunePlanId;

    /**
     * healthCheckId
     */
    private final Integer healthCheckId;

    /**
     * 应用id
     */
    private final Integer       appId;
    /**
     * 当前阶段index
     */
    private final int           currentStageIndex;
    /**
     * 当前阶段
     */
    private final PipelineStage currentStage;

    /**
     * 流程整体状态
     */
    private final Status status;

    /**
     * 流程类型
     */
    private final PipelineStatus pipelineStatus;

    /**
     * 阶段列表
     */
    private List<PipelineItemDTO> pipelineItems;

    public PipelineDTO(TunePipeline tunePipeline, TunePipeline batchTunePipeline) {
        PipelineStage currentStage;
        if (batchTunePipeline != null && batchTunePipeline.getStage().equals(TuneStage.BATCH_NONE)) {
            currentStage = PipelineStage.PARAMETER;
        } else {
            currentStage = PipelineStage.of(tunePipeline.getStage());
        }
        log.info("PipelineDTO pipeLineStatus is: {}", tunePipeline.getContext().getPipelineStatus());
        if (PipelineStatus.GRAY.equals(tunePipeline.getContext().getPipelineStatus())) {
            if ((isParameter(tunePipeline, batchTunePipeline))) {
                currentStage = PipelineStage.PARAMETER;
            }
            if (isGrayJvm(tunePipeline)) {
                currentStage = PipelineStage.PREDICT;
            }
            if(batchTunePipeline != null && batchTunePipeline.getStage().equals(TuneStage.BATCH_NONE)){
                currentStage = PipelineStage.PROCESS;
            }
            log.info("PipelineDTO gray pipeline currentStage is: {}", currentStage);
        }
        this.pipelineStatus = tunePipeline.getContext().getPipelineStatus();
        this.pipelineId = tunePipeline.getPipelineId();
        this.tunePlanId = tunePipeline.getTunePlanId();
        this.appId = tunePipeline.getAppId();
        this.status = tunePipeline.getStatus();
        log.info("PipelineDTO  pipelineId is: {}, pipelineStatus is: {}", tunePipeline.getPipelineId(),
                tunePipeline.getContext().getPipelineStatus());
        this.healthCheckId = PipelineStatus.GRAY.equals(tunePipeline.getContext().getPipelineStatus()) ? 1
                : tunePipeline.getContext().getHealthCheckId();
        this.currentStageIndex = currentStage.getIndex();
        this.currentStage = currentStage;
        this.pipelineItems = Lists.newArrayList(new PipelineItemDTO(PipelineStage.HEALTHY),
                new PipelineItemDTO(PipelineStage.PREDICT),
                new PipelineItemDTO(PipelineStage.PARAMETER),
                new PipelineItemDTO(PipelineStage.PROCESS),
                new PipelineItemDTO(PipelineStage.EFFECT));
        PipelineStage finalCurrentStage = currentStage;
        this.pipelineItems.forEach(p -> p.changeStatus(finalCurrentStage));
    }

    private Boolean isParameter(TunePipeline tunePipeline, TunePipeline batchTunePipeline) {
        return  tunePipeline.getStage().equals(TuneStage.ADJUSTMENT_PARAMETER);
    }

    private Boolean isGrayJvm(TunePipeline tunePipeline) {
        return tunePipeline.getStage().equals(TuneStage.GRAY_JVM) || tunePipeline.getStage().equals(TuneStage.GRAY_NONE);
    }

}