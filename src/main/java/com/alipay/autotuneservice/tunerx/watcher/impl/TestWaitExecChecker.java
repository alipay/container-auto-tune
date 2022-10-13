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

import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTaskDataRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TuneTaskStatus;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.function.Function;

/**
 * 等待执行，准备执行参数
 *
 * @author chenqu
 * @version : TestWaitExecChecker.java, v 0.1 2022年04月18日 17:03 chenqu Exp $
 */
@Slf4j
public class TestWaitExecChecker extends EventChecker {

    private Function<TunePipeline, TuneContext> func;
    private String                              trialParams;
    private String                              jvm;
    private Integer                             jvmMarketId;

    public TestWaitExecChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
        this.func = (pipeline) -> {
            TuneContext tuneContext = this.tunePipeline.getContext();
            MetaData metaData = tuneContext.getMetaData();
            metaData.setReplicas(1);
            //获取调参参数
            metaData.setJvmCmd(jvm);
            metaData.setJvmMarketId(jvmMarketId);
            metaData.setDesc("TEST");
            tuneContext.setMarketId(jvmMarketId);
            return tuneContext;
        };
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.TEST_WAIT_EXEC;
    }

    @Override
    public boolean doCheck() {
        TuningParamTaskDataRecord taskDataRecord = tuningParamTaskData.getData(this.tunePipeline
            .getPipelineId());
        if (taskDataRecord == null) {
            return Boolean.FALSE;
        }
        TuneTaskStatus tuneTaskStatus = TuneTaskStatus.valueOf(taskDataRecord.getTaskStatus());
        if (tuneTaskStatus != TuneTaskStatus.NEXT) {
            return Boolean.FALSE;
        }
        this.trialParams = taskDataRecord.getTrialParams();
        return Boolean.TRUE;
    }

    @Override
    public void submitNext() {
        //获取应用默认jvm参数
        AppInfoRecord appInfoRecord = appInfoRepository.getById(tunePipeline.getAppId());
        String jvmConfig = appInfoRecord.getAppDefaultJvm();
        if (StringUtils.isEmpty(jvmConfig)) {
            jvmConfig = "";
        }
        jvmConfig = invokeJvm(jvmConfig);
        //合并
        this.jvm = generateJvm(trialParams, jvmConfig);
        log.info("trialParams=[{}], jvmConfig=[{}] ---> build=[{}]", trialParams, jvmConfig, jvm);
        if (StringUtils.isEmpty(jvm)) {
            log.error("TestWaitExecChecker --> jvm is empty");
            return;
        }
        this.jvmMarketId = jvmMarketInfo.getOrInsertJvmByCMD(jvm, tunePipeline.getAppId(),
            tunePipeline.getPipelineId());
        //update一下
        this.jvm = String.format("%s %s", jvm, UserUtil.getTuneJvmConfig(jvmMarketId));
        jvmMarketInfo.updateJvmConfig(jvmMarketId, jvm);
        //决策下一步的状态
        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.TEST_START_RUN,
            this.func.apply(this.tunePipeline));
    }
}