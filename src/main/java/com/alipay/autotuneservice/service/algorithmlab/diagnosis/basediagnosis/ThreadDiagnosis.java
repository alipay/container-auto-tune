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
 * @version ThreadDiagnosis.java, v 0.1 2022年10月25日 17:57 hongshu
 */
@Slf4j
public class ThreadDiagnosis {

    public static final double THREAD_COUNT_HIGH_MAX_THRESHOLD = 5000;
    public static final double THREAD_COUNT_HIGH_AVG_THRESHOLD = 5000;

    public static final double THREAD_DEADLOCK_HIGH_THRESHOLD = 5;


    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#THREAD_COUNT_HIGH }
     * @param threadCount
     * @return
     */
    public static boolean threadCountHigh(List<Double> threadCount){
        return BaseAlgorithm.max(threadCount)>THREAD_COUNT_HIGH_MAX_THRESHOLD
                && BaseAlgorithm.average(threadCount,true)>THREAD_COUNT_HIGH_AVG_THRESHOLD;
    }


    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#THREAD_DEADLOCK }
     * @param
     * @return
     */
    public static boolean threadDeadlock(List<Long> deadLockedCount){
        return BaseAlgorithm.maxLong(deadLockedCount)>THREAD_DEADLOCK_HIGH_THRESHOLD;
    }






}
