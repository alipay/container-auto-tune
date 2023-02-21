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

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.controller.model.EffectTypeVO;
import com.alipay.autotuneservice.controller.model.TuneEffectVO;
import com.alipay.autotuneservice.controller.model.TuneTestTimePipeVO;
import com.alipay.autotuneservice.controller.model.tuneprediction.AppTunePredictVO;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.TuneLogInfo;
import com.alipay.autotuneservice.dao.TuneParamTrialDataRepository;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.TuningParamTaskData;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTaskDataRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTrialDataRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.alipay.autotuneservice.dynamodb.repository.ContainerStatisticsService;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.common.EffectTypeEnum;
import com.alipay.autotuneservice.model.common.TrailTuneTaskStatus;
import com.alipay.autotuneservice.model.common.TuneStatus;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.PipelineStatus;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TuneTaskStatus;
import com.alipay.autotuneservice.model.tune.trail.TrailTuneContext;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.TuneEffectService;
import com.alipay.autotuneservice.util.DateUtils;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.model.common.TuneStatus.OPTIMIZATION;
import static com.alipay.autotuneservice.model.common.TuneStatus.UNCHANGED;
import static com.alipay.autotuneservice.model.common.TuneStatus.WORSEN;

/**
 * @author huoyuqi
 * @version TuneEffectServiceImpl.java, v 0.1 2022年05月05日 11:38 上午 huoyuqi
 */
@Slf4j
@Service
public class TuneEffectServiceImpl implements TuneEffectService {

    private final static String EFFECT = "effect";

    @Autowired
    private TunePlanRepository tunePlanRepository;

    @Autowired
    private PodInfo podInfo;

    @Autowired
    private ContainerStatisticsService repository;

    @Autowired
    private TuneLogInfo tuneLogInfo;

    @Autowired
    private TuningParamTaskData tuningParamTaskData;

    @Autowired
    private TuneParamTrialDataRepository tuneParamTrialDataRepository;

    @Autowired
    private TunePipelineRepository tunePipelineRepository;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private AppInfoRepository appInfoRepository;

