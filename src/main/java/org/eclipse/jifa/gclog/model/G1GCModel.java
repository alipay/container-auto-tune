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
package org.eclipse.jifa.gclog.model;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import org.eclipse.jifa.gclog.event.GCEvent;
import org.eclipse.jifa.gclog.event.evnetInfo.GCMemoryItem;
import org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea;
import org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType;
import org.eclipse.jifa.gclog.model.modeInfo.GCLogStyle;
import org.eclipse.jifa.gclog.util.LongData;
import org.eclipse.jifa.gclog.vo.TimeRange;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.HEAP;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.HUMONGOUS;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.METASPACE;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.OLD;
import static org.eclipse.jifa.gclog.model.GCEventType.FULL_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_CYCLE;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_MARK;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_MARK_ABORT;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_REBUILD_REMEMBERED_SETS;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_MIXED_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_PAUSE_CLEANUP;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_REMARK;
import static org.eclipse.jifa.gclog.model.GCEventType.YOUNG_GC;
import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_INT;

public class G1GCModel extends GCModel {
    private long heapRegionSize = UNKNOWN_INT;   // in b
    private boolean regionSizeExact = false;
    private static GCCollectorType collector = GCCollectorType.G1;

    public void setRegionSizeExact(boolean regionSizeExact) {
        this.regionSizeExact = regionSizeExact;
    }

    public void setHeapRegionSize(long heapRegionSize) {
        this.heapRegionSize = heapRegionSize;
    }

    public long getHeapRegionSize() {
        return heapRegionSize;
    }


    public G1GCModel() {
        super(collector);
    }

    private static List<GCEventType> allEventTypes = GCModel.calcAllEventTypes(collector);
    private static List<GCEventType> pauseEventTypes = GCModel.calcPauseEventTypes(collector);
    private static List<GCEventType> mainPauseEventTypes = GCModel.calcMainPauseEventTypes(collector);
    private static List<GCEventType> parentEventTypes = GCModel.calcParentEventTypes(collector);
    private static List<GCEventType> importantEventTypes = Lists.newArrayList(YOUNG_GC, G1_MIXED_GC, FULL_GC, G1_CONCURRENT_CYCLE,
            G1_CONCURRENT_MARK, G1_REMARK, G1_CONCURRENT_REBUILD_REMEMBERED_SETS, G1_PAUSE_CLEANUP);

    @Override
    protected List<GCEventType> getAllEventTypes() {
        return allEventTypes;
    }

    @Override
    protected List<GCEventType> getPauseEventTypes() {
        return pauseEventTypes;
    }

    @Override
    protected List<GCEventType> getMainPauseEventTypes() {
        return mainPauseEventTypes;
    }

    @Override
    protected List<GCEventType> getImportantEventTypes() {
        return importantEventTypes;
    }

    @Override
    protected List<GCEventType> getParentEventTypes() {
        return parentEventTypes;
    }


    private boolean collectionResultUsingRegion(GCEvent event) {
        GCEventType type = event.getEventType();
        return (type == YOUNG_GC || type == FULL_GC || type == G1_MIXED_GC) && event.getMemoryItems() != null;
    }

    private void inferHeapRegionSize() {
        if (heapRegionSize != UNKNOWN_INT) {
            return;
        }
        for (int i = getGcEvents().size() - 1; i >= 0; i--) {
            GCEvent event = getGcEvents().get(i);
            if (!collectionResultUsingRegion(event)) {
                continue;
            }
            if (event.getMemoryItem(HEAP).getPreUsed() == UNKNOWN_INT) {
                continue;
            }
            long regionCount = Arrays.stream(event.getMemoryItems())
                    .filter(item -> item != null && item.getArea() != METASPACE && item.getArea() != HEAP)
                    .mapToLong(GCMemoryItem::getPreUsed)
                    .sum();
            double bytesPerRegion = event.getMemoryItem(HEAP).getPreUsed() / (double) regionCount;
            heapRegionSize = (int) Math.pow(2, Math.ceil(Math.log(bytesPerRegion) / Math.log(2)));
            return;
        }
    }

    private void adjustMemoryInfo() {
        if (heapRegionSize == UNKNOWN_INT) {
            return;
        }
        for (GCEvent event : getGcEvents()) {
            if (!collectionResultUsingRegion(event)) {
                continue;
            }
            for (GCMemoryItem item : event.getMemoryItems()) {
                if (item != null && item.getArea() != MemoryArea.METASPACE && item.getArea() != HEAP) {
                    item.multiply(heapRegionSize);
                }
            }
        }
    }

    @Override
    protected void doBeforeCalculatingDerivedInfo() {
        if (getLogStyle() == GCLogStyle.UNIFIED) {
            inferHeapRegionSize();
            adjustMemoryInfo();
        }
    }

    @Override
    protected void calculateUsedAvgAfterOldGC(TimeRange range, LongData[][] data) {
        AtomicReference<GCEvent> lastMixedGC = new AtomicReference<>();
        AtomicDouble lastRemarkEndTime = new AtomicDouble(Double.MAX_VALUE);
        iterateEventsWithinTimeRange(getGcEvents(), range, event -> {
            GCEventType type = event.getEventType();
            // read old from the last mixed gc of old gc cycle
            if (type == G1_MIXED_GC) {
                lastMixedGC.set(event);
            } else if (type == YOUNG_GC || type == G1_CONCURRENT_CYCLE || type == FULL_GC) {
                GCEvent mixedGC = lastMixedGC.get();
                if (mixedGC != null) {
                    if (mixedGC.getMemoryItem(OLD) != null) {
                        data[1][3].add(mixedGC.getMemoryItem(OLD).getPostUsed());
                    }
                    lastMixedGC.set(null);
                }
            }
            // read humongous and metaspace from the gc after remark
            if (event.getEventType() == G1_CONCURRENT_CYCLE) {
                if (event.getLastPhaseOfType(G1_CONCURRENT_MARK_ABORT) != null) {
                    return;
                }
                GCEvent remark = event.getLastPhaseOfType(G1_REMARK);
                if (remark != null) {
                    lastRemarkEndTime.set(remark.getEndTime());
                }
            } else if ((event.getEventType() == YOUNG_GC || event.getEventType() == FULL_GC || event.getEventType() == G1_MIXED_GC)
                    && event.getStartTime() > lastRemarkEndTime.get()) {
                if (event.getMemoryItem(HUMONGOUS) != null) {
                    data[2][3].add(event.getMemoryItem(HUMONGOUS).getPreUsed());

                }
                if (event.getMemoryItem(METASPACE) != null) {
                    data[4][3].add(event.getMemoryItem(METASPACE).getPreUsed());
                }
                lastRemarkEndTime.set(Double.MAX_VALUE);
            }
        });
    }
}
