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

import com.alipay.autotuneservice.controller.model.monitor.MetricVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huoyuqi
 * @version ThreadPoolVO.java, v 0.1 2022年12月06日 10:48 上午 huoyuqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreadPoolMonitorVO {

    private List<MetricVO> activeCount = new ArrayList<>();

    private List<MetricVO> poolSize = new ArrayList<>();

    private List<MetricVO> corePoolSize = new ArrayList<>();

    private List<MetricVO> largestPoolSize = new ArrayList<>();

    private List<MetricVO> maximumPoolSize = new ArrayList<>();

    private List<MetricVO> blockQueue = new ArrayList<>();

    private List<MetricVO> idlePoolSize = new ArrayList<>();

    private List<MetricVO> rejectCount = new ArrayList<>();

}