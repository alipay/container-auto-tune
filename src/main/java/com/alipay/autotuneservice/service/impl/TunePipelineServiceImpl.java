/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.controller.model.TunePlanVO;
import com.alipay.autotuneservice.dao.TuneParamTrialDataRepository;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.dto.PipelineDTO;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TunePipelinePhase;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TuneActionStatus;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.service.pipeline.TunePipelineService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version TunePipelineServiceImpl.java, v 0.1 2022年04月11日 18:05 dutianze
 */
@Slf4j
@Service
public class TunePipelineServiceImpl implements TunePipelineService {

    @Autowired
    private TunePipelineRepository       tunePipelineRepository;
    @Autowired
    private TunePlanRepository           tunePlanRepository;
    @Autowired
    private TuneParamTrialDataRepository repository;

    @Override
    public TunePipeline createPipeline(TuneContext context) {
        // check
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(context.getAppId());
        Preconditions.checkNotNull(context.getAccessToken());
        List<TunePipeline> runningPipelines = tunePipelineRepository.findByAppIdAndStatus(context.getAppId(), Status.RUNNING);
        if (CollectionUtils.isNotEmpty(runningPipelines)) {
            log.error("createPipeline error, same time only on running pipeline, context:{}", context);
            throw new RuntimeException("same time only on running pipeline");
        }
        // build
        TunePipeline tunePipeline = new TunePipeline();
        tunePipeline.setAccessToken(context.getAccessToken());
        tunePipeline.setAppId(context.getAppId());
        tunePipeline.setMachineId(MachineId.TUNE_PIPELINE);
        tunePipeline.setStatus(Status.RUNNING);
        tunePipeline.setStage(TuneStage.NONE);
        if (StringUtils.isNotEmpty(context.getGrayJvm())) {
            log.info("createPipeline gray pipeline");
            tunePipeline.setMachineId(MachineId.TUNE_JVM_PIPELINE);
            tunePipeline.setStatus(Status.RUNNING);
            tunePipeline.setStage(TuneStage.NONE_JVM);
        }
        TunePipelinePhase tunePipelinePhase = new TunePipelinePhase(tunePipeline, context);
        tunePipeline.setCurrentPhase(tunePipelinePhase);
        tunePipeline.setTunePlanId(context.getTunePlanId());
        // save
        return tunePipelineRepository.saveOneWithTransaction(tunePipeline);
    }

    @Override
    public List<TunePipeline> findByAppIdAndStatus(Integer appId, Status status) {
        return tunePipelineRepository.findByAppIdAndStatus(appId, status);
    }

    @Override
    public PipelineDTO findMainPipelineByPipelineId(Integer pipelineId) {
        //TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(MachineId.TUNE_PIPELINE, pipelineId);
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
        TunePipeline batchTunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(MachineId.TUNE_BATCH_PIPELINE, pipelineId);
        if (tunePipeline == null) {
            return null;
        }
        return new PipelineDTO(tunePipeline, batchTunePipeline);
    }

    @Override
    public List<PipelineDTO> batchFindMainPipelinesByPlanIds(List<Integer> planIds) {
        //List<TunePipeline> pipelines = tunePipelineRepository.batchFindMainPipelinesByPlanIds(MachineId.TUNE_PIPELINE, planIds);
        //todo 修改
        List<TunePipeline> pipelines = tunePipelineRepository.batchFindPipelinesByPlanIds(planIds);
        Map<Integer, TunePipeline> pipelineIdMapBatchPipeline = tunePipelineRepository.batchFindMainPipelinesByPlanIds(
                MachineId.TUNE_BATCH_PIPELINE, planIds)
                .stream()
                .collect(Collectors.toMap(TunePipeline::getPipelineId, e -> e, (e, n) -> e));
        return pipelines.stream().map(p -> new PipelineDTO(p, pipelineIdMapBatchPipeline.get(p.getPipelineId()))).collect(
                Collectors.toList());
    }

    @Override
    public List<TunePipeline> findByPlanId(Integer planId) {
        return tunePipelineRepository.findByPlanId(planId);
    }

    private List<TunePipeline> filterByTunePlanId(List<TunePipeline> tunePipelines, Integer tunePlanId) {
        return Optional.ofNullable(tunePipelines).orElse(Lists.newArrayList()).stream()
                .filter(Objects::nonNull)
                .filter(pipeline -> tunePlanId.equals(pipeline.getTunePlanId())).collect(Collectors.toList());
    }

    @Override
    public TunePlanVO getTunePipelineById(Integer pipelineId) {
        TunePipeline tunePipeline = tunePipelineRepository.findByPipelineId(pipelineId);
        if (tunePipeline == null) {
            return new TunePlanVO();
        }
        TunePlanVO tunePipelineVO = new TunePlanVO();
        tunePipelineVO.setTunePlanId(tunePipeline.getTunePlanId());
        return tunePipelineVO;
    }

    @Override
    public TunePlan findByPipelineId(Integer pipelineId) {
        TunePlanVO pipelineVO = this.getTunePipelineById(pipelineId);
        if (pipelineVO.getTunePlanId() == null) {
            return null;
        }
        return tunePlanRepository.findTunePlanById(pipelineVO.getTunePlanId());
    }

    @Override
    public Boolean checkTunePlanIsAuto(Integer pipelineId) {
        TunePlan tunePlan = findByPipelineId(pipelineId);
        return tunePlan != null && tunePlan.getActionStatus() == TuneActionStatus.AUTO;
    }

    @Override
    public Boolean cancelPipeline(Integer pipelineId) {
        log.info("cancelPipeline, pipelineId:{}", pipelineId);
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
        if (tunePipeline == null || !tunePipeline.isAlive()) {
            throw new ServerException(ResultCode.UNSUPPORTED_OPERATOR_ERROR);
        }
        List<TunePipeline> aliveTunePipelines = tunePipelineRepository.findByPlanId(tunePipeline.getTunePlanId());
        aliveTunePipelines.forEach(aliveTunePipeline -> aliveTunePipeline.setStatus(Status.CANCEL));
        tunePipelineRepository.saveWithTransaction(aliveTunePipelines.toArray(new TunePipeline[0]));
        tunePlanRepository.updateTuneStatusById(tunePipeline.getTunePlanId(), TunePlanStatus.CANCEL);
        repository.updateTaskStatus(pipelineId);
        return true;
    }
}