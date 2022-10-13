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

/**
 * 调参主流程执行入口
 *
 * @author chenqu
 * @version : TuneInvokeService.java, v 0.1 2022年04月18日 15:06 chenqu Exp $
 */
public interface TuneInvokeService {

    /**
     * 提交调节任务,返回流程ID
     *
     * @param tunePlanId
     * @return
     */
    public Integer submitTunePlan(Integer tunePlanId);

    /**
     * 修改参数提交任务，返回流程ID
     * @param tunePlanId
     * @param jvmParam
     * @param grayRatio
     * @return
     */
    public Integer submitJvm(Integer tunePlanId, String jvmParam, Double grayRatio);

}