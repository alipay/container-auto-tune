/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.tunerx.watcher.impl;

import com.alipay.autotuneservice.model.pipeline.PipelineStatus;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.function.Function;

/**
 * @author huoyuqi
 * @version StartWaitChecker.java, v 0.1 2022年08月16日 5:12 下午 huoyuqi
 */
@Slf4j
public class StartWaitChecker extends EventChecker {

    private Function<TunePipeline, TuneContext> func;

    public StartWaitChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
        this.func = (pipeline) -> {
            TuneContext tuneContext = new TuneContext();
            tuneContext.setPipelineId(tunePipeline.getPipelineId());
            tuneContext.setPipelineStatus(PipelineStatus.GRAY);
            return tuneContext;
        };
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.NONE_JVM;
    }

    @Override
    public boolean doCheck() {
        return Boolean.TRUE;
    }

    @Override
    public void submitNext() {
        //决策下一步的状态
        log.info(String.format("StartWaitChecker pipelineId= %s", tunePipeline.getPipelineId()));
        submitEvent(this.tunePipeline.getPipelineId(), TuneEventType.NEXT_STEP, this.func.apply(this.tunePipeline));
    }
}