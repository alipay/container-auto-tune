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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TuneChangeDefinition;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import com.alipay.autotuneservice.service.riskcheck.entity.CheckResponse;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCheckParam;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskControlStatus;
import com.alipay.autotuneservice.tunepool.TuneSource;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : TestWaitExecChecker.java, v 0.1 2022???04???18??? 17:03 chenqu Exp $
 */
@Slf4j
public class BatchWaitChecker extends EventChecker {

    public BatchWaitChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.BATCH_WAITING;
    }

    @Override
    public boolean doCheck() {
        TuneEntity tuneEntity = TuneEntity.builder().accessToken(tunePipeline.getAccessToken())
            .appId(tunePipeline.getAppId()).pipelineId(tunePipeline.getPipelineId()).build();
        TuneSource tuneSource = tuneProcessor.getTuneSource(tuneEntity);
        log.info("tuneEntity={}, poolStatus={}", tuneEntity.toString(), tuneSource.batchTunePool()
            .getPoolStatus());
        boolean flag = tuneSource.batchTunePool().getPoolStatus() == TunePoolStatus.TERMINATED;
        if (!flag) {
            return Boolean.FALSE;
        }
        return doRiskChecker();
    }

    @Override
    public void submitNext() {
        TuneContext context = tunePipeline.getContext();
        int batchNum = context.getBatchCount();
        TuneEventType tuneEventType = batchNum < 4 ? TuneEventType.BATCH_NEXT
            : TuneEventType.BATCH_SUCCESS;
        if (tuneEventType == TuneEventType.BATCH_NEXT) {
            context.setBatchCount(batchNum + 1);
        }
        if (tuneEventType == TuneEventType.BATCH_SUCCESS) {
            //??????appInfo???defaultJvm
            AppInfoRecord record = new AppInfoRecord();
            String jvm = jvmMarketInfo.getJvmInfo(context.getMarketId()).getJvmConfig();
            record.setAppDefaultJvm(jvm);
            record.setId(context.getAppId());
            appInfoRepository.updateAppDefaultJvm(record);
            log.info(String.format("update app=[%s] jvm=[%s] is success!", context.getAppId(),
                record.getAppDefaultJvm()));
        }
        //????????????????????????
        submitEvent(this.tunePipeline.getPipelineId(), tuneEventType,
            this.tunePipeline.getContext());
    }

    private boolean doRiskChecker() {
        log.info("-----??????????????????-----");
        TuneLogInfoRecord record = new TuneLogInfoRecord();
        record.setPipelineId(this.tunePipeline.getPipelineId());
        record.setAppId(this.tunePipeline.getAppId());
        record.setJvmMarketId(this.tunePipeline.getContext().getMarketId());
        record.setBatchNo(this.tunePipeline.getContext().getBatchCount());
        //??????risk??????ID
        TuneLogInfoRecord tuneLogInfoRecord = tuneLogInfo.findRecord(record);
        if (StringUtils.isEmpty(tuneLogInfoRecord.getRiskTraceId())) {
            //??????????????????
            RiskCheckParam riskCheckParam = new RiskCheckParam();
            riskCheckParam.setAppID(this.tunePipeline.getAppId());
            //??????pod
            String result = tuneLogInfoRecord.getBatchPods();
            if (StringUtils.isEmpty(result)) {
                return Boolean.TRUE;
            }
            List<TuneChangeDefinition> definitions = JSON.parseObject(result, new TypeReference<List<TuneChangeDefinition>>() {
            });
            List<String> pods = definitions.stream().map(TuneChangeDefinition::getCreatePod).collect(Collectors.toList());
            riskCheckParam.setPodnames(pods);
            //????????????
            riskCheckParam.setCheckTime(2);
            String riskTraceId = riskCheckService.submitRiskCheckJob(riskCheckParam);
            //????????????
            tuneLogInfo.updateRiskTraceId(tuneLogInfoRecord.getId(), riskTraceId);
            //TODO ???????????????,??????FALSE
            return Boolean.TRUE;
        }
        CheckResponse checkResponse = riskCheckService.getRiskCheckResult(tuneLogInfoRecord.getRiskTraceId());
        if (RiskControlStatus.END != checkResponse.getStatus()) {
            return Boolean.FALSE;
        }
        return !checkResponse.getResult().existRisk();
    }
}