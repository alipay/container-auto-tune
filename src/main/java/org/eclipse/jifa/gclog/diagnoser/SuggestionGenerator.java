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

import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType;
import org.eclipse.jifa.gclog.model.modeInfo.GCLogStyle;
import org.eclipse.jifa.gclog.util.I18nStringView;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.CHECK_EVACUATION_FAILURE;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.DECREASE_CMSIOF;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.DECREASE_IHOP;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.ENLARGE_HEAP;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.OLD_SYSTEM_GC;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.SHRINK_YOUNG_GEN;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.SHRINK_YOUNG_GEN_G1;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.UPGRADE_TO_11_G1_FULL_GC;
import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_INT;

public abstract class SuggestionGenerator {
    protected GCModel model;
    protected BitSet givenCause = new BitSet();
    protected List<I18nStringView> result = new ArrayList<>();

    public SuggestionGenerator(GCModel model) {
        this.model = model;
    }

    protected void addSuggestion(SuggestionType type, Object... params) {
        // don't add duplicate suggestions
        if (givenCause.get(type.ordinal())) {
            return;
        }
        givenCause.set(type.ordinal());
        result.add(new I18nStringView(SuggestionType.I18N_PREFIX + type.toString(), params));
    }

    protected void suggestShrinkYoungGen() {
        if (model.getCollectorType() == GCCollectorType.G1) {
            addSuggestion(SHRINK_YOUNG_GEN_G1);
        } else {
            addSuggestion(SHRINK_YOUNG_GEN);
        }
    }

    protected void suggestOldSystemGC() {
        if (model.hasOldGC()) {
            addSuggestion(OLD_SYSTEM_GC);
        }
    }

    protected void suggestEnlargeHeap(boolean suggestHeapSize) {
        if (suggestHeapSize) {
            long size = model.getRecommendMaxHeapSize();
            if (size != UNKNOWN_INT) {
                addSuggestion(ENLARGE_HEAP, "recommendSize", size);
            } else {
                addSuggestion(ENLARGE_HEAP);
            }
        } else {
            addSuggestion(ENLARGE_HEAP);
        }
    }

    protected void fullGCSuggestionCommon() {
        if (model.getCollectorType() == GCCollectorType.G1 && model.getLogStyle() == GCLogStyle.PRE_UNIFIED) {
            addSuggestion(UPGRADE_TO_11_G1_FULL_GC);
        }
    }

    protected void suggestStartOldGCEarly() {
        switch (model.getCollectorType()) {
            case CMS:
                addSuggestion(DECREASE_CMSIOF);
                break;
            case G1:
                addSuggestion(DECREASE_IHOP);
                break;
        }
    }

    protected void suggestCheckEvacuationFailure() {
        if (model.getCollectorType() == GCCollectorType.G1) {
            addSuggestion(CHECK_EVACUATION_FAILURE);
        }
    }
}
