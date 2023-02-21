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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.jifa.common.JifaException;
import org.eclipse.jifa.common.util.ErrorUtil;
import org.eclipse.jifa.gclog.event.GCEvent;
import org.eclipse.jifa.gclog.event.OutOfMemory;
import org.eclipse.jifa.gclog.event.evnetInfo.GCCause;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.model.ZGCModel;
import org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType;
import org.eclipse.jifa.gclog.util.I18nStringView;
import org.eclipse.jifa.gclog.util.Key2ValueListMap;
import org.eclipse.jifa.gclog.vo.TimeRange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.eclipse.jifa.gclog.diagnoser.AbnormalSeverity.HIGH;
import static org.eclipse.jifa.gclog.diagnoser.AbnormalSeverity.ULTRA;
import static org.eclipse.jifa.gclog.diagnoser.AbnormalType.ALLOCATION_STALL;
import static org.eclipse.jifa.gclog.diagnoser.AbnormalType.HEAP_MEMORY_FULL_GC;
import static org.eclipse.jifa.gclog.diagnoser.AbnormalType.LONG_YOUNG_GC_PAUSE;
import static org.eclipse.jifa.gclog.diagnoser.AbnormalType.METASPACE_FULL_GC;
import static org.eclipse.jifa.gclog.event.TimedEvent.newByStartEnd;
import static org.eclipse.jifa.gclog.model.GCEventType.FULL_GC;
import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_DOUBLE;

/**
 * To diagnose abnormal in gclog, we mainly try to analyze 3 things:
 * 1. what's going wrong
 * 2. why it is going wrong
 * 3. how to deal with it
 * Currently, we have just implemented finding global serious and definite problem
 * and give general suggestions based on phenomenon without analyzing cause specific cause.
 * In the future, we will
 * 1. do local diagnose on each event, find abnormal of event info, explain its cause
 * and give appropriate suggestion if necessary.
 * 2. Try to find accurate cause and give "the best" suggestion for those serious based on local diagnose.
 */
public class GlobalDiagnoser {
    private GCModel model;
    private AnalysisConfig config;

    private Key2ValueListMap<String, Double> allProblems = new Key2ValueListMap<>();
    private List<AbnormalPoint> mostSeriousProblemList = new ArrayList<>();
    private List<AbnormalPoint> mergedMostSeriousProblemList = new ArrayList<>();
    private AbnormalPoint mostSerious = AbnormalPoint.LEAST_SERIOUS;

    public GlobalDiagnoser(GCModel model, AnalysisConfig config) {
        this.model = model;
        this.config = config;
    }

    public GlobalAbnormalInfo diagnose() {
        findAllAbnormalPoints();
        mergeTimeRanges();
        return generateVo();
    }

    private void findAllAbnormalPoints() {
        for (Method rule : globalDiagnoseRules) {
            try {
                rule.invoke(this);
            } catch (Exception e) {
                ErrorUtil.shouldNotReachHere();
            }
        }
    }

    // Extend the start time forward by 2.5 min so that user can see what happened before the problem.
    // Extend the end time backward by 2.5 min so adjacent events can be merged.
    private static long EXTEND_TIME = 150 * 1000;

    // allow changing this value for testing
    public static void setExtendTime(long extendTime) {
        EXTEND_TIME = extendTime;
    }

    private void mergeTimeRanges() {
        if (mostSerious == AbnormalPoint.LEAST_SERIOUS) {
            return;
        }
        AbnormalPoint first = mostSeriousProblemList.get(0);
        mostSeriousProblemList.sort(Comparator.comparingDouble(ab -> ab.getSite().getStartTime()));
        double start = UNKNOWN_DOUBLE;
        double end = UNKNOWN_DOUBLE;
        for (AbnormalPoint ab : mostSeriousProblemList) {
            if (start == UNKNOWN_DOUBLE) {
                start = ab.getSite().getStartTime();
                end = Math.max(ab.getSite().getStartTime(), ab.getSite().getEndTime());
            } else if (ab.getSite().getStartTime() - end <= 2 * EXTEND_TIME) {
                end = Math.max(Math.max(ab.getSite().getStartTime(), ab.getSite().getEndTime()), end);
            } else {
                AbnormalPoint merged = new AbnormalPoint(first.getType(), newByStartEnd(start, end), first.getSeverity());
                mergedMostSeriousProblemList.add(merged);
                start = ab.getSite().getStartTime();
                end = Math.max(ab.getSite().getStartTime(), ab.getSite().getEndTime());
            }
        }
        if (start != UNKNOWN_DOUBLE) {
            AbnormalPoint merged = new AbnormalPoint(first.getType(), newByStartEnd(start, end), first.getSeverity());
            mergedMostSeriousProblemList.add(merged);
        }
    }

