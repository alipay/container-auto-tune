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
 * @version MemDiagnosis.java, v 0.1 2022年10月25日 17:57 hongshu
 */
@Slf4j
public class MemDiagnosis {
    private static final double MEM_MAX_USE_HIGH_THRESHOLD = 0.9;
    private static final double MEM_AVE_USE_HIGH_THRESHOLD = 0.6;


    private static final double MEM_MAX_USE_LOW_THRESHOLD = 0.6;
    private static final double MEM_AVE_USE_LOW_THRESHOLD = 0.3;

    private static final int MEM_BURST_INTRENAL = 5;
    private static final double MEM_BURST_THRESHOLD = 0.3;

    // 内存碎片率
    private static final double MEM_FRAGMENT_HIGH_THRESHOLD = 0.9;

    // TLB命中率
    private static final double MEM_TLB_HIT_RATE_THRESHOLD = 0.3;

    // 大对象检测
    private static final double MEM_BIG_OBJ_RATE_THRESHOLD = 0.25;
    // 1g
    private static final long MEM_BIG_OBJ_SIZE_THRESHOLD = 1024 * 1024 *1024;

    // 对象数目检测 100w
    private static final long MEM_OBJ_NUM_THRESHOLD = 100000000L;
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_UTIL_HIGH }
     * @param memData
     * @return
     */
    public static boolean memUtilHigh(List<Double> memData){
        return BaseAlgorithm.max(memData)>MEM_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(memData,true)>MEM_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_UTIL_LOW }
     * @param memData
     * @return
     */
    public static boolean memUtilLow(List<Double> memData){
        return BaseAlgorithm.max(memData)<MEM_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(memData,true)<MEM_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_UTIL_BURST }
     * @param memData
     * @return
     */
    public static boolean memUtilBurst(List<Double> memData){
        return BaseAlgorithm.burst(memData, MEM_BURST_INTRENAL)>MEM_BURST_THRESHOLD;
    }

    /**
     * 内存泄漏检测
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_LEAK }
     * @param memData
     * @return
     */
    public static boolean memLeak(List<Double> memData){
        return false;
    }
    /**
     * 内存碎片率检测
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_FRAGMENT_HIGH }
     * @param fragmentData
     * @return
     */
    public static boolean memFragmentBurst(List<Double> fragmentData){
        return false;
    }

    /**
     * TLB缓存检测
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_TLB_MISS_HIGH }
     * @param hitRate
     * @return
     */
    public static boolean memTLBRate(List<Double> hitRate){
        return false;
    }


    /**
     * SWAP交换分区配置有问题
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_SWAP_OPEN }
     * @param isOff
     * @return
     */
    public static boolean memSwapOpen(boolean isOff){
        return false;
    }
    /**
     * 内存缓冲区使用情况
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_CACHE_ERROR }
     * @param cacheRate
     * @return
     */
    public static boolean memCacheError(List<Double> cacheRate){
        return false;
    }
    /**
     * 虚拟内存使用是否存在问题
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_VIRTUAL_ERROR }
     * @param virtualData
     * @return
     */
    public static boolean memVirtualError(List<Double> virtualData){
        return false;
    }

    /**
     * 大对象
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_BIG_OBJ }
     * @param
     * @return
     */
    public static boolean memBigObjeckCheck(double objSize, long heapSize){
        return objSize>MEM_BIG_OBJ_SIZE_THRESHOLD || objSize/heapSize>MEM_BIG_OBJ_RATE_THRESHOLD;
    }
    /**
     * 对象数目
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MEM_OBJ_COUNT_GREAT }
     * @param itemNum
     * @return
     */
    public static boolean memObjNumCheck(long itemNum){
        return itemNum > MEM_OBJ_NUM_THRESHOLD;
    }
}
