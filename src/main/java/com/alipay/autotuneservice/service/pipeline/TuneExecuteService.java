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
package com.alipay.autotuneservice.service.pipeline;

import com.alipay.autotuneservice.dao.TuneLogInfo;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.TuningParamTaskData;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEvent;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.model.tune.TuneTaskStatus;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.model.tunepool.MetaData.Type;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import com.alipay.autotuneservice.tunepool.TunePool;
import com.alipay.autotuneservice.tunepool.TuneProcessor;
import com.alipay.autotuneservice.tunepool.TuneSource;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dutianze
 * @version TaskExecuteService.java, v 0.1 2022年03月30日 17:53 dutianze
 */
@Slf4j
@Service
@Lazy
public class TuneExecuteService {

    @Autowired
    private TunePipelineRepository pipelineRepository;
    @Autowired
    private TuneProcessor          tuneProcessor;
    @Autowired
    private TuneEventProducer      tuneEventProducer;
    @Autowired
    private TuningParamTaskData    tuningParamTaskData;
    @Autowired
    private TuneLogInfo            tuneLogInfo;
    @Autowired
    private TunePlanRepository     tunePlanRepository;
    @Autowired
    private TunePipelineRepository tunePipelineRepository;

    public void execute(TunePipeline tunePipeline) {
        TuneStage stage = tunePipeline.getStage();
        log.info("execute tuneTask:{}", tunePipeline);
        switch (stage) {
            case HEALTHY_CHECK:
                log.info("execute ---- 健康检查");
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            case EVALUATE_BENEFIT:
                log.info("execute ---- 评估预期收益");
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            case ADJUSTMENT_PARAMETER: {
                log.info("execute ---- 生成调优参数");
                // 新建实验pipeline
                if (StringUtils.isNotEmpty(tunePipeline.getContext().getGrayJvm())) {
                    tunePipeline.setMachineId(MachineId.TUNE_PIPELINE);
                    pipelineRepository.saveWithTransaction(tunePipeline);
                    break;
                }
                tunePipeline.setStatus(Status.WAIT);
                TunePipeline testTunePipeline = tunePipeline.generateNewTunePipeline(TuneStage.TEST_NONE);
                pipelineRepository.saveWithTransaction(tunePipeline, testTunePipeline);
                break;
            }
            case TUNING_PROCESS: {
                log.info("execute ---- 执行调优进程");
                // 新建分批pipeline
                tunePipeline.setStatus(Status.WAIT);
                TunePipeline batchTunePipeline = tunePipeline.generateNewTunePipeline(TuneStage.BATCH_NONE);
                pipelineRepository.saveWithTransaction(tunePipeline, batchTunePipeline);
                break;
            }
            case VERIFY_EFFECT:
                log.info("execute ---- 验证调优效果");
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            case CLOSED: {
                log.info("execute ---- 退出");
                // 处理关闭事件，关闭pipeline
                if (grayCancel(tunePipeline)) {
                    cancelGrayPipeline(tunePipeline);
                    break;
                }
                tunePipeline.setStatus(Status.CLOSED);
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            }
            default:
                break;
        }
    }

    public void executeJvm(TunePipeline tunePipeline) {
        TuneStage stage = tunePipeline.getStage();
        log.info("execute tuneTask:{}", tunePipeline);
        switch (stage) {
            case GRAY_JVM:
                log.info("execute ---- 灰度基线");
                tunePipeline.setStatus(Status.WAIT);
                TunePipeline grayTunePipeline = tunePipeline.generateNewTunePipeline(TuneStage.GRAY_NONE);
                pipelineRepository.saveWithTransaction(tunePipeline, grayTunePipeline);
                break;
            case CLOSED: {
                log.info("execute ---- 退出");
                // 处理关闭事件，关闭pipeline
                cancelGrayPipeline(tunePipeline);
                break;
            }
            default:
                break;
        }
    }

