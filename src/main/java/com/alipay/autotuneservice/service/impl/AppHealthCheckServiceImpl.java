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

import com.alipay.autotuneservice.controller.model.HealthCheckVO;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.HealthCheckResultRepository;
import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckResultRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.common.HealthCheckStatus;
import com.alipay.autotuneservice.service.AppHealthCheckService;
import com.alipay.autotuneservice.service.algorithmlab.CacluModeEnum;
import com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.DiagnosisLab;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.BaseDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.SingleReport;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.GsonUtil;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : AppHealthCheckService.java, v 0.1 2022年04月26日 10:50 chenqu Exp $
 */
@Slf4j
@Service
public class AppHealthCheckServiceImpl implements AppHealthCheckService {
    public static final int                         HEALTH_CHECK_COUNT = 10;
    private final       AppInfoRepository           appInfoRepository;
    private final       JvmMonitorMetricRepository  jvmMonitorMetricDataRepository;
    private final       HealthCheckResultRepository healthCheckResultRepository;
    private final       PodInfo                     podInfo;

    public AppHealthCheckServiceImpl(AppInfoRepository appInfoRepository, JvmMonitorMetricRepository jvmMonitorMetricDataRepository,
                                     HealthCheckResultRepository healthCheckResultRepository, PodInfo podInfo) {
        this.appInfoRepository = appInfoRepository;
        this.jvmMonitorMetricDataRepository = jvmMonitorMetricDataRepository;
        this.healthCheckResultRepository = healthCheckResultRepository;
        this.podInfo = podInfo;
    }

    /**
     * 提交之后得到相应的结果
     *
     * @param appId
     * @return
     */
    @Override
    public Integer submitHealthCheck(Integer appId) {
        // 前置检测
        List<HealthCheckResultRecord> recordList = healthCheckResultRepository.findByAppIdAndStatus(appId,
                HealthCheckStatus.RUNNING.name());
        if (CollectionUtils.isNotEmpty(recordList)) {
            throw new RuntimeException(String.format("存在检测中的任务,任务ID=[%s]", recordList.get(0).getId()));
        }

        AppInfoRecord appInfoRecord = appInfoRepository.getById(appId);
        if (appInfoRecord == null) {
            throw new RuntimeException(String.format("未找到指定应用=[%s]", appId));
        }
        long tuneTime = 0;
        if (StringUtils.isNotEmpty(appInfoRecord.getAppTag())) {
            AppTag appTag = GsonUtil.fromJson(appInfoRecord.getAppTag(), AppTag.class);
            if (appTag == null || appTag.getCollector() == null
                    || (appTag.getCollector() != GarbageCollector.CMS_GARBAGE_COLLECTOR
                    && appTag.getCollector() != GarbageCollector.G1_GARBAGE_COLLECTOR)) {
                throw new RuntimeException(String.format("unsupported GarbageCollector，appId=[%s]", appId));
            }
            if (appTag.getLastModifyTime() != null) {
                tuneTime = appTag.getLastModifyTime();
                if (System.currentTimeMillis() - tuneTime < 60 * 60) {
                    throw new RuntimeException(
                            String.format("The latest parameter adjustment is within 1h, please re evaluate later，appId=[%s]", appId));
                }
            }
        }
        List<PodInfoRecord> pods = podInfo.getPodInstallTuneAgentNumsByAppId(appId);
        if (CollectionUtils.isEmpty(pods)) {
            throw new RuntimeException(String.format("此应用没有安装agent，appId=[%s]", appId));
        }

        //提交任务
        int id = healthCheckResultRepository.insert(appInfoRecord.getId(), UserUtil.getAccessToken(), UserUtil.getUserName(),
                DateUtils.now(), DateUtils.now(), CacluModeEnum.ONLINE.getMode(), HealthCheckStatus.RUNNING.name(), null, null, null);
        try {
            List<JvmMonitorMetricData> metricData = getMetricDataFromDb(pods, tuneTime);
            if (CollectionUtils.isEmpty(metricData)) {
                throw new RuntimeException(String.format("No monitor metric data，please check report,appId=[%s]", appId));
            }
            DiagnosisReport report = DiagnosisLab.timingDataDiagnosis(metricData, appInfoRecord.getAppDefaultJvm());
            if (report == null || CollectionUtils.isEmpty(report.getReports())) {
                throw new RuntimeException(String.format("Diagnose error，no check report,appId=[%s]", appId));
            }
            report.getBaseInfo().setAppId(appId);
            report.getBaseInfo().setAppName(appInfoRecord.getAppName());
            report.getBaseInfo().setCreateTime(System.currentTimeMillis());
            report.setChecked(true);
            BaseDiagnosis.packModReport(report);
            List<SingleReport> problem = report.getReports().stream().filter(r -> !r.isNormal()).collect(Collectors.toList());
            healthCheckResultRepository.update(id, DateUtils.now(), HealthCheckStatus.ENDING.name(), GsonUtil.toJson(problem),
                    GsonUtil.toJson(report), GsonUtil.toJson(report));
        } catch (Exception e) {
            healthCheckResultRepository.update(id, DateUtils.now(), HealthCheckStatus.INTERRUPT.name(), null,
                    null, null);
            log.error(ExceptionUtils.getStackTrace(e));
            throw e;
        }

        return id;
    }

