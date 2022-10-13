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
package com.alipay.autotuneservice.controller.model;

import com.alipay.autotuneservice.controller.model.tuneprediction.OptimizationType;
import com.alipay.autotuneservice.model.common.EffectTypeEnum;
import com.alipay.autotuneservice.model.common.TuneStatus;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author huoyuqi
 * @version ProblemTuneEffectVO.java, v 0.1 2022年04月27日 4:31 下午 huoyuqi
 */
@Data
public class EffectTypeVO {

    /**
     * 调优类型
     */
    private String           effectTypeEnum;

    /**
     * 参照结果
     */
    private Double           referResult;

    /**
     * 观察结果
     */
    private Double           observeResult;

    /**
     * 调优效果 比例
     */
    private Double           tuneRate;

    /**
     * 节省多少
     */
    private Double           reduce;

    /**
     * 状态 优化or恶化
     */
    private TuneStatus       status;

    /**
     * 原因
     */
    private String           reason;

    /**
     * 上升下降箭头
     */
    private OptimizationType optimizationType;

    /**
     * 单位， 根据类型而定
     */
    private String           unit;

    public EffectTypeVO() {

    }

    public EffectTypeVO(String effectTypeEnum, Double referResult, Double observeResult,
                        Double tuneRate, TuneStatus status, String reason, Double reduce) {
        this.effectTypeEnum = effectTypeEnum;
        this.referResult = referResult;
        this.observeResult = observeResult;
        this.tuneRate = tuneRate;
        this.status = status;
        this.reason = reason;
        this.reduce = reduce;
        this.optimizationType = status == TuneStatus.OPTIMIZATION ? OptimizationType.DOWN
            : OptimizationType.UP;
        if (StringUtils.equals(this.effectTypeEnum, EffectTypeEnum.QPS.name())
            && status == TuneStatus.OPTIMIZATION) {
            this.optimizationType = OptimizationType.UP;
        }
        buildUnit(EffectTypeEnum.valueOf(effectTypeEnum));
    }

    public void buildUnit(EffectTypeEnum effectTypeEnum) {
        switch (effectTypeEnum) {
            case FGC_TIME:
            case YGC_TIME:
            case RT:
                this.unit = "ms";
                break;
            case CPU:
                this.unit = "core";
                break;
            case MEM:
                this.unit = "MB";
                break;
            default:
                this.unit = "";
        }
    }
}