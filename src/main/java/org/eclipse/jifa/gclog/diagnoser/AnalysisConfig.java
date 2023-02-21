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
package org.eclipse.jifa.gclog.diagnoser;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.vo.TimeRange;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class AnalysisConfig {
    /*
     * Notice: This class should be kept in sync with initializePage in GCLog.vue.
     */
    private TimeRange timeRange;
    private double longPauseThreshold;
    private double longConcurrentThreshold;
    private double youngGCFrequentIntervalThreshold;
    private double oldGCFrequentIntervalThreshold;
    private double fullGCFrequentIntervalThreshold;
    private double highOldUsageThreshold;
    private double highHumongousUsageThreshold;
    private double highHeapUsageThreshold;
    private double highMetaspaceUsageThreshold;
    private double smallGenerationThreshold;
    private double highPromotionThreshold;
    private double badThroughputThreshold;
    private double tooManyOldGCThreshold;

    // Basically mirror of analysisConfigModel in GCLog.vue, but this function is for testing and debugging.
    // No need to keep sync with frontend
    public static AnalysisConfig defaultConfig(GCModel model, Long startTime, Long endTime) {
        AnalysisConfig config = new AnalysisConfig();
        config.setTimeRange(new TimeRange(model.getStartTime(), model.getEndTime()));
        if(startTime != null && endTime != null){
            config.setTimeRange(new TimeRange(startTime, endTime));
        }
        config.setLongPauseThreshold(model.isPauseless() ? 30 : 400);
        config.setLongConcurrentThreshold(30000);
        config.setYoungGCFrequentIntervalThreshold(1000);
        config.setOldGCFrequentIntervalThreshold(15000);
        config.setFullGCFrequentIntervalThreshold(model.isGenerational() ? 60000 : 2000);
        config.setHighOldUsageThreshold(80);
        config.setHighHumongousUsageThreshold(50);
        config.setHighHeapUsageThreshold(60);
        config.setHighMetaspaceUsageThreshold(80);
        config.setSmallGenerationThreshold(10);
        config.setHighPromotionThreshold(3);
        config.setBadThroughputThreshold(90);
        config.setTooManyOldGCThreshold(20);
        return config;
    }





}
