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

import lombok.Data;

import java.util.Map;

/**
 * @author huoyuqi
 * @version WorkLoadMarketVO.java, v 0.1 2022年10月26日 5:54 下午 huoyuqi
 */
@Data
public class WorkLoadMarketVO {

    /**
     * 节省金钱
     */
    private Double totalIncome;

    /**
     * 优化workloads
     */
    private Integer optimizedWorkloads;

    /**
     * 优化 pods
     */
    private Integer optimizedPods;

    /**
     * 优化次数
     */
    private Integer tuneTimes;

    /**
     * 每天优化的app 数量
     */
    private Map<String, Long> optimizedAppMap;

    /**
     * 每天优化的pod 数量
     */
    private Map<String, Long> optimizedPodMap;

}