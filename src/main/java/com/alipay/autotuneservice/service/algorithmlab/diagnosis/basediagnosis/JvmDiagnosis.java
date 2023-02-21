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
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hongshu
 * @version JvmDiagnosis.java, v 0.1 2022年10月25日 17:57 hongshu
 */
@Slf4j
public class JvmDiagnosis {
    private static final int BURST_INTRENAL = 5;
    private static final double YGC_COUNT_HIGH_THRESHOLD = 900;
    private static final double YGC_COUNT_BURST_THRESHOLD = 0.3;
    private static final double YGC_TIME_GREAT_THRESHOLD = 100;
    private static final double FGC_COUNT_HIGH_THRESHOLD = 3;
    public static final double FGC_COUNT_SUM_HIGH_THRESHOLD = 900;
    public static final double FGC_COUNT_SUM_LOW_THRESHOLD = 5;
    private static final double FGC_COUNT_BURST_THRESHOLD = 0.3;
    private static final double FGC_TIME_GREAT_THRESHOLD = 100;

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#YGC_COUNT_HIGH }
     * @param ygcCountData
     * @return
     */
    public static boolean ygcCountHigh(List<Double> ygcCountData){
        return BaseAlgorithm.max(ygcCountData)>YGC_COUNT_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#YGC_COUNT_BURST }
     * @param ygcCountData
     * @return
     */
    public static boolean ygcCountBurst(List<Double> ygcCountData){
        return BaseAlgorithm.burst(ygcCountData,BURST_INTRENAL)<YGC_COUNT_BURST_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#YGC_TIME_GREAT }
     * @param ygcTimeData
     * @return
     */
    public static boolean ygcTimeGreat(List<Double> ygcTimeData){
        return BaseAlgorithm.max(ygcTimeData)>YGC_TIME_GREAT_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#FGC_COUNT_HIGH }
     * @param fgcCountData
     * @return
     */
    public static boolean fgcCountHigh(List<Double> fgcCountData){
        return BaseAlgorithm.max(fgcCountData)>FGC_COUNT_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#FGC_COUNT_BURST }
     * @param fgcCountData
     * @return
     */
    public static boolean fgcCountBurst(List<Double> fgcCountData){
        return BaseAlgorithm.burst(fgcCountData,BURST_INTRENAL)<FGC_COUNT_BURST_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#FGC_TIME_GREAT }
     * @param fgcTimeData
     * @return
     */
    public static boolean fgcTimeGreat(List<Double> fgcTimeData){
        return BaseAlgorithm.max(fgcTimeData)>FGC_TIME_GREAT_THRESHOLD;
    }

    /**
     *  空间利用率
     */
    private static final double HEAP_MAX_USE_HIGH_THRESHOLD = 0.9;
    private static final double HEAP_AVE_USE_HIGH_THRESHOLD = 0.6;
    private static final double HEAP_MAX_USE_LOW_THRESHOLD = 0.6;
    private static final double HEAP_AVE_USE_LOW_THRESHOLD = 0.3;
    private static final double HEAP_BURST_THRESHOLD = 0.3;

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#HEAP_UTIL_HIGH }
     * @param heapData
     * @return
     */
    public static boolean heapUtilHigh(List<Double> heapData){
        return BaseAlgorithm.max(heapData)>HEAP_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(heapData,true)>HEAP_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#HEAP_UTIL_LOW }
     * @param heapData
     * @return
     */
    public static boolean heapUtilLow(List<Double> heapData){
        return BaseAlgorithm.max(heapData)<HEAP_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(heapData,true)<HEAP_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#HEAP_UTIL_BURST }
     * @param heapData
     * @return
     */
    public static boolean heapUtilBurst(List<Double> heapData){
        return BaseAlgorithm.burst(heapData,BURST_INTRENAL)<HEAP_BURST_THRESHOLD;
    }


    private static final double OLD_MAX_USE_HIGH_THRESHOLD = 0.9;
    private static final double OLD_AVE_USE_HIGH_THRESHOLD = 0.6;
    private static final double OLD_MAX_USE_LOW_THRESHOLD = 0.6;
    private static final double OLD_AVE_USE_LOW_THRESHOLD = 0.3;
    private static final double OLD_BURST_THRESHOLD = 0.3;
    public static final long OLD_PROMOTION_BURST_THRESHOLD = 1000000000L;

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#OLD_UTIL_HIGH }
     * @param oldUtilData
     * @return
     */
    public static boolean oldUtilHigh(List<Double> oldUtilData){
        return BaseAlgorithm.max(oldUtilData)>OLD_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(oldUtilData,true)>OLD_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#OLD_UTIL_LOW }
     * @param oldUtilData
     * @return
     */
    public static boolean oldUtilLow(List<Double> oldUtilData){
        return BaseAlgorithm.max(oldUtilData)<OLD_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(oldUtilData,true)<OLD_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#OLD_UTIL_BURST }
     * @param oldUtilData
     * @return
     */
    public static boolean oldUtilBurst(List<Double> oldUtilData){
        return BaseAlgorithm.burst(oldUtilData,BURST_INTRENAL)<OLD_BURST_THRESHOLD;
    }


    private static final double YOUNG_MAX_USE_HIGH_THRESHOLD = 0.9;
    private static final double YOUNG_AVE_USE_HIGH_THRESHOLD = 0.6;
    private static final double YOUNG_MAX_USE_LOW_THRESHOLD = 0.6;
    private static final double YOUNG_AVE_USE_LOW_THRESHOLD = 0.3;
    private static final double YOUNG_BURST_THRESHOLD = 0.3;

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#YOUNG_UTIL_HIGH }
     * @param youngUtilData
     * @return
     */
    public static boolean youngUtilHigh(List<Double> youngUtilData){
        return BaseAlgorithm.max(youngUtilData)>YOUNG_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(youngUtilData,true)>YOUNG_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#YOUNG_UTIL_LOW }
     * @param youngUtilData
     * @return
     */
    public static boolean youngUtilLow(List<Double> youngUtilData){
        return BaseAlgorithm.max(youngUtilData)<YOUNG_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(youngUtilData,true)<YOUNG_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#YOUNG_UTIL_BURST }
     * @param youngUtilData
     * @return
     */
    public static boolean youngUtilBurst(List<Double> youngUtilData){
        return BaseAlgorithm.burst(youngUtilData,BURST_INTRENAL)<YOUNG_BURST_THRESHOLD;
    }



    private static final double META_MAX_USE_HIGH_THRESHOLD = 0.9;
    private static final double META_AVE_USE_HIGH_THRESHOLD = 0.6;
    private static final double META_MAX_USE_LOW_THRESHOLD = 0.6;
    private static final double META_AVE_USE_LOW_THRESHOLD = 0.3;
    private static final double META_BURST_THRESHOLD = 0.3;
    public static final double STOP_THE_WORLD_LONG_THRESHOLD = 5000;
    private static final double CODE_CACHE_SIZE_LOW_THRESHOLD = 0.3;
    private static final double CODE_CACHE_SIZE_HIGH_THRESHOLD = 0.75;
    private static final double CODE_CACHE_SIZE_AVERAGE_THRESHOLD = 0.6;

    // GCCollectorType choose: Kb
    public static final double GC_TYPE_CHOOSE_HEAP_THRESHOLD = 8*1024*1024;

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#META_UTIL_HIGH }
     * @param metaUtilData
     * @return
     */
    public static boolean metaUtilHigh(List<Double> metaUtilData){
        return BaseAlgorithm.max(metaUtilData)>META_MAX_USE_HIGH_THRESHOLD
                && BaseAlgorithm.average(metaUtilData,true)>META_AVE_USE_HIGH_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#META_UTIL_LOW }
     * @param metaUtilData
     * @return
     */
    public static boolean metaUtilLow(List<Double> metaUtilData){
        return BaseAlgorithm.max(metaUtilData)<META_MAX_USE_LOW_THRESHOLD
                && BaseAlgorithm.average(metaUtilData,true)<META_AVE_USE_LOW_THRESHOLD;
    }

    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#META_UTIL_BURST }
     * @param metaUtilData
     * @return
     */
    public static boolean metaUtilBurst(List<Double> metaUtilData){
        return BaseAlgorithm.burst(metaUtilData,BURST_INTRENAL)<META_BURST_THRESHOLD;
    }


    // collector type error
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#GC_TYPE_UNREASONABLE }
     * @param
     * @return
     */
    public static boolean gcTypeUnreasonable(GarbageCollector garbageCollector, double heapSize){
        return heapSize>GC_TYPE_CHOOSE_HEAP_THRESHOLD && garbageCollector==GarbageCollector.CMS_GARBAGE_COLLECTOR;
    }

    // oom
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#OUT_OF_MEMORY }
     * @param
     * @return
     */
    public static boolean oomError(){
        return false;
    }

    // jdk version
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#JDK_VERSION_LOW }
     * @param
     * @return
     */
    public static boolean jdkVersionLow(){
        return false;
    }

    // MaxGCPauseMillis
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MAX_GC_PAUSE_MILLIS_UNREASONABLE }
     * @param
     * @return
     */
    public static boolean maxGcPauseMillis(){
        return false;
    }

    // Xmx_Xms_DIFF
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#Xmx_Xms_DIFF }
     * @param
     * @return
     */
    public static boolean xmxXmsDiff(){
        return false;
    }

    // MaxNewSize_NewSize_DIFF
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MaxNewSize_NewSize_DIFF }
     * @param
     * @return
     */
    public static boolean maxNewSizeDiff(){
        return false;
    }

    // MetaSpaceSize_MaxMetaSpaceSize_DIFF
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#MetaSpaceSize_MaxMetaSpaceSize_DIFF }
     * @param
     * @return
     */
    public static boolean maxMetaSpaceSizeDiff(){
        return false;
    }

    // PrintGCApplicationStoppedTime_LOSS
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#PrintGCApplicationStoppedTime_LOSS }
     * @param
     * @return
     */
    public static boolean stopTimePrintLoss(){
        return false;
    }

    // STOP_THE_WORLD_LONG
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#STOP_THE_WORLD_LONG }
     * @param
     * @return
     */
    public static boolean stopTheWorldLong(){
        return false;
    }

    // ReservedCodeCacheSize_SMALL
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#ReservedCodeCacheSize_SMALL }
     * @param
     * @return
     */
    public static boolean reservedCodeCacheSizeSmall(List<Double> codeCacheUtil){

        return BaseAlgorithm.max(codeCacheUtil)>CODE_CACHE_SIZE_HIGH_THRESHOLD
                && BaseAlgorithm.average(codeCacheUtil,true)>CODE_CACHE_SIZE_AVERAGE_THRESHOLD;
    }

    // SurvivorRatio_SMALL
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#SurvivorRatio_SMALL }
     * @param
     * @return
     */
    public static boolean survivorRatioSmall(){
        return false;
    }

    // G1ReservePercent_SMALL
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#G1ReservePercent_SMALL }
     * @param
     * @return
     */
    public static boolean G1ReservePercentSmall(){
        return false;
    }

    // SafePoint_long
    private static final double SAFE_POINT_THRESHOLD = 1000;
    /**
     * {@link com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum#SAFE_POINT_LONG }
     * @param
     * @return
     */
    public static boolean safePointLong(List<Double> safePointData){
        return false;
    }

}