    private List<JvmMonitorMetricData> getMetricDataFromDb(List<PodInfoRecord> pods, long tuneTime) {
        // 默认读取3天的数据，若三天内有发生调优，则读取调优后的数据
        int checkDay = 3;
        long baseTime = org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -(checkDay - 1)).getTime();
        long start = Math.max(tuneTime, baseTime);
        if (CollectionUtils.isNotEmpty(pods)) {
            return pods.parallelStream().filter(t -> StringUtils.isNotEmpty(t.getPodName()))
                    .map(s -> this.jvmMonitorMetricDataRepository.queryByPodName(s.getPodName(), start, System.currentTimeMillis()))
                    .flatMap(Collection::stream).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Integer getHealthScore(Integer appId) {
        HealthCheckResultRecord record = healthCheckResultRepository.findFirstByAppId(appId);
        if (record == null || StringUtils.isEmpty(record.getReport())) {
            return null;
        }
        return (int) GsonUtil.fromJson(record.getReport(), DiagnosisReport.class).getScore();
    }

    @Override
    public Integer getHealthScoreByCheckId(Integer checkId) {
        HealthCheckResultRecord record = healthCheckResultRepository.selectById(checkId);
        if (record == null || StringUtils.isEmpty(record.getReport())) {
            return null;
        }
        return (int) GsonUtil.fromJson(record.getReport(), DiagnosisReport.class).getScore();
    }

    @Override
    public Map<Integer, Integer> getHealthScoreByCheckIds(List<Integer> checkIds) {
        if (CollectionUtils.isEmpty(checkIds)) {
            return new HashMap<>();
        }
        List<HealthCheckResultRecord> records = healthCheckResultRepository.selectByIds(checkIds);
        if (CollectionUtils.isEmpty(records)) {
            return new HashMap<>();
        }
        return records.stream().filter(r -> r != null && StringUtils.isEmpty(r.getReport()))
                .collect(Collectors
                        .toMap(HealthCheckResultRecord::getId, v -> (int) GsonUtil.fromJson(v.getReport(), DiagnosisReport.class)
                                .getScore()));
    }

    @Override
    public HealthCheckVO refreshCheck(Integer healthCheckId, int count) {
        HealthCheckResultRecord record = healthCheckResultRepository.selectById(healthCheckId);
        DiagnosisReport report = GsonUtil.fromJson(record.getReport(), DiagnosisReport.class);

        //将结果异常优先
        report.getDefModReports().values().forEach(r -> r.sort(this::func));
        report.getGroupModReports().values().forEach(r -> r.sort(this::func));

        List<String> serviceEnum = Arrays.stream(DiagnosisEnum.values()).map(DiagnosisEnum::name).collect(Collectors.toList());
        count = Math.min(count, HEALTH_CHECK_COUNT);
        List<String> modelList = serviceEnum.subList(0, count);

        HealthCheckVO healthCheckVO = new HealthCheckVO();
        healthCheckVO.setId(healthCheckId);
        healthCheckVO.setAppId(record.getAppId());
        healthCheckVO.setCheckedNum(modelList.size());
        healthCheckVO.setCheckStartTime(DateUtils.asTimestamp(record.getCreatedTime()));
        healthCheckVO.setCheckEndTime(DateUtils.asTimestamp(record.getUpdateTime()) + 5 * 1000);
        healthCheckVO.setCheckTime(healthCheckVO.getCheckEndTime() - healthCheckVO.getCheckStartTime());

        healthCheckVO.setStatus(modelList.size() == HEALTH_CHECK_COUNT ? HealthCheckStatus.ENDING : HealthCheckStatus.RUNNING);

        // 装载检测数据
        healthCheckVO.setTitles(modelList);
        LinkedHashMap<String, List<SingleReport>> content = new LinkedHashMap<>();
        modelList.forEach(model -> {
            content.put(model, report.getDefModReports().get(model));
        });
        healthCheckVO.setContents(content);
        healthCheckVO.setGroupContents(report.getGroupModReports());

        //检测完成，更新状态
        if (modelList.size() == HEALTH_CHECK_COUNT) {
            healthCheckVO.setScore((int) report.getScore());
            healthCheckVO.setCheckNum((int) (report.getReports().stream().filter(r -> !r.isNormal()).count()));
            healthCheckVO.setReport(GsonUtil.fromJson(record.getReportDetail(), DiagnosisReport.class));
        }
        return healthCheckVO;
    }

    private int func(SingleReport a, SingleReport b) {
        if (a == null || b == null) {
            return 0;
        }
        if (a.isNormal() && b.isNormal()) {
            return 0;
        } else if (a.isNormal() && !b.isNormal()) {
            return 1;
        } else if (!a.isNormal() && b.isNormal()) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public HealthCheckVO getLastData(Integer appId) {
        try {
            AppInfoRecord appRecord = appInfoRepository.findByIdAndToken(UserUtil.getAccessToken(), appId);
            if (appRecord == null) {
                return null;
            }
            HealthCheckResultRecord record = healthCheckResultRepository.findFirstByAppId(appId);
            if (record == null) {
                record = healthCheckResultRepository.findFirst();
                if (record != null) {
                    HealthCheckVO healthCheckVO = refreshCheck(record.getId(), HEALTH_CHECK_COUNT);
                    healthCheckVO.setScore(0);
                    healthCheckVO.setCheckStartTime(null);
                    healthCheckVO.setCheckTime(null);
                    healthCheckVO.setCheckEndTime(null);
                    DiagnosisReport report = healthCheckVO.getReport();
                    report.setScore(0);
                    report.setChecked(false);
                    report.setProblemCount(0);
                    return healthCheckVO;
                } else {
                    return null;
                }
            }
            return refreshCheck(record.getId(), HEALTH_CHECK_COUNT);
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e));
            return null;
        }
    }
}