    private GlobalAbnormalInfo generateVo() {
        MostSeriousProblemSummary summary = null;
        if (mostSerious != AbnormalPoint.LEAST_SERIOUS) {
            AbnormalPoint first = mergedMostSeriousProblemList.get(0);
            summary = new MostSeriousProblemSummary(
                    mergedMostSeriousProblemList.stream()
                            .sorted((ab1, ab2) -> Double.compare(ab2.getSite().getDuration(), ab1.getSite().getDuration()))
                            .limit(3)
                            .sorted(Comparator.comparingDouble(ab -> ab.getSite().getStartTime()))
                            .map(ab -> new TimeRange(
                                    Math.max(ab.getSite().getStartTime() - EXTEND_TIME, model.getStartTime()),
                                    Math.min(ab.getSite().getEndTime() + EXTEND_TIME, model.getEndTime())
                            ))
                            .collect(Collectors.toList()),
                    new I18nStringView(AbnormalType.I18N_PREFIX + first.getType().getName()),
                    first.generateDefaultSuggestions(model)
            );
        }
        return new GlobalAbnormalInfo(summary, allProblems.getInnerMap());
    }

    private static List<Method> globalDiagnoseRules = new ArrayList<>();

    static {
        initializeRules();
    }

    private static void initializeRules() {
        Method[] methods = GlobalDiagnoser.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(GlobalDiagnoseRule.class) != null) {
                method.setAccessible(true);
                int mod = method.getModifiers();
                if (Modifier.isAbstract(mod) || Modifier.isFinal(mod) ||
                        !(Modifier.isPublic(mod) || Modifier.isProtected(mod))) {
                    throw new JifaException("Illegal method modifier: " + method);
                }
                globalDiagnoseRules.add(method);
            }
        }
    }

    @GlobalDiagnoseRule
    protected void longGCPause() {
        model.iterateEventsWithinTimeRange(model.getAllEvents(), config.getTimeRange(), event -> {
            event.pauseEventOrPhasesDo(pauseEvent -> {
                if (pauseEvent.getPause() <= config.getLongPauseThreshold()) {
                    return;
                }
                if (pauseEvent.isYoungGC()) {
                    addAbnormalPoint(new AbnormalPoint(LONG_YOUNG_GC_PAUSE, pauseEvent, HIGH));
                }
            });
        });
    }

    @GlobalDiagnoseRule
    protected void allocationStall() {
        if (model.getCollectorType() != GCCollectorType.ZGC) {
            return;
        }
        ZGCModel zModel = (ZGCModel) model;
        for (GCEvent stall : zModel.getAllocationStalls()) {
            addAbnormalPoint(new AbnormalPoint(ALLOCATION_STALL, stall, ULTRA));
        }
    }

    @GlobalDiagnoseRule
    protected void outOfMemory() {
        for (OutOfMemory oom : model.getOoms()) {
            addAbnormalPoint(new AbnormalPoint(AbnormalType.OUT_OF_MEMORY, oom, ULTRA));
        }
    }

    @GlobalDiagnoseRule
    protected void fullGC() {
        boolean shouldAvoidFullGC = model.shouldAvoidFullGC();
        model.iterateEventsWithinTimeRange(model.getGcEvents(), config.getTimeRange(), event -> {
            if (event.getEventType() != FULL_GC) {
                return;
            }
            GCCause cause = event.getCause();
            if (cause.isMetaspaceFullGCCause()) {
                addAbnormalPoint(new AbnormalPoint(METASPACE_FULL_GC, event, ULTRA));
            } else if (shouldAvoidFullGC && cause.isHeapMemoryTriggeredFullGCCause()) {
                addAbnormalPoint(new AbnormalPoint(HEAP_MEMORY_FULL_GC, event, ULTRA));
            } else if (cause == GCCause.SYSTEM_GC) {
                addAbnormalPoint(new AbnormalPoint(AbnormalType.SYSTEM_GC, event, HIGH));
            }
        });
    }

    private void addAbnormalPoint(AbnormalPoint point) {
        allProblems.put(point.getType().getName(), point.getSite().getStartTime());
        int compare = AbnormalPoint.compareByImportance.compare(point, mostSerious);
        if (compare < 0) {
            mostSeriousProblemList.clear();
            mostSerious = point;
        }
        if (compare <= 0) {
            mostSeriousProblemList.add(point);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface GlobalDiagnoseRule {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlobalAbnormalInfo {
        private MostSeriousProblemSummary mostSeriousProblem;
        private Map<String, List<Double>> seriousProblems;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class MostSeriousProblemSummary {
        private List<TimeRange> sites;
        private I18nStringView problem;
        private List<I18nStringView> suggestions;
    }
}
