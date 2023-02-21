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
package com.alipay.autotuneservice.schedule.riskstatistic;

import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.PodFeaturePreData;
import com.alipay.autotuneservice.dynamodb.bean.RiskStatisticPreData;
import com.alipay.autotuneservice.model.ProblemContent;
import com.alipay.autotuneservice.model.common.HealthCheckEnum;
import com.alipay.autotuneservice.model.common.RiskStatisticProEnum;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.util.GsonUtil;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fangxueyang
 * @version RiskCheckPreData.java, v 0.1 2022年08月15日 17:37 hongshu
 */
@Data
public class PodFeaturePreDataPack implements Serializable {

    private static final float P99 = 0.99f;
    // length of statistical interval
    private static final int INCREASE_RATE_INTERVAL = 5;


    public static long calcFgcCount(Integer appId, List<JvmMonitorMetricData> list){
        return list.stream()
                .filter(r -> (Objects.equals(appId, r.getAppId()) && r.getFgc_count()>0))
                .count();
    }
    public static long calcFgcSum(Integer appId, List<JvmMonitorMetricData> list){
        return list.stream()
                .filter(r -> (Objects.equals(appId, r.getAppId()) && r.getFgc_count()>0))
                .mapToLong(JvmMonitorMetricData::getFgc_count)
                .sum();
    }

    public static float calcFgcTime(Integer appId, List<JvmMonitorMetricData> list){
        OptionalDouble opd = list.stream()
                .filter(r -> (Objects.equals(appId, r.getAppId()) && r.getFgc_time()>0))
                .mapToDouble(JvmMonitorMetricData::getFgc_time)
                .average();
        if(opd.isPresent()){
            return (float) opd.getAsDouble();
        }
        return 0;
    }

    public static float calcFgcCountP99(Integer appId, List<JvmMonitorMetricData> list){
        List<JvmMonitorMetricData> filterData = list.stream()
                .filter(r -> (Objects.equals(appId, r.getAppId()) && r.getFgc_count()>0)).collect(Collectors.toList());
        return getPercentile(filterData,P99);
    }

    /**
     * caclu percentile in array
     * @param filterData    for calulate array
     * @param p  the percent
     * @return  data
     */
    private static float getPercentile(List<JvmMonitorMetricData> filterData, float p) {
        if(CollectionUtils.isNotEmpty(filterData)){
            //从小到大排序
            filterData.sort((o1, o2) -> o1.getFgc_count() < o2.getFgc_count() ? 1 : -1);
            double px =  p*(filterData.size()-1);
            int i = (int)Math.floor(px);
            double g = px - i;
            if(px-i == 0){
                return filterData.get(i).getFgc_count();
            }else {
                return (float) ((1-g)*filterData.get(i).getFgc_count()+g*filterData.get(i+1).getFgc_count());
            }
        }
        return 0;
    }

    public static float calcMetaUtilMean(Integer appId, List<JvmMonitorMetricData> list){
        return  (float) list.stream()
                .filter(r -> (Objects.equals(appId, r.getAppId())))
                .mapToDouble(JvmMonitorMetricData::getMeta_util)
                .average().getAsDouble();
    }

    /**
     * memory burst detection
     *
     * @param appId
     * @param list
     * @return
     */
    public static float calcIncreaseRate(Integer appId, List<JvmMonitorMetricData> list){
        List<JvmMonitorMetricData> filterList = list.stream()
                .filter(r -> (Objects.equals(appId, r.getAppId()) && r.getOld_util()>0))
                .collect(Collectors.toList());
        return  (float) calcIncreaseRateByInterval(filterList, INCREASE_RATE_INTERVAL);
    }

    private static double calcIncreaseRateByInterval(List<JvmMonitorMetricData> filterList, int interval) {
        if(filterList.size()<2){
            return 0.0f;
        }
        double maxDiff = 0.0f;
        double min = filterList.get(0).getOld_util();
        int benchLow = 0;
        int benchHigh = benchLow + interval;
        //init
        for(int i=benchLow; i<benchHigh && i<filterList.size() ; i++){
            double tmp = filterList.get(i).getOld_util();
            if(tmp<=min){
                min = tmp;
            }else {
                maxDiff = Math.max((tmp-min)/min, maxDiff);
            }
        }
        if(filterList.size()<=interval){
            return maxDiff;
        }

        // sliding filter
        while (benchHigh<=filterList.size()){
            // 1. check the date removed is min
            if(filterList.get(benchLow).getOld_util()==min){
                // recalculate the min data and index
                for(int j=benchLow+1; j<benchHigh; j++){
                    double tmp = filterList.get(j).getOld_util();
                    if(j==benchLow+1){
                        min = tmp;
                    }else {
                        min = Math.min(tmp,min);
                    }
                }
            }
            maxDiff = Math.max((filterList.get(benchHigh-1).getOld_util()-min)/min,maxDiff);
            benchLow++;
            benchHigh++;
        }
        return maxDiff;
    }

