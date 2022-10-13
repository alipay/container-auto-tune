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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTaskDataRecord;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TuneChangeDefinition;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.model.tune.TuneTaskStatus;
import com.alipay.autotuneservice.tunerx.watcher.EventChecker;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 等待实验结果
 *
 * @author chenqu
 * @version : TestWaitExecChecker.java, v 0.1 2022年04月18日 17:03 chenqu Exp $
 */
@Slf4j
public class TestWaitChecker extends EventChecker {

    private TuneTaskStatus tuneTaskStatus;

    public TestWaitChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        super(applicationContext, tunePipeline);
    }

    @Override
    public TuneStage tuneStage() {
        return TuneStage.TEST_WAITING;
    }

    @Override
    public boolean doCheck() {
        //判断状态是不是终态
        TuningParamTaskDataRecord record = tuningParamTaskData
            .getData(tunePipeline.getPipelineId());
        this.tuneTaskStatus = TuneTaskStatus.valueOf(record.getTaskStatus());
        //判断表数据是否有值,如果无,进行定点补偿
        remedy(record);
        return tuneTaskStatus.isFinal();
    }

    @Override
    public void submitNext() {
        TuneEventType eventType = null;
        switch (tuneTaskStatus) {
            case CANCEL:
                //整体取消
                eventType = TuneEventType.CANCEL;
                break;
            case OPTIMIZE:
            case FINISH:
                //进行调参流程
                eventType = TuneEventType.TEST_SUCCESS;
                break;
            case NEXT:
                //获取下一个调参任务
                eventType = TuneEventType.TEST_NEXT;
                break;
            default:
        }
        if (eventType == null) {
            return;
        }
        if (eventType == TuneEventType.CANCEL) {
            //tunePlan置为完成态
            tunePlanRepository.updateTuneStatusById(this.tunePipeline.getTunePlanId(),
                TunePlanStatus.END);
        }
        //决策下一步的状态
        submitEvent(this.tunePipeline.getPipelineId(), eventType, tunePipeline.getContext());
    }

    private void remedy(TuningParamTaskDataRecord record) {
        TuneContext tuneContext = tunePipeline.getContext();
        try {
            if (TuneTaskStatus.RUNNING != tuneTaskStatus) {
                return;
            }
            if (StringUtils.isNotEmpty(record.getPods())) {
                JSONArray jsonArray = JSON.parseArray(record.getPods());
                if (jsonArray != null && jsonArray.size() > 0) {
                    return;
                }
            }
            //判断jvm是否有
            Integer marketId = tuneContext.getMarketId();
            List<PodInfoRecord> podInfoRecords = podInfo.getByAllPodByAppId(tuneContext.getAppId());
            //判断是否包含marketId
            Optional<PodInfoRecord> optional = podInfoRecords.stream().filter(podInfoRecord -> {
                String jvm = podInfoRecord.getPodJvm();
                if (StringUtils.isEmpty(jvm)) {
                    return Boolean.FALSE;
                }
                return StringUtils.contains(jvm, UserUtil.getTuneJvmConfig(marketId));
            }).findFirst();
            if (!optional.isPresent()) {
                throw new RuntimeException("not found pod");
            }
            PodInfoRecord podInfoRecord = optional.get();
            String createPodName = podInfoRecord.getPodName();
            //获取deletePodName
            String deletePodName = null;
            for (int i = podInfoRecords.size(); i-- > 0; ) {
                PodInfoRecord podInfo = podInfoRecords.get(i);
                //判断pod状态
                if (StringUtils.equals(podInfo.getPodStatus(), "INVALID")) {
                    deletePodName = podInfo.getPodName();
                    break;
                }
            }
            if (StringUtils.isEmpty(createPodName) || StringUtils.isEmpty(deletePodName)) {
                throw new RuntimeException("not found deletePodName");
            }
            //组建信息
            List<TuneChangeDefinition> changePods = Lists.newArrayList();
            TuneChangeDefinition definition = new TuneChangeDefinition();
            definition.setDeletePod(deletePodName);
            definition.setCreatePod(createPodName);
            changePods.add(definition);
            List<String> comparePods = Lists.newArrayList();
            if (StringUtils.isEmpty(record.getComparePods())) {
                comparePods = podInfoRecords.stream()
                        .filter(podInfo -> StringUtils.equals(podInfo.getPodStatus(), "ALIVE"))
                        .map(PodInfoRecord::getPodName)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(comparePods) && comparePods.size() > 5) {
                    comparePods = comparePods.subList(0, 5);
                }
            }
            //更新算法pod信息
            tuningParamTaskData.updateChangePod(changePods, record.getPipelineId(), comparePods);
        } catch (Exception e) {
            log.error("TestWaitChecker remedy is error", e);
            retry(record.getTrialStartTime(), tuneContext);
        }
    }

    private void retry(LocalDateTime localDateTime, TuneContext tuneContext) {
        try {
            long time = DateUtils.asTimestamp(localDateTime);
            if (System.currentTimeMillis() - time <= 10 * 60 * 1000) {
                return;
            }
            if (!tuneContext.isTestRetry()) {
                //更新context
                tuneContext.setTestRetry(Boolean.TRUE);
                tunePipelinePhaseRepository.updateContext(tunePipeline.getCurrentPhase().getId(),
                    tuneContext);
                //给予一次重试机会
                tuningParamTaskData.updateStatus(tunePipeline.getPipelineId(), TuneTaskStatus.NEXT);
            }
        } catch (Exception e) {
            //do noting
            log.error("retry is error", e);
        }
    }
}