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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jifa.gclog.event.GCEvent;
import org.eclipse.jifa.gclog.event.evnetInfo.CpuTime;
import org.eclipse.jifa.gclog.event.evnetInfo.GCMemoryItem;
import org.eclipse.jifa.gclog.event.evnetInfo.GCSpecialSituation;
import org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea;
import org.eclipse.jifa.gclog.model.GCEventType;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType;
import org.eclipse.jifa.gclog.parser.ParseRule.ParseRuleContext;
import org.eclipse.jifa.gclog.parser.ParseRule.PrefixAndValueParseRule;
import org.eclipse.jifa.gclog.util.Constant;
import org.eclipse.jifa.gclog.util.GCLogUtil;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jifa.gclog.model.GCEventType.CMS_CONCURRENT_FAILURE;
import static org.eclipse.jifa.gclog.model.GCEventType.CMS_CONCURRENT_INTERRUPTED;
import static org.eclipse.jifa.gclog.model.GCEventType.CMS_CONCURRENT_MARK_SWEPT;
import static org.eclipse.jifa.gclog.model.GCEventType.CMS_CONCURRENT_SWEEP;
import static org.eclipse.jifa.gclog.model.GCEventType.CMS_INITIAL_MARK;
import static org.eclipse.jifa.gclog.model.GCEventType.FULL_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_MARK_ABORT;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_MARK_RESET_FOR_OVERFLOW;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_MIXED_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.YOUNG_GC;
import static org.eclipse.jifa.gclog.parser.ParseRule.ParseRuleContext.GCID;
import static org.eclipse.jifa.gclog.parser.ParseRule.ParseRuleContext.UPTIME;

public abstract class JDK11G1OrGenerationalGCLogParser extends AbstractJDK11GCLogParser {
    private static List<ParseRule> withoutGCIDRules;
    private static List<ParseRule> withGCIDRules;

    public static List<ParseRule> getSharedWithoutGCIDRules() {
        return withoutGCIDRules;
    }

    public static List<ParseRule> getSharedWithGCIDRules() {
        return withGCIDRules;
    }

    static {
        initializeParseRules();
    }

    private static void initializeParseRules() {
        withoutGCIDRules = new ArrayList<>(AbstractJDK11GCLogParser.getSharedWithoutGCIDRules());

        withGCIDRules = new ArrayList<>(AbstractJDK11GCLogParser.getSharedWithGCIDRules());
        withGCIDRules.add(JDK11G1OrGenerationalGCLogParser::parseHeap);
        withGCIDRules.add(new PrefixAndValueParseRule("Pause Young", JDK11G1OrGenerationalGCLogParser::parseYoungFullGC));
        withGCIDRules.add(new PrefixAndValueParseRule("Pause Full", JDK11G1OrGenerationalGCLogParser::parseYoungFullGC));
        withGCIDRules.add(JDK11G1OrGenerationalGCLogParser::parseWorker);
        withGCIDRules.add(JDK11G1OrGenerationalGCLogParser::parseCpuTime);
        // subclass will add more rules
    }

    protected abstract List<ParseRule> getWithoutGCIDRules();

    protected abstract List<ParseRule> getWithGCIDRules();

    private static boolean parseCpuTime(AbstractGCLogParser parser, ParseRuleContext context, String text) {
        GCModel model = parser.getModel();
        //[0.524s][info   ][gc,cpu       ] GC(0) User=22.22s Sys=23.23s Real=24.24s
        if (!text.startsWith("User=") || !text.endsWith("s")) {
            return false;
        }
        CpuTime cpuTime = GCLogUtil.parseCPUTime(text);
        GCEvent event = model.getLastEventOfGCID(context.get(GCID));
        if (event != null) {
            event = ((JDK11G1OrGenerationalGCLogParser) parser).getCPUTimeEventOrPhase(event);
            if (event != null) {
                event.setCpuTime(cpuTime);
            }
        }
        return true;
    }

    protected abstract GCEvent getCPUTimeEventOrPhase(GCEvent event);

    @Override
    protected final void doParseLineWithoutGCID(String detail, double uptime) {
        ParseRuleContext context = new ParseRuleContext();
        context.put(UPTIME, uptime);
        doParseUsingRules(this, context, detail, getWithoutGCIDRules());
    }

