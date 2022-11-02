/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.schedule;

import com.alipay.autotuneservice.configuration.EnvHandler;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.service.TuneEffectService;
import com.alipay.autotuneservice.util.TraceIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version TuneEffectTask.java, v 0.1 2022年07月04日 7:09 下午 huoyuqi
 */
@Slf4j
@Component
public class TuneEffectTask {

    private static final String LOCK_LEY = "tmaster_TuneEffectTask";

    @Autowired
    private EnvHandler             envHandler;
    @Autowired
    private RedisClient            redisClient;
    @Autowired
    private TunePlanRepository     tunePlanRepository;
    @Autowired
    private TuneEffectService      tuneEffectService;
    @Autowired
    private TunePipelineRepository tunePipelineRepository;

    @Scheduled(fixedRate = 60 * 2000)
    public void doTask() {
        if (envHandler.isDev()) {
            return;
        }
        try {
            TraceIdGenerator.generateAndSet();
            invoke();
            //redisClient.doExec(LOCK_LEY, this::invoke);
        } finally {
            TraceIdGenerator.clear();
        }
    }

    public void invoke() {
        try {
            log.info("TuneEffectTask#doTask 开始执行任务");
            List<TunePlan> tunePlans = tunePlanRepository.findByStatus();
            if (CollectionUtils.isEmpty(tunePlans)) {
                return;
            }
            List<Integer> planIds = tunePlans.stream().map(TunePlan::getId).collect(Collectors.toList());
            List<TunePipeline> repositories = tunePipelineRepository.batchFindPipelinesByPlanIds(planIds);
            if (CollectionUtils.isEmpty(repositories)) {
                return;
            }
            //构建map key:tunePlanId value:PipelineId
            Map<Integer, Integer> planIdMap = new HashMap<>();
            repositories.forEach(p -> {
                if (planIds.contains(p.getTunePlanId())) {
                    planIdMap.put(p.getTunePlanId(), p.getId());
                }
            });

            // 判断tunePlan
            tunePlans.parallelStream().filter(
                    tunePlan -> isTrigger(tunePlan)).
                    forEach(item -> {
                        if (planIdMap.get(item.getId()) != null) { tuneEffectService.triggerTuneEffect(planIdMap.get(item.getId())); }
                    });
            log.info("TuneEffectTask#doTask 结束执行任务");
        } catch (Exception e) {
            log.error("TuneEffectTask is error", e);
        }
    }

    private Boolean isTrigger(TunePlan tunePlan){
        Boolean isEffect = tunePlan.getTuneEffectVO() != null && tunePlan.getTunePlanStatus() == TunePlanStatus.END
                && tunePlan.getTuneEffectVO().getObserveEndTime() != null
                && System.currentTimeMillis() > (tunePlan.getTuneEffectVO().getObserveEndTime() - 2 * 60 * 1000)
                && tunePlan.getTuneEffectVO().getScore() == null;
        Boolean isGrayPredict = tunePlan.getPredictEffectVO() != null
                && tunePlan.getTunePlanStatus() != TunePlanStatus.CANCEL
                && tunePlan.getPredictEffectVO().getObserveEndTime() != null
                && System.currentTimeMillis() > tunePlan.getPredictEffectVO().getObserveEndTime()
                && tunePlan.getPredictEffectVO().getScore() == null;
        log.info("isTrigger isEffect: {}, isGrayPredict: {}", isEffect, isGrayPredict);
        return isEffect || isGrayPredict;
    }

}