    public void executeGray(TunePipeline tunePipeline) {
        TuneStage stage = tunePipeline.getStage();
        log.info("execute tunePipeline:{}", tunePipeline);
        switch (stage) {
            case GRAY_WAIT_EXEC: {
                log.info("execute ----  等待执行");
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            }
            case GRAY_WAITING:
                log.info("execute ---- 提交灰度任务，等待处理完成");
                TuneEntity tuneEntity = TuneEntity.builder()
                        .accessToken(tunePipeline.getAccessToken())
                        .appId(tunePipeline.getAppId())
                        .pipelineId(tunePipeline.getPipelineId())
                        .build();
                TuneSource tuneSource = tuneProcessor.getTuneSource(tuneEntity);
                TunePool tunePool = tuneSource.experimentTunePool();
                MetaData metaData = tunePool.getTuneMeta();
                metaData.setReplicas(tunePipeline.getContext().getMetaData().getReplicas());
                metaData.setDesc(String.valueOf(tunePipeline.getContext().getMetaData().getReplicas()));
                metaData.setType(Type.NUMBER);
                metaData.setJvmMarketId(tunePipeline.getContext().getMetaData().getJvmMarketId());
                metaData.setJvmCmd(tunePipeline.getContext().getMetaData().getJvmCmd());
                tunePool.registerTuneMeta(metaData)
                        .moveStatus(TunePoolStatus.RUNNABLE)
                        .refresh();
                //记录log
                logBatch(tunePipeline);
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            case GRAY_EVALUATE:
                log.info("execute ---- 灰度评估");
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            case GRAY_PIPELINE_SUCCESS: {
                log.info("execute ---- 流程成功, 触发主流程流转");
                this.branchSuccess(tunePipeline);
                break;
            }
            case GRAY_FAIL: {
                log.info("execute ---- 批次失败，触发主流程失败");
                tunePipeline.setStatus(Status.EXCEPTION);
                pipelineRepository.saveWithTransaction(tunePipeline);

                // 向主pipeline发送取消事件
                TuneEvent tuneEvent = new TuneEvent(tunePipeline.getPipelineId(), TuneEventType.CANCEL);
                tuneEventProducer.send(tuneEvent);
                break;
            }
            case CLOSED: {
                log.info("execute ---- 关闭");
                cancelBranchPipeline(tunePipeline);
                break;
            }
            default:
                break;
        }
    }

    public void executeTest(TunePipeline tunePipeline) {
        TuneStage stage = tunePipeline.getStage();
        log.info("execute tunePipeline:{}", tunePipeline);
        switch (stage) {
            case TEST_WAIT_EXEC:
                log.info("execute ---- 等待执行");
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            case TEST_WAITING:
                log.info("execute ---- 等待实验结果");
                TuneEntity tuneEntity = TuneEntity.builder()
                        .accessToken(tunePipeline.getAccessToken())
                        .appId(tunePipeline.getAppId())
                        .pipelineId(tunePipeline.getPipelineId())
                        .build();
                TuneSource tuneSource = tuneProcessor.getTuneSource(tuneEntity);
                TunePool tunePool = tuneSource.experimentTunePool();
                MetaData metaData = tunePool.getTuneMeta();
                metaData.setReplicas(1);
                metaData.setType(Type.NUMBER);
                metaData.setJvmMarketId(tunePipeline.getContext().getMetaData().getJvmMarketId());
                metaData.setJvmCmd(tunePipeline.getContext().getMetaData().getJvmCmd());
                tunePool.registerTuneMeta(metaData)
                        .moveStatus(TunePoolStatus.RUNNABLE)
                        .refresh();
                //修改算法表状态
                tuningParamTaskData.updateStatus(tunePipeline.getPipelineId(), TuneTaskStatus.RUNNING);
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            case TEST_SUCCESS: {
                log.info("execute ---- 实验成功");
                this.branchSuccess(tunePipeline);
                break;
            }
            case CLOSED: {
                log.info("execute ---- 关闭");
                cancelBranchPipeline(tunePipeline);
                break;
            }
        }
    }

    public void executeBatch(TunePipeline tunePipeline) {
        TuneStage stage = tunePipeline.getStage();
        log.info("execute tunePipeline:{}", tunePipeline);
        switch (stage) {
            case BATCH_WAIT_EXEC: {
                log.info("execute ---- 准备下一分组, 等待执行");
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            }
            case BATCH_WAITING:
                log.info("execute ---- 提交分批任务，等待实验结果");
                TuneEntity tuneEntity = TuneEntity.builder()
                        .accessToken(tunePipeline.getAccessToken())
                        .appId(tunePipeline.getAppId())
                        .pipelineId(tunePipeline.getPipelineId())
                        .build();
                TuneSource tuneSource = tuneProcessor.getTuneSource(tuneEntity);
                TunePool tunePool = tuneSource.batchTunePool();
                MetaData metaData = tunePipeline.getContext().getMetaData();
                tunePool.updateTuneMeta(metaData)
                        .moveStatus(TunePoolStatus.RUNNABLE)
                        .refresh();
                //记录log
                logBatch(tunePipeline);
                pipelineRepository.saveWithTransaction(tunePipeline);
                break;
            case BATCH_PIPELINE_SUCCESS: {
                log.info("execute ---- 流程成功, 触发主流程流转");
                this.branchSuccess(tunePipeline);
                break;
            }
            case BATCH_FAIL: {
                log.info("execute ---- 批次失败，触发主流程失败");
                tunePipeline.setStatus(Status.EXCEPTION);
                pipelineRepository.saveWithTransaction(tunePipeline);

                // 向主pipeline发送取消事件
                TuneEvent tuneEvent = new TuneEvent(tunePipeline.getPipelineId(), TuneEventType.CANCEL);
                tuneEventProducer.send(tuneEvent);
                break;
            }
            case CLOSED: {
                log.info("execute ---- 关闭");
                cancelBranchPipeline(tunePipeline);
                break;
            }
        }
    }

