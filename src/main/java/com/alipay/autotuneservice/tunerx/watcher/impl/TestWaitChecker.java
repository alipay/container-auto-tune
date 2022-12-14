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
 * ??????????????????
 *
 * @author chenqu
 * @version : TestWaitExecChecker.java, v 0.1 2022???04???18??? 17:03 chenqu Exp $
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
        //???????????????????????????
        TuningParamTaskDataRecord record = tuningParamTaskData
            .getData(tunePipeline.getPipelineId());
        this.tuneTaskStatus = TuneTaskStatus.valueOf(record.getTaskStatus());
        //???????????????????????????,?????????,??????????????????
        remedy(record);
        return tuneTaskStatus.isFinal();
    }

    @Override
    public void submitNext() {
        TuneEventType eventType = null;
        switch (tuneTaskStatus) {
            case CANCEL:
                //????????????
                eventType = TuneEventType.CANCEL;
                break;
            case OPTIMIZE:
            case FINISH:
                //??????????????????
                eventType = TuneEventType.TEST_SUCCESS;
                break;
            case NEXT:
                //???????????????????????????
                eventType = TuneEventType.TEST_NEXT;
                break;
            default:
        }
        if (eventType == null) {
            return;
        }
        if (eventType == TuneEventType.CANCEL) {
            //tunePlan???????????????
            tunePlanRepository.updateTuneStatusById(this.tunePipeline.getTunePlanId(),
                TunePlanStatus.END);
        }
        //????????????????????????
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
            //??????jvm?????????
            Integer marketId = tuneContext.getMarketId();
            List<PodInfoRecord> podInfoRecords = podInfo.getByAllPodByAppId(tuneContext.getAppId());
            //??????????????????marketId
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
            //??????deletePodName
            String deletePodName = null;
            for (int i = podInfoRecords.size(); i-- > 0; ) {
                PodInfoRecord podInfo = podInfoRecords.get(i);
                //??????pod??????
                if (StringUtils.equals(podInfo.getPodStatus(), "INVALID")) {
                    deletePodName = podInfo.getPodName();
                    break;
                }
            }
            if (StringUtils.isEmpty(createPodName) || StringUtils.isEmpty(deletePodName)) {
                throw new RuntimeException("not found deletePodName");
            }
            //????????????
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
            //????????????pod??????
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
                //??????context
                tuneContext.setTestRetry(Boolean.TRUE);
                tunePipelinePhaseRepository.updateContext(tunePipeline.getCurrentPhase().getId(),
                    tuneContext);
                //????????????????????????
                tuningParamTaskData.updateStatus(tunePipeline.getPipelineId(), TuneTaskStatus.NEXT);
            }
        } catch (Exception e) {
            //do noting
            log.error("retry is error", e);
        }
    }
}