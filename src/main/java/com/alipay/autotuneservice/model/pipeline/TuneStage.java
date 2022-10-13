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
package com.alipay.autotuneservice.model.pipeline;

/**
 * @author dutianze
 * @version TuneStage.java, v 0.1 2022年03月29日 17:20 dutianze
 */
public enum TuneStage {

    // =======================================================调参主阶段========================================================
    /**
     * 开始
     */
    NONE(MachineId.TUNE_PIPELINE),

    /**
     * 进行健康检测
     */
    HEALTHY_CHECK(MachineId.TUNE_PIPELINE),

    /**
     * 评估预期收益
     */
    EVALUATE_BENEFIT(MachineId.TUNE_PIPELINE),

    /**
     * 生成调优参数
     */
    ADJUSTMENT_PARAMETER(MachineId.TUNE_PIPELINE),

    /**
     * 执行调优进程
     */
    TUNING_PROCESS(MachineId.TUNE_PIPELINE),

    /**
     * 验证调优效果
     */
    VERIFY_EFFECT(MachineId.TUNE_PIPELINE),

    /**
     * 退出
     */
    CLOSED(MachineId.TUNE_PIPELINE),

    // =======================================================实验阶段========================================================
    TEST_NONE(MachineId.TUNE_TEST_PIPELINE),
    /**
     * 等待执行
     */
    TEST_WAIT_EXEC(MachineId.TUNE_TEST_PIPELINE),

    /**
     * 等待实验结果
     */
    TEST_WAITING(MachineId.TUNE_TEST_PIPELINE),

    /**
     * 实验成功
     */
    TEST_SUCCESS(MachineId.TUNE_TEST_PIPELINE),

    // =======================================================分批阶段========================================================
    BATCH_NONE(MachineId.TUNE_BATCH_PIPELINE),
    /**
     * 等待执行
     */
    BATCH_WAIT_EXEC(MachineId.TUNE_BATCH_PIPELINE),

    /**
     * 等待实验结果
     */
    BATCH_WAITING(MachineId.TUNE_BATCH_PIPELINE),

    /**
     * 流程成功
     */
    BATCH_PIPELINE_SUCCESS(MachineId.TUNE_BATCH_PIPELINE),

    /**
     * 批次失败
     */
    BATCH_FAIL(MachineId.TUNE_BATCH_PIPELINE),

    // =======================================================提交JVM主阶段========================================================
    /**
     * 开始
     */
    NONE_JVM(MachineId.TUNE_JVM_PIPELINE),

    /**
     * 灰度基线变更
     */
    GRAY_JVM(MachineId.TUNE_JVM_PIPELINE),

    /**
     * 确认按钮
     */
    CONFIRM_JVM(MachineId.TUNE_JVM_PIPELINE),

    /**
     * 生成调优参数
     */
    GRAY_FINISH_STATE_JVM(MachineId.TUNE_JVM_PIPELINE),

    /**
     * 退出
     */
    CLOSED_JVM(MachineId.TUNE_JVM_PIPELINE),

    // =======================================================灰度基线阶段========================================================

    GRAY_NONE(MachineId.GRAY_PIPELINE),
    /**
     * 等待执行
     */
    GRAY_WAIT_EXEC(MachineId.GRAY_PIPELINE),

    /**
     * 等待实验结果
     */
    GRAY_WAITING(MachineId.GRAY_PIPELINE),

    /**
     * 灰度评估
     */
    GRAY_EVALUATE(MachineId.GRAY_PIPELINE),

    /**
     * 流程成功
     */
    GRAY_PIPELINE_SUCCESS(MachineId.GRAY_PIPELINE),

    /**
     * 批次失败
     */
    GRAY_FAIL(MachineId.GRAY_PIPELINE), ;

    private final MachineId machineId;

    TuneStage(MachineId machineId) {
        this.machineId = machineId;
    }

    public MachineId getMachineId() {
        return machineId;
    }
}
