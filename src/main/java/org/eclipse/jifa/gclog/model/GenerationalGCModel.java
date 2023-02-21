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

import org.eclipse.jifa.gclog.event.GCEvent;
import org.eclipse.jifa.gclog.event.evnetInfo.GCSpecialSituation;
import org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea;
import org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType;
import org.eclipse.jifa.gclog.model.modeInfo.GCLogStyle;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jifa.gclog.model.GCEventType.FULL_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.YOUNG_GC;

public abstract class GenerationalGCModel extends GCModel {
    public GenerationalGCModel(GCCollectorType type) {
        super(type);
    }

    private void removeYoungGCThatBecomeFullGC() {
        if (getLogStyle() != GCLogStyle.UNIFIED) {
            return;
        }
        List<GCEvent> newEvents = new ArrayList<>();
        List<GCEvent> oldEvents = getGcEvents();
        boolean remove = false;
        for (int i = 0; i < oldEvents.size() - 1; i++) {
            GCEvent event = oldEvents.get(i);
            GCEvent nextEvent = oldEvents.get(i + 1);
            remove = event.getEventType() == YOUNG_GC && nextEvent.getEventType() == FULL_GC &&
                    event.getStartTime() <= nextEvent.getStartTime() && event.getEndTime() >= nextEvent.getEndTime();
            if (remove) {
                event.setEventType(FULL_GC);
                event.setPhases(nextEvent.getPhases());
                i++; // remove the full gc
            }
            newEvents.add(event);
        }
        if (!remove) {
            newEvents.add(oldEvents.get(oldEvents.size() - 1));
        }
        setGcEvents(newEvents);
    }

    private void fixYoungGCPromotionFail() {
        for (GCEvent event : getGcEvents()) {
            if (event.getEventType() == YOUNG_GC && event.hasSpecialSituation(GCSpecialSituation.PROMOTION_FAILED)) {
                // when there is promotion fail, overwrite its original gccause with promotion failed
                event.setEventType(FULL_GC);
                event.setCause(GCSpecialSituation.PROMOTION_FAILED.getName());
                event.getSpecialSituations().remove(GCSpecialSituation.PROMOTION_FAILED);
            }
        }
    }

    private void youngGenUsedShouldBeZeroAfterFullGC() {
        if (getLogStyle() != GCLogStyle.PRE_UNIFIED) {
            return;
        }
        for (GCEvent event : getGcEvents()) {
            if (event.getEventType() == FULL_GC && event.getMemoryItem(MemoryArea.YOUNG) != null) {
                event.getMemoryItem(MemoryArea.YOUNG).setPostUsed(0);
            }
        }
    }

    @Override
    protected void doBeforeCalculatingDerivedInfo() {
        removeYoungGCThatBecomeFullGC();
        fixYoungGCPromotionFail();
        youngGenUsedShouldBeZeroAfterFullGC();
    }
}
