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
package com.alipay.autotuneservice.controller;

import com.alipay.autotuneservice.base.cache.LocalCache;
import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.configuration.ResourcePermission;
import com.alipay.autotuneservice.configuration.ResourcePermission.ResourceType;
import com.alipay.autotuneservice.controller.model.AppTuneFigureVO;
import com.alipay.autotuneservice.controller.model.HistoryPlanVO;
import com.alipay.autotuneservice.controller.model.HistoryTunePlanEffectVo;
import com.alipay.autotuneservice.controller.model.HistoryTunePlanVo;
import com.alipay.autotuneservice.controller.model.tuneplan.QueryTunePlanVO;
import com.alipay.autotuneservice.controller.model.tuneplan.TunePlanActionEnum;
import com.alipay.autotuneservice.dao.HealthCheckInfo;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckInfoRecord;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.EffectTypeEnum;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.model.tune.TuneActionStatus;
import com.alipay.autotuneservice.model.tune.TuneParam;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.service.TuneEvaluateService;
import com.alipay.autotuneservice.service.TuneInvokeService;
import com.alipay.autotuneservice.service.TunePlanService;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.ObjectUtil;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dutianze
 * @version TunePlanController.java, v 0.1 2022年04月28日 16:39 dutianze
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/tune-plan")
public class TunePlanController {

    @Autowired
    private HealthCheckInfo            healthCheckInfo;
    @Autowired
    private TunePlanRepository         repository;
    @Autowired
    private TuneInvokeService          tuneInvokeService;
    @Autowired
    private TunePlanService            tunePlanService;
    @Autowired
    private TuneEvaluateService        tuneEvaluateService;
    @Autowired
    private LocalCache<Object, Object> localCache;

    private static final String SUBMIT_PLAN_LOCK_KEY = "TMASTER_SUBMIT_PLAN_LOCK_";

