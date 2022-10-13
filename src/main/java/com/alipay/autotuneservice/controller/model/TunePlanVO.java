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

import com.alipay.autotuneservice.model.dto.PipelineDTO;
import com.alipay.autotuneservice.model.tune.TuneActionStatus;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import lombok.Data;

/**
 * @author huangkaifei
 * @version : TunePlanVO.java, v 0.1 2022年05月04日 10:34 PM huangkaifei Exp $
 */
@Data
public class TunePlanVO {
    /**
     * 调优计划名称
     **/
    private String           planName;
    /**
     * 调优执行方式
     **/
    private TuneActionStatus tuneActionType;
    /**
     * 调优计划状态
     **/
    private TunePlanStatus   tunePlanStatus;
    /**
     * 调参阶段
     **/
    private PipelineDTO      pipelineDTO;
    /**
     * 调优计划评分
     **/
    private String           tunePlanScore;
    /**
     * 计划ID
     */
    private Integer          tunePlanId;
    /**
     * 实体数量
     **/
    private Integer          entityNums;
    /**
     * 安装agent数量
     */
    private long             agentNums;
    private long             startTime;
    private long             updateTime;
    /**
     * 总耗时 min
     */
    private long             totalTime;
    /**
     * 调优计划总耗时
     */
    private String           tunePlanTotalTime;

    public void buildTotalTime() {
        this.totalTime = updateTime > startTime ? (updateTime - startTime) / 60000 : 0;
    }

    public void buildTunePlanTIme() {
        long totalTime = updateTime > startTime ? (updateTime - startTime) / 60000 : 0;
        if (totalTime < 60) {
            this.tunePlanTotalTime = String.format("%smin", totalTime);
            return;
        }
        long hour = totalTime / 60;
        long min = totalTime % 60;
        if (min == 0) {
            this.tunePlanTotalTime = String.format("%sh", hour);
            return;
        }
        this.tunePlanTotalTime = String.format("%sh%smin", hour, min);
    }
}