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

import com.alipay.autotuneservice.controller.model.TuneEffectVO;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * @author huoyuqi
 * @version GrayEvaluateChecker.java, v 0.1 2022年08月08日 2:35 下午 huoyuqi
 */
@Slf4j
public class GrayEvaluateChecker extends EventChecker {

    public GrayEvaluateChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.GRAY_EVALUATE;
    }

    @Override
    public boolean doCheck() {
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePipeline.getTunePlanId());
        Preconditions.checkNotNull(tunePlan, "tunePlanId数据库中查询不到");
        TuneEffectVO effectVO = tunePlan.getPredictEffectVO();
        if (effectVO != null && effectVO.checkFinishEvaluate()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public void submitNext() {
        //更新下tune_plan的状态
        tunePlanRepository.updateTuneStatusById(tunePipeline.getTunePlanId(),
            TunePlanStatus.RUNNING);
        //决策下一步的状态
        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.GRAY_SUCCESS,
            this.tunePipeline.getContext());
    }
}