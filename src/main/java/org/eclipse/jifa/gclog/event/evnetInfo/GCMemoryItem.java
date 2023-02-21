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
package org.eclipse.jifa.gclog.event.evnetInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static org.eclipse.jifa.gclog.util.Constant.KB2MB;
import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_INT;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GCMemoryItem {
    private MemoryArea area;

    // memory size in kb
    private long preUsed = UNKNOWN_INT;
    private long postUsed = UNKNOWN_INT;
    private long total = UNKNOWN_INT;

    public GCMemoryItem(MemoryArea area) {
        this.area = area;
    }


    public GCMemoryItem(MemoryArea area, long[] memories) {
        this(area, memories[0], memories[1], memories[2]);
    }

    public long getMemoryReduction() {
        return minus(preUsed, postUsed);
    }

    /**
     * unknown value in this or anotherItem will lead result to be unknown.
     */
    public GCMemoryItem merge(GCMemoryItem anotherItem) {
        if (anotherItem == null) {
            return new GCMemoryItem(area, UNKNOWN_INT, UNKNOWN_INT, UNKNOWN_INT);
        }
        return new GCMemoryItem(area,
                plus(preUsed, anotherItem.preUsed),
                plus(postUsed, anotherItem.postUsed),
                plus(total, anotherItem.total));
    }

    /**
     * unknown value in this will lead result to be unknown.
     * unknown value in anotherItem are seen as 0
     */
    public GCMemoryItem mergeIfPresent(GCMemoryItem anotherItem) {
        if (anotherItem == null) {
            return this;
        }
        return new GCMemoryItem(area,
                plusIfPresent(preUsed, anotherItem.preUsed),
                plusIfPresent(postUsed, anotherItem.postUsed),
                plusIfPresent(total, anotherItem.total));
    }

    /**
     * unknown value in this or anotherItem will lead result to be unknown.
     */
    public GCMemoryItem subtract(GCMemoryItem anotherItem) {
        if (anotherItem == null) {
            return new GCMemoryItem(area, UNKNOWN_INT, UNKNOWN_INT, UNKNOWN_INT);
        }
        return new GCMemoryItem(area,
                minus(preUsed, anotherItem.preUsed),
                minus(postUsed, anotherItem.postUsed),
                minus(total, anotherItem.total));
    }

    /**
     * unknown value in this will lead result to be unknown.
     * unknown value in anotherItem are seen as 0
     */
    public GCMemoryItem subtractIfPresent(GCMemoryItem anotherItem) {
        if (anotherItem == null) {
            return this;
        }
        return new GCMemoryItem(area,
                minusIfPresent(preUsed, anotherItem.preUsed),
                minusIfPresent(postUsed, anotherItem.postUsed),
                minusIfPresent(total, anotherItem.total));
    }

    public GCMemoryItem updateIfAbsent(GCMemoryItem anotherItem) {
        if (anotherItem == null) {
            return this;
        }
        return new GCMemoryItem(area,
                preUsed == UNKNOWN_INT ? anotherItem.preUsed : preUsed,
                postUsed == UNKNOWN_INT ? anotherItem.postUsed : postUsed,
                total == UNKNOWN_INT ? anotherItem.total : total);
    }

    private static long plus(long x, long y) {
        if (x == UNKNOWN_INT || y == UNKNOWN_INT) {
            return UNKNOWN_INT;
        }
        return x + y;
    }

    private static long plusIfPresent(long x, long y) {
        if (x == UNKNOWN_INT || y == UNKNOWN_INT) {
            return x;
        }
        return x + y;
    }

    private static long minus(long x, long y) {
        if (x == UNKNOWN_INT || y == UNKNOWN_INT) {
            return UNKNOWN_INT;
        }
        return x - y;
    }

    private static long minusIfPresent(long x, long y) {
        if (x == UNKNOWN_INT || y == UNKNOWN_INT) {
            return x;
        }
        return x - y;
    }

    public void multiply(long x) {
        if (preUsed != UNKNOWN_INT) {
            preUsed *= x;
        }
        if (postUsed != UNKNOWN_INT) {
            postUsed *= x;
        }
        if (total != UNKNOWN_INT) {
            total *= x;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GCMemoryItem that = (GCMemoryItem) o;
        return preUsed == that.preUsed && postUsed == that.postUsed && total == that.total && area == that.area;
    }

    @Override
    public int hashCode() {
        return Objects.hash(area, preUsed, postUsed, total);
    }

    public boolean isEmpty() {
        return preUsed == UNKNOWN_INT && postUsed == UNKNOWN_INT && total == UNKNOWN_INT;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String area = this.area.toString().toLowerCase();
        sb.append((char) (area.charAt(0) - 32)).append(area.substring(1)).append(": ");
        if (isEmpty()) {
            sb.append("unknown");
        } else {
            if (preUsed != UNKNOWN_INT) {
                sb.append((long) (Math.max(0, preUsed) / KB2MB / KB2MB)).append("M->");
            }
            if (postUsed != UNKNOWN_INT) {
                sb.append((long) (Math.max(0, postUsed) / KB2MB / KB2MB)).append('M');
            } else {
                sb.append("unknown");
            }
            if (total != UNKNOWN_INT) {
                sb.append('(').append((long) (Math.max(0, total) / KB2MB / KB2MB)).append("M)");
            }
        }
        return sb.toString();
    }
}
