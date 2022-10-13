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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.controller.model.tuneprediction.AppTunePredictVO;
import com.alipay.autotuneservice.controller.model.TuneEffectVO;

/**
 * @author huoyuqi
 * @version TuneEffectService.java, v 0.1 2022年05月05日 10:54 上午 huoyuqi
 */
public interface TuneEffectService {

    TuneEffectVO tuneEffect(Integer pipelineId);

    TuneEffectVO tuneProcessEffect(Integer pipelineId);

    /**
     * 提前一分钟触发
     * @param pipelineId
     */
    void triggerTuneEffect(Integer pipelineId);

    /**
     * 异步触发 观察时间写入 灰度观察时间、效果评估观察时间
     *
     * @param pipelineId
     */
    void asyncTuneEffect(Integer pipelineId, String type);

    /**
     * 预评估调参收益
     *
     * @param appId
     * @return
     */
    AppTunePredictVO predictTuneEffect(Integer appId, Integer pipelineId);

    /**
     * 灰度评估调参收益
     * @param appId
     * @param pipelineId
     * @return
     */
    AppTunePredictVO grayEffect(Integer appId, Integer pipelineId);

    /**
     * 异步提交 参照pod相关指标
     *
     * @param appId
     * @param pipelineId
     * @return
     */
    Boolean asyncSubmitTunePredict(Integer appId, Integer pipelineId);

}