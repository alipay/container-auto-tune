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

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GCCause {
    private String name;

    private static final Map<String, GCCause> name2cause = new HashMap<>();

    public static GCCause SYSTEM_GC = new GCCause("System.gc()");
    public static GCCause DIAGNOSTIC_COMMAND = new GCCause("Diagnostic Command");
    public static GCCause FULL_GC_ALOT = new GCCause("FullGCALot");
    public static GCCause SCAVENGE_ALOT = new GCCause("ScavengeAlot");
    public static GCCause ALLOCATION_PROFILER = new GCCause("Allocation Profiler");
    public static GCCause JVMTI_FORCE_GC = new GCCause("JvmtiEnv ForceGarbageCollection");
    public static GCCause GC_LOCKER = new GCCause("GCLocker Initiated GC");
    public static GCCause HEAP_INSPECTION = new GCCause("Heap Inspection Initiated GC");
    public static GCCause HEAP_DUMP = new GCCause("Heap Dump Initiated GC");
    public static GCCause NO_GC = new GCCause("No GC");
    public static GCCause ALLOCATION_FAILURE = new GCCause("Allocation Failure");
    public static GCCause TENURED_GENERATION_FULL = new GCCause("Tenured Generation Full");
    public static GCCause METADATA_GENERATION_THRESHOLD = new GCCause("Metadata GC Threshold");
    public static GCCause PERMANENT_GENERATION_FULL = new GCCause("Permanent Generation Full");
    public static GCCause CMS_GENERATION_FULL = new GCCause("CMS Generation Full");
    public static GCCause CMS_INITIAL_MARK = new GCCause("CMS Initial Mark");
    public static GCCause CMS_FINAL_REMARK = new GCCause("CMS Final Remark");
    public static GCCause CMS_CONCURRENT_MARK = new GCCause("CMS Concurrent Mark");
    public static GCCause CMS_FAILURE = new GCCause("CMS Failure");
    public static GCCause OLD_GENERATION_EXPANDED_ON_LAST_SCAVENGE = new GCCause("Old Generation Expanded On Last Scavenge");
    public static GCCause OLD_GENERATION_TOO_FULL_TO_SCAVENGE = new GCCause("Old Generation Too Full To Scavenge");
    public static GCCause ERGONOMICS = new GCCause("Ergonomics");
    public static GCCause G1_EVACUATION_PAUSE = new GCCause("G1 Evacuation Pause");
    public static GCCause G1_HUMONGOUS_ALLOCATION = new GCCause("G1 Humongous Allocation");
    public static GCCause LAST_DITCH_COLLECTION = new GCCause("Last ditch collection");
    public static GCCause LAST_GC_CAUSE = new GCCause("ILLEGAL VALUE - last gc cause - ILLEGAL VALUE");
    public static GCCause PROMOTION_FAILED = new GCCause("promotion failed");
    public static GCCause UPDATE_ALLOCATION_CONTEXT_STATS = new GCCause("Update Allocation Context Stats");
    public static GCCause WHITEBOX_YOUNG = new GCCause("WhiteBox Initiated Young GC");
    public static GCCause WHITEBOX_CONCURRENT_MARK = new GCCause("WhiteBox Initiated Concurrent Mark");
    public static GCCause WHITEBOX_FULL = new GCCause("WhiteBox Initiated Full GC");
    public static GCCause META_CLEAR_SOFT_REF = new GCCause("Metadata GC Clear Soft References");
    public static GCCause TIMER = new GCCause("Timer");
    public static GCCause WARMUP = new GCCause("Warmup");
    public static GCCause ALLOC_RATE = new GCCause("Allocation Rate");
    public static GCCause ALLOC_STALL = new GCCause("Allocation Stall");
    public static GCCause PROACTIVE = new GCCause("Proactive");
    public static GCCause PREVENTIVE = new GCCause("G1 Preventive Collection");
    public static GCCause GCIH = new GCCause("GCIH compaction");
    public static GCCause ELASTIC_HEAP = new GCCause("Elastic Heap Initiated GC");
    public static GCCause ELASTIC_HEAP_FULL = new GCCause("Elastic Heap Initiated Full GC");
    public static GCCause UNKNOWN_GCCAUSE = new GCCause("unknown GCCause");

    static {
        name2cause.put("System.gc", SYSTEM_GC); // HACK: sometimes "()" is missing
    }

    private GCCause(String name) {
        this.name = name;
        name2cause.put(name, this);
    }

    public static GCCause getCause(String name) {
        return name2cause.getOrDefault(name, null);
    }

    public String getName() {
        return name;
    }

    public boolean isMetaspaceFullGCCause() {
        return this == METADATA_GENERATION_THRESHOLD || this == META_CLEAR_SOFT_REF || this == LAST_DITCH_COLLECTION;
    }

    private static final List<GCCause> HeapMemoryTriggeredFullGCCauses = Lists.newArrayList(GC_LOCKER, ALLOCATION_FAILURE,
            ERGONOMICS, G1_HUMONGOUS_ALLOCATION, PROMOTION_FAILED);

    public boolean isHeapMemoryTriggeredFullGCCause() {
        return HeapMemoryTriggeredFullGCCauses.contains(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
