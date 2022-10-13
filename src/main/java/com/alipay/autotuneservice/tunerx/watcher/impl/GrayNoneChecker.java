/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.tunerx.watcher.impl;

import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.pipeline.PipelineStatus;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.params.DecisionedTuneParam;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.function.Function;

/**
 * @author huoyuqi
 * @version GrayNoneChecker.java, v 0.1 2022年08月08日 2:15 下午 huoyuqi
 */
@Slf4j
public class GrayNoneChecker extends EventChecker {

    private Function<TunePipeline, TuneContext> func;
    private String                              jvm;
    private Integer                             jvmMarketId;
    private Integer                             totalNum;

    public GrayNoneChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
        this.func = (pipeline) -> {
            TuneContext tuneContext = tunePipeline.getContext();
            MetaData metaData = tuneContext.getMetaData();
            metaData.setJvmCmd(jvm);
            metaData.setJvmMarketId(jvmMarketId);
            tuneContext.setPipelineStatus(PipelineStatus.GRAY);
            if (totalNum != null) {
                tuneContext.setTotalNum(totalNum);
            }
            return tuneContext;
        };
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.GRAY_NONE;
    }

    @Override
    public boolean doCheck() {
        log.info("参数校验开始");
        TuneContext tuneContext = tunePipeline.getContext();
        AppInfoRecord appInfoRecord = appInfoRepository.getById(tuneContext.getAppId());
        tuneParamService.submitAutoTuneParam(tuneContext.getAppId(), tuneContext.getPipelineId(), appInfoRecord.getAppDefaultJvm(),
                tuneContext.getGrayJvm());
        return Boolean.TRUE;
    }

    @Override
    public void submitNext() {
        //当前机器总数
        List<PodInfoRecord> podInfoRecords = podInfo.getByAppId(tunePipeline.getAppId());
        if (CollectionUtils.isNotEmpty(podInfoRecords)) {
            this.totalNum = podInfoRecords.size();
        }
        try {
            DecisionedTuneParam decisionedTuneParam = tuneParamService.getDecisionedTuneParams(tunePipeline.getAppId(),
                    this.tunePipeline.getPipelineId());
            //获取jvmMarketId
            String jvm = decisionedTuneParam.getDecisionedTuneParams();
            log.info("参数校验jvm=" + jvm);
            if (StringUtils.isEmpty(jvm)) {
                throw new RuntimeException("jvm is required");
            }
            jvm = invokeJvm(jvm);
            this.jvmMarketId = jvmMarketInfo.getOrInsertJvmByCMD(jvm, tunePipeline.getAppId(), tunePipeline.getPipelineId());
            //update一下
            this.jvm = String.format("%s %s", jvm, UserUtil.getTuneJvmConfig(jvmMarketId));
            jvmMarketInfo.updateJvmConfig(jvmMarketId, this.jvm);
            TuneContext tuneContext = tunePipeline.getContext();
            AppInfoRecord record = appInfoRepository.getById(tuneContext.getAppId());
            tuneParamService.submitGrayAutoTuneParam(tuneContext.getAppId(), tuneContext.getPipelineId(), record.getAppDefaultJvm(), this.jvm);
        } catch (Exception e) {
            //do noting
            log.error("GrayNoneChecker is error", e);
        }

        //效果评估写入参照观察数据
        try {
            tuneEffectService.asyncSubmitTunePredict(tunePipeline.getAppId(), this.tunePipeline.getPipelineId());
        } catch (Exception e) {
            log.error("GrayNoneChecker asyncTuneEffect is error", e);
        }
        //决策下一步的状态

        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.GRAY_START, this.func.apply(this.tunePipeline));
    }

}