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
package com.alipay.autotuneservice.model.dto;

import com.alipay.autotuneservice.model.pipeline.TuneStage;

/**
 * @author dutianze
 * @version PipelineStage.java, v 0.1 2022年05月13日 16:23 dutianze
 */
public enum PipelineStage implements Comparable<PipelineStage> {

    /**
     * 健康检测阶段
     */
    HEALTHY(0),

    /**
     * 实验参数
     */
    PREDICT(1),

    /**
     * 确认参数
     */
    PARAMETER(2),

    /**
     * 调参进程
     */
    PROCESS(3),

    /**
     * 效果验证
     */
    EFFECT(4);

    private final int index;

    PipelineStage(Integer index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static PipelineStage of(TuneStage tuneStage) {
        switch (tuneStage) {
            case HEALTHY_CHECK:
                return HEALTHY;
            case ADJUSTMENT_PARAMETER:
                return PREDICT;
            case TUNING_PROCESS:
                return PROCESS;
            case VERIFY_EFFECT:
            case CLOSED:
                return EFFECT;
        }
        return HEALTHY;
    }


    public static PipelineStage with(TuneStage tuneStage) {
        switch (tuneStage) {
            case HEALTHY_CHECK:
                return HEALTHY;
            case GRAY_JVM:
                return PREDICT;
            case TUNING_PROCESS:
                return PROCESS;
            case VERIFY_EFFECT:
            case CLOSED:
                return EFFECT;
        }
        return HEALTHY;
    }


}