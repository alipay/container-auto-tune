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
package com.alipay.autotuneservice.tunerx.watcher;

import com.alipay.autotuneservice.model.pipeline.*;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 定义了一个事件监听者,用于驱动事件的运转
 *
 * @author chenqu
 * @version : TuneEventWatcher.java, v 0.1 2022年04月18日 16:20 chenqu Exp $
 */
@Slf4j
@Service
public class TuneEventWatcher {

    private ObservableEmitter<TunePipeline> jemitter;
    @Autowired
    private AsyncTaskExecutor               webTaskExecutor;
    @Autowired
    private EventCheckerFactory             eventCheckerFactory;

    @PostConstruct
    public void init() {
        ExecutorScheduler observeScheduler = new ExecutorScheduler(webTaskExecutor);
        ExecutorScheduler subscribeExecutor = new ExecutorScheduler(webTaskExecutor);
        //整体进程分为两部分：1、获取checker。 2、判断是否通过check。 3、执行submitNext
        Observable.create((ObservableEmitter<TunePipeline> emitter) -> jemitter = emitter)
                .observeOn(observeScheduler)
                .subscribeOn(subscribeExecutor)
                .filter(tunePipeline -> eventCheckerFactory.getTuneStages().contains(tunePipeline.getStage()))
                .map(tunePipeline -> eventCheckerFactory.findChecker(tunePipeline))
                .filter(EventChecker::check)
                .subscribe(EventChecker::submit);
    }

    public void fire(TunePipeline tunePipeline) {
        try {
            this.jemitter.onNext(tunePipeline);
        } catch (Exception e) {
            log.error("tuneEventWatcher-->fire is error", e);
        }
    }
}