    private void branchSuccess(TunePipeline tunePipeline) {
        tunePipeline.setStatus(Status.CLOSED);
        TunePipeline waitingBranch = pipelineRepository.findByPipelineIdAndStatus(tunePipeline.getPipelineId(), Status.WAIT);
        waitingBranch.setStatus(Status.RUNNING);
        try {
            if (tunePipeline.isGray()) {
                waitingBranch.getContext().setMetaData(tunePipeline.getContext().getMetaData());
                waitingBranch.getContext().setGrayJvm(tunePipeline.getContext().getGrayJvm());
            }
        } catch (Exception e) {
            log.info("branchSuccess judge gray occurs an error, pipelineId: {}", tunePipeline.getId());
        }
        pipelineRepository.saveWithTransaction(tunePipeline, waitingBranch);

        // 向主pipeline发送完成事件, 进入下一阶段
        TuneEvent tuneEvent = new TuneEvent(tunePipeline.getPipelineId(), TuneEventType.NEXT_STEP);
        tuneEvent.setContext(tunePipeline.getContext());
        tuneEventProducer.send(tuneEvent);
    }

    private void cancelBranchPipeline(TunePipeline tunePipeline) {
        tunePipeline.setStatus(Status.CANCEL);
        TunePipeline waitingBranch = pipelineRepository.findByPipelineIdAndStatus(tunePipeline.getPipelineId(), Status.WAIT);
        if (waitingBranch == null) {
            return;
        }
        waitingBranch.setStatus(Status.CANCEL);
        pipelineRepository.saveWithTransaction(tunePipeline, waitingBranch);
    }

    private void cancelGrayPipeline(TunePipeline tunePipeline) {
        List<TunePipeline> aliveTunePipelines = tunePipelineRepository.findByPlanId(tunePipeline.getTunePlanId());
        aliveTunePipelines.forEach(aliveTunePipeline -> aliveTunePipeline.setStatus(Status.CANCEL));
        tunePipelineRepository.saveWithTransaction(aliveTunePipelines.toArray(new TunePipeline[0]));
        tunePlanRepository.updateTuneStatusById(tunePipeline.getTunePlanId(), TunePlanStatus.CANCEL);
    }

    private void logBatch(TunePipeline tunePipeline) {
        TuneContext tuneContext = tunePipeline.getContext();
        MetaData metaData = tuneContext.getMetaData();
        TuneLogInfoRecord record = new TuneLogInfoRecord();
        record.setPipelineId(tunePipeline.getPipelineId());
        record.setAppId(tunePipeline.getAppId());
        record.setJvmMarketId((int) metaData.getJvmMarketId());
        record.setAction("BATCH");
        record.setCreatedTime(LocalDateTime.now());
        record.setBatchNo(tuneContext.getBatchCount());
        record.setBatchRatio(tuneContext.getBatchRatio());
        record.setBatchTotalNum((int) metaData.getReplicas());
        tuneLogInfo.updateChangePodInfo(record, Lists.newArrayList());
    }

    /**
     * 判断是否是灰度取消按钮
     *
     * @param tunePipeline
     * @return
     */
    private Boolean grayCancel(TunePipeline tunePipeline) {
        if (tunePipeline.isGray()) {
            TunePlan record = tunePlanRepository.findTunePlanById(tunePipeline.getTunePlanId());
            if (null != record && null != record.getTuneStatus() && record.getTuneStatus().equals(TunePlanStatus.CANCEL)) {
                return true;
            }
        }
        return false;
    }

}