    @Override
    protected final void doParseLineWithGCID(String detail, int gcid, double uptime) {
        ParseRuleContext context = new ParseRuleContext();
        context.put(UPTIME, uptime);
        context.put(GCID, gcid);
        doParseUsingRules(this, context, detail, getWithGCIDRules());
    }

    /**
     * for reference
     * [0.501s][info   ][gc,start     ] GC(0) Pause Young (Normal) (G1 Evacuation Pause)
     * [0.524s][info   ][gc           ] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 18M->19M(20M) 21.21ms
     * [6.845s][info][gc,start      ] GC(26) Pause Full (System.gc())
     * [0.276s][info ][gc,start     ] GC(34) Pause Young (Allocation Failure)
     * [10.115s][info ][gc,start     ] GC(25) Pause Full (Ergonomics)
     * [15.732s][info][gc,start       ] GC(42) Pause Young (Mixed) (G1 Evacuation Pause)
     * [56.810s][info][gc,start      ] GC(33) Pause Young (Concurrent Start) (GCLocker Initiated GC)
     */
    private static void parseYoungFullGC(AbstractGCLogParser parser, ParseRuleContext context, String title, String text) {
        GCModel model = parser.getModel();
        String[] parts = GCLogUtil.splitByBracket(text);
        int causeIndex = 0;
        GCEventType eventType = title.endsWith("Young") ? YOUNG_GC : FULL_GC;
        GCSpecialSituation specialSituation = null;
        if (parser.getMetadata().getCollector() == GCCollectorType.G1 && eventType == YOUNG_GC) {
            switch (parts[0]) {
                case "Concurrent Start":
                    specialSituation = GCSpecialSituation.INITIAL_MARK;
                    break;
                case "Prepare Mixed":
                    specialSituation = GCSpecialSituation.PREPARE_MIXED;
                    break;
                case "Mixed":
                    eventType = G1_MIXED_GC;
                    break;
            }
            causeIndex++;
        }
        String cause = parts[causeIndex];
        boolean end = text.endsWith("ms");
        GCEvent event;
        if (!end || (event = model.getLastEventOfGCID(context.get(GCID))) == null) {
            event = new GCEvent();
            event.setStartTime(context.get(UPTIME));
            event.setEventType(eventType);
            event.setCause(cause);
            event.addSpecialSituation(specialSituation);
            event.setGcid(context.get(GCID));
            model.putEvent(event);
        }
        if (end) {
            int tailBegin = text.lastIndexOf(' ');
            tailBegin = text.lastIndexOf(' ', tailBegin - 1);
            if (tailBegin > 0) {
                parseCollectionAndDuration(event, context, text.substring(tailBegin + 1));
            }
        }
    }

    //  18M->19M(20M) 21.21ms
    protected static void parseCollectionAndDuration(GCEvent event, ParseRuleContext context, String s) {
        if (StringUtils.isBlank(s)) {
            return;
        }
        for (String part : s.split(" ")) {
            if (part.contains("->") && part.endsWith(")") && !part.startsWith("(")) {
                long[] memories = GCLogUtil.parseMemorySizeFromTo(part);
                GCMemoryItem item = new GCMemoryItem(MemoryArea.HEAP, memories);
                event.setMemoryItem(item);
            } else if (part.endsWith("ms")) {
                double duration = GCLogUtil.toMillisecond(part);
                event.setDuration(duration);
                if (event.getStartTime() == Constant.UNKNOWN_DOUBLE) {
                    event.setStartTime((double) context.get(UPTIME) - duration);
                }
            }
        }
    }

