/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.tunerx.watcher.impl;

import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTaskDataRecord;
import com.alipay.autotuneservice.model.pipeline.PipelineStatus;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.model.tune.TuneTaskStatus;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import com.alipay.autotuneservice.tunepool.TunePool;
import com.alipay.autotuneservice.tunepool.TuneSource;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.function.Function;

/**
 * @author chenqu
 * @version : TestWaitExecChecker.java, v 0.1 2022年04月18日 17:03 chenqu Exp $
 */
@Slf4j
public class AdjustmentParameterChecker extends EventChecker {

    private Function<TunePipeline, TuneContext> func;
    private Integer                             jvmMarketId;
    private TunePlanStatus                      tunePlanStatus;

    public AdjustmentParameterChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
        this.func = (pipeline) -> {

            if (StringUtils.isNotEmpty(tunePipeline.getContext().getGrayJvm())) {
                TuneContext tuneContext = tunePipeline.getContext();
                tuneContext.setPipelineStatus(PipelineStatus.GRAY);
                tuneContext.setMarketId((int) tuneContext.getMetaData().getJvmMarketId());
                return tunePipeline.getContext();
            }
            TuneContext tuneContext = new TuneContext();
            tuneContext.setPipelineId(tunePipeline.getPipelineId());
            tuneContext.setMarketId(jvmMarketId);
            return tuneContext;
        };
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.ADJUSTMENT_PARAMETER;
    }

    @Override
    public boolean doCheck() {
        Integer pipelineId = tunePipeline.getPipelineId();
        try{
            if (tunePipeline.isGray()) {
                return grayConfirm();
            }
        }catch (Exception e){
            log.error("AdjustmentParameterChecker doCheck occur an error pipelineId: {}", pipelineId);
        }

        TuningParamTaskDataRecord record = tuningParamTaskData.getData(pipelineId);
        if (record == null || StringUtils.isEmpty(record.getTrialParams())) {
            return Boolean.FALSE;
        }
        if (!StringUtils.equals(record.getTaskStatus(), TuneTaskStatus.NEXT.name())) {
            return Boolean.FALSE;
        }
        this.jvmMarketId = jvmMarketInfo.insert(record.getTrialParams());

        //判断参数是否存在
        return Boolean.TRUE;
    }

    @Override
    public void submitNext() {
        //决策下一步的状态
        TuneEventType tuneEventType = TuneEventType.TEST_START;
        if(tunePipeline.isGray()){
            tuneEventType = tunePlanStatus.isCancel() ? TuneEventType.CANCEL : TuneEventType.NEXT_STEP;
        }
        log.info("AdjustmentParameterChecker submitNext");
        submitEvent(this.tunePipeline.getPipelineId(), tuneEventType, this.func.apply(this.tunePipeline));
    }

    private Boolean  grayConfirm(){
        TunePlan tunePlan = tunePipelineService.findByPipelineId(tunePipeline.getPipelineId());
        if (tunePlan.getTuneStatus() == null) {
            return Boolean.FALSE;
        }
        this.tunePlanStatus = tunePlan.getTuneStatus();
        //1.判断tunePlan中是否确认执行下一步
        if (tunePlanStatus.isConfirm()) {
            return Boolean.TRUE;
        }
        TuneEntity tuneEntity;
        //2.取消或者超时 执行下面逻辑
        if (tunePlan.isGrayCancel()) {
            //更改相应的jvm及jvmMarketId
            TuneContext tuneContext = tunePipeline.getContext();
            MetaData metaData = tuneContext.getMetaData();
            AppInfoRecord appInfoRecord = appInfoRepository.getById(tuneContext.getAppId());
            metaData.setJvmCmd(appInfoRecord.getAppDefaultJvm());
            metaData.setJvmMarketId(getJvmMarketId(appInfoRecord.getAppDefaultJvm()));
            // 回滚所有机器
            List<PodInfoRecord> jvmPodInfoRecords = podInfo.getByAppId(tunePlan.getAppId());
            Integer grayRollbackNum = jvmPodInfoRecords.size();
            metaData.setReplicas(grayRollbackNum);
            metaData.setDesc(String.valueOf(grayRollbackNum));
            tuneContext.setMetaData(metaData);
            //更改状态为取消
            tunePlanRepository.updateStatusById(tunePlan.getId(), TunePlanStatus.CANCEL);
            //触发回滚
            tuneEntity = TuneEntity.builder()
                    .accessToken(tunePipeline.getAccessToken())
                    .appId(tunePipeline.getAppId())
                    .pipelineId(tunePipeline.getPipelineId())
                    .build();
            TuneSource tuneSource = tuneProcessor.getTuneSource(tuneEntity);
            TunePool tunePool = tuneSource.experimentTunePool();
            tunePool.updateTuneMeta(metaData)
                    .moveStatus(TunePoolStatus.RUNNABLE)
                    .refresh();
        }
        tuneEntity = TuneEntity.builder()
                .accessToken(tunePipeline.getAccessToken())
                .appId(tunePipeline.getAppId())
                .pipelineId(tunePipeline.getPipelineId())
                .build();
        TuneSource tuneSource1 = tuneProcessor.getTuneSource(tuneEntity);
        boolean flag = tuneSource1.experimentTunePool().getPoolStatus() == TunePoolStatus.TERMINATED;
        if (flag && tunePlan.getTuneStatus().isSubmit()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private long getJvmMarketId(String jvm) {
        if (jvm.contains(UserUtil.TUNE_JVM_APPEND)) {
            int startIndex = jvm.indexOf(UserUtil.TUNE_JVM_APPEND);
            return Long.parseLong(jvm.substring(startIndex).split(" ", 2)[0].split("=")[1]);
        }
        return 0;
    }
}