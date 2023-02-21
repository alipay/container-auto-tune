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
package com.alipay.autotuneservice.configuration;

import com.alipay.autotuneservice.base.cache.LocalCache;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.common.PodStatus;
import com.alipay.autotuneservice.service.PodService;
import com.alipay.autotuneservice.util.AgentConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VMReplaceRunner implements ApplicationRunner, CommandLineRunner {

    private final ScheduledExecutorService               heartBeatService;
    private final Map<String, List<Long>>                counter;
    @Autowired
    private       AsyncTaskExecutor                      webTaskExecutor;
    private       ObservableEmitter<List<PodInfoRecord>> jemitter;
    @Autowired
    private       LocalCache<Object, Object>             localCache;
    @Autowired
    private       PodService                             podService;
    @Autowired
    private       PodInfo                                podInfo;

    public VMReplaceRunner() {
        this.counter = Maps.newConcurrentMap();
        this.heartBeatService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ExecutorScheduler observeScheduler = new ExecutorScheduler(webTaskExecutor);
        ExecutorScheduler subscribeExecutor = new ExecutorScheduler(webTaskExecutor);
        Observable.create((ObservableEmitter<List<PodInfoRecord>> emitter) -> jemitter = emitter)
                .observeOn(observeScheduler)
                .subscribeOn(subscribeExecutor)
                .filter(podInfoRecords -> !CollectionUtils.isEmpty(podInfoRecords))
                .subscribe(podInfoRecords -> podInfoRecords.stream()
                        .filter(podInfoRecord -> StringUtils.equals(podInfoRecord.getServerType(), "VM")).forEach(this::doReplace));
    }

    @Override
    public void run(String... args) throws Exception {
        heartBeatService.scheduleAtFixedRate(() -> {
            try {
                log.info("****  Start to Schedule  ****");
                List<PodInfoRecord> podInfoRecords = podInfo.getAllPods();
                //按照应用分类
                Map<Integer, List<PodInfoRecord>> podMap = Maps.newHashMap();
                podInfoRecords.stream()
                        .filter(podInfoRecord -> podInfoRecord.getAppId() != null)
                        .forEach(podInfoRecord -> {
                            Integer appId = podInfoRecord.getAppId();
                            if (podMap.containsKey(appId)) {
                                podMap.get(appId).add(podInfoRecord);
                                return;
                            }
                            podMap.put(appId, Lists.newArrayList(podInfoRecord));
                        });
                //定时器启动
                podMap.forEach((appId, records) -> jemitter.onNext(records));
            } catch (Exception e) {
                log.error("heartBeatService occur error", e);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void doReplace(PodInfoRecord podInfoRecord) {
        log.info("doReplace:{}", podInfoRecord.getPodName());
        String hostName = podInfoRecord.getPodName();
        String key = String.format(AgentConstant.AGENT_HEART_KEY, hostName);
        Object obj = localCache.get(key);
        if (obj == null) {
            List<Long> count = Lists.newArrayList();
            if (counter.containsKey(key)) {
                count = counter.get(key);
            }
            if (count.size() > 5) {
                //进行汰换
                podService.updatePodStatue(podInfoRecord.getId(), PodStatus.INVALID);
                remove(key);
                return;
            }
            count.add(System.currentTimeMillis());
            counter.put(key, count);
            return;
        }
        long healthTime = (long) obj;
        if (System.currentTimeMillis() - healthTime <= 300000) {
            return;
        }
        //进行汰换
        podService.updatePodStatue(podInfoRecord.getId(), PodStatus.INVALID);
        remove(key);
    }

    private void remove(String key) {
        counter.remove(key);
        localCache.remove(key);
    }

}

