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
package org.eclipse.jifa.gclog.parser;

import org.eclipse.jifa.common.util.ErrorUtil;
import org.eclipse.jifa.gclog.event.GCEvent;
import org.eclipse.jifa.gclog.event.evnetInfo.GCSpecialSituation;
import org.eclipse.jifa.gclog.model.G1GCModel;
import org.eclipse.jifa.gclog.model.GCEventType;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.util.Constant;
import org.eclipse.jifa.gclog.util.GCLogUtil;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jifa.gclog.model.GCEventType.FULL_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_ADJUST_POINTERS;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_COLLECT_EVACUATION;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_COLLECT_OTHER;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_COLLECT_POST_EVACUATION;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_COLLECT_PRE_EVACUATION;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_COMPACT_HEAP;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_CLEANUP_FOR_NEXT_MARK;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_CLEAR_CLAIMED_MARKS;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_CYCLE;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_MARK;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_MARK_ABORT;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_MARK_FROM_ROOTS;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_MARK_RESET_FOR_OVERFLOW;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_PRECLEAN;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_REBUILD_REMEMBERED_SETS;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_SCAN_ROOT_REGIONS;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_MARK_LIVE_OBJECTS;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_MIXED_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_PAUSE_CLEANUP;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_PREPARE_FOR_COMPACTION;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_REMARK;
import static org.eclipse.jifa.gclog.model.GCEventType.YOUNG_GC;
import static org.eclipse.jifa.gclog.parser.ParseRule.FixedContentParseRule;
import static org.eclipse.jifa.gclog.parser.ParseRule.ParseRuleContext;
import static org.eclipse.jifa.gclog.parser.ParseRule.ParseRuleContext.GCID;
import static org.eclipse.jifa.gclog.parser.ParseRule.ParseRuleContext.UPTIME;
import static org.eclipse.jifa.gclog.parser.ParseRule.PrefixAndValueParseRule;

public class JDK11G1GCLogParser extends JDK11G1OrGenerationalGCLogParser {
    private final static GCEventType[] YOUNG_MIXED = {YOUNG_GC, G1_MIXED_GC};
    private final static GCEventType[] CONCURRENT_CYCLE_CPU_TIME_EVENTS = {
            YOUNG_GC, G1_MIXED_GC, FULL_GC, G1_PAUSE_CLEANUP, G1_REMARK};

    /*
     * [0.001s][warning][gc] -XX:+PrintGCDetails is deprecated. Will use -Xlog:gc* instead.
     * [0.004s][info   ][gc,heap] Heap region size: 1M
     * [0.019s][info   ][gc     ] Using G1
     * [0.019s][info   ][gc,heap,coops] Heap address: 0x0000000095c00000, size: 1700 MB, Compressed Oops mode: 32-bit
     * [0.050s][info   ][gc           ] Periodic GC disabled
     *
     * [0.751s][info][gc,start     ] GC(0) Pause Young (Normal) (G1 Evacuation Pause)
     * [0.752s][info][gc,task      ] GC(0) Using 2 workers of 2 for evacuation
     * [0.760s][info][gc,phases    ] GC(0)   Pre Evacuate Collection Set: 0.0ms
     * [0.760s][info][gc,phases    ] GC(0)   Evacuate Collection Set: 5.9ms
     * [0.760s][info][gc,phases    ] GC(0)   Post Evacuate Collection Set: 2.3ms
     * [0.760s][info][gc,phases    ] GC(0)   Other: 0.3ms
     * [0.760s][info][gc,heap      ] GC(0) Eden regions: 6->0(5)
     * [0.760s][info][gc,heap      ] GC(0) Survivor regions: 0->1(1)
     * [0.760s][info][gc,heap      ] GC(0) Old regions: 0->0
     * [0.760s][info][gc,heap      ] GC(0) Humongous regions: 0->0
     * [0.760s][info][gc,metaspace ] GC(0) Metaspace: 10707K->10707K(1058816K)
     * [0.760s][info][gc           ] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 96M->3M(2048M) 8.547ms
     * [0.760s][info][gc,cpu       ] GC(0) User=0.01s Sys=0.01s Real=0.01s
     *
     * [2.186s][info][gc           ] GC(5) Concurrent Cycle
     * [2.186s][info][gc,marking   ] GC(5) Concurrent Clear Claimed Marks
     * [2.186s][info][gc,marking   ] GC(5) Concurrent Clear Claimed Marks 0.016ms
     * [2.186s][info][gc,marking   ] GC(5) Concurrent Scan Root Regions
     * [2.189s][info][gc,marking   ] GC(5) Concurrent Scan Root Regions 3.214ms
     * [2.189s][info][gc,marking   ] GC(5) Concurrent Mark (2.189s)
     * [2.189s][info][gc,marking   ] GC(5) Concurrent Mark Reset For Overflow
     * [2.189s][info][gc,marking   ] GC(5) Concurrent Mark From Roots
     * [2.190s][info][gc,task      ] GC(5) Using 2 workers of 2 for marking
     * [2.190s][info][gc,marking   ] GC(5) Concurrent Mark From Roots 0.226ms
     * [2.190s][info][gc,marking   ] GC(5) Concurrent Preclean
     * [2.190s][info][gc,marking   ] GC(5) Concurrent Preclean 0.030ms
     * [2.190s][info][gc,marking   ] GC(5) Concurrent Mark (2.189s, 2.190s) 0.272ms
     * [2.190s][info][gc,start     ] GC(5) Pause Remark
     * [2.193s][info][gc,stringtable] GC(5) Cleaned string and symbol table, strings: 10318 processed, 0 removed, symbols: 69242 processed, 330 removed
     * [2.193s][info][gc            ] GC(5) Pause Remark 14M->14M(2048M) 3.435ms
     * [2.193s][info][gc,cpu        ] GC(5) User=0.01s Sys=0.00s Real=0.00s
     * [2.193s][info][gc,marking    ] GC(5) Concurrent Rebuild Remembered Sets
     * [2.193s][info][gc,marking    ] GC(5) Concurrent Rebuild Remembered Sets 0.067ms
     * [2.194s][info][gc,start      ] GC(5) Pause Cleanup
     * [2.194s][info][gc            ] GC(5) Pause Cleanup 14M->14M(2048M) 0.067ms
     * [2.194s][info][gc,cpu        ] GC(5) User=0.00s Sys=0.00s Real=0.00s
     * [2.194s][info][gc,marking    ] GC(5) Concurrent Cleanup for Next Mark
     * [2.217s][info][gc,marking    ] GC(5) Concurrent Cleanup for Next Mark 23.105ms
     * [2.217s][info][gc            ] GC(5) Concurrent Cycle 30.799ms
     *
     * [6.845s][info][gc,task       ] GC(26) Using 2 workers of 2 for full compaction
     * [6.845s][info][gc,start      ] GC(26) Pause Full (System.gc())
     * [6.857s][info][gc,phases,start] GC(26) Phase 1: Mark live objects
     * [6.907s][info][gc,stringtable ] GC(26) Cleaned string and symbol table, strings: 11395 processed, 5 removed, symbols: 69956 processed, 0 removed
     * [6.907s][info][gc,phases      ] GC(26) Phase 1: Mark live objects 49.532ms
     * [6.907s][info][gc,phases,start] GC(26) Phase 2: Prepare for compaction
     * [6.922s][info][gc,phases      ] GC(26) Phase 2: Prepare for compaction 15.369ms
     * [6.922s][info][gc,phases,start] GC(26) Phase 3: Adjust pointers
     * [6.947s][info][gc,phases      ] GC(26) Phase 3: Adjust pointers 25.161ms
     * [6.947s][info][gc,phases,start] GC(26) Phase 4: Compact heap
     * [6.963s][info][gc,phases      ] GC(26) Phase 4: Compact heap 16.169ms
     * [6.966s][info][gc,heap        ] GC(26) Eden regions: 4->0(6)
     * [6.966s][info][gc,heap        ] GC(26) Survivor regions: 1->0(1)
     * [6.966s][info][gc,heap        ] GC(26) Old regions: 80->6
     * [6.966s][info][gc,heap        ] GC(26) Humongous regions: 2->2
     * [6.966s][info][gc,metaspace   ] GC(26) Metaspace: 22048K->22048K(1069056K)
     * [6.966s][info][gc             ] GC(26) Pause Full (System.gc()) 1368M->111M(2048M) 120.634ms
     * [6.966s][info][gc,cpu         ] GC(26) User=0.22s Sys=0.01s Real=0.12s
     *
     */
    private static List<ParseRule> withoutGCIDRules;
    private static List<ParseRule> withGCIDRules;

    static {
        initializeParseRules();
    }

    private static void initializeParseRules() {
        withoutGCIDRules = new ArrayList<>(getSharedWithoutGCIDRules());
        withoutGCIDRules.add(new PrefixAndValueParseRule("Heap region size", JDK11G1GCLogParser::parseHeapRegionSize));

        withGCIDRules = new ArrayList<>(getSharedWithGCIDRules());
        withGCIDRules.add(new PrefixAndValueParseRule("  Pre Evacuate Collection Set", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("  Evacuate Collection Set", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("  Post Evacuate Collection Set", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("  Other", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Cycle", JDK11G1GCLogParser::parseConcurrentCycle));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Clear Claimed Marks", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Scan Root Regions", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Mark From Roots", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Mark", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Mark Reset For Overflow", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Preclean", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Pause Remark", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Rebuild Remembered Sets", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Pause Cleanup", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Cleanup for Next Mark", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Phase 1: Mark live objects", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Phase 2: Prepare for compaction", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Phase 3: Adjust pointers", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Phase 4: Compact heap", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new PrefixAndValueParseRule("Concurrent Mark Abort", JDK11G1OrGenerationalGCLogParser::parsePhase));
        withGCIDRules.add(new FixedContentParseRule("To-space exhausted", JDK11G1GCLogParser::parseToSpaceExhausted));
    }

    @Override
    protected List<ParseRule> getWithoutGCIDRules() {
        return withoutGCIDRules;
    }

    @Override
    protected List<ParseRule> getWithGCIDRules() {
        return withGCIDRules;
    }

    private static void parseConcurrentCycle(AbstractGCLogParser parser, ParseRuleContext context, String prefix, String value) {
        GCModel model = parser.getModel();
        GCEventType eventType = G1_CONCURRENT_CYCLE;
        boolean end = value.endsWith("ms");
        GCEvent event;
        if (!end || (event = model.getLastEventOfType(eventType)).getDuration() != Constant.UNKNOWN_DOUBLE) {
            event = new GCEvent();
            event.setStartTime(context.get(UPTIME));
            event.setEventType(eventType);
            event.setGcid(context.get(GCID));
            model.putEvent(event);
        }
        parseCollectionAndDuration(event, context, value);
    }

    private static void parseHeapRegionSize(AbstractGCLogParser parser, ParseRuleContext context, String prefix, String value) {
        G1GCModel model = (G1GCModel) parser.getModel();
        model.setHeapRegionSize(GCLogUtil.toByte(value));
        model.setRegionSizeExact(true);
    }

    private static void parseToSpaceExhausted(AbstractGCLogParser parser, ParseRuleContext context) {
        GCModel model = parser.getModel();
        GCEvent event = model.getLastEventOfType(YOUNG_MIXED);
        if (event == null) {
            // log may be incomplete
            return;
        }
        event.addSpecialSituation(GCSpecialSituation.TO_SPACE_EXHAUSTED);
    }

    @Override
    protected GCEvent getCPUTimeEventOrPhase(GCEvent event) {
        if (event.getEventType() == YOUNG_GC || event.getEventType() == FULL_GC || event.getEventType() == G1_MIXED_GC) {
            return event;
        } else if (event.getEventType() == G1_CONCURRENT_CYCLE) {
            return getModel().getLastEventOfType(CONCURRENT_CYCLE_CPU_TIME_EVENTS);
        } else {
            return null;
        }
    }

    @Override
    protected GCEventType getGCEventType(String eventString) {
        switch (eventString) {
            case "Pre Evacuate Collection Set":
                return G1_COLLECT_PRE_EVACUATION;
            case "Evacuate Collection Set":
                return G1_COLLECT_EVACUATION;
            case "Post Evacuate Collection Set":
                return G1_COLLECT_POST_EVACUATION;
            case "Other":
                return G1_COLLECT_OTHER;
            case "Concurrent Clear Claimed Marks":
                return G1_CONCURRENT_CLEAR_CLAIMED_MARKS;
            case "Concurrent Scan Root Regions":
                return G1_CONCURRENT_SCAN_ROOT_REGIONS;
            case "Concurrent Mark From Roots":
                return G1_CONCURRENT_MARK_FROM_ROOTS;
            case "Concurrent Mark":
                return G1_CONCURRENT_MARK;
            case "Concurrent Preclean":
                return G1_CONCURRENT_PRECLEAN;
            case "Pause Remark":
                return G1_REMARK;
            case "Concurrent Rebuild Remembered Sets":
                return G1_CONCURRENT_REBUILD_REMEMBERED_SETS;
            case "Pause Cleanup":
                return G1_PAUSE_CLEANUP;
            case "Concurrent Cleanup for Next Mark":
                return G1_CONCURRENT_CLEANUP_FOR_NEXT_MARK;
            case "Phase 1: Mark live objects":
                return G1_MARK_LIVE_OBJECTS;
            case "Phase 2: Prepare for compaction":
                return G1_PREPARE_FOR_COMPACTION;
            case "Phase 3: Adjust pointers":
                return G1_ADJUST_POINTERS;
            case "Phase 4: Compact heap":
                return G1_COMPACT_HEAP;
            case "Concurrent Mark Abort":
                return G1_CONCURRENT_MARK_ABORT;
            case "Concurrent Mark Reset For Overflow":
                return G1_CONCURRENT_MARK_RESET_FOR_OVERFLOW;
            default:
                ErrorUtil.shouldNotReachHere();
        }
        return null;
    }
}
