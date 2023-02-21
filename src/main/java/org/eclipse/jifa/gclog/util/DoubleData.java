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
package org.eclipse.jifa.gclog.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_DOUBLE;

public class DoubleData {

    private int n = 0;
    private double sum = 0;
    private double min = Double.MAX_VALUE;
    // min value of double is not Double.MIN_VALUE
    private double max = -Double.MAX_VALUE;
    private List<Double> originalData;
    private boolean dataSorted;

    public DoubleData(boolean recordOriginalData) {
        // recording all data is expensive, only do it if necessary
        if (recordOriginalData) {
            originalData = new ArrayList<>();
        }
    }

    public DoubleData() {
        this(false);
    }

    public double getMedian() {
        return getPercentile(0.5);
    }

    public double getPercentile(double percentile) {
        // should not call this method if originalData is null
        if (originalData.size() == 0) {
            return UNKNOWN_DOUBLE;
        }
        if (!dataSorted) {
            Collections.sort(originalData);
            dataSorted = true;
        }

        double p = (n - 1) * percentile;
        int i = (int) Math.floor(p);
        double weight = p - i;
        if (weight == 0) {
            return originalData.get(i);
        } else {
            return weight * originalData.get(i + 1) + (1 - weight) * originalData.get(i);
        }
    }

    public void add(double x) {
        if (x == UNKNOWN_DOUBLE) {
            return;
        }
        if (originalData != null) {
            originalData.add(x);
            dataSorted = false;
        }
        sum += x;
        n++;
        min = Math.min(min, x);
        max = Math.max(max, x);
    }

    public int getN() {
        return n;
    }

    public double getSum() {
        if (n == 0) {
            return UNKNOWN_DOUBLE;
        }
        return sum;
    }

    public double getMin() {
        if (n == 0) {
            return UNKNOWN_DOUBLE;
        }
        return min;
    }

    public double getMax() {
        if (n == 0) {
            return UNKNOWN_DOUBLE;
        }
        return max;
    }

    public double average() {
        if (n == 0) {
            return UNKNOWN_DOUBLE;
        }
        return sum / n;
    }
}
