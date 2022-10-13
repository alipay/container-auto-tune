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

import com.alipay.autotuneservice.controller.model.tuneplan.QueryTunePlanVO;
import com.alipay.autotuneservice.controller.model.tuneplan.TunePlanActionEnum;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;

/**
 * @author huangkaifei
 * @version : TunePlanService.java, v 0.1 2022年05月06日 4:27 PM huangkaifei Exp $
 */
public interface TunePlanService {

    /**
     * 调优进程执行动作： 确认执行，暂停，终止
     *
     * @param pipelineId
     * @param actionEnum
     * @return
     */
    Boolean execAction(Integer pipelineId, TunePlanActionEnum actionEnum);

    /**
     * findTunePlans
     */
    QueryTunePlanVO findTunePlans(Integer appId, TunePlanStatus status, String planName,
                                  Long startTime, Long endTime);
}