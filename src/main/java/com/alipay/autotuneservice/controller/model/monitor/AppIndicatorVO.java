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
package com.alipay.autotuneservice.controller.model.monitor;

import lombok.Data;

/**
 * @author huoyuqi
 * @version AppIndicator.java, v 0.1 2022年10月24日 10:39 上午 huoyuqi
 */
@Data
public class AppIndicatorVO {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 命名空间
     */
    private String nameSpace;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 监控指标相关信息
     */
    private MetricVOS metricVOS;

    public AppIndicatorVO(String appName, MetricVOS metricVOS) {
        this.appName = appName;
        this.metricVOS = metricVOS;
    }

    public AppIndicatorVO() {

    }
}