    /**
     * evaluate all indicators problem,
     * if more and more indicators, split later
     * @param garbageCollector
     * @param rspd
     * @return
     */
    public static String evaluteProblem(GarbageCollector garbageCollector,List<RiskStatisticPreData> rspd){
        if(CollectionUtils.isEmpty(rspd)){
            return null;
        }
        Map<String, ProblemContent> proMap = Maps.newHashMap();
        rspd.stream().filter(s -> s.getPodFeatureDict()!=null).forEach(r -> {
            PodFeaturePreData  preData = r.getPodFeatureDict();
            // FGC_COUNT
            RiskStatisticProEnum enumTmp = RiskStatisticProEnum.enumFromCollectorAndType(garbageCollector, HealthCheckEnum.FGC_COUNT);
            proMap.computeIfAbsent(enumTmp.name(), t -> {
                float thold = Float.parseFloat(enumTmp.getThreshold());
                if((GarbageCollector.CMS_GARBAGE_COLLECTOR==garbageCollector && preData.getFgcCountP99()>thold)
                        || (GarbageCollector.G1_GARBAGE_COLLECTOR==garbageCollector && preData.getFgcCount()>thold)){
                    return ProblemContent.newInstance().bdProblem_type(HealthCheckEnum.FGC_COUNT.name()).bdProblem_pod(null).bdProblem_text(enumTmp.getDesc());
                }
                return null;
            });

            // OLD_UTIL HEAP_MEMORY FGC_TIME G1_GARBAGE_COLLECTOR
            Map<HealthCheckEnum,Float> checkEnumFloatMap = new HashMap<HealthCheckEnum,Float>(){{
                put(HealthCheckEnum.OLD_UTIL,preData.getIncreaseRate());
                put(HealthCheckEnum.HEAP_MEMORY,preData.getMetaUtilMean());
                put(HealthCheckEnum.FGC_TIME,preData.getFgcTime());
            }};
            if(GarbageCollector.G1_GARBAGE_COLLECTOR == garbageCollector){
                checkEnumFloatMap.put(HealthCheckEnum.HEAP_META,preData.getMetaUtilMean());
            }
            commonEvalute(proMap,garbageCollector,checkEnumFloatMap);

            // HEAP_OLD_IDLE
            if(GarbageCollector.G1_GARBAGE_COLLECTOR == garbageCollector){
                RiskStatisticProEnum enumHeapMem = RiskStatisticProEnum.enumFromCollectorAndType(garbageCollector, HealthCheckEnum.HEAP_OLD);
                proMap.computeIfAbsent(enumHeapMem.name(), t -> {
                    if(preData.getFgcCount()<=3 && preData.getFgcSum()<=5){
                        return ProblemContent.newInstance().bdProblem_type(HealthCheckEnum.HEAP_OLD.name()).bdProblem_pod(null).bdProblem_text(enumTmp.getDesc());
                    }
                    return null;
                });
            }
        });
        return GsonUtil.toJson(proMap);
    }

    private static void commonEvalute(Map<String, ProblemContent> proMap, GarbageCollector garbageCollector, Map<HealthCheckEnum, Float> healthCheckEnumMap) {
        healthCheckEnumMap.forEach((r,s) -> {
            RiskStatisticProEnum enumTmp = RiskStatisticProEnum.enumFromCollectorAndType(garbageCollector, r);
            proMap.computeIfAbsent(enumTmp.name(), t -> {
                if((enumTmp.isCompType() && s>Float.parseFloat(enumTmp.getThreshold()))
                        || (!enumTmp.isCompType() && s<Float.parseFloat(enumTmp.getThreshold()))){
                    return ProblemContent.newInstance().bdProblem_type(r.name()).bdProblem_pod(null).bdProblem_text(enumTmp.getDesc());
                }
                return null;
            });
        });
    }


}