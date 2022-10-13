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
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;

import java.util.List;

/**
 * @author chenqu
 * @version : TunePlanRepository.java, v 0.1 2022年04月18日 15:41 chenqu Exp $
 */
public interface TunePlanRepository {

    TunePlan findRunningTunePlanById(Integer id);

    TunePlan findTunePlanById(Integer id);

    List<TunePlan> findTunePlanByAppId(Integer appId);

    List<TunePlan> findByAppIdAndStatus(Integer appId, TunePlanStatus status, Long startTime,
                                        Long endTime);

    TunePlan save(TunePlan tunePlan);

    List<TunePlan> findByAppIdAndTime(Integer appId, Long start, Long end);

    TunePlan findByAppIdDescTime(Integer appId, Long start, Long end);

    List<TunePlan> findByAppIdLimit(Integer appId, Long end);

    int updateEffectById(Integer id, String tuneEffect, Double income);

    int updateGrayPredictById(Integer id, String tuneEffect, Double income);

    int updatePredictEffect(Integer id, String predictEffect);

    TunePlan findLastTunePlanByAppId(Integer appId);

    List<TunePlan> batchFindLastTunePlanByAppId(List<Integer> appIds);

    List<TunePlan> batchFindTunePlanByPipelineId(List<Integer> pipelineIds);

    void updateTuneStatusById(Integer id, TunePlanStatus tuneStatus);

    void updateStatusById(Integer id, TunePlanStatus tuneStatus);

    List<TunePlan> findByStatus();
}