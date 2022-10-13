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

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fangxueyang
 * @version RiskCheckPreData.java, v 0.1 2022年08月15日 17:37 hongshu
 */
@Data
public class PodFeaturePreData implements Serializable {
    /**
     * number of reports contain fgc
     */
    private long  fgcCount;
    /**
     * all count one day
     */
    private long  fgcSum;
    /**
     * Surge monitoring，
     */
    private float increaseRate;
    /**
     * average meta used
     */
    private float metaUtilMean;
    /**
     * average fgc time
     */
    private float fgcTime;

    private float fgcCountP99;

    public PodFeaturePreData bdFgcCount(long fgcCount) {
        this.fgcCount = fgcCount;
        return this;
    }

    public PodFeaturePreData bdFgcSum(long fgcSum) {
        this.fgcSum = fgcSum;
        return this;
    }

    public PodFeaturePreData bdIncreaseRate(float increaseRate) {
        this.increaseRate = increaseRate;
        return this;
    }

    public PodFeaturePreData bdMetaUtilMean(float metaUtilMean) {
        this.metaUtilMean = metaUtilMean;
        return this;
    }

    public PodFeaturePreData bdFgcTime(float fgcTime) {
        this.fgcTime = fgcTime;
        return this;
    }

    public PodFeaturePreData bdFgcCountP99(float fgcCountP99) {
        this.fgcCountP99 = fgcCountP99;
        return this;
    }

    public static PodFeaturePreData newInstance() {
        return new PodFeaturePreData();
    }
}