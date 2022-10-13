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

import lombok.Getter;

/**
 * @author dutianze
 * @version TuneEventType.java, v 0.1 2022年03月29日 17:22 dutianze
 */
@Getter
public enum TuneEventType {

    /**
     * 取消事件
     */
    CANCEL,

    // =======================================================主流程事件========================================================

    /**
     * 任务进入下一阶段
     */
    NEXT_STEP,

    /**
     * 重试任务
     */
    RETRY_STEP,

    /**
     * 关闭任务
     */
    CLOSE,

    /**
     * 任务执行超时
     */
    TIMEOUT,

    /**
     * 执行失败需要重试
     */
    FAILURE_RETRY,

    /**
     * 执行失败
     */
    FAILURE,

    // =======================================================实验事件========================================================
    /**
     * 创建实验
     */
    TEST_START,

    /**
     * 实验开始运行
     */
    TEST_START_RUN,

    /**
     * 下一个实验
     */
    TEST_NEXT,

    /**
     * 实验成功
     */
    TEST_SUCCESS,

    // =======================================================分批事件========================================================
    /**
     * 创建分批
     */
    BATCH_START,

    /**
     * 分批开始运行
     */
    BATCH_START_RUN,

    /**
     * 分批执行失败
     */
    BATCH_FAILURE,

    /**
     * 下一个批次
     */
    BATCH_NEXT,

    /**
     * 所有批次执行完成
     */
    BATCH_SUCCESS,

    // =======================================================灰度事件========================================================
    /**
     * 灰度开始
     */
    GRAY_START,

    /**
     * 灰度开始执行
     */
    GRAY_START_RUN,

    /**
     * 灰度执行失败
     */
    GRAY_FAILURE,

    /**
     * 灰度效果评估
     */
    GRAY_EVA,

    /**
     * 回滚执行
     */
    GRAY_NEXT,

    /**
     * 批次执行完成
     */
    GRAY_SUCCESS

}