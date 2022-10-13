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

import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import com.alipay.autotuneservice.tunepool.TuneSource;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * @author huoyuqi
 * @version GrayWaitChecker.java, v 0.1 2022年08月08日 2:17 下午 huoyuqi
 */
@Slf4j
public class GrayWaitChecker extends EventChecker {

    public GrayWaitChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.GRAY_WAITING;
    }

    @Override
    public boolean doCheck() {
        TuneEntity tuneEntity = TuneEntity.builder().accessToken(tunePipeline.getAccessToken())
            .appId(tunePipeline.getAppId()).pipelineId(tunePipeline.getPipelineId()).build();
        TuneSource tuneSource = tuneProcessor.getTuneSource(tuneEntity);
        //改成实验类型
        log.info("tuneEntity={}, poolStatus={}", tuneEntity.toString(), tuneSource
            .experimentTunePool().getPoolStatus());
        boolean flag = tuneSource.experimentTunePool().getPoolStatus() == TunePoolStatus.TERMINATED;
        if (!flag) {
            return Boolean.FALSE;
        }
        return true;
    }

    @Override
    public void submitNext() {
        try {
            //提交评估
            tuneEffectService.asyncTuneEffect(this.tunePipeline.getPipelineId(), "gray");
        } catch (Exception e) {
            log.error("verifyEffectChecker asyncTuneEffect is error", e);
        }
        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.GRAY_EVA,
            this.tunePipeline.getContext());
    }
}