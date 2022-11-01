/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.schedule.riskstatistic;

import com.alipay.autotuneservice.configuration.EnvHandler;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.HealthCheckInfo;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.HealthCheckData;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.PodFeaturePreData;
import com.alipay.autotuneservice.dynamodb.bean.RiskStatisticPreData;
import com.alipay.autotuneservice.dynamodb.repository.HealthCheckDataRepository;
import com.alipay.autotuneservice.dynamodb.repository.JvmMonitorMetricDataService;
import com.alipay.autotuneservice.dynamodb.repository.RiskStatisticPreDataRepository;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.common.HealthCheckStatus;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.util.GsonUtil;
import com.alipay.autotuneservice.util.TraceIdGenerator;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.alipay.autotuneservice.schedule.riskstatistic.PodFeaturePreDataPack.*;

/**
 * @author fangxueyang
 * @version ProxyController.java, v 0.1 2022年08月12日 12:02 hongshu
 */
@Slf4j
@Component
public class RiskStatisticPreTask {

    private static final String LOCK_LEY = "tmaster_RiskStatisticPreTask";

    private final RedisClient redisClient;
    private final EnvHandler  envHandler;
    private final RiskStatisticPreDataRepository riskCheckPreDataRepository;
    private final JvmMonitorMetricDataService    jvmMonitorMetricDataRepository;
    private final AppInfoRepository              appInfoRepository;
    private final PodInfo podInfo;
    private final HealthCheckDataRepository healthCheckDataRepository;
    private final HealthCheckInfo healthCheckInfo;


    public RiskStatisticPreTask(RedisClient redisClient, EnvHandler envHandler, RiskStatisticPreDataRepository riskCheckPreDataRepository,
                                JvmMonitorMetricDataService jvmMonitorMetricDataRepository, AppInfoRepository appInfoRepository,
                                PodInfo podInfo, HealthCheckDataRepository healthCheckDataRepository, HealthCheckInfo healthCheckInfo) {
        this.redisClient = redisClient;
        this.envHandler = envHandler;
        this.riskCheckPreDataRepository = riskCheckPreDataRepository;
        this.jvmMonitorMetricDataRepository = jvmMonitorMetricDataRepository;
        this.appInfoRepository = appInfoRepository;
        this.podInfo = podInfo;
        this.healthCheckDataRepository = healthCheckDataRepository;
        this.healthCheckInfo = healthCheckInfo;
    }

