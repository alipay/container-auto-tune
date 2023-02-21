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
package org.eclipse.jifa.gclog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseStatistics {
    private List<ParentStatisticsInfo> parents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentStatisticsInfo {
        private PhaseStatisticItem       self;
        private List<PhaseStatisticItem> phases;
        private List<PhaseStatisticItem> causes;
    }

    @Data
    @NoArgsConstructor
    public static class PhaseStatisticItem {
        private String name;
        private int    count;
        private double intervalAvg;
        private double intervalMin;
        private double durationAvg;
        private double durationMax;
        private double durationTotal;
        private String unicode;

        public PhaseStatisticItem(String name, int count, double intervalAvg, double intervalMin, double durationAvg, double durationMax,
                                  double durationTotal) {
            this.name = name;
            this.count = count;
            this.intervalAvg = intervalAvg;
            this.intervalMin = intervalMin;
            this.durationAvg = durationAvg;
            this.durationMax = durationMax;
            this.durationTotal = durationTotal;
        }

    }
}
