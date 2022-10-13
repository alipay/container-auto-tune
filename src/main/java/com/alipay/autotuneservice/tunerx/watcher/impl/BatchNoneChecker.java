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
package com.alipay.autotuneservice.tunerx.watcher.impl;

import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TuneActionStatus;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.params.DecisionedTuneParam;
import com.alipay.autotuneservice.model.tune.params.TuneParamUpdateStatus;
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
 * @author chenqu
 * @version : TestWaitExecChecker.java, v 0.1 2022年04月18日 17:03 chenqu Exp $
 */
@Slf4j
public class BatchNoneChecker extends EventChecker {

    private Function<TunePipeline, TuneContext> func;
    private boolean                             change = Boolean.FALSE;
    private String                              jvm;
    private Integer                             jvmMarketId;
    private List<TuneConfig>                    decisionedTuneGroups;
    private Integer                             totalNum;

    public BatchNoneChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
        this.func = (pipeline) -> {
            TuneContext tuneContext = this.tunePipeline.getContext();
            if (StringUtils.isNotEmpty(jvm)) {
                MetaData metaData = tuneContext.getMetaData();
                metaData.setJvmCmd(jvm);
                metaData.setJvmMarketId(jvmMarketId);
            }
            if (CollectionUtils.isNotEmpty(decisionedTuneGroups)) {
                decisionedTuneGroups.forEach(decisionedTuneGroup -> {
                    if (decisionedTuneGroup.getNumber() == 0) {
                        return;
                    }
                    tuneContext.getBatchMap().put(decisionedTuneGroup.getNumber(), decisionedTuneGroup.getPercent());
                });
            }
            if (CollectionUtils.isEmpty(decisionedTuneGroups)) {
                //获取config_info
                decisionedTuneGroups = configInfoService.findTuneGroupsByAppId(tuneContext.getAppId());
            }
            if (totalNum != null) {
                tuneContext.setTotalNum(totalNum);
            }
            return tuneContext;
        };
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.BATCH_NONE;
    }

    @Override
    public boolean doCheck() {
        log.info("参数校验开始");
        //判断是否为托管
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(this.tunePipeline.getTunePlanId());
        if (tunePlan.getActionStatus() == TuneActionStatus.AUTO) {
            //调用传递jvm参数
            TuneContext tuneContext = this.tunePipeline.getContext();
            AppInfoRecord appInfoRecord = appInfoRepository.getById(tuneContext.getAppId());
            tuneParamService.submitAutoTuneParam(tuneContext.getAppId(), tuneContext
                .getPipelineId(), appInfoRecord.getAppDefaultJvm(), tuneContext.getMetaData()
                .getJvmCmd());
            return Boolean.TRUE;
        }
        //人工确认态,获取调整是否结束
        TuneParamUpdateStatus status = tuneParamService.queryTuneParamStatus(
            this.tunePipeline.getAppId(), this.tunePipeline.getPipelineId());
        if (status == null) {
            return Boolean.FALSE;
        }
        log.info("参数校验status=" + status.name());
        change = status == TuneParamUpdateStatus.END;
        return change;
    }

    @Override
    public void submitNext() {
        //当前机器总数
        List<PodInfoRecord> podInfoRecords = podInfo.getByAppId(tunePipeline.getAppId());
        if (CollectionUtils.isNotEmpty(podInfoRecords)) {
            this.totalNum = podInfoRecords.size();
        }
        if (change) {
            try {
                DecisionedTuneParam decisionedTuneParam = tuneParamService.getDecisionedTuneParams(
                    this.tunePipeline.getAppId(), this.tunePipeline.getPipelineId());
                //获取jvmMarketId
                String jvm = decisionedTuneParam.getDecisionedTuneParams();
                log.info("参数校验jvm=" + jvm);
                if (StringUtils.isEmpty(jvm)) {
                    throw new RuntimeException("jvm is required");
                }
                jvm = invokeJvm(jvm);
                this.jvmMarketId = jvmMarketInfo.getOrInsertJvmByCMD(jvm, tunePipeline.getAppId(),
                    tunePipeline.getPipelineId());
                //update一下
                this.jvm = String.format("%s %s", jvm, UserUtil.getTuneJvmConfig(jvmMarketId));
                jvmMarketInfo.updateJvmConfig(jvmMarketId, this.jvm);
                //调整分组
                this.decisionedTuneGroups = decisionedTuneParam.getDecisionedTuneGroups();
            } catch (Exception e) {
                //do noting
                log.error("TuningProcessChecker is error", e);
            }
        }
        //决策下一步的状态
        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.BATCH_START,
            this.func.apply(this.tunePipeline));
    }
}