    @PostMapping("/submit")
    public ServiceBaseResult<Integer> submitTunePlan(@RequestParam String planName,
                                                     @RequestParam Integer healthCheckId,
                                                     @RequestParam TuneActionStatus actionStatus) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> Preconditions.checkArgument(StringUtils.isNotBlank(planName)))
                .makeResult(() -> {
                    HealthCheckInfoRecord healthCheckInfoRecord = healthCheckInfo.selectById(healthCheckId);
                    if (healthCheckInfoRecord == null) {
                        throw new ServerException(ResultCode.HEALTH_CHECK_NOT_FOUND);
                    }
                    // check duplicate plan
                    List<TunePlan> tunePlans = repository.findTunePlanByAppId(healthCheckInfoRecord.getAppId());
                    List<TunePlan> runningTunePlans = tunePlans.stream()
                            .filter(t -> !t.getTunePlanStatus().isFinal())
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(runningTunePlans)) {
                        throw new ServerException(ResultCode.ALREADY_HAVE_RUNNING_PLAN);
                    }
                    // save plan
                    TunePlan tunePlan = TunePlan.builder()
                            .withHealthCheckId(healthCheckId)
                            .withAccessToken(UserUtil.getAccessToken())
                            .withAppId(healthCheckInfoRecord.getAppId())
                            .withPlanName(planName)
                            .withActionStatus(actionStatus)
                            .withTunePlanStatus(TunePlanStatus.RUNNING)
                            .withTuneParam(new TuneParam())
                            .build();
                    TunePlan saved = repository.save(tunePlan);
                    tuneInvokeService.submitTunePlan(saved.getId());
                    return saved.getId();
                });
    }

    @PostMapping("/submitjvm")
    public ServiceBaseResult<Integer> submitJvmTunePlan(@RequestParam String planName,
                                                        @RequestParam Integer appId,
                                                        @RequestParam String jvm,
                                                        @RequestParam Double ratio) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> Preconditions.checkArgument(StringUtils.isNotBlank(planName)))
                .makeResult(() -> {
                    // check duplicate plan
                    log.info("submitJvmTunePlan planName:{}, appId:{}, jvm:{}, ratio:{}", planName, appId, jvm, ratio);
                    List<TunePlan> tunePlans = repository.findTunePlanByAppId(appId);
                    List<TunePlan> runningTunePlans = tunePlans.stream()
                            .filter(t -> !t.getTunePlanStatus().isFinal())
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(runningTunePlans)) {
                        throw new ServerException(ResultCode.ALREADY_HAVE_RUNNING_PLAN);
                    }
                    // save plan
                    TunePlan tunePlan = TunePlan.builder()
                            .withAccessToken(UserUtil.getAccessToken())
                            .withAppId(appId)
                            .withPlanName(planName)
                            .withTunePlanStatus(TunePlanStatus.RUNNING)
                            .withActionStatus(TuneActionStatus.AUTO)
                            .withTuneParam(new TuneParam())
                            .build();
                    TunePlan saved = repository.save(tunePlan);
                    tuneInvokeService.submitJvm(saved.getId(), jvm, ratio);
                    return saved.getId();
                });
    }

    @PostMapping("/submitConfirmJvmPlan")
    public ServiceBaseResult<Integer> submitConfirmJvmPlan(@RequestParam Integer planId,
                                                           @RequestParam TunePlanStatus tunePlanStatus) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                    // save plan
                    TunePlan tunePlan = repository.findTunePlanById(planId);
                    if (null != tunePlan) {
                        tunePlan.setTuneStatus(tunePlanStatus);
                        tunePlan.setActionStatus(TuneActionStatus.AUTO);
                        repository.save(tunePlan);
                    }
                    return tunePlan.getId();
                });
    }

    /**
     * 历史调参情况
     *
     * @param appId
     * @return
     */
    @GetMapping("/historyPlan")
    @ResourcePermission(path = "appId", type = ResourceType.APP_ID)
    public ServiceBaseResult<List<HistoryTunePlanVo>> historyTunePlan(@RequestParam Integer appId,
                                                                      @RequestParam(value = "start", required = false) Long start,
                                                                      @RequestParam(value = "end", required = false) Long end) {

        //返回：开始时间、结束时间、调节状态、诊断的分数
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> {
                    Preconditions.checkArgument(appId > 0, "appid can not be less than 0");
                })
                .makeResult(() -> {
                    //返回：开始时间、结束时间、调节状态、诊断的分数
                    List<TunePlan> tunePlans = repository.findByAppIdAndTime(appId, start, end);
                    if (CollectionUtils.isEmpty(tunePlans)) {
                        return null;
                    }
                    List<Integer> healthIds = tunePlans.stream().map(TunePlan::getHealthCheckId).collect(Collectors.toList());
                    Map<Integer, Integer> map = new HashMap<>();
                    List<HealthCheckInfoRecord> records = healthCheckInfo.batchGetHealthIdsByHealthIds(healthIds);
                    records.stream().filter(r -> healthIds.contains(r.getId())).forEach(
                            p -> map.put(p.getId(), Integer.parseInt(p.getGrade())));
                    return tunePlans.stream().map(tunePlan -> {
                        HistoryTunePlanVo historyTunePlanVo = new HistoryTunePlanVo();
                        historyTunePlanVo.setBeginTime(DateUtils.asTimestamp(tunePlan.getCreatedTime()));
                        historyTunePlanVo.setEndTime(DateUtils.asTimestamp(tunePlan.getUpdateTime()));
                        historyTunePlanVo.setAppId(appId);
                        historyTunePlanVo.setTunePlanStatus(tunePlan.getTunePlanStatus().name());
                        historyTunePlanVo.setGrade(map.get(tunePlan.getHealthCheckId()));
                        return historyTunePlanVo;
                    }).collect(Collectors.toList());
                });
    }

    /**
     * 历史调参情况
     */
    @GetMapping("/historyTunePlanEffect")
    public ServiceBaseResult<HistoryPlanVO> historyTunePlanEffect(@RequestParam Integer appId,
                                                                  @RequestParam(value = "start", required = false) Long start,
                                                                  @RequestParam(value = "end", required = false) Long end) {
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> {
                    Preconditions.checkArgument(appId > 0, "appid can not be less than 0");
                })
                .makeResult(() -> {
                    //返回：开始时间、类型、调优结果、调优分数
                    HistoryPlanVO result = new HistoryPlanVO();
                    List<TunePlan> records = repository.findByAppIdAndTime(appId, start, end);
                    if (CollectionUtils.isEmpty(records)) {
                        return null;
                    }
                    HistoryTunePlanEffectVo cpuPlanEffectVo = new HistoryTunePlanEffectVo(EffectTypeEnum.CPU.getCode());
                    HistoryTunePlanEffectVo memPlanEffectVo = new HistoryTunePlanEffectVo(EffectTypeEnum.MEM.getCode());
                    HistoryTunePlanEffectVo rtPlanEffectVo = new HistoryTunePlanEffectVo(EffectTypeEnum.RT.getCode());
                    HistoryTunePlanEffectVo fgcCountEffectVo = new HistoryTunePlanEffectVo(EffectTypeEnum.FGC_COUNT.getCode());
                    HistoryTunePlanEffectVo fgcTimeEffectVo = new HistoryTunePlanEffectVo(EffectTypeEnum.FGC_TIME.getCode());
                    records.stream().filter(record -> record.getTuneEffectVO() != null).forEach(
                            pod -> pod.getTuneEffectVO().getTuneResultVOList().forEach(item -> {
                                String effectType = EffectTypeEnum.valueOf(item.getEffectTypeEnum()).getCode();
                                if (StringUtils.equals(effectType, "CPU") && item.getReduce() > 0.0) {
                                    cpuPlanEffectVo.setLastPointDesc(item.getReduce() + cpuPlanEffectVo.getLastPointDesc());
                                }
                                if (StringUtils.equals(effectType, "MEMORY") && item.getReduce() > 0.0) {
                                    memPlanEffectVo.setLastPointDesc(item.getReduce() + memPlanEffectVo.getLastPointDesc());
                                }
                                if (StringUtils.equals(effectType, "RT") && item.getReduce() != null && item.getReduce() > 0.0) {
                                    rtPlanEffectVo.setLastPointDesc(rtPlanEffectVo.getLastPointDesc() == 0 ? item.getReduce()
                                            : Math.max(rtPlanEffectVo.getLastPointDesc(), item.getReduce()));
                                    rtPlanEffectVo.setLastTuneTime(DateUtils.asTimestamp(pod.getCreatedTime()));
                                }
                                if (StringUtils.equals(effectType, "FGC_COUNT") && item.getReduce() != null && item.getReduce() > 0.0) {
                                    fgcCountEffectVo.setLastPointDesc(fgcCountEffectVo.getLastPointDesc() == 0 ? item.getReduce()
                                            : Math.max(fgcCountEffectVo.getLastPointDesc(), item.getReduce()));
                                    fgcCountEffectVo.setLastTuneTime(DateUtils.asTimestamp(pod.getCreatedTime()));
                                }
                                if (StringUtils.equals(effectType, "FGC_TIME") && item.getReduce() != null && item.getReduce() > 0.0) {
                                    fgcTimeEffectVo.setLastPointDesc(fgcTimeEffectVo.getLastPointDesc() == 0 ? item.getReduce()
                                            : Math.max(fgcTimeEffectVo.getLastPointDesc(), item.getReduce()));
                                    fgcTimeEffectVo.setLastTuneTime(DateUtils.asTimestamp(pod.getCreatedTime()));
                                }
                            }));
                    cpuPlanEffectVo.setHistoryCount(optimizationTimes(records, EffectTypeEnum.CPU.name()));
                    memPlanEffectVo.setHistoryCount(optimizationTimes(records, EffectTypeEnum.MEM.getCode()));
                    rtPlanEffectVo.setHistoryCount(optimizationTimes(records, EffectTypeEnum.RT.name()));
                    fgcCountEffectVo.setHistoryCount(optimizationTimes(records, EffectTypeEnum.FGC_COUNT.getCode()));
                    fgcTimeEffectVo.setHistoryCount(optimizationTimes(records, EffectTypeEnum.FGC_TIME.getCode()));
                    result.getResourceList().add(cpuPlanEffectVo);
                    result.getResourceList().add(memPlanEffectVo);
                    result.getPerformanceList().add(rtPlanEffectVo);
                    result.getStabilityList().add(fgcCountEffectVo);
                    result.getStabilityList().add(fgcTimeEffectVo);
                    return result;
                });
    }

    private Long optimizationTimes(List<TunePlan> records, String type) {
        return records.stream().filter(p -> p.getTuneEffectVO() != null).mapToLong(
                item -> item.getTuneEffectVO().getTuneResultVOList().stream()
                        .filter(q -> StringUtils.equals(type, EffectTypeEnum.valueOf(q.getEffectTypeEnum()).getCode())
                                && q.getReduce() != null && q.getReduce() > 0.0).count()).sum();
    }

    private Integer getGrade(String type, List<TunePlan> records, Long count) {
        Long totalCount = records.stream().filter(p -> p.getTuneEffectVO() != null).filter(
                item -> (item.getTuneEffectVO().getTuneResultVOList().stream().filter(
                        item1 -> StringUtils.equals(EffectTypeEnum.valueOf(item1.getEffectTypeEnum()).getCode(), type)).filter(
                        p -> p.getReduce() != null && p.getReduce() != 0.0).count()) >= 1).count();
        return (int) Math.round(tuneEvaluateService.divisionLevel(totalCount, count));
    }

    @GetMapping("/appTuneFigure")
    public ServiceBaseResult<List<AppTuneFigureVO>> appTuneFigure(@RequestParam Integer appId,
                                                                  @RequestParam(value = "start", required = false) Long start,
                                                                  @RequestParam(value = "end", required = false) Long end) {
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> Preconditions.checkArgument(appId > 0, "appid can not be less than 0"))
                .makeResult(() -> {
                    List<TunePlan> records = repository.findByAppIdAndTime(appId, start, end);
                    if (CollectionUtils.isEmpty(records)) {
                        return null;
                    }
                    return Stream.of("CPU", "RT", "MEMORY", "FGC_TIME", "FGC_COUNT")
                            .map(type -> {
                                AppTuneFigureVO appTuneFigureVO = new AppTuneFigureVO();
                                appTuneFigureVO.setTuneType(type);
                                appTuneFigureVO.setGrade((getGrade(type, records, optimizationTimes(records, type))));
                                return appTuneFigureVO;
                            }).collect(Collectors.toList());
                });
    }

    /**
     * 调优计划的操作: 确认执行, 暂停, 终止
     *
     * @param pipelineId
     * @param actionId   10001: 确认执行; 10002: 暂停; 10003: 终止
     * @return
     */
    @PostMapping("/{pipelineId}/action/{actionId}")
    public ServiceBaseResult<Boolean> execTunePlanAction(@PathVariable(value = "pipelineId") Integer pipelineId,
                                                         @PathVariable(value = "actionId") Integer actionId) {
        return ServiceBaseResult.invoker().paramCheck(() -> {
            if (!TunePlanActionEnum.checkValid(actionId)) {
                throw new IllegalArgumentException(String.format("The request actionId=%s is invalid.", actionId));
            }
        }).makeResult(() -> tunePlanService.execAction(pipelineId, TunePlanActionEnum.getByCode(actionId)));
    }

    /**
     * 根据appId查询应用的指定调优计划状态的调优计划列表
     *
     * @param appId  应用Id
     * @param status 调优计划的状态
     * @return 调优计划List
     */
    @GetMapping("{appId}/list")
    public ServiceBaseResult<QueryTunePlanVO> listTunePlan(@PathVariable(value = "appId") Integer appId,
                                                           @RequestParam(value = "status", required = false) TunePlanStatus status,
                                                           @RequestParam(value = "planName", required = false) String planName,
                                                           @RequestParam(value = "startTime", required = false) Long startTime,
                                                           @RequestParam(value = "endTime", required = false) Long endTime) {
        return ServiceBaseResult.invoker().paramCheck(() -> {
                    ObjectUtil.checkIntegerPositive(appId);
                })
                .makeResult(() -> {
                    log.info("listTunePlan enter. appId={}, status={}, planName={}, startTime={}, endTime={}", appId, status, planName,
                            startTime, endTime);
                    return tunePlanService.findTunePlans(appId, status, planName, startTime, endTime);
                });
    }
}