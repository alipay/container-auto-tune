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

import com.alibaba.cola.statemachine.Action;
import com.alibaba.cola.statemachine.Condition;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.StateMachineFactory;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import com.alibaba.cola.statemachine.impl.StateMachineException;
import com.alipay.autotuneservice.model.pipeline.MachineId;
import com.alipay.autotuneservice.model.pipeline.TuneEvent;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * @author dutianze
 * @version TuneTaskDomainService.java, v 0.1 2022年03月29日 17:00 dutianze
 */
@Slf4j
@Service
public class TuneFlowService {

    @Autowired
    private TuneExecuteService taskExecuteService;

    /**
     * 驱动任务阶段流转
     */
    public void fireTask(TunePipeline tunePipeline, TuneEvent tuneEvent) {
        log.info("fireTask, tunePipeline:{}, tuneEvent:{}", tunePipeline, tuneEvent);
        MachineId machineId = tunePipeline.getMachineId();
        StateMachine<TuneStage, TuneEventType, TunePipeline> stateMachine;
        switch (machineId) {
            case TUNE_PIPELINE:
                stateMachine = this.buildTuneStateMachine(machineId.name());
                break;
            case TUNE_TEST_PIPELINE:
                stateMachine = this.buildTuneTestStateMachine(machineId.name());
                break;
            case TUNE_BATCH_PIPELINE:
                stateMachine = this.buildTuneBatchStateMachine(machineId.name());
                break;
            case TUNE_JVM_PIPELINE:
                stateMachine = this.buildJvmTuneStateMachine(machineId.name());
                break;
            case GRAY_PIPELINE:
                stateMachine = this.buildTuneGrayStateMachine(machineId.name());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + machineId);
        }
        tunePipeline.patchContext(tuneEvent.getContext());
        stateMachine.fireEvent(tunePipeline.getStage(), tuneEvent.getEventType(), tunePipeline);
    }

    public StateMachine<TuneStage, TuneEventType, TunePipeline> buildJvmTuneStateMachine(String machineId) {
        StateMachineBuilder<TuneStage, TuneEventType, TunePipeline> builder = StateMachineBuilderFactory.create();
        // 创建任务
        builder.externalTransition()
                .from(TuneStage.NONE_JVM)
                .to(TuneStage.GRAY_JVM)
                .on(TuneEventType.NEXT_STEP)
                .when(TunePipeline::canFlowToNext)
                .perform(doJvmAction());
        // 取消任务
        Stream.of(TuneStage.GRAY_JVM, TuneStage.GRAY_FINISH_STATE_JVM,TuneStage.ADJUSTMENT_PARAMETER)
                .forEach(state -> builder.externalTransition()
                        .from(state)
                        .to(TuneStage.CLOSED)
                        .on(TuneEventType.CANCEL)
                        .when(TunePipeline::canFlowToNext)
                        .perform(doJvmAction()));
        // 跳转到参数进程
        builder.externalTransition()
                .from(TuneStage.GRAY_JVM)
                .to(TuneStage.ADJUSTMENT_PARAMETER)
                .on(TuneEventType.NEXT_STEP)
                .when(TunePipeline::canFlowToNext)
                .perform(doAction());
        try {
            return builder.build(machineId);
        } catch (StateMachineException e) {
            return StateMachineFactory.get(machineId);
        }
    }

    /**
     * 灰度基线流程
     */
    public StateMachine<TuneStage, TuneEventType, TunePipeline> buildTuneGrayStateMachine(String machineId) {
        StateMachineBuilder<TuneStage, TuneEventType, TunePipeline> machineBuilder = StateMachineBuilderFactory.create();
        machineBuilder.externalTransition()
                .from(TuneStage.GRAY_NONE)
                .to(TuneStage.GRAY_WAIT_EXEC)
                .on(TuneEventType.GRAY_START)
                .when(directPass())
                .perform(doTuneGrayAction());
        // 开始灰度事件 --> [ 等待执行阶段 -> 等待阶段] ( 提交灰度任务 )
        machineBuilder.externalTransition()
                .from(TuneStage.GRAY_WAIT_EXEC)
                .to(TuneStage.GRAY_WAITING)
                .on(TuneEventType.GRAY_START_RUN)
                .when(directPass())
                .perform(doTuneGrayAction());

        // 阶段评估事件 --> [ 等待阶段 -> 阶段评估 ] ( 当前批次阶段成功, 进入阶段评估 )
        machineBuilder.externalTransition()
                .from(TuneStage.GRAY_WAITING)
                .to(TuneStage.GRAY_EVALUATE)
                .on(TuneEventType.GRAY_EVA)
                .when(directPass())
                .perform(doTuneGrayAction());

        // 流程成功事件 --> [ 等待阶段 -> 流程全部成功阶段 ] ( 流程成功, 触发主流前进 )
        machineBuilder.externalTransition()
                .from(TuneStage.GRAY_EVALUATE)
                .to(TuneStage.GRAY_PIPELINE_SUCCESS)
                .on(TuneEventType.GRAY_SUCCESS)
                .when(directPass())
                .perform(doTuneGrayAction());
        // 阶段失败事件 --> [ 等待阶段 - 失败阶段 ] ( 结束所有流程 )
        machineBuilder.externalTransition()
                .from(TuneStage.GRAY_EVALUATE)
                .to(TuneStage.GRAY_FAIL)
                .on(TuneEventType.GRAY_FAILURE)
                .when(directPass())
                .perform(doTuneGrayAction());
        // 取消事件 - 整体取消
        Stream.of(TuneStage.GRAY_NONE, TuneStage.GRAY_WAIT_EXEC, TuneStage.GRAY_WAITING,
                TuneStage.GRAY_PIPELINE_SUCCESS, TuneStage.GRAY_FAIL)
                .forEach(state -> machineBuilder.externalTransition()
                        .from(state)
                        .to(TuneStage.CLOSED)
                        .on(TuneEventType.CANCEL)
                        .when(directPass())
                        .perform(doTuneGrayAction()));
        try {
            return machineBuilder.build(machineId);
        } catch (StateMachineException e) {
            return StateMachineFactory.get(machineId);
        }
    }

    public StateMachine<TuneStage, TuneEventType, TunePipeline> buildTuneStateMachine(String machineId) {
        StateMachineBuilder<TuneStage, TuneEventType, TunePipeline> builder = StateMachineBuilderFactory.create();
        // 创建任务
        builder.externalTransition()
                .from(TuneStage.NONE)
                .to(TuneStage.HEALTHY_CHECK)
                .on(TuneEventType.NEXT_STEP)
                .when(directPass())
                .perform(doAction());
        // 取消任务
        Stream.of(TuneStage.HEALTHY_CHECK, TuneStage.EVALUATE_BENEFIT, TuneStage.ADJUSTMENT_PARAMETER,
                TuneStage.TUNING_PROCESS, TuneStage.VERIFY_EFFECT)
                .forEach(state -> builder.externalTransition()
                        .from(state)
                        .to(TuneStage.CLOSED)
                        .on(TuneEventType.CANCEL)
                        .when(directPass())
                        .perform(doAction()));
        // 评估预期收益
        builder.externalTransition()
                .from(TuneStage.HEALTHY_CHECK)
                .to(TuneStage.EVALUATE_BENEFIT)
                .on(TuneEventType.NEXT_STEP)
                .when(TunePipeline::canFlowToNext)
                .perform(doAction());
        // 生成调优参数
        builder.externalTransition()
                .from(TuneStage.EVALUATE_BENEFIT)
                .to(TuneStage.ADJUSTMENT_PARAMETER)
                .on(TuneEventType.NEXT_STEP)
                .when(TunePipeline::canFlowToNext)
                .perform(doAction());
        // 执行调优进程
        builder.externalTransition()
                .from(TuneStage.ADJUSTMENT_PARAMETER)
                .to(TuneStage.TUNING_PROCESS)
                .on(TuneEventType.NEXT_STEP)
                .when(TunePipeline::canFlowToNext)
                .perform(doAction());
        // 验证调优效果
        builder.externalTransition()
                .from(TuneStage.TUNING_PROCESS)
                .to(TuneStage.VERIFY_EFFECT)
                .on(TuneEventType.NEXT_STEP)
                .when(TunePipeline::canFlowToNext)
                .perform(doAction());
        // 正常结束
        builder.externalTransition()
                .from(TuneStage.VERIFY_EFFECT)
                .to(TuneStage.CLOSED)
                .on(TuneEventType.NEXT_STEP)
                .when(TunePipeline::canFlowToNext)
                .perform(doAction());
        try {
            return builder.build(machineId);
        } catch (StateMachineException e) {
            return StateMachineFactory.get(machineId);
        }
    }

    /**
     * 调参实验流程
     */
    public StateMachine<TuneStage, TuneEventType, TunePipeline> buildTuneTestStateMachine(String machineId) {
        StateMachineBuilder<TuneStage, TuneEventType, TunePipeline> machineBuilder = StateMachineBuilderFactory.create();
        machineBuilder.externalTransition()
                .from(TuneStage.TEST_NONE)
                .to(TuneStage.TEST_WAIT_EXEC)
                .on(TuneEventType.TEST_START)
                .when(directPass())
                .perform(doTuneTestAction());
        // 开始实验事件 --> [ 等待执行阶段 -> 等待阶段] ( 提交实验任务 )
        machineBuilder.externalTransition()
                .from(TuneStage.TEST_WAIT_EXEC)
                .to(TuneStage.TEST_WAITING)
                .on(TuneEventType.TEST_START_RUN)
                .when(directPass())
                .perform(doTuneTestAction());
        // 实验成功事件 --> [ 等待阶段 -> 实验成功阶段 ] ( 完成当前流程, 触发主流程进入下一阶段的事件 )
        machineBuilder.externalTransition()
                .from(TuneStage.TEST_WAITING)
                .to(TuneStage.TEST_SUCCESS)
                .on(TuneEventType.TEST_SUCCESS)
                .when(directPass())
                .perform(doTuneTestAction());
        // 实验失败事件 --> [ 等待阶段 - 等待执行阶段 ] ( 当前流程标记为失败, 触发下一轮实验事件 )
        machineBuilder.externalTransition()
                .from(TuneStage.TEST_WAITING)
                .to(TuneStage.TEST_WAIT_EXEC)
                .on(TuneEventType.TEST_NEXT)
                .when(directPass())
                .perform(doTuneTestAction());

        // 取消事件 - 整体取消
        Stream.of(TuneStage.TEST_NONE, TuneStage.TEST_WAIT_EXEC, TuneStage.TEST_WAITING,
                TuneStage.TEST_SUCCESS)
                .forEach(state -> machineBuilder.externalTransition()
                        .from(state)
                        .to(TuneStage.CLOSED)
                        .on(TuneEventType.CANCEL)
                        .when(directPass())
                        .perform(doTuneTestAction()));
        try {
            return machineBuilder.build(machineId);
        } catch (StateMachineException e) {
            return StateMachineFactory.get(machineId);
        }
    }

    /**
     * 调参分批流程
     */
    public StateMachine<TuneStage, TuneEventType, TunePipeline> buildTuneBatchStateMachine(String machineId) {
        StateMachineBuilder<TuneStage, TuneEventType, TunePipeline> machineBuilder = StateMachineBuilderFactory.create();
        machineBuilder.externalTransition()
                .from(TuneStage.BATCH_NONE)
                .to(TuneStage.BATCH_WAIT_EXEC)
                .on(TuneEventType.BATCH_START)
                .when(directPass())
                .perform(doTuneBatchAction());
        // 开始分批事件 --> [ 等待执行阶段 -> 等待阶段] ( 提交分批任务 )
        machineBuilder.externalTransition()
                .from(TuneStage.BATCH_WAIT_EXEC)
                .to(TuneStage.BATCH_WAITING)
                .on(TuneEventType.BATCH_START_RUN)
                .when(directPass())
                .perform(doTuneBatchAction());
        // 阶段成功事件 --> [ 等待阶段 -> 等待执行阶段 ] ( 当前批次阶段成功, 进入下一批次的等待执行阶段, 并触发开始执行事件 )
        machineBuilder.externalTransition()
                .from(TuneStage.BATCH_WAITING)
                .to(TuneStage.BATCH_WAIT_EXEC)
                .on(TuneEventType.BATCH_NEXT)
                .when(directPass())
                .perform(doTuneBatchAction());
        // 流程成功事件 --> [ 等待阶段 -> 流程全部成功阶段 ] ( 流程成功, 触发主流前进 )
        machineBuilder.externalTransition()
                .from(TuneStage.BATCH_WAITING)
                .to(TuneStage.BATCH_PIPELINE_SUCCESS)
                .on(TuneEventType.BATCH_SUCCESS)
                .when(directPass())
                .perform(doTuneBatchAction());
        // 阶段失败事件 --> [ 等待阶段 - 失败阶段 ] ( 结束所有流程 )
        machineBuilder.externalTransition()
                .from(TuneStage.BATCH_WAITING)
                .to(TuneStage.BATCH_FAIL)
                .on(TuneEventType.BATCH_FAILURE)
                .when(directPass())
                .perform(doTuneBatchAction());
        // 取消事件 - 整体取消
        Stream.of(TuneStage.BATCH_NONE, TuneStage.BATCH_WAIT_EXEC, TuneStage.BATCH_WAITING,
                TuneStage.BATCH_PIPELINE_SUCCESS, TuneStage.BATCH_FAIL)
                .forEach(state -> machineBuilder.externalTransition()
                        .from(state)
                        .to(TuneStage.CLOSED)
                        .on(TuneEventType.CANCEL)
                        .when(directPass())
                        .perform(doTuneBatchAction()));
        try {
            return machineBuilder.build(machineId);
        } catch (StateMachineException e) {
            return StateMachineFactory.get(machineId);
        }
    }

    private Condition<TunePipeline> directPass() {
        return (ctx) -> true;
    }

    private Action<TuneStage, TuneEventType, TunePipeline> doAction() {
        return (from, to, eventType, pipeline) -> {
            log.info("pipeline:{} from:{} to:{} on:{}", pipeline.getId(), from, to, eventType);
            pipeline.flowTo(to);
            taskExecuteService.execute(pipeline);
        };
    }

    private Action<TuneStage, TuneEventType, TunePipeline> doTuneTestAction() {
        return (from, to, eventType, pipeline) -> {
            log.info("pipeline:{} from:{} to:{} on:{}", pipeline.getId(), from, to, eventType);
            pipeline.flowTo(to);
            taskExecuteService.executeTest(pipeline);
        };
    }

    private Action<TuneStage, TuneEventType, TunePipeline> doTuneBatchAction() {
        return (from, to, eventType, pipeline) -> {
            log.info("pipeline:{} from:{} to:{} on:{}", pipeline.getId(), from, to, eventType);
            pipeline.flowTo(to);
            taskExecuteService.executeBatch(pipeline);
        };
    }

    private Action<TuneStage, TuneEventType, TunePipeline> doJvmAction() {
        return (from, to, eventType, pipeline) -> {
            log.info("pipeline:{} from:{} to:{} on:{}", pipeline.getId(), from, to, eventType);
            pipeline.flowTo(to);
            taskExecuteService.executeJvm(pipeline);
        };
    }

    private Action<TuneStage, TuneEventType, TunePipeline> doTuneGrayAction() {
        return (from, to, eventType, pipeline) -> {
            log.info("pipeline:{} from:{} to:{} on:{}", pipeline.getId(), from, to, eventType);
            pipeline.flowTo(to);
            taskExecuteService.executeGray(pipeline);
        };
    }
}