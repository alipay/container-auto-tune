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

import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author dutianze
 * @version TuneContext.java, v 0.1 2022年04月14日 16:19 dutianze
 */
@Data
public class TuneContext implements Serializable {

    /**
     * accessToken
     */
    private String                 accessToken;

    /**
     * 应用id
     */
    private Integer                appId;

    /**
     * jvm参数id
     */
    private Integer                marketId;

    /**
     * 流程id
     */
    private Integer                pipelineId;

    /**
     * 效果验证id
     */
    private Integer                analyzeId;

    /**
     * 调参元数据
     */
    private MetaData               metaData       = new MetaData();

    /**
     * 计划ID
     */
    private Integer                tunePlanId;

    /**
     * 分批次数,从1开始
     */
    private Integer                batchCount     = 1;
    /**
     * 分批比例
     */
    private Integer                batchRatio     = 10;
    /**
     * 调参机器数
     */
    private Integer                totalNum;

    /**
     * 健康检测Id
     */
    private Integer                healthCheckId;

    /**
     * 调参分组
     */
    private Map<Integer, Double>   batchMap       = Maps.newHashMap();

    /**
     * 调参分组信息
     */
    private Map<Integer, MetaData> batchMeatMap   = Maps.newHashMap();

    /**
     * 调优效果剩余时间
     */
    private Long                   effectTime;

    /**
     * 恢度分批次数,从1开始
     */
    private Integer                grayCount      = 1;

    /**
     * 灰度分组信息
     */
    private Map<Integer, Double>   grayMap        = Maps.newHashMap();

    /**
     * 灰度jvm
     */
    private String                 grayJvm;

    /**
     * 流程类型
     */
    private PipelineStatus         pipelineStatus = PipelineStatus.MAIN;

    /**
     * 灰度比例
     */
    private Double                 grayRatio;

    private boolean                testRetry      = Boolean.FALSE;

    public TuneContext(String accessToken, Integer appId) {
        this.accessToken = accessToken;
        this.appId = appId;
    }

    public TuneContext(String accessToken, Integer appId, String grayJvm, Double grayRatio) {
        this.accessToken = accessToken;
        this.appId = appId;
        this.grayJvm = grayJvm;
        this.grayRatio = grayRatio;
        this.pipelineStatus = PipelineStatus.GRAY;
    }

    public TuneContext() {
    }
}