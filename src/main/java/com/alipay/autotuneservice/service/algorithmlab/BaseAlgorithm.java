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
package com.alipay.autotuneservice.service.algorithmlab;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class BaseAlgorithm {

    public static double max(List<Double> input){
        if(CollectionUtils.isEmpty(input)){
            return 0;
        }
        return input.stream().reduce(Double::max).get();
    }

    public static long maxLong(List<Long> input){
        if(CollectionUtils.isEmpty(input)){
            return 0;
        }
        return input.stream().reduce(Long::max).get();
    }

    public static double min(List<Double> input){
        if(CollectionUtils.isEmpty(input)){
            return 1;
        }
        return input.stream().reduce(Double::min).get();
    }

    public static double average(List<Double> input, boolean filter){
        if(CollectionUtils.isEmpty(input)){
            return 0;
        }
        return input.stream().filter(r -> r!=0.0).mapToDouble(r -> r).average().getAsDouble();
    }
    public static double averageLong(List<Long> input, boolean filter){
        if(CollectionUtils.isEmpty(input)){
            return 0;
        }
        return input.stream().filter(r -> r!=0.0).mapToDouble(r -> r).average().getAsDouble();
    }

    public static double sum(List<Double> input){
        return input.stream().mapToDouble(r -> r).sum();
    }

    public static double burst(List<Double> input, int interval) {
        if(input.size()<2){
            return 0.0f;
        }
        double maxDiff = 0.0f;
        double min = input.get(0);
        int benchLow = 0;
        int benchHigh = benchLow + interval;
        //init
        for(int i=benchLow; i<benchHigh && i<input.size() ; i++){
            double tmp = input.get(i);
            if(tmp<=min){
                min = tmp;
            }else {
                maxDiff = Math.max((tmp-min)/min, maxDiff);
            }
        }
        if(input.size()<=interval){
            return maxDiff;
        }

        // sliding filter
        while (benchHigh<=input.size()){
            // 1. check the date removed is min
            if(input.get(benchLow)==min){
                // recalculate the min data and index
                for(int j=benchLow+1; j<benchHigh; j++){
                    double tmp = input.get(j);
                    if(j==benchLow+1){
                        min = tmp;
                    }else {
                        min = Math.min(tmp,min);
                    }
                }
            }
            maxDiff = Math.max((input.get(benchHigh-1)-min)/min,maxDiff);
            benchLow++;
            benchHigh++;
        }
        return maxDiff;
    }

    /**
     * caclu percentile in array
     * @param filterData    for calulate array
     * @param p  the percent
     * @return  data
     */
    private static double getPercentile(List<Double> filterData, float p) {
        if(CollectionUtils.isNotEmpty(filterData)){
            //从小到大排序
            filterData.sort((o1, o2) -> o1 < o2 ? 1 : -1);
            double px =  p*(filterData.size()-1);
            int i = (int)Math.floor(px);
            double g = px - i;
            if(px-i == 0){
                return filterData.get(i);
            }else {
                return (float) ((1-g)*filterData.get(i)+g*filterData.get(i+1));
            }
        }
        return 0;
    }

}
