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
package com.alipay.autotuneservice.controller.model.meter;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huangkaifei
 * @version : MeterMetricValue.java, v 0.1 2022年08月25日 2:17 PM huangkaifei Exp $
 */
@Data
@Builder
public class MeterMetricResult implements Serializable {

    private static final long serialVersionUID = 3405336410402514633L;

    /**
     * metric name
     */
    private String metricName;
    /**
     * query metric value by specific PromQL
     */
    private String metricResult;

}