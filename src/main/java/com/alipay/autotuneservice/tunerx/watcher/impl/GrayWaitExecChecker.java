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

import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.function.Function;

/**
 * @author huoyuqi
 * @version GrayWaitExecChecker.java, v 0.1 2022年08月08日 2:19 下午 huoyuqi
 */
@Slf4j
public class GrayWaitExecChecker extends EventChecker {
    private Function<TunePipeline, TuneContext> func;

    public GrayWaitExecChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
        this.func = (pipeline) -> {
            /**
             * 构建context
             */
            TuneContext tuneContext = this.tunePipeline.getContext();
            //获取机器数
            Double percent = tuneContext.getGrayRatio();
            Integer totalNum = tuneContext.getTotalNum();
            if (totalNum == null) {
                List<PodInfoRecord> podInfoRecords = podInfo.getByAppId(tunePipeline.getAppId());
                totalNum = podInfoRecords.size();
            }
            //组织分批参数
            MetaData metaData = tuneContext.getMetaData();
            int num = (int) (totalNum * percent);
            metaData.setReplicas(num <= 0 ? 1 : num);
            metaData.setDesc(String.valueOf(num));
            //获取调参参数
            return tuneContext;
        };
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.GRAY_WAIT_EXEC;
    }

    @Override
    public boolean doCheck() {
        TunePlan tunePlan = tunePipelineService.findByPipelineId(this.tunePipeline.getPipelineId());
        //更改状态为running
        tunePlanRepository.updateTuneStatusById(tunePlan.getId(), TunePlanStatus.RUNNING);
        return Boolean.TRUE;
    }

    @Override
    public void submitNext() {
        TuneContext tuneContext = this.tunePipeline.getContext();
        log.info(String.format("grayWaitExecChecker pipelineId= %s jvmId=%s",
            tunePipeline.getPipelineId(), tuneContext.getMarketId()));
        //决策下一步的状态
        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.GRAY_START_RUN,
            this.func.apply(this.tunePipeline));
    }
}