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
 * @version DiskDiagnosis.java, v 0.1 2022年10月25日 17:57 hongshu
 */
@Slf4j
public class DiskDiagnosis {
    private static final double DISK_MAX_USE_HIGH_THRESHOLD = 0.9;
    private static final double DISK_AVE_USE_HIGH_THRESHOLD = 0.6;
    private static final double DISK_MAX_USE_LOW_THRESHOLD = 0.9;
    private static final double DISK_AVE_USE_LOW_THRESHOLD = 0.6;
    private static final double DISK_IO_MAX_USE_HIGH_THRESHOLD = 0.9;
    private static final double DISK_IO_AVE_USE_HIGH_THRESHOLD = 0.6;
    private static final double DISK_IO_MAX_USE_LOW_THRESHOLD = 0.9;
    private static final double DISK_IO_AVE_USE_LOW_THRESHOLD = 0.6;
    private static final int DISK_BURST_INTRENAL = 5;
    private static final double DISK_BURST_THRESHOLD = 0.3;


    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#DISK_UTIL_HIGH }
     * @param diskData
     * @return
     */
    public static boolean diskUtilHigh(List<Double> diskData){
        return BaseAlgorithm.max(diskData)>DISK_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(diskData,true)>DISK_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#DISK_UTIL_LOW }
     * @param diskData
     * @return
     */
    public static boolean diskUtilLow(List<Double> diskData){
        return BaseAlgorithm.max(diskData)<DISK_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(diskData,true)<DISK_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#DISK_IO_UTIL_HIGH }
     * @param diskData
     * @return
     */
    public static boolean diskIOUtilHigh(List<Double> diskData){
        return BaseAlgorithm.max(diskData)>DISK_IO_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(diskData,true)>DISK_IO_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#DISK_IO_UTIL_LOW }
     * @param diskData
     * @return
     */
    public static boolean diskIOUtilLow(List<Double> diskData){
        return BaseAlgorithm.max(diskData)<DISK_IO_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(diskData,true)<DISK_IO_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#DISK_IO_UTIL_BURST }
     * @param diskData
     * @return
     */
    public static boolean diskIOUtilBurst(List<Double> diskData){
        return BaseAlgorithm.burst(diskData,DISK_BURST_INTRENAL)>DISK_BURST_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#DISK_IO_UTIL_REDUCE }
     * @param diskData
     * @return
     */
    public static boolean diskIOUtilReduce(List<Double> diskData){
        return false;
    }






}