    /**
     * every day 02:00
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void run() {
        if (envHandler.isDev()) {
            return;
        }
        try {
            TraceIdGenerator.generateAndSet();
            redisClient.doExec(LOCK_LEY, () -> {
                log.info("RiskStatisticPreTask scheduled start");
                try {
                    // query app info
                    List<AppInfoRecord> appInfoRecords = this.appInfoRepository.getReportedApp();
                    Optional.ofNullable(appInfoRecords).ifPresent(r -> {
                        r.stream().filter(s -> {
                            List<HealthCheckInfoRecord> recordList = healthCheckInfo.findByAppId(s.getId());
                            //判断是否有正在进行中的诊断,存在则不进行计算
                            Optional<HealthCheckInfoRecord> optional = recordList.stream()
                                    .filter(record -> HealthCheckStatus.RUNNING.name().equals(record.getStatus()))
                                    .findAny();
                            if(optional.isPresent()){
                                return false;
                            }

                            if(StringUtils.isNotEmpty(s.getAppTag())){
                                AppTag appTag = GsonUtil.fromJson(s.getAppTag(), AppTag.class);
                                if(appTag!=null){
                                    return appTag.getCollector()!=null &&
                                            (appTag.getCollector()==GarbageCollector.CMS_GARBAGE_COLLECTOR
                                                    || appTag.getCollector()==GarbageCollector.G1_GARBAGE_COLLECTOR);
                                }
                            }

                            return false;
                        }).forEach(t -> {
                            this.caluPreData(t);
                            this.saveDailyStatData(t);
                        });
                    });
                } catch (Exception e) {
                    log.error("RiskStatisticPreTask error", e);
                }
            });
        } finally {
            TraceIdGenerator.clear();
        }
    }

    /**
     * for each tenant
     */
    public void caluPreData(AppInfoRecord appInfoRecord) {
        int appId = appInfoRecord.getId();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            long todayZero = dayFormat.parse(dayFormat.format(new Date())).getTime();
            long yesZero = dayFormat.parse(dayFormat.format(DateUtils.addDays(new Date(), -1))).getTime();
            String dt = dayFormat.format(DateUtils.addDays(new Date(), -1));
            long tuneTime = 0;
            if(StringUtils.isNotEmpty(appInfoRecord.getAppTag())){
                AppTag appTag = GsonUtil.fromJson(appInfoRecord.getAppTag(), AppTag.class);
                if(appTag!=null && appTag.getLastModifyTime()!=null){
                    tuneTime = appTag.getLastModifyTime();
                }
            }

            // query pod info
            List<PodInfoRecord> podInfoRecords =this.podInfo.getPodInstallTuneAgentNumsByAppId(appId);
            // query monitor data base pod and dt
            if(CollectionUtils.isNotEmpty(podInfoRecords)){
                long finalTuneTime = tuneTime;
                podInfoRecords.parallelStream().filter(t -> StringUtils.isNotEmpty(t.getPodName()))
                        .forEach(s -> {
                            // 若采集当天发生调优，使用调优后的数据计算
                            List<JvmMonitorMetricData>  jd = ((yesZero<=finalTuneTime)&&(finalTuneTime<=todayZero)) ?
                                    this.jvmMonitorMetricDataRepository.queryByPodName(s.getPodName(),finalTuneTime,todayZero)
                                    : this.jvmMonitorMetricDataRepository.queryByPodNameAndDt(s.getPodName(),Long.parseLong(dt));

                            insertPreData(jd,appId,dt,s,RiskStatisticCaType.Offline.getType());
                        });
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void caluPreDataOnline(AppInfoRecord appInfoRecord, long tuneTime) {
        int appId = appInfoRecord.getId();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            String dt = dayFormat.format(new Date());
            long todayZero = dayFormat.parse(dt).getTime();
            // query pod info
            List<PodInfoRecord> podInfoRecords =this.podInfo.getPodInstallTuneAgentNumsByAppId(appId);
            if(CollectionUtils.isNotEmpty(podInfoRecords)){
                podInfoRecords.parallelStream().filter(t -> StringUtils.isNotEmpty(t.getPodName()))
                        .forEach(s -> {
                            // 若采集当天发生调优，使用调优后的数据计算
                            List<JvmMonitorMetricData>  jd = (todayZero<=tuneTime) ?
                                    this.jvmMonitorMetricDataRepository.queryByPodName(s.getPodName(),tuneTime,System.currentTimeMillis())
                                    : this.jvmMonitorMetricDataRepository.queryByPodNameAndDt(s.getPodName(),Long.parseLong(dt));
                            if(CollectionUtils.isNotEmpty(jd)){
                                insertPreData(jd,appId,dt,s, RiskStatisticCaType.Online.getType());
                            }
                        });
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertPreData(List<JvmMonitorMetricData> jd, int appId, String dt, PodInfoRecord s, int type) {
        Optional.ofNullable(jd).ifPresent(t -> this.riskCheckPreDataRepository.insert(RiskStatisticPreData.newInstance()
                .bdAppId(appId).bdDt(dt).bdPodName(s.getPodName()).bdTs(System.currentTimeMillis())
                .bdType(type)
                .bdPodFeatureDict(PodFeaturePreData.newInstance()
                        .bdFgcCount(calcFgcCount(appId,jd)).bdFgcSum(calcFgcSum(appId,jd))
                        .bdFgcTime(calcFgcTime(appId,jd)).bdFgcCountP99(calcFgcCountP99(appId,jd))
                        .bdMetaUtilMean(calcMetaUtilMean(appId,jd)).bdIncreaseRate(calcIncreaseRate(appId,jd)))));
    }

    // HealthCheckEnum
    public void saveDailyStatData(AppInfoRecord appInfoRecord){
        String dt = new SimpleDateFormat("yyyyMMdd").format(DateUtils.addDays(new Date(), -1));
        List<RiskStatisticPreData> rspd = this.riskCheckPreDataRepository.getPreDataDayAndApp(dt,appInfoRecord.getId());
        GarbageCollector gCollector = GsonUtil.fromJson(appInfoRecord.getAppTag(), AppTag.class).getCollector();
        this.healthCheckDataRepository.insert(HealthCheckData.newInstance()
                .bdApp(appInfoRecord.getAppName()).bdDt(dt).bdApp_id(appInfoRecord.getId())
                .bdJvm_problem(evaluteProblem(gCollector,rspd)).bdJvm_state("problem")
                .bdMode("cost").bdSuggest(null).bdType(RiskStatisticCaType.Offline.getType())
                .bdTimestamp(com.alipay.autotuneservice.util.DateUtils.of(com.alipay.autotuneservice.util.DateUtils.now())));
    }

    public void saveDailyStatDataOnline(AppInfoRecord appInfoRecord, long tuneTime){
        List<RiskStatisticPreData> rspd = Lists.newArrayList();
        // get today data
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
        String dt = dayFormat.format(new Date());
        List<RiskStatisticPreData> todayDatas = this.riskCheckPreDataRepository.getPreDataDayAndApp(dt,appInfoRecord.getId());
        if(CollectionUtils.isNotEmpty(todayDatas)){
            // if check many times, get newest
            rspd.add(todayDatas.get(todayDatas.size()-1));
        }
        // get data before, if tuneTime < 7d, get tuneTime ~ now    else   get  now-7day ~ now
        String start = tuneTime<DateUtils.addDays(new Date(), -6).getTime() ?
                dayFormat.format(DateUtils.addDays(new Date(), -6)) : dayFormat.format(new Date(tuneTime));
        List<RiskStatisticPreData> beforeDatas = this.riskCheckPreDataRepository.getPreDataRange(appInfoRecord.getId(), start,
                dayFormat.format(DateUtils.addDays(new Date(), -1)));
        if(CollectionUtils.isNotEmpty(beforeDatas)){
            rspd.addAll(beforeDatas);
        }

        GarbageCollector gCollector = GsonUtil.fromJson(appInfoRecord.getAppTag(), AppTag.class).getCollector();
        this.healthCheckDataRepository.insert(HealthCheckData.newInstance()
                .bdApp(appInfoRecord.getAppName()).bdDt(dt).bdApp_id(appInfoRecord.getId())
                .bdJvm_problem(evaluteProblem(gCollector,rspd)).bdJvm_state("problem")
                .bdMode("cost").bdSuggest(null).bdType(RiskStatisticCaType.Online.getType())
                .bdTimestamp(com.alipay.autotuneservice.util.DateUtils.of(com.alipay.autotuneservice.util.DateUtils.now())));
    }


}