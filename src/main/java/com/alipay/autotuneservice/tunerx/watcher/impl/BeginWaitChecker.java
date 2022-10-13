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

import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import org.springframework.context.ApplicationContext;

import java.util.function.Function;

/**
 * @author chenqu
 * @version : TestWaitExecChecker.java, v 0.1 2022年04月18日 17:03 chenqu Exp $
 */
public class BeginWaitChecker extends EventChecker {

    private Function<TunePipeline, TuneContext> func;

    public BeginWaitChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
        this.func = (pipeline) -> {
            TuneContext tuneContext = new TuneContext();
            tuneContext.setPipelineId(tunePipeline.getPipelineId());
            return tuneContext;
        };
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.NONE;
    }

    @Override
    public boolean doCheck() {
        return Boolean.TRUE;
    }

    @Override
    public void submitNext() {
        //决策下一步的状态
        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.NEXT_STEP,
            this.func.apply(this.tunePipeline));
    }
}