    /**
     * 调优效果展示
     */
    @Override
    public TuneEffectVO tuneEffect(Integer pipelineId) {
        // 根据pipelineId获取tunePlan中的观察时间不为空，则返回相应的结果
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(MachineId.TUNE_PIPELINE, pipelineId);
        Preconditions.checkNotNull(tunePipeline, "pipelineID数据库中不存在");
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePipeline.getTunePlanId());
        Preconditions.checkNotNull(tunePlan, "tunePlanId数据库中查询不到");
        TuneEffectVO effectVO = tunePlan.getTuneEffectVO();
        if (effectVO != null && effectVO.getObserveEndTime() != null) {
            if (System.currentTimeMillis() < effectVO.getObserveEndTime()) {
                return new TuneEffectVO(effectVO.getObserveEndTime() - System.currentTimeMillis());
            }
            return effectVO;
        }
        return new TuneEffectVO();
    }

    @Override
    public TuneEffectVO tuneProcessEffect(Integer pipelineId) {
        return buildBaseVO(pipelineId, "process");
    }

    @Override
    @Async("subExecutor")
    public void triggerTuneEffect(Integer pipelineId) {
        log.info("triggerTuneEffect enter pipelineId={}", pipelineId);
        buildBaseVO(pipelineId, EFFECT);
    }

    @Override
    public void asyncTuneEffect(Integer pipelineId, String type) {
        try {
            // 多延长10分钟观察时间从10分钟往后算
            TunePlan tunePlan = buildTunePLan(pipelineId);
            if (tunePlan.getTuneEffectVO() == null) {
                throw new RuntimeException(String.format("asyncTuneEffect get referResult occurs an error, pipelineID=[%s]", pipelineId));
            }
            long start = System.currentTimeMillis();
            TuneEffectVO tuneEffectVO = tunePlan.getTuneEffectVO();
            tuneEffectVO.setObserveStartTime(start + 1 * 60 * 1000L);
            tuneEffectVO.setObserveEndTime(start + 11 * 60 * 1000L);
            if (StringUtils.equals(type, "effect")) {
                tunePlanRepository.updateEffectById(tunePlan.getId(), JSON.toJSONString(tuneEffectVO), tuneEffectVO.getTotalIncome());
                return;
            }
            tunePlanRepository.updateGrayPredictById(tunePlan.getId(), JSON.toJSONString(tuneEffectVO), tuneEffectVO.getTotalIncome());
        } catch (Exception e) {
            log.error("asyncTuneEffect 观察24小时 occurs an error", e);
        }
    }

    @Override
    public AppTunePredictVO predictTuneEffect(Integer appId, Integer pipelineId) {
        log.info("predictTuneEffect enter. appId={}, pipelineId={}", appId, pipelineId);
        //获取实验分组
        TuneTestTimePipeVO pipeVO = new TuneTestTimePipeVO();
        getTimePipeline(pipelineId, pipeVO.getTimePipeline());
        checkPipelineFinish(pipelineId, pipeVO);
        //组建基础参数
        AppTunePredictVO appTunePredictVO = new AppTunePredictVO();
        appTunePredictVO.setTuneTestTimePipeVO(pipeVO);
        if (!pipeVO.isFinish()) {
            return appTunePredictVO;
        }
        // 获取预期评估
        TuneEffectVO tuneEffectVO = buildPredictTuneEffect(appId, pipelineId);
        appTunePredictVO.setPredictTuneEffect(tuneEffectVO);
        return appTunePredictVO;
    }

    @Override
    public AppTunePredictVO grayEffect(Integer appId, Integer pipelineId) {
        log.info("predictTuneEffect enter. appId={}, pipelineId={}", appId, pipelineId);
        //获取实验分组
        TuneTestTimePipeVO pipeVO = new TuneTestTimePipeVO();
        getTimePipeline(pipelineId, pipeVO.getTimePipeline());
        //组建基础参数
        AppTunePredictVO appTunePredictVO = new AppTunePredictVO();
        appTunePredictVO.setTuneTestTimePipeVO(pipeVO);

        // 获取灰度评估
        TuneEffectVO tuneEffectVO = buildGrayEffect(pipelineId);
        appTunePredictVO.setPredictTuneEffect(tuneEffectVO);
        return appTunePredictVO;
    }

    private TuneEffectVO buildGrayEffect(Integer pipelineId) {
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
        Preconditions.checkNotNull(tunePipeline, "pipelineID数据库中不存在");
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePipeline.getTunePlanId());
        Preconditions.checkNotNull(tunePlan, "tunePlanId数据库中查询不到");
        TuneEffectVO effectVO = tunePlan.getPredictEffectVO();
        if (effectVO != null && effectVO.checkFinishEvaluate()) {
            return effectVO;
        }
        return new TuneEffectVO();
    }

    @Override
    public Boolean asyncSubmitTunePredict(Integer appId, Integer pipelineId) {
        submitPredict(pipelineId);
        return true;
    }

    @Async("subExecutor")
    public void submitPredict(Integer pipelineId) {
        try {
            tuneProcessEffect(pipelineId);
        } catch (Exception e) {
            log.error("submitPredict occurs an error.", e);
        }
    }

    private TuneEffectVO buildBaseVO(Integer pipelineId, String tuneType) {
        //根据pipelinedId 找到planId
        TunePlan tunePlan = buildTunePLan(pipelineId);
        //1.构建开始时间 结束时间
        TuneEffectVO tuneEffectVO = buildBase(pipelineId, tunePlan, tuneType);
        List<PodInfoRecord> podInfoRecords = podInfo.getAllAlivePodsByApp(tunePlan.getAppId());
        List<String> podList = podInfoRecords.parallelStream().map(PodInfoRecord::getPodName).collect(Collectors.toList());
        tuneEffectVO.setPodNum(podList.size());
        //2.单项提升 包含单项类型、参照检查结果、观察结果、提升比例、调优状态、恶化原因
        if (StringUtils.equals("process", tuneType)) {
            List<EffectTypeVO> typeVOS = buildBaseMetric(podList, tuneEffectVO.getCheckStartTime(), tuneEffectVO.getCheckEndTime(),
                    tunePlan.getId(), tuneType);
            tuneEffectVO.setTuneResultVOList(typeVOS);
            tunePlanRepository.updateEffectById(tunePlan.getId(), JSON.toJSONString(tuneEffectVO), tuneEffectVO.getTotalIncome());
            return tuneEffectVO;
        }
        List<EffectTypeVO> typeVOS = buildBaseMetric(podList, tuneEffectVO.getObserveStartTime(), tuneEffectVO.getObserveEndTime(),
                tunePlan.getId(), tuneType);
        tuneEffectVO.setTuneResultVOList(typeVOS);
        //3.调优收益 从数据库中获取相应的pod
        calIncomes(tunePlan, tuneEffectVO, podInfoRecords, typeVOS, pipelineId);
        //4.判断是否是灰度评估
        int updateEffectRes = isGrayPredict(pipelineId, tunePlan) ? tunePlanRepository.updateGrayPredictById(tunePlan.getId(),
                JSON.toJSONString(tuneEffectVO), tuneEffectVO.getTotalIncome()) : tunePlanRepository.updateEffectById(tunePlan.getId(),
                JSON.toJSONString(tuneEffectVO), tuneEffectVO.getTotalIncome());
        appInfoService.updateAppTime(tunePlan.getAppId());
        log.info("buildBaseVO - updateEffectRes={}", updateEffectRes);
        updateAppTime(tunePlan.getAppId());
        return tuneEffectVO;
    }

    private void updateAppTime(Integer appId) {
        AppInfo record = appInfoRepository.findById(appId);
        if (null == record || null == record.getAppTag()) {
            throw new RuntimeException("查询appInfo表中数据为空");
        }
        AppTag tag = record.getAppTag() != null ? record.getAppTag() : null;
        if (tag != null) {
            tag.setLastModifyTime(System.currentTimeMillis());
            record.setAppTag(tag);
            appInfoRepository.save(record.getId(), tag);
        }
    }

    private void calIncomes(TunePlan tunePlan, TuneEffectVO tuneEffectVO, List<PodInfoRecord> podInfoRecords, List<EffectTypeVO> typeVOS,
                            Integer pipelineId) {
        //获取cpuLimit memLimit
        Optional<PodInfoRecord> cpuCoreOptional = podInfoRecords.stream().filter(item -> item != null && item.getCpuCoreLimit() != null)
                .findAny();
        Integer cpuLimit = cpuCoreOptional.isPresent() ? cpuCoreOptional.get().getCpuCoreLimit() : 8;
        Optional<PodInfoRecord> memOptional = podInfoRecords.stream().filter(item -> item != null && item.getMemLimit() != null).findAny();
        Integer memLimit = memOptional.isPresent() ? memOptional.get().getMemLimit() : 2048;
        //pod 单价
        double podPrice = cpuLimit * 0.001 + memLimit * 0.01 / 1024;
        typeVOS.forEach(type -> {
            if (StringUtils.equals(type.getEffectTypeEnum(), EffectTypeEnum.CPU.name())) {
                tuneEffectVO.setTuneReduceCpu(type.getReduce());
            }
            if (StringUtils.equals(type.getEffectTypeEnum(), EffectTypeEnum.MEM.name())) {
                tuneEffectVO.setTuneReduceMem(type.getReduce());
            }
        });
        //pod数量
        double podNum = tuneEffectVO.getTuneReduceCpu() / cpuLimit * 0.1 + tuneEffectVO.getTuneReduceMem() / memLimit * 0.9;
        //收益 pod数量*podNum
        double totalIncome = podNum * podPrice;
        tuneEffectVO.setTotalIncome(totalIncome > 0.0 && totalIncome < 0.1 ? 0.1 : totalIncome);
        tuneEffectVO.setTuneReducePod(podNum);
        //4.总的分数 根据每项优化和恶化的比例算分
        TuneEffectVO vo = buildGrade(typeVOS, tunePlan.getHealthCheckId(), pipelineId, tunePlan.getAppId());
        tuneEffectVO.setScore(vo.getScore());
        tuneEffectVO.setPromoteRate(vo.getPromoteRate());
    }

    private TunePlan buildTunePLan(Integer pipelineId) {
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
        if (tunePipeline == null || tunePipeline.getTunePlanId() == null) {
            throw new RuntimeException(String.format("pipelineID数据库中不存在, 检测pipelineID=[%s]", pipelineId));
        }
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePipeline.getTunePlanId());
        if (tunePlan == null) {
            throw new RuntimeException(String.format("pipelineID数据库中不存在, 检测appId=[%s]", pipelineId));
        }
        return tunePlan;
    }

    private TuneEffectVO buildBase(Integer pipelineId, TunePlan tunePlan, String tuneType) {
        TuneEffectVO tuneEffectVO = new TuneEffectVO();
        tuneEffectVO.setId(pipelineId);
        //todo 测试一小时
        //tuneEffectVO.setCheckStartTime(DateUtils.asTimestamp(tunePlan.getCreatedTime().plusDays(-1)));
        tuneEffectVO.setCheckStartTime(DateUtils.asTimestamp(tunePlan.getCreatedTime().plusHours(-1)));
        tuneEffectVO.setCheckEndTime(DateUtils.asTimestamp(tunePlan.getCreatedTime()));
        if (StringUtils.equals(tuneType, "effect")) {
            Long startTime = isGray(pipelineId) ? tunePlan.getPredictEffectVO().getObserveStartTime()
                    : tunePlan.getTuneEffectVO().getObserveStartTime();
            Long endTime = isGray(pipelineId) ? tunePlan.getPredictEffectVO().getObserveEndTime()
                    : tunePlan.getTuneEffectVO().getObserveEndTime();
            tuneEffectVO.setObserveStartTime(startTime);
            tuneEffectVO.setObserveEndTime(endTime);
        }
        return tuneEffectVO;
    }

    private List<EffectTypeVO> buildBaseMetric(List<String> podList, Long start, Long end, Integer planId, String tuneType) {
        AtomicReference<Double> fgcCount = new AtomicReference<>(0.0);
        AtomicReference<Double> heapSum = new AtomicReference<>(0.0);
        AtomicReference<Double> fgcTime = new AtomicReference<>(0.0);
        AtomicReference<Double> ygcTime = new AtomicReference<>(0.0);
        AtomicReference<Double> ygcCount = new AtomicReference<>(0.0);
        double cpu = podList.parallelStream().mapToDouble(pod -> repository.queryContainerStatsByPodName(pod, start, end).stream().
                mapToDouble(ContainerStatistics::getUsedCpuCores).max().orElse(0.0)).sum();
        return StringUtils.equals("process", tuneType) ? buildProcessMetric(heapSum.get(), fgcCount.get(), fgcTime.get(),
                ygcCount.get(), ygcTime.get(), cpu) : buildEffectMetric(planId, heapSum.get(), fgcCount.get(), fgcTime.get(),
                ygcCount.get(), ygcTime.get(), cpu);
    }

    /**
     * 构建参照时单项内容 包含单项的参照结果
     */
    private List<EffectTypeVO> buildProcessMetric(Double mem, Double fgcCount, Double fgcTime, Double ygcCount, Double ygcTime,
                                                  Double cpu) {
        List<EffectTypeVO> effectTypeVOS = new ArrayList<>();
        effectTypeVOS.add(new EffectTypeVO(EffectTypeEnum.MEM.name(), mem, null, 0.0, TuneStatus.OPTIMIZATION, "", 0.0));
        effectTypeVOS.add(new EffectTypeVO(EffectTypeEnum.FGC_COUNT.name(), fgcCount, null, 0.0, TuneStatus.OPTIMIZATION, "", 0.0));
        effectTypeVOS.add(new EffectTypeVO(EffectTypeEnum.YGC_COUNT.name(), ygcCount, null, 0.0, TuneStatus.OPTIMIZATION, "", 0.0));
        effectTypeVOS.add(new EffectTypeVO(EffectTypeEnum.CPU.name(), cpu, null, 0.0, TuneStatus.OPTIMIZATION, "", 0.0));
        effectTypeVOS.add(new EffectTypeVO(EffectTypeEnum.FGC_TIME.name(), fgcTime, null, 0.0, TuneStatus.OPTIMIZATION, "", 0.0));
        effectTypeVOS.add(new EffectTypeVO(EffectTypeEnum.YGC_TIME.name(), ygcTime, null, 0.0, TuneStatus.OPTIMIZATION, "", 0.0));
        effectTypeVOS.add(new EffectTypeVO(EffectTypeEnum.RT.name(), 0.00, 0.00, 0.0, TuneStatus.UNCHANGED, "", 0.0));
        return effectTypeVOS;
    }

    /**
     * 构建预期调优效果单项内容 包含单项类型、检查结果、预期提升比例、调优状态
     */
    private List<EffectTypeVO> buildEffectMetric(Integer planId, Double mem, Double fgcCount, Double fgcTime, Double ygcCount,
                                                 Double ygcTime, Double cpu) {
        //计算调优效果情况
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(planId);
        if (tunePlan == null) {
            throw new RuntimeException(String.format("planId数据库中不存在, 检测planId=[%s]", planId));
        }
        TuneEffectVO tuneEffectVO = tunePlan.getTuneEffectVO();
        if (tuneEffectVO == null) {
            throw new RuntimeException(String.format("planId数据库中tuneEffectVO为空, 检测planId=[%s]", planId));
        }
        List<EffectTypeVO> vos = new ArrayList<>();
        tuneEffectVO.getTuneResultVOList().forEach(item -> {
            if (StringUtils.equals(item.getEffectTypeEnum(), EffectTypeEnum.MEM.name())) {
                vos.add(constructEffectMetric(item.getReferResult(), EffectTypeEnum.MEM, mem));
            }
            if (StringUtils.equals(item.getEffectTypeEnum(), EffectTypeEnum.FGC_COUNT.name())) {
                vos.add(constructEffectMetric(item.getReferResult(), EffectTypeEnum.FGC_COUNT, fgcCount));
            }
            if (StringUtils.equals(item.getEffectTypeEnum(), EffectTypeEnum.YGC_COUNT.name())) {
                vos.add(constructEffectMetric(item.getReferResult(), EffectTypeEnum.YGC_COUNT, ygcCount));
            }
            if (StringUtils.equals(item.getEffectTypeEnum(), EffectTypeEnum.CPU.name())) {
                vos.add(constructEffectMetric(item.getReferResult(), EffectTypeEnum.CPU, cpu));
            }
            if (StringUtils.equals(item.getEffectTypeEnum(), EffectTypeEnum.FGC_TIME.name())) {
                vos.add(constructEffectMetric(item.getReferResult(), EffectTypeEnum.FGC_TIME, fgcTime));
            }
            if (StringUtils.equals(item.getEffectTypeEnum(), EffectTypeEnum.YGC_TIME.name())) {
                vos.add(constructEffectMetric(item.getReferResult(), EffectTypeEnum.YGC_TIME, ygcTime));
            }
        });
        vos.add(new EffectTypeVO(EffectTypeEnum.RT.name(), 0.00, 0.00, 0.0, TuneStatus.UNCHANGED, "", 0.0));
        return vos;
    }

    private EffectTypeVO constructEffectMetric(Double refer, EffectTypeEnum type, double observe) {
        double reduce = refer - observe;
        return new EffectTypeVO(type.name(), refer, observe, reduce / Math.max(refer, 1),
                judgeTuneStatus(refer, observe, type), "", reduce);
    }

    /**
     * 构建评估效果分数  优化个数 * 1/6 * (100-健康检查分数）+健康检查分数
     */
    private TuneEffectVO buildGrade(List<EffectTypeVO> typeVOS, Integer healthId, Integer pipelineId, Integer appId) {
        return new TuneEffectVO();
    }

    private void getTimePipeline(Integer pipelineId, List<TuneTestTimePipeVO.TimeDetail> timePipeline) {
        List<TuneLogInfoRecord> records = tuneLogInfo.findRecordByPipeline(pipelineId, "DELETE");
        Set<TuneLogInfoRecord> recordSet = new TreeSet<>(Comparator.comparing(TuneLogInfoRecord::getChangePodName));
        recordSet.addAll(records);
        recordSet.stream()
                .filter(record -> StringUtils.contains(record.getActionDesc(), "EXPERIMENT"))
                .map(record -> {
                    TuneTestTimePipeVO.TimeDetail timeDetail = new TuneTestTimePipeVO.TimeDetail();
                    timeDetail.setStartTime(DateUtils.asTimestamp(record.getCreatedTime()));
                    if (record.getChangetTime() != null) {
                        timeDetail.setEndTime(DateUtils.asTimestamp(record.getChangetTime()));
                    }
                    timeDetail.setPodName(record.getChangePodName());
                    return timeDetail;
                }).forEach(timePipeline::add);
    }

    private void checkPipelineFinish(Integer pipelineId, TuneTestTimePipeVO pipeVO) {
        TuningParamTaskDataRecord dataRecord = tuningParamTaskData.getData(pipelineId);
        if (Objects.isNull(dataRecord)) {
            log.warn("checkPipelineFinish can not get TuningParamTaskDataRecord for pipelineId={}", pipelineId);
            return;
        }
        if (dataRecord.getStartTime() != null) {
            pipeVO.setStartTime(DateUtils.asTimestamp(dataRecord.getStartTime()));
        }
        boolean isFinish = TuneTaskStatus.valueOf(dataRecord.getTaskStatus()).isFinal();
        pipeVO.setFinish(isFinish);
        if (isFinish) {
            LocalDateTime localDateTime = dataRecord.getEndTime() == null ? dataRecord.getModifyTime() : dataRecord.getEndTime();
            pipeVO.setFinishTime(DateUtils.asTimestamp(localDateTime));
        }
    }

    private TuneEffectVO buildPredictTuneEffect(Integer appId, Integer pipelineId) {
        log.info("buildPredictTuneEffect enter. appId={}, pipelineId={}", appId, pipelineId);
        //根据pipelinedId 找到planId
        TunePlan tunePlan = buildTunePLan(pipelineId);
        if (Objects.nonNull(tunePlan.getPredictEffectVO())) {
            return tunePlan.getPredictEffectVO();
        }
        //1.构建开始时间 结束时间
        List<PodInfoRecord> podInfoRecords = podInfo.getAllAlivePodsByApp(appId);
        if (CollectionUtils.isEmpty(podInfoRecords)) {
            log.info("buildPredictTuneEffect - can not get ALIVE pod for appId={}, pipelineId={}", appId, pipelineId);
            return null;
        }
        TrailTuneContext trailTuneContext = getTrailTuneContext(pipelineId);
        if (Objects.isNull(trailTuneContext)) {
            log.error("buildPredictTuneEffect- can not get trailTuneContext for appId={}, pipelineId={}", appId, pipelineId);
            return null;
        }
        TuneEffectVO tuneEffectVO = new TuneEffectVO();
        tuneEffectVO.setId(pipelineId);
        tuneEffectVO.setCheckStartTime(trailTuneContext.getStartTime());
        tuneEffectVO.setObserveEndTime(trailTuneContext.getEndTime());
        tuneEffectVO.setCheckEndTime(trailTuneContext.getEndTime());
        List<String> podList = podInfoRecords.parallelStream().map(PodInfoRecord::getPodName).collect(Collectors.toList());
        int podNums = podList.size();
        tuneEffectVO.setPodNum(podNums);
        tuneEffectVO.setCurrentPodNum(podNums);
        //3.调优收益 从数据库中获取相应的pod
        // 将预期结果保存
        tunePlanRepository.updatePredictEffect(tunePlan.getId(), JSON.toJSONString(tuneEffectVO));
        return tuneEffectVO;
    }

    private TrailTuneContext getTrailTuneContext(Integer pipelineId) {
        TuningParamTrialDataRecord trialData = tuneParamTrialDataRepository.getTrialData(pipelineId, TrailTuneTaskStatus.FINISH);
        if (trialData == null) {
            // 实验阶段评估未完成
            log.info("getTrailTuneContext - pipelineId={}, 未查询到实验调参记录.", pipelineId);
            return null;
        }
        TrailTuneContext result = TrailTuneContext.convert(trialData);
        log.info("getTrailTuneContext - pipelineId={}, result={}", pipelineId, JSON.toJSONString(result));
        return result;
    }

    private EffectTypeVO buildPredictEffectTypeV0(EffectTypeEnum effectTypeEnum, double refer, double trail) {
        double reduce = refer - trail;
        if ((EffectTypeEnum.MEM == effectTypeEnum || EffectTypeEnum.CPU == effectTypeEnum) && Double.compare(trail, 0) == 0) {
            trail = refer;
            reduce = 0;
        }
        return new EffectTypeVO(effectTypeEnum.name(), refer, trail,
                calRate(refer, trail),
                judgeTuneStatus(refer, trail, effectTypeEnum), "",
                reduce);
    }

    private TuneStatus judgeTuneStatus(Double refer, Double trail, EffectTypeEnum effectTypeEnum) {
        switch (effectTypeEnum) {
            case FGC_COUNT:
            case FGC_TIME:
            case YGC_COUNT:
            case YGC_TIME:
            case CPU:
            case MEM:
                if (trail.compareTo(refer) == 0) {
                    return UNCHANGED;
                }
                return trail < refer ? OPTIMIZATION : WORSEN;
            default:
                return UNCHANGED;
        }
    }

    private Double calRate(double refer, double trail) {
        double res = Math.abs(refer - trail) / refer;
        if (Double.isInfinite(res)) {
            return trail;
        }
        return Double.isNaN(res) ? 0.0 : res;
    }

    private Boolean isGray(Integer pipelineId) {
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
        return tunePipeline.getPipelineStatus() == PipelineStatus.GRAY;
    }

    private Boolean isGrayPredict(Integer pipelineId, TunePlan tunePlan) {
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
        // 满足两个灰度情况下并且在预期评估阶段
        return tunePipeline.getPipelineStatus() == PipelineStatus.GRAY && tunePlan.getTuneStatus() == null;
    }
}



