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

public enum SuggestionType {
    UPGRADE_TO_11_G1_FULL_GC("upgradeTo11G1FullGC"),
    CHECK_SYSTEM_GC("checkSystemGC"),
    DISABLE_SYSTEM_GC("disableSystemGC"),
    OLD_SYSTEM_GC("oldSystemGC"),
    CHECK_METASPACE("checkMetaspace"),
    ENLARGE_METASPACE("enlargeMetaspace"),
    ENLARGE_HEAP("enlargeHeap"),
    INCREASE_CONC_GC_THREADS("increaseConcGCThreads"),
    INCREASE_Z_ALLOCATION_SPIKE_TOLERANCE("increaseZAllocationSpikeTolerance"),
    DECREASE_IHOP("decreaseIHOP"),
    DECREASE_CMSIOF("decreaseCMSIOF"),
    CHECK_LIVE_OBJECTS("checkLiveObjects"),
    CHECK_REFERENCE_GC("checkReferenceGC"),
    CHECK_CPU_TIME("checkCPUTime"),
    SHRINK_YOUNG_GEN("shrinkYoungGen"),
    SHRINK_YOUNG_GEN_G1("shrinkYoungGenG1"),
    CHECK_EVACUATION_FAILURE("checkEvacuationFailure"),
    CHECK_FAST_PROMOTION("checkFastPromotion"),
    CHECK_MEMORY_LEAK("checkMemoryLeak");

    public static final String I18N_PREFIX = "jifa.gclog.diagnose.suggestion.";

    private String name;

    SuggestionType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
