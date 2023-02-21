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

import java.math.BigInteger;

import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_DOUBLE;
import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_INT;

public class LongData {

    private int n;
    private BigInteger sum = BigInteger.ZERO;
    private long min = Integer.MAX_VALUE;
    private long max = Integer.MIN_VALUE;

    public void add(long x) {
        if (x == UNKNOWN_INT) {
            return;
        }
        sum = sum.add(BigInteger.valueOf(x));
        n++;
        min = Math.min(min, x);
        max = Math.max(max, x);
    }

    public int getN() {
        return n;
    }

    public long getSum() {
        if (n == 0) {
            return UNKNOWN_INT;
        }
        return sum.longValue();
    }

    public long getMin() {
        if (n == 0) {
            return UNKNOWN_INT;
        }
        return min;
    }

    public long getMax() {
        if (n == 0) {
            return UNKNOWN_INT;
        }
        return max;
    }

    public double average() {
        if (n == 0) {
            return UNKNOWN_DOUBLE;
        }
        return sum.divide(BigInteger.valueOf(n)).doubleValue();
    }
}
