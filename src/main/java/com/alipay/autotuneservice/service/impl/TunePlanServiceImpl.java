/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.controller.model.TuneEffectVO;
import com.alipay.autotuneservice.controller.model.TunePlanVO;
import com.alipay.autotuneservice.controller.model.tuneplan.QueryTunePlanVO;
import com.alipay.autotuneservice.controller.model.tuneplan.TunePlanActionEnum;
import com.alipay.autotuneservice.dao.HealthCheckInfo;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.common.InstallType;
import com.alipay.autotuneservice.model.dto.PipelineDTO;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.PodService;
import com.alipay.autotuneservice.service.TunePlanService;
import com.alipay.autotuneservice.service.pipeline.TunePipelineService;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.ObjectUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : TunePlanServiceImpl.java, v 0.1 2022年05月06日 4:29 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class TunePlanServiceImpl implements TunePlanService {

    @Autowired
    private TunePlanRepository  tunePlanRepository;
    @Autowired
    private TunePipelineService tunePipelineService;
    @Autowired
    private PodService          podService;
    @Autowired
    private HealthCheckInfo     healthCheckInfo;
    @Autowired
    private AppInfoService      appInfoService;
    @Autowired
    private RedisClient         redisClient;

    @Override
    public Boolean execAction(Integer pipelineId, TunePlanActionEnum actionEnum) {
        //根据pipeline检查状态
        TunePlanVO tunePipelineVO = tunePipelineService.getTunePipelineById(pipelineId);
        if (tunePipelineVO.getTunePlanId() == null) {
            throw new UnsupportedOperationException(String.format("根据pipelineId=%s未查询到运行中的调优计划",
                pipelineId));
        }
        Integer tunePlanId = tunePipelineVO.getTunePlanId();
        // check tune plan status
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePlanId);
        if (TunePlanStatus.END.equals(tunePlan.getTunePlanStatus())) {
            log.error("TunePlanServiceImpl#execAction - The tunePlanId={} was finished.",
                tunePlanId);
            throw new UnsupportedOperationException(String.format(
                "The tunePlanId=%s has been finished.", tunePlanId));
        }
        Boolean result = doAction(tunePlanId, actionEnum);
        log.info("execAction result={} for pipelineId={}", result, pipelineId);
        return result;
    }

    private Boolean doAction(Integer tunePlanId, TunePlanActionEnum targetAction) {
        List<TunePipeline> filterList = Optional.ofNullable(tunePipelineService.findByPlanId(tunePlanId))
                .orElse(Lists.newArrayList()).stream()
                .filter(o -> o.getStatus() != Status.CLOSED)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            log.info("doAction - can not find TunePipeline by tunePlanId={}", tunePlanId);
            return Boolean.FALSE;
        }
        // 目前一个tune plan只有一个pipeline, 因此先获取第一个
        log.info("updateTunePlanStatus for planId={} and action={}", tunePlanId, targetAction.name());
        updateTunePlanStatus(tunePlanId, targetAction.getStatus());
        return Boolean.TRUE;
    }

    @Override
    public QueryTunePlanVO findTunePlans(Integer appId, TunePlanStatus status, String planName, Long startTime, Long endTime) {
        Preconditions.checkArgument(appId >= 0, "appId不能为负数");
        QueryTunePlanVO queryTunePlanVO = new QueryTunePlanVO();
        // appId -> List<TunePlan>
        List<TunePlan> tunePlans = tunePlanRepository.findByAppIdAndStatus(appId, status, startTime, endTime);
        if (CollectionUtils.isEmpty(tunePlans)) {
            return queryTunePlanVO;
        }
        if (StringUtils.isNotBlank(planName)) {
            tunePlans = tunePlans.stream().filter(
                            item -> StringUtils.isNotBlank(item.getPlanName()) && item.getPlanName().contains(planName))
                    .collect(Collectors.toList());
        }
        List<Integer> planIds = tunePlans.stream().map(TunePlan::getId).collect(Collectors.toList());
        List<PipelineDTO> pipelineDTOS = tunePipelineService.batchFindMainPipelinesByPlanIds(planIds);
        List<TunePlanVO> collect = tunePlans.stream()
                .filter(Objects::nonNull)
                .map(tunePlan -> {
                    try {
                        TunePlanVO tunePlanVO = new TunePlanVO();
                        tunePlanVO.setTunePlanId(tunePlan.getId());
                        tunePlanVO.setPlanName(tunePlan.getPlanName());
                        tunePlanVO.setTuneActionType(tunePlan.getActionStatus());
                        tunePlanVO.setTunePlanScore(getPlanScore(tunePlan));
                        Optional<PipelineDTO> first = pipelineDTOS.stream()
                                .filter(pipelineDTO -> pipelineDTO.getTunePlanId().equals(tunePlan.getId()))
                                .findFirst();
                        if (first.isPresent()) {
                            PipelineDTO pipelineDTO = first.get();
                            tunePlanVO.setPipelineDTO(pipelineDTO);
                            tunePlanVO.setTunePlanStatus(
                                    pipelineDTO.getStatus() == Status.CANCEL ? TunePlanStatus.CANCEL : tunePlan.getTunePlanStatus());
                        }
                        tunePlanVO.setStartTime(DateUtils.asTimestamp(tunePlan.getCreatedTime()));
                        tunePlanVO.setUpdateTime(tunePlanVO.getTunePlanStatus() == TunePlanStatus.RUNNING ? System.currentTimeMillis()
                                : DateUtils.asTimestamp(tunePlan.getUpdateTime()));
                        tunePlanVO.buildTotalTime();
                        tunePlanVO.buildTunePlanTIme();
                        tunePlanVO.setEntityNums(getNumsByType(appId, InstallType.POD, (id) -> podService.getAppPodNum(id)));
                        tunePlanVO.setAgentNums(getNumsByType(appId, InstallType.AGENT,
                                (id) -> appInfoService.findAppInstallInfoV1(id).getAttachTuneAgentNums()));
                        return tunePlanVO;
                    } catch (Exception e) {
                        log.error("findTunePlans build tunePlanVO occurs an error.", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((o1, o2) -> o1.getStartTime() > o2.getStartTime() ? -1 : 1)
                .collect(Collectors.toList());
        queryTunePlanVO.setTunePlanList(CollectionUtils.isEmpty(collect) ? Lists.newArrayList() : collect);
        queryTunePlanVO.buildTunePlanNums();
        queryTunePlanVO.buildTunePlanList(status);
        return queryTunePlanVO;
    }

    private int getNumsByType(Integer appId, InstallType type,
                              Function<Integer, Integer> getNumsFunc) {
        if (!ObjectUtil.checkInteger(appId)) {
            return 0;
        }
        String cacheKey = String.format("now_%s_Nums_%s", type.name(), appId);
        Integer nums = (Integer) redisClient.get(cacheKey);
        if (ObjectUtil.checkInteger(nums)) {
            return nums;
        }
        Integer appPodNum = getNumsFunc.apply(appId);
        redisClient.setNx(cacheKey, appPodNum, 2, TimeUnit.MINUTES);
        return appPodNum;
    }

    private String getPlanScore(TunePlan tunePlan) {
        try {
            // tunePlan非END状态使用healthcheck的score， END状态使用调参收益的分数
            if (tunePlan.getTunePlanStatus() != TunePlanStatus.END) {
                return healthCheckInfo.selectById(tunePlan.getHealthCheckId()).getGrade();
            }
            TuneEffectVO tuneEffectVO = tunePlan.getTuneEffectVO();
            if (Objects.isNull(tuneEffectVO)) {
                return "";
            }
            if (tuneEffectVO.getScore() == null) {
                return healthCheckInfo.selectById(tunePlan.getHealthCheckId()).getGrade();
            }
            return tuneEffectVO.getScore().toString();
        } catch (Exception e) {
            log.info("getPlanScore occurs an error", e);
            return "";
        }
    }

    private void updateTunePlanStatus(Integer tunePlanId, TunePlanStatus planStatus) {
        // update status and update_time
        tunePlanRepository.updateTuneStatusById(tunePlanId, planStatus);
    }
}