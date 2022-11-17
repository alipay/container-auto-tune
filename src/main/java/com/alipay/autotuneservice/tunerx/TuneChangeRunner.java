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
package com.alipay.autotuneservice.tunerx;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.TuneLogInfo;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TuningParamTaskData;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.model.common.PodStatus;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TuneChangeDefinition;
import com.alipay.autotuneservice.model.tunepool.*;
import com.alipay.autotuneservice.service.PodService;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 最终一致性执行器,用于check、下发任务
 */
@Service
@Slf4j
public class TuneChangeRunner extends TuneAbstractRunner {

    private ObservableEmitter<TuneConsistencyRq.ChangeRq> jemitter;
    @Autowired
    private AsyncTaskExecutor                             subExecutor;
    @Autowired
    private PodService                                    podService;
    @Autowired
    private AsyncTaskExecutor                             webTaskExecutor;
    @Autowired
    private PodInfo                                       podInfo;
    @Autowired
    private TuneLogInfo                                   tuneLogInfo;
    @Autowired
    private TuningParamTaskData                           tuningParamTaskData;
    @Autowired
    private TunePipelineRepository                        tunePipelineRepository;

    @PostConstruct
    public void init() {
        ExecutorScheduler observeScheduler = new ExecutorScheduler(webTaskExecutor);
        ExecutorScheduler subscribeExecutor = new ExecutorScheduler(subExecutor);
        //整体进程分为两部分：1、执行不一致检查。 2、进行一致性性修复任务下发(分批)
        Observable.create((ObservableEmitter<TuneConsistencyRq.ChangeRq> emitter) -> jemitter = emitter)
                .observeOn(observeScheduler)
                .subscribeOn(subscribeExecutor)
                .subscribe(changeRq -> {
                    log.info(Thread.currentThread().getName() + ":" + JSONObject.toJSONString(changeRq));
                    //执行参数订正
                    int restartMaxNum = changeRq.getRestartMaxNum();
                    if (changeRq.getChangeNum() <= 0) {
                        //可能的负数情况,重启个数就为-1
                        restartMaxNum = -1;
                    }
                    List<String> deletePods = Lists.newArrayList();
                    Boolean isGray = isGrayPipeline(changeRq.getPipelineId());
                    //事件搞个监听
                    Future<Boolean> future = doFuture(changeRq.getAppId(), changeRq.getPipelineId(),
                            changeRq.getMetaData().getJvmMarketId(),
                            restartMaxNum, changeRq.generateUnionKey(), deletePods, changeRq.getPoolType(), isGray);
                    TuneConsistencyRq.ChangeRq nexChangeRq = new TuneConsistencyRq.ChangeRq();
                    BeanUtils.copyProperties(changeRq, nexChangeRq);
                    try {
                        Boolean result = future.get(12, TimeUnit.MINUTES);
                        if (result) {
                            switch (changeRq.getPoolType()) {
                                case EXPERIMENT:
                                    //更新算法
                                    updateTaskData(changeRq, deletePods);
                                    break;
                                case BATCH:
                                    //更新log
                                    String actionDesc = changeRq.getChangeNum() - changeRq.getRestartMaxNum() <= 0 ? "SUCCESS" : "";
                                    updateBatchLog(changeRq, deletePods, actionDesc);
                                    break;
                                default:
                                    break;
                            }
                            //判断是否终止
                            if (changeRq.getChangeNum() - changeRq.getRestartMaxNum() <= 0) {
                                //TODO 有问题,变更成功，任务完成
                                changePoolToT(changeRq);
                                return;
                            }
                            return;
                        }
                        throw new RuntimeException("change pod timeout");
                    } catch (Exception e) {
                        //变更失败,记录日志
                        log.error("change is error", e);
                    }
                });
    }

    public void fire(TuneConsistencyRq.ChangeRq changeRq) {
        this.jemitter.onNext(changeRq);
    }

    private Future<Boolean> doFuture(Integer appId, Integer pipelineId, long jvmMarketId, int restartMaxNum, String unionKey,
                                     List<String> deletePods, PoolType poolType, boolean isGray) {
        //TODO 自定义线程池
        return subExecutor.submit(() -> podService.changePod(appId, (int) jvmMarketId, restartMaxNum, poolType, (list) -> {
                    long count = getInvalidCount(list);
                    int runCount = 0;
                    while (count != list.size() && runCount <= 20) {
                        try {
                            runCount++;
                            TimeUnit.SECONDS.sleep(30);
                            count = getInvalidCount(list);
                        } catch (InterruptedException e) {
                            //do noting
                        }
                    }
                    return runCount > 10 ? Boolean.FALSE : Boolean.TRUE;
                }, (podInfoRecord, errorMsg) -> {
                    //记录
                    writeLog(podInfoRecord, appId, jvmMarketId, pipelineId, errorMsg, unionKey);
                }, deletePods::addAll, isGray)
        );
    }

    private long getInvalidCount(List<PodInfoRecord> list) {
        return list.stream().filter(podInfoRecord -> {
            PodInfoRecord record = podInfo.getById(podInfoRecord.getId());
            return StringUtils.equals(record.getPodStatus(), PodStatus.INVALID.name());
        }).count();
    }

    @Async(value = "webTaskExecutor")
    void writeLog(PodInfoRecord podInfoRecord, Integer appId, long jvmMarketId, Integer pipelineId,
                  String errorMsg, String unionKey) {
        TuneLogInfoRecord record = new TuneLogInfoRecord();
        record.setAction("DELETE");
        record.setAppId(appId);
        record.setChangePodName(podInfoRecord.getPodName());
        record.setChangetTime(LocalDateTime.now());
        if (StringUtils.isNotEmpty(errorMsg)) {
            record.setErrorMsg(errorMsg);
        }
        record.setActionDesc(unionKey);
        record.setPipelineId(pipelineId);
        record.setJvmMarketId((int) jvmMarketId);
        tuneLogInfo.insertPodInfo(record);
    }

    private void updateTaskData(TuneConsistencyRq.ChangeRq changeRq, List<String> deletePods) throws Exception {
        //获取最新pod
        List<PodInfoRecord> records = podInfo.getByAppId(changeRq.getAppId());
        List<String> comparePods = records.stream()
                .filter(record -> !deletePods.contains(record.getPodName()))
                .map(PodInfoRecord::getPodName)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(comparePods) && comparePods.size() > 5) {
            comparePods = comparePods.subList(0, 5);
        }
        //更新算法
        List<TuneChangeDefinition> changeDefinitions = waitingChangePods(changeRq, deletePods);
        tuningParamTaskData.updateChangePod(changeDefinitions, changeRq.getPipelineId(), comparePods);
    }

    private void updateBatchLog(TuneConsistencyRq.ChangeRq changeRq, List<String> deletePods,
                                String actionDesc) throws Exception {
        List<TuneChangeDefinition> changeDefinitions = waitingChangePods(changeRq, deletePods);
        if (CollectionUtils.isEmpty(changeDefinitions)) {
            return;
        }
        TuneLogInfoRecord record = new TuneLogInfoRecord();
        record.setPipelineId(changeRq.getPipelineId());
        record.setAppId(changeRq.getAppId());
        record.setJvmMarketId((int) changeRq.getMetaData().getJvmMarketId());
        record.setBatchNo(Integer.parseInt(changeRq.getMetaData().getDesc()));
        record.setAction(actionDesc);
        tuneLogInfo.updateChangePodInfo(record, changeDefinitions);
    }

    private List<TuneChangeDefinition> waitingChangePods(TuneConsistencyRq.ChangeRq changeRq,
                                                         List<String> deletePods) throws Exception {
        List<String> pods = getRecords(changeRq);
        //进入下一轮循环,更新算法观察podName
        int count = 0;
        while (CollectionUtils.isEmpty(pods) && count <= 30) {
            count++;
            TimeUnit.SECONDS.sleep(30);
            pods = getRecords(changeRq);
        }
        if (CollectionUtils.isEmpty(pods)) {
            log.error("pod is empty");
            return Lists.newArrayList();
        }
        if (pods.size() < deletePods.size()) {
            log.error("change pod num is error: delete > create");
            return Lists.newArrayList();
        }
        List<TuneChangeDefinition> changePods = Lists.newArrayList();
        for (int i = 0; i < deletePods.size(); i++) {
            TuneChangeDefinition definition = new TuneChangeDefinition();
            definition.setDeletePod(deletePods.get(i));
            definition.setCreatePod(pods.get(i));
            changePods.add(definition);
        }
        return changePods;
    }

    private List<String> getRecords(TuneConsistencyRq.ChangeRq changeRq) {
        List<PodInfoRecord> records = podInfo.getByAppId(changeRq.getAppId());
        String tuneJvmConfig = UserUtil.getTuneJvmConfig((int) changeRq.getMetaData().getJvmMarketId());
        return records.stream()
                .filter(record -> {
                    String jvm = record.getPodJvm();
                    return StringUtils.contains(jvm, tuneJvmConfig);
                })
                .map(PodInfoRecord::getPodName)
                .collect(Collectors.toList());
    }

    private Boolean isGrayPipeline(Integer pipelineId) {
        try {
            TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(
                    MachineId.GRAY_PIPELINE, pipelineId);
            if (null == tunePipeline) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

}