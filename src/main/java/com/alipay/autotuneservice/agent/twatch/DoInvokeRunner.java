/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.agent.twatch;

import com.alipay.autotuneservice.agent.twatch.model.AgentActionRequest;
import com.alipay.autotuneservice.base.cache.LocalCache;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.dynamodb.repository.TwatchInfoService;
import com.alipay.autotuneservice.util.AgentConstant;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 触发执行任务
 */
@Service
@Slf4j
public class DoInvokeRunner {

    private ObservableEmitter<Context> jemitter;
    @Autowired
    private LocalCache<Object, Object> localCache;
    @Autowired
    private AsyncTaskExecutor          subExecutor;
    @Autowired
    private AsyncTaskExecutor          webTaskExecutor;
    @Autowired
    private TwatchInfoService          twatchInfoRepository;

    @PostConstruct
    public void init() {
        ExecutorScheduler observeScheduler = new ExecutorScheduler(webTaskExecutor);
        ExecutorScheduler subscribeExecutor = new ExecutorScheduler(subExecutor);
        //创建执行线程池
        Observable.create((ObservableEmitter<Context> emitter) -> jemitter = emitter)
                .observeOn(observeScheduler)
                .filter(context -> {
                    //判断是否为空
                    return context.getAgentActionRequest() != null && context.countDownLatch != null;
                })
                .filter(context -> {
                    //判断是否为空
                    return !context.getAgentActionRequest().checkEmpty();
                })
                .filter(context -> {
                    //判断Agent不能为空
                    return StringUtils.isNotEmpty(context.getAgentActionRequest().getAgentName());
                })
                .subscribeOn(subscribeExecutor)
                .subscribe(context -> {
                    AgentActionRequest agentActionRequest = context.getAgentActionRequest();
                    //写缓存
                    log.info("doSave sessionId=" + agentActionRequest.getSessionId());
                    localCache.put(AgentConstant.generateQueueKey(agentActionRequest.getAgentName()), agentActionRequest);
                    context.countDownLatch.countDown();
                });
    }

    public void invoke(AgentActionRequest request, CountDownLatch countDownLatch) {
        this.jemitter.onNext(new Context(request, countDownLatch));
    }

    public List<TwatchInfoDo> findInfoByPod(String podName) {
        return twatchInfoRepository.findInfoByPod(podName);
    }

    public List<TwatchInfoDo> findInfoByContainer(String containerId) {
        return twatchInfoRepository.findInfoByContainerId(containerId);
    }

    private class Context {
        @Getter
        private AgentActionRequest agentActionRequest;
        @Getter
        private CountDownLatch     countDownLatch;

        public Context(AgentActionRequest agentActionRequest, CountDownLatch countDownLatch) {
            this.agentActionRequest = agentActionRequest;
            this.countDownLatch = countDownLatch;
        }
    }
}