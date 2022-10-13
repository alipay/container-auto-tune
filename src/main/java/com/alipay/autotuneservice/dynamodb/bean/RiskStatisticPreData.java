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
package com.alipay.autotuneservice.dynamodb.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fangxueyang
 * @version RiskCheckPreData.java, v 0.1 2022年08月15日 17:37 hongshu
 */
@Data
public class RiskStatisticPreData implements Serializable {
    private Integer           appId;

    /**
     * format: yyyyMMdd
     */
    private String            dt;

    private String            podName;

    private Long              ts;

    /**
     *  {@link com.alipay.autotuneservice.schedule.riskstatistic.RiskStatisticCaType }
     */
    private int               type;

    /**
     * pre process data
     */
    private PodFeaturePreData podFeatureDict;

    public RiskStatisticPreData bdAppId(Integer appId) {
        this.appId = appId;
        return this;
    }

    public RiskStatisticPreData bdDt(String dt) {
        this.dt = dt;
        return this;
    }

    public RiskStatisticPreData bdPodName(String podName) {
        this.podName = podName;
        return this;
    }

    public RiskStatisticPreData bdPodFeatureDict(PodFeaturePreData podFeatureDict) {
        this.podFeatureDict = podFeatureDict;
        return this;
    }

    public RiskStatisticPreData bdTs(Long ts) {
        this.ts = ts;
        return this;
    }

    public RiskStatisticPreData bdType(int type) {
        this.type = type;
        return this;
    }

    public static RiskStatisticPreData newInstance() {
        return new RiskStatisticPreData();
    }

}