    /**
     * [0.524s][info   ][gc,heap      ] GC(0) Eden regions: 5->6(7)
     * [0.524s][info   ][gc,heap      ] GC(0) Survivor regions: 8->9(10)
     * [0.524s][info   ][gc,heap      ] GC(0) Old regions: 11->12
     * [0.524s][info   ][gc,heap      ] GC(0) Humongous regions: 13->14
     * [0.524s][info   ][gc,metaspace ] GC(0) Metaspace: 15K->16K(17K)
     * [2.285s][info ][gc,heap      ] GC(2) Old: 23127K->2019K(43712K)
     * [0.160s][info ][gc,heap      ] GC(0) ParNew: 17393K->2175K(19648K)
     * [0.160s][info ][gc,heap      ] GC(0) CMS: 0K->130K(43712K)
     * [0.160s][info ][gc,metaspace ] GC(0) Metaspace: 5147K->5147K(1056768K)
     */
    private static boolean parseHeap(AbstractGCLogParser parser, ParseRuleContext context, String s) {
        GCModel model = parser.getModel();
        String[] parts = s.split(": ");
        if (parts.length != 2) {
            return false;
        }
        String generationName = parts[0];
        if (generationName.endsWith(" regions")) {
            generationName = generationName.substring(0, generationName.length() - " regions".length());
        }
        MemoryArea generation = MemoryArea.getMemoryArea(generationName);
        if (generation == null) {
            return false;
        }
        // format check done

        GCEvent event = model.getLastEventOfGCID(context.get(GCID));
        if (event == null) {
            // log may be incomplete
            return true;
        }
        if (event.getEventType() == CMS_CONCURRENT_MARK_SWEPT) {
            event = event.getLastPhaseOfType(CMS_CONCURRENT_SWEEP);
            if (event == null) {
                return true;
            }
        }
        long[] memories = GCLogUtil.parseMemorySizeFromTo(parts[1], 1);
        // will multiply region size before calculating derived info for g1
        GCMemoryItem item = new GCMemoryItem(generation, memories);
        event.setMemoryItem(item);
        return true;
    }

    /**
     * e.g.
     * [2.983s][info   ][gc,marking    ] GC(1) Concurrent Clear Claimed Marks
     * [2.983s][info   ][gc,marking    ] GC(1) Concurrent Clear Claimed Marks 25.25ms
     * [3.266s][info   ][gc,phases     ] GC(2) Phase 1: Mark live objects 50.50ms
     * [3.002s][info   ][gc            ] GC(1) Pause Cleanup 1480M->1480M(1700M) 41.41ms
     * <p>
     * two cases of phases in gclog: one line summary , two lines of begin and end
     */
    protected static void parsePhase(AbstractGCLogParser parser, ParseRuleContext context, String phaseName, String value) {
        GCModel model = parser.getModel();
        phaseName = phaseName.trim();
        GCEventType phaseType = ((JDK11G1OrGenerationalGCLogParser) parser).getGCEventType(phaseName);
        boolean end = value.endsWith("ms");
        GCEvent event;
        // cms does not have a line to indicate its beginning, hard code here
        if (parser.getMetadata().getCollector() == GCCollectorType.CMS &&
                phaseType == CMS_INITIAL_MARK && !end) {
            event = new GCEvent();
            event.setEventType(CMS_CONCURRENT_MARK_SWEPT);
            event.setStartTime(context.get(UPTIME));
            event.setGcid(context.get(GCID));
            model.putEvent(event);
        } else {
            event = model.getLastEventOfGCID(context.get(GCID));
        }
        if (event == null) {
            // log may be incomplete
            return;
        }
        GCEvent phase = event.getLastPhaseOfType(phaseType);
        if (phase == null) {
            phase = new GCEvent();
            phase.setEventType(phaseType);
            phase.setGcid(context.get(GCID));
            phase.setStartTime(context.get(UPTIME));
            if (phaseType == G1_CONCURRENT_MARK_ABORT || phaseType == G1_CONCURRENT_MARK_RESET_FOR_OVERFLOW ||
                    phaseType == CMS_CONCURRENT_INTERRUPTED || phaseType == CMS_CONCURRENT_FAILURE) {
                phase.setDuration(0);
            }
            model.addPhase(event, phase);
        }
        parseCollectionAndDuration(phase, context, value);
    }

    //[0.502s][info   ][gc,task      ] GC(0) Using 8 workers of 8 for evacuation
    //[2.984s][info   ][gc,task       ] GC(1) Using 2 workers of 2 for marking
    private static boolean parseWorker(AbstractGCLogParser parser, ParseRuleContext context, String text) {
        GCModel model = parser.getModel();
        String[] parts = GCLogUtil.splitBySpace(text);
        if (parts.length >= 7 && "Using".equals(parts[0]) && "workers".equals(parts[2])) {
            if ("evacuation".equals(parts[6])) {
                model.setParallelThread(Integer.parseInt(parts[4]));
            } else if ("marking".equals(parts[6])) {
                model.setConcurrentThread(Integer.parseInt(parts[4]));
            }
            return true;
        }
        return false;
    }

    protected abstract GCEventType getGCEventType(String eventString);
}
