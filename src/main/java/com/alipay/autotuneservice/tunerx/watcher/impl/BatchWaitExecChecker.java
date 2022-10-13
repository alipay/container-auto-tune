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

import com.alipay.autotuneservice.controller.model.configVO.ConfigInfoVO;
import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TuneActionStatus;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author chenqu
 * @version : TestWaitExecChecker.java, v 0.1 2022年04月18日 17:03 chenqu Exp $
 */
@Slf4j
public class BatchWaitExecChecker extends EventChecker {

    private final static Map<Integer, Double>   BATCH_MAP = ImmutableMap.of(1, 0.1, 2, 0.3, 3, 0.6,
                                                              4, 1d);
    private Function<TunePipeline, TuneContext> func;

    public BatchWaitExecChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
        this.func = (pipeline) -> {
            TuneContext tuneContext = this.tunePipeline.getContext();
            Integer batchNum = tuneContext.getBatchCount();
            //获取机器数
            if (MapUtils.isEmpty(tuneContext.getBatchMap())) {
                ConfigInfoVO configInfoVO = configInfoService.findAPPConfigByAPPID(tuneContext.getAppId());
                List<TuneConfig> tuneConfigs = configInfoVO.getTuneGroupConfig();
                if (CollectionUtils.isNotEmpty(tuneConfigs)) {
                    tuneConfigs.forEach(tuneConfig -> tuneContext.getBatchMap().put(tuneConfig.getNumber(), tuneConfig.getPercent()));
                }
            }
            if (MapUtils.isEmpty(tuneContext.getBatchMap())) {
                tuneContext.getBatchMap().putAll(BATCH_MAP);
            }
            Double percent = tuneContext.getBatchMap().get(batchNum);
            if (percent == null || percent <= 0) {
                batchNum = batchNum >= 4 ? 4 : batchNum;
                percent = BATCH_MAP.get(batchNum);
                tuneContext.getBatchMap().put(batchNum, percent);
            }
            Integer totalNum = tuneContext.getTotalNum();
            if (totalNum == null) {
                List<PodInfoRecord> podInfoRecords = podInfo.getByAppId(tunePipeline.getAppId());
                totalNum = podInfoRecords.size();
            }
            //组织分批参数
            MetaData metaData = tuneContext.getMetaData();
            int num = (int) (totalNum * percent);
            metaData.setReplicas(num <= 0 ? 1 : num);
            metaData.setDesc(String.valueOf(batchNum));
            tuneContext.setBatchRatio((int) (percent * 100));
            //记录
            tuneContext.getBatchMeatMap().put(batchNum, metaData);
            //获取调参参数
            return tuneContext;
        };
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.BATCH_WAIT_EXEC;
    }

    @Override
    public boolean doCheck() {
        if (this.tunePipeline.getContext().getBatchCount() > 4) {
            return Boolean.FALSE;
        }
        TunePlan tunePlan = tunePipelineService.findByPipelineId(this.tunePipeline.getPipelineId());
        if (tunePlan.getActionStatus() == TuneActionStatus.MANUAL) {
            log.info("------this is manual------");
            //人工确认,每批调节需要确认才可
            if ((tunePlan.getTunePlanStatus() != TunePlanStatus.CONFIRM)
                || !configInfoService.checkTuneIsEnableByAppID(tunePlan.getAppId())) {
                return Boolean.FALSE;
            }
        }
        //更改状态为running
        tunePlanRepository.updateTuneStatusById(tunePlan.getId(), TunePlanStatus.RUNNING);
        return Boolean.TRUE;
    }

    @Override
    public void submitNext() {
        TuneContext tuneContext = this.tunePipeline.getContext();
        log.info(String.format("batch tune jvmId=%s", tuneContext.getMarketId()));
        //决策下一步的状态
        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.BATCH_START_RUN,
            this.func.apply(this.tunePipeline));
    }
}