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
package com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis;


import com.alipay.autotuneservice.service.algorithmlab.BaseAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hongshu
 * @version CpuDiagnosis.java, v 0.1 2022年10月25日 17:57 hongshu
 */
@Slf4j
public class CpuDiagnosis {
    private static final double CPU_MAX_USE_HIGH_THRESHOLD = 0.9;
    private static final double CPU_AVE_USE_HIGH_THRESHOLD = 0.6;
    private static final double CPU_MAX_USE_LOW_THRESHOLD = 0.6;
    private static final double CPU_AVE_USE_LOW_THRESHOLD = 0.3;

    private static final double CPU_USER_MAX_USE_HIGH_THRESHOLD = 0.8;
    private static final double CPU_USER_AVE_USE_HIGH_THRESHOLD = 0.5;
    private static final double CPU_USER_MAX_USE_LOW_THRESHOLD = 0.4;
    private static final double CPU_USER_AVE_USE_LOW_THRESHOLD = 0.3;

    private static final int BURST_INTRENAL = 5;
    private static final double CPU_BURST_THRESHOLD = 0.3;

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#CPU_UTIL_HIGH }
     * @param cpuData
     * @return
     */
    public static boolean cpuUtilHigh(List<Double> cpuData){
        return BaseAlgorithm.max(cpuData)>CPU_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(cpuData,true)>CPU_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#CPU_UTIL_LOW }
     * @param cpuData
     * @return
     */
    public static boolean cpuUtilLow(List<Double> cpuData){
        return BaseAlgorithm.max(cpuData)<CPU_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(cpuData,true)<CPU_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#CPU_UTIL_BURST }
     * @param cpuData
     * @return
     */
    public static boolean cpuUtilBurst(List<Double> cpuData){
        return BaseAlgorithm.burst(cpuData,BURST_INTRENAL)<CPU_BURST_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#CPU_USER_UTIL_HIGH }
     * @param cpuData
     * @return
     */
    public static boolean cpuUserUtilHigh(List<Double> cpuData){
        return BaseAlgorithm.max(cpuData)>CPU_USER_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(cpuData,true)>CPU_USER_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#CPU_USER_UTIL_LOW }
     * @param cpuData
     * @return
     */
    public static boolean cpuUserUtilLow(List<Double> cpuData){
        return BaseAlgorithm.max(cpuData)<CPU_USER_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(cpuData,true)<CPU_USER_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#CPU_USER_UTIL_BURST }
     * @param cpuData
     * @return
     */
    public static boolean cpuUserUtilBurst(List<Double> cpuData){
        return BaseAlgorithm.burst(cpuData,BURST_INTRENAL)<CPU_BURST_THRESHOLD;
    }
}
