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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jifa.common.listener.ProgressListener;
import org.eclipse.jifa.common.request.PagingRequest;
import org.eclipse.jifa.common.vo.PageView;
import org.eclipse.jifa.gclog.diagnoser.AnalysisConfig;
import org.eclipse.jifa.gclog.diagnoser.GlobalDiagnoser;
import org.eclipse.jifa.gclog.event.GCEvent;
import org.eclipse.jifa.gclog.event.OutOfMemory;
import org.eclipse.jifa.gclog.event.Safepoint;
import org.eclipse.jifa.gclog.event.TimedEvent;
import org.eclipse.jifa.gclog.event.evnetInfo.GCCause;
import org.eclipse.jifa.gclog.event.evnetInfo.GCEventLevel;
import org.eclipse.jifa.gclog.event.evnetInfo.GCMemoryItem;
import org.eclipse.jifa.gclog.event.evnetInfo.GCPause;
import org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea;
import org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType;
import org.eclipse.jifa.gclog.model.modeInfo.GCLogMetadata;
import org.eclipse.jifa.gclog.model.modeInfo.GCLogStyle;
import org.eclipse.jifa.gclog.model.modeInfo.VmOptions;
import org.eclipse.jifa.gclog.util.Constant;
import org.eclipse.jifa.gclog.util.DoubleData;
import org.eclipse.jifa.gclog.util.LongData;
import org.eclipse.jifa.gclog.vo.MemoryStatistics;
import org.eclipse.jifa.gclog.vo.MemoryStatistics.MemoryStatisticsItem;
import org.eclipse.jifa.gclog.vo.ObjectStatistics;
import org.eclipse.jifa.gclog.vo.PauseStatistics;
import org.eclipse.jifa.gclog.vo.PhaseStatistics;
import org.eclipse.jifa.gclog.vo.PhaseStatistics.ParentStatisticsInfo;
import org.eclipse.jifa.gclog.vo.PhaseStatistics.PhaseStatisticItem;
import org.eclipse.jifa.gclog.vo.TimeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.EDEN;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.HEAP;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.HUMONGOUS;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.METASPACE;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.OLD;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.SURVIVOR;
import static org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea.YOUNG;
import static org.eclipse.jifa.gclog.model.GCEventType.CMS_CONCURRENT_MARK_SWEPT;
import static org.eclipse.jifa.gclog.model.GCEventType.FULL_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_CONCURRENT_CYCLE;
import static org.eclipse.jifa.gclog.model.GCEventType.G1_MIXED_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.SAFEPOINT;
import static org.eclipse.jifa.gclog.model.GCEventType.YOUNG_GC;
import static org.eclipse.jifa.gclog.model.GCEventType.ZGC_GARBAGE_COLLECTION;
import static org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType.CMS;
import static org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType.G1;
import static org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType.PARALLEL;
import static org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType.SERIAL;
import static org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType.UNKNOWN;
import static org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType.ZGC;

/**
 * GCModel contains all direct information from log and analysed data for query
 */
public abstract class GCModel {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GCModel.class);

    // These 3 event lists below are used to support events like young/mixed/old/full. Other events. like
    // safepoint, allocation stall will be save in other lists. gcEvents and allEvents may be used in parsing.
    // When calculating derived info, gcEvents may be transformed, and allEvents and gcCollectionEvents will be
    // rebuilt.
    private List<GCEvent> gcEvents           = new ArrayList<>(); // store parent events only
    private List<GCEvent> allEvents          = new ArrayList<>(); // store all events, order by their appearance in log
    private List<GCEvent> gcCollectionEvents = new ArrayList<>(); // store events that contain collection info

    private List<Safepoint>   safepoints = new ArrayList<>();
    private List<OutOfMemory> ooms       = new ArrayList<>();
    // time from beginning of program
    private double            startTime  = Constant.UNKNOWN_DOUBLE;
    private double            endTime    = Constant.UNKNOWN_DOUBLE;

    private int parallelThread   = Constant.UNKNOWN_INT;
    private int concurrentThread = Constant.UNKNOWN_INT;

    // in ms. referenceTimestamp + uptime of events is the true timestamp of events.
    // notice that uptime may not begin from 0
    private double    referenceTimestamp = Constant.UNKNOWN_DOUBLE;
    //shared basic info among different collectors
    private VmOptions vmOptions;

    //data prepared for api
    private GCCollectorType collectorType;
    private GCLogStyle      logStyle;
    private GCLogMetadata   metadata;
    private List<String>    gcEventsDetailCache;
    private Long            storageInfoId;

    public Long getStorageInfoId() {
        return storageInfoId;
    }

    public void setStorageInfoId(Long storageInfoId) {
        this.storageInfoId = storageInfoId;
    }

    public GCModel() {
    }

    public GCModel(GCCollectorType collectorType) {
        this.collectorType = collectorType;
    }

    public void setCollectorType(GCCollectorType collectorType) {
        this.collectorType = collectorType;
    }

    public GCCollectorType getCollectorType() {
        return collectorType;
    }

    public GCLogStyle getLogStyle() {
        return logStyle;
    }

    public void setLogStyle(GCLogStyle logStyle) {
        this.logStyle = logStyle;
    }

    public List<GCEvent> getGcCollectionEvents() {
        return gcCollectionEvents;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    private static final double START_TIME_ZERO_THRESHOLD = 60000;

    public void setStartTime(double startTime) {
        if (startTime < START_TIME_ZERO_THRESHOLD) {
            startTime = 0;
        }
        this.startTime = startTime;
    }

    public boolean isGenerational() {
        return collectorType != ZGC;
    }

    public boolean isPauseless() {
        return collectorType == ZGC;
    }

    public List<GCEvent> getAllEvents() {
        return allEvents;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public double getDuration() {
        return getEndTime() - getStartTime();
    }

    public boolean isEmpty() {
        return gcEvents.isEmpty();
    }

    public List<GCEvent> getGcEvents() {
        return gcEvents;
    }

    public void setGcEvents(List<GCEvent> gcEvents) {
        this.gcEvents = gcEvents;
    }

    public GCEvent createAndGetEvent() {
        GCEvent event = new GCEvent(gcEvents.size());
        gcEvents.add(event);
        return event;
    }

    public boolean hasOldGC() {
        return collectorType == G1 || collectorType == CMS;
    }

    public <T extends TimedEvent> void iterateEventsWithinTimeRange(List<T> eventList, TimeRange range, Consumer<T> consumer) {
        int indexLow = binarySearchEventIndex(eventList, range.getStart(), true);
        int indexHigh = binarySearchEventIndex(eventList, range.getEnd(), false);

        for (int i = indexLow; i < indexHigh; i++) {
            consumer.accept(eventList.get(i));
        }
    }

    // Return index of the first event after time if searchLow, first event after time if !searchLow  .
    // eventList must be ordered by startTime.
    private int binarySearchEventIndex(List<? extends TimedEvent> eventList, double time, boolean searchLow) {
        if (searchLow && time <= getStartTime()) {
            return 0;
        } else if (!searchLow && time >= getEndTime()) {
            return eventList.size();
        }

        TimedEvent eventForSearching = new TimedEvent(time);
        int result = Collections.binarySearch(eventList, eventForSearching, Comparator.comparingDouble(TimedEvent::getStartTime));
        if (result < 0) {
            return -(result + 1);
        } else {
            if (searchLow) {
                while (result >= 0 && eventList.get(result).getStartTime() >= time) {
                    result--;
                }
                return result + 1;
            } else {
                while (result < eventList.size() && eventList.get(result).getStartTime() <= time) {
                    result++;
                }
                return result;
            }
        }
    }

    public List<Safepoint> getSafepoints() {
        return safepoints;
    }

    public void addSafepoint(Safepoint safepoint) {
        safepoints.add(safepoint);
    }

    public List<OutOfMemory> getOoms() {
        return ooms;
    }

    public void addOom(OutOfMemory oom) {
        ooms.add(oom);
    }

    private TimeRange makeValidTimeRange(TimeRange range) {
        if (range == null) {
            return new TimeRange(getStartTime(), getEndTime());
        }
        double start = Math.max(range.getStart(), getStartTime());
        double end = Math.min(range.getEnd(), getEndTime());
        return new TimeRange(start, end);
    }

    private void putPhaseStatisticData(GCEvent event, String name, Map<String, DoubleData[]> map) {
        DoubleData[] data = map.getOrDefault(name, null);
        if (data == null) {
            data = new DoubleData[2];
            data[0] = new DoubleData();
            data[1] = new DoubleData();
            map.put(name, data);
        }
        data[0].add(event.getInterval());
        data[1].add(event.getDuration());
    }

    private PhaseStatisticItem makePhaseStatisticItem(String name, DoubleData[] data) {
        return new PhaseStatisticItem(name, data[1].getN(), data[0].average(), data[0].getMin()
                , data[1].average(), data[1].getMax(), data[1].getSum());
    }

    public PhaseStatistics getPhaseStatistics(TimeRange range) {
        range = makeValidTimeRange(range);
        List<GCEventType> parents = getParentEventTypes();
        // DoubleData[] is an array of interval and duration
        Map<String, DoubleData[]> parentData = new HashMap<>();
        List<Map<String, DoubleData[]>> phaseData = new ArrayList<>();
        List<Map<String, DoubleData[]>> causeData = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            phaseData.add(new HashMap<>());
            causeData.add(new HashMap<>());
        }
        iterateEventsWithinTimeRange(gcEvents, range, event -> {
            int index = parents.indexOf(event.getEventType());
            if (index < 0) {
                return;
            }
            putPhaseStatisticData(event, event.getEventType().getName(), parentData);
            if (event.getCause() != null) {
                putPhaseStatisticData(event, event.getCause().getName(), causeData.get(index));
            }
            if (event.getPhases() != null) {
                for (GCEvent phase : event.getPhases()) {
                    putPhaseStatisticData(phase, phase.getEventType().getName(), phaseData.get(index));
                }
            }
        });
        List<ParentStatisticsInfo> result = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            String name = parents.get(i).getName();
            if (parentData.containsKey(name)) {
                result.add(new ParentStatisticsInfo(
                        makePhaseStatisticItem(parents.get(i).getName(), parentData.get(name)),
                        phaseData.get(i).entrySet().stream().map(entry -> makePhaseStatisticItem(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList()),
                        causeData.get(i).entrySet().stream().map(entry -> makePhaseStatisticItem(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList())
                ));
            }
        }
        return new PhaseStatistics(result);
    }

    public PauseStatistics getPauseStatistics(TimeRange range) {
        range = makeValidTimeRange(range);
        DoubleData pause = new DoubleData(true);
        iterateEventsWithinTimeRange(gcEvents, range, e -> {
            e.pauseEventOrPhasesDo(event -> pause.add(event.getPause()));
        });
        return new PauseStatistics(
                pause.getN() == 0 ? Constant.UNKNOWN_DOUBLE : 1 - pause.getSum() / range.length(),
                pause.average(),
                pause.getMedian(),
                pause.getPercentile(0.99),
                pause.getPercentile(0.999),
                pause.getMax(),
                pause.getSum()
        );
    }

    public Map<String, int[]> getPauseDistribution(TimeRange range, int[] partitions) {
        range = makeValidTimeRange(range);

        Map<String, int[]> distribution = new HashMap<>();
        iterateEventsWithinTimeRange(gcEvents, range, e -> {
            e.pauseEventOrPhasesDo(event -> {
                if (event.getPause() >= 0) {
                    String eventType = event.getEventType().getName();
                    int pause = (int) event.getPause();
                    int index = Arrays.binarySearch(partitions, pause);
                    if (index < 0) {
                        index = -index - 2;
                    }
                    if (index < 0) {
                        return;
                    }
                    int[] nums = distribution.getOrDefault(eventType, null);
                    if (nums == null) {
                        nums = new int[partitions.length];
                        distribution.put(eventType, nums);
                    }
                    nums[index]++;
                }
            });
        });
        return distribution;
    }

    public MemoryStatistics getMemoryStatistics(TimeRange range) {
        range = makeValidTimeRange(range);

        // 1st dimension is generation, see definition of MemoryStatistics
        // 2nd dimension is capacityAvg, usedMax, usedAvgAfterFullGC,usedAvgAfterOldGC see definition of MemoryStatisticsItem
        // usedAvgAfterOldGC is more complicated, will deal with it afterwards
        LongData[][] data = new LongData[5][4];
        MemoryArea[] generations = {YOUNG, OLD, HUMONGOUS, HEAP, METASPACE};
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                data[i][j] = new LongData();
            }
        }
        iterateEventsWithinTimeRange(gcCollectionEvents, range, event -> {
            for (int genIndex = 0; genIndex < generations.length; genIndex++) {
                MemoryArea generation = generations[genIndex];
                GCMemoryItem memory = event.getMemoryItem(generation);
                if (memory != null) {
                    data[genIndex][0].add(memory.getTotal());
                    data[genIndex][1].add(Math.max(memory.getPreUsed(), memory.getPostUsed()));
                    if (event.isFullGC() && generation != YOUNG) {
                        data[genIndex][2].add(memory.getPostUsed());
                    }
                }
            }
        });
        calculateUsedAvgAfterOldGC(range, data);

        // generate result
        MemoryStatistics statistics = new MemoryStatistics();
        statistics.setYoung(
                new MemoryStatisticsItem((long) data[0][0].average(), data[0][1].getMax(), Constant.UNKNOWN_LONG, Constant.UNKNOWN_LONG));
        statistics.setOld(new MemoryStatisticsItem((long) data[1][0].average(), data[1][1].getMax(), (long) data[1][2].average(),
                (long) data[1][3].average()));
        statistics.setHumongous(new MemoryStatisticsItem((long) data[2][0].average(), data[2][1].getMax(), (long) data[2][2].average(),
                (long) data[2][3].average()));
        statistics.setHeap(new MemoryStatisticsItem((long) data[3][0].average(), data[3][1].getMax(), (long) data[3][2].average(),
                (long) data[3][3].average()));
        statistics.setMetaspace(new MemoryStatisticsItem(Constant.UNKNOWN_LONG, data[4][1].getMax(), (long) data[4][2].average(),
                (long) data[4][3].average()));
        // Metaspace capacity printed in gclog may be reserve space rather than commit size, so we
        // try to read it from vm option
        if (isMetaspaceCapacityReliable()) {
            statistics.getMetaspace().setCapacityAvg((long) data[4][0].average());
        } else if (vmOptions != null) {
            statistics.getMetaspace().setCapacityAvg(vmOptions.getMetaspaceSize());
        }
        return statistics;
    }

    protected void calculateUsedAvgAfterOldGC(TimeRange range, LongData[][] data) {
        // for overriding
    }

    public ObjectStatistics getObjectStatistics(TimeRange range) {
        range = makeValidTimeRange(range);
        LongData allocation = new LongData();
        LongData promotion = new LongData();
        iterateEventsWithinTimeRange(gcCollectionEvents, range, event -> {
            allocation.add(event.getAllocation());
            promotion.add(event.getPromotion());
        });
        return new ObjectStatistics(
                allocation.getSum() != Constant.UNKNOWN_DOUBLE ? allocation.getSum() / range.length() : Constant.UNKNOWN_DOUBLE,
                promotion.getSum() != Constant.UNKNOWN_DOUBLE ? promotion.getSum() / range.length() : Constant.UNKNOWN_DOUBLE,
                (long) promotion.average(), promotion.getMax()
        );
    }

    // decide start and end time using events
    public void autoDecideStartEndTime() {
        gcEvents.sort(Comparator.comparingDouble(GCEvent::getStartTime));
        if (gcEvents.size() == 0) {
            return;
        }
        GCEvent event = gcEvents.get(gcEvents.size() - 1);
        double endTime = event.getEndTime();
        if (event.hasPhases()) {
            endTime = Math.max(endTime, event.getPhases().get(event.getPhases().size() - 1).getEndTime());
        }
        setEndTime(Math.max(this.endTime, endTime));
    }

    public Map<String, List<Object[]>> getTimeGraphData(String[] dataTypes) {
        Map<String, List<Object[]>> result = new LinkedHashMap<>();
        for (String dataType : dataTypes) {
            if (dataType.endsWith("Used") || dataType.endsWith("Capacity")) {
                result.put(dataType, getTimeGraphMemoryData(dataType));
            } else if (dataType.equals("promotion")) {
                result.put(dataType, getTimeGraphPromotionData());
            } else if (dataType.equals("reclamation")) {
                result.put(dataType, getTimeGraphReclamationData());
            } else {
                result.put(dataType, getTimeGraphDurationData(dataType));
            }
        }
        return result;
    }

    /**
     * 数据格式
     *
     * "2022-07-12 19:14:58.766 12.780: [Young GC (G1 Evacuation Pause), 0.050s] [Young: 352M->48M(352M)] [Old: 7M->18M(6816M)"
     * + "] [Heap: 359M->66M(7168M)] [promotion 11366 K] [User=0.16s Sys=0.02s Real=0.05s]";
     */
    public Map<String, List<Object[]>> constructTimeGraphData() {

        GCDetailFilter filter = new GCDetailFilter("Young GC", null, null, null, null);
        GCDetailFilter filterFullGc = new GCDetailFilter("Full GC", null, null, null, null);

        //分别构建fgc, ygc两种类型
        Map<String, List<Object[]>> timeGraph = getTimeGraph(getGCDetails(filter), "Young GC");
        Map<String, List<Object[]>> fgcTimeGraph = getTimeGraph(getGCDetails(filterFullGc), "Full GC");

        //fgc，ygc timeGraph合并
        timeGraph.forEach((k, v) -> v.addAll(fgcTimeGraph.get(k)));

        return timeGraph;
    }

    public List<GCTimeStamp> constructFgcTimeGraphData(Long startTime) {

        GCDetailFilter filterFullGc = new GCDetailFilter("Full GC", null, null, null, null);

        //分别构建fgc, ygc两种类型
        Map<String, List<Object[]>> fgcTimeGraph = getTimeGraph(getGCDetails(filterFullGc), "fgc");

        //获取FullGc之后 young Gen的使用
        List<GCTimeStamp> GCTimeStamps = fgcTimeGraph.get("Young Gen").stream().map(
                item -> new GCTimeStamp(startTime, item[0], item[1], item[2], "young Gen"))
                .collect(Collectors.toList());
        //获取FullGc之后 Old Gen的使用
        List<GCTimeStamp> GCTimeStampOlds = fgcTimeGraph.get("Old Gen").stream().map(
                item -> new GCTimeStamp(startTime, item[0], item[1], item[2], "Old Gen"))
                .collect(Collectors.toList());
        GCTimeStamps.addAll(GCTimeStampOlds);
        return GCTimeStamps;
    }

    private Map<String, List<Object[]>> getTimeGraph(List<String> gcContext, String type) {
        Map<String, List<Object[]>> timeGraph = new LinkedHashMap<>();
        List<Object[]> pauseResult = new ArrayList<>();
        List<Object[]> youngResult = new ArrayList<>();
        List<Object[]> oldResult = new ArrayList<>();
        List<Object[]> heapResult = new ArrayList<>();
        List<Object[]> metaspaceResult = new ArrayList<>();
        List<Object[]> metaAllocateResult = new ArrayList<>();

        gcContext.forEach(context -> {
            try {
                //1.转换成时间戳
                String timeStr = context.substring(24);
                String timeStamp = timeStr.substring(0, timeStr.indexOf(":"));
                double time = Double.parseDouble(timeStamp) * 1000;

                //2.转换成暂停时间
                String pauseBegin = context.substring(context.indexOf(",") + 2);
                int pauseEndIndex = pauseBegin.indexOf("]") - 1;
                double pauseTime = Double.parseDouble(pauseBegin.substring(0, pauseEndIndex));
                pauseResult.add(new Object[] {(long) time, pauseTime, type});

                //3.转化成youngBefore、youngAfter
                GCTimeStamp gcTimeStamp = getBeAndAf(pauseBegin);
                youngResult.add(new Object[] {(long) time, gcTimeStamp.getBefore(), gcTimeStamp.getAfter()});
                //4.转化成oldBefore、oldAfter
                gcTimeStamp = getBeAndAf(gcTimeStamp.getContext());
                oldResult.add(new Object[] {(long) time, gcTimeStamp.getBefore(), gcTimeStamp.getAfter()});

                //5.转化成heapBefore、heapAfter 分为ygc fgc两种type
                gcTimeStamp = getBeAndAf(gcTimeStamp.getContext());
                heapResult.add(new Object[] {(long) time, gcTimeStamp.getBefore(), gcTimeStamp.getAfter(), type});

                //6.转换成MetaSpaceGCBefore、MetaSpaceGCAfter
                if (StringUtils.equals(type, "Full GC")) {
                    gcTimeStamp = getBeAndAf(gcTimeStamp.getContext());
                    metaspaceResult.add(new Object[] {(long) time, gcTimeStamp.getBefore(), gcTimeStamp.getAfter()});

                    gcTimeStamp = getMetaSpace(gcTimeStamp.getContext());
                    metaAllocateResult.add(new Object[]{(long) time, gcTimeStamp.getBefore()});
                }

            } catch (Exception e) {
                //do nothing
            }

        });
        timeGraph.put("Young Gen", youngResult);
        timeGraph.put("Old Gen", oldResult);
        timeGraph.put("Pause GC Duration", pauseResult);
        timeGraph.put("Heap", heapResult);
        timeGraph.put("metaSpace", metaspaceResult);
        timeGraph.put("metaAllocated", metaAllocateResult);
        return timeGraph;
    }

    /**
     * 解析 gc、heap前后相关信息
     *
     * @param context
     * @return
     */
    private GCTimeStamp getBeAndAf(String context) {
        String before = context.substring(context.indexOf(":") + 2);
        int startIndex = before.indexOf("M");
        String beforeResult = before.substring(0, startIndex);

        if (StringUtils.equals("0", beforeResult)) {
            return new GCTimeStamp(before, Double.parseDouble(beforeResult), Double.parseDouble(beforeResult));
        }
        String after = before.substring(startIndex + 3);
        int endIndex = after.indexOf("M");
        String afterResult = after.substring(0, endIndex);
        return new GCTimeStamp(after, Double.parseDouble(beforeResult), Double.parseDouble(afterResult));
    }

    /**
     * 解析 获取metaspace
     * [Full GC (Allocation Failure), 4.735s] [Young: 0M->0M(776M)] [Old: 6757M->1071M(6392M)] [Heap: 6757M->1071M(7168M)] [Metaspace:
     * 207M->207M(1258M)] [interval 31.714s] [User=6.05s Sys=0.10s Real=4.73s]
     *
     * @param context
     * @return
     */
    private GCTimeStamp getMetaSpace(String context) {
        String before = context.substring(context.indexOf("(") + 1);
        int endIndex = before.indexOf("M");
        String result = before.substring(0, endIndex);
        return new GCTimeStamp(before, Double.parseDouble(result),Double.parseDouble(result));

    }

    private List<Object[]> getTimeGraphMemoryData(String dataType) {
        boolean used = dataType.endsWith("Used");
        String areString = dataType.substring(0, dataType.length() - (used ? "Used" : "Capacity").length());
        MemoryArea area = MemoryArea.getMemoryArea(areString);
        List<Object[]> result = new ArrayList<>();
        for (GCEvent event : this.gcCollectionEvents) {
            GCMemoryItem memory = event.getMemoryItem(area);
            if (memory == null) {
                continue;
            }
            if (used) {
                if (memory.getPreUsed() != Constant.UNKNOWN_LONG) {
                    result.add(new Object[] {(long) event.getStartTime(), memory.getPreUsed()});
                }
                if (memory.getPostUsed() != Constant.UNKNOWN_LONG) {
                    result.add(new Object[] {(long) event.getEndTime(), memory.getPostUsed()});
                }
            } else {
                if (memory.getTotal() != Constant.UNKNOWN_LONG) {
                    result.add(new Object[] {(long) event.getEndTime(), memory.getTotal()});
                }
            }
        }
        result.sort(Comparator.comparingLong(d -> (long) d[0]));
        return result;
    }

    private List<Object[]> getTimeGraphPromotionData() {
        return allEvents.stream()
                .filter(event -> event.getPromotion() >= 0)
                .map(event -> new Object[] {(long) event.getStartTime(), event.getPromotion()})
                .collect(Collectors.toList());
    }

    private List<Object[]> getTimeGraphReclamationData() {
        return gcCollectionEvents.stream()
                .filter(event -> event.getReclamation() != Constant.UNKNOWN_LONG)
                .map(event -> new Object[] {(long) event.getStartTime(), event.getReclamation()})
                .collect(Collectors.toList());
    }

    private List<Object[]> getTimeGraphDurationData(String phaseName) {
        return allEvents.stream()
                .filter(event -> event.getEventType().getName().equals(phaseName)
                        && event.getDuration() != Constant.UNKNOWN_DOUBLE)
                .map(event -> new Object[] {(long) event.getStartTime(), event.getDuration()})
                .collect(Collectors.toList());
    }

    public GlobalDiagnoser.GlobalAbnormalInfo getGlobalDiagnoseInfo(AnalysisConfig config) {
        config.setTimeRange(makeValidTimeRange(config.getTimeRange()));
        return new GlobalDiagnoser(this, config).diagnose();
    }

    public long getRecommendMaxHeapSize() {
        // not supported
        return Constant.UNKNOWN_INT;
    }

    public void putEvent(GCEvent event) {
        gcEvents.add(event);
        allEvents.add(event);
    }

    public void addPhase(GCEvent parent, GCEvent phase) {
        allEvents.add(phase);
        parent.addPhase(phase);
    }

    private static final List<GCCollectorType> SUPPORTED_COLLECTORS = Lists.newArrayList(G1, CMS, SERIAL, PARALLEL, UNKNOWN, ZGC);

    private boolean isGCCollectorSupported(GCCollectorType collectorType) {
        return SUPPORTED_COLLECTORS.contains(collectorType);
    }

    public void calculateDerivedInfo(ProgressListener progressListener) {
        if (!isGCCollectorSupported(collectorType)) {
            throw new UnsupportedOperationException("Collector not supported.");
        }

        allEvents = null;
        // must be done before other steps
        filterInvalidEvents();
        autoDecideStartEndTime();
        decideAndFixEventInfo();

        // let subclass do something
        doBeforeCalculatingDerivedInfo();

        rebuildEventLists();

        // calculate derived data for events themselves
        calculateEventTimestamps();
        calculateEventsInterval();
        calculateEventsMemoryInfo();

        // data in events should not change after this line
        // calculate specific data prepared for route api, order of these calls doesn't matter
        calculateGCEventDetails();
        calculateGcModelMetadata();
    }

    protected void doBeforeCalculatingDerivedInfo() {
    }

    private void rebuildEventLists() {
        allEvents = new ArrayList<>();
        for (GCEvent event : gcEvents) {
            allEvents.add(event);
            if (event.hasPhases()) {
                allEvents.addAll(event.getPhases());
            }
        }
        allEvents.sort(Comparator.comparingDouble(GCEvent::getStartTime));
    }

    private void decideAndFixEventInfo() {
        for (GCEvent event : gcEvents) {
            List<GCEvent> phases = event.getPhases();
            if (phases == null) {
                continue;
            }
            for (int i = phases.size() - 1; i >= 0; i--) {
                GCEvent phase = phases.get(i);
                if (phase.getDuration() == Constant.UNKNOWN_DOUBLE) {
                    //this is unlikely to happen, just give a reasonable value
                    phase.setDuration(phases.get(phases.size() - 1).getStartTime() - phase.getStartTime());
                }
            }
            if (event.getDuration() == Constant.UNKNOWN_DOUBLE && getStartTime() != Constant.UNKNOWN_DOUBLE) {
                event.setDuration(phases.get(phases.size() - 1).getEndTime() - event.getStartTime());
            }
        }
    }

    private void calculateEventTimestamps() {
        double referenceTimestamp = getReferenceTimestamp();
        if (referenceTimestamp == Constant.UNKNOWN_DOUBLE) {
            return;
        }
        for (GCEvent event : getGcEvents()) {
            event.setStartTimestamp(referenceTimestamp + event.getStartTime());
            if (event.hasPhases()) {
                for (GCEvent phase : event.getPhases()) {
                    phase.setStartTimestamp(referenceTimestamp + phase.getStartTime());
                }
            }
        }
    }

    private void calculateGCEventDetails() {
        // toString of events will not change in the future, cache them
        gcEventsDetailCache = new ArrayList<>();
        for (GCEvent event : gcEvents) {
            gcEventsDetailCache.add(event.toString());
        }
    }

    /**
     * calculate heap size(young, humongous, old, metaspace,total),
     * object allocation, reclamation and promotion
     */
    private void calculateEventsMemoryInfo() {
        for (GCEvent event : gcEvents) {
            calculateEventMemoryItems(event);
        }
        gcCollectionEvents.sort(Comparator.comparingDouble(GCEvent::getStartTime));

        long lastTotalMemory = 0;
        for (GCEvent event : gcCollectionEvents) {
            GCMemoryItem young = event.getMemoryItem(YOUNG);
            GCMemoryItem total = event.getMemoryItem(HEAP);
            GCMemoryItem humongous = event.getMemoryItem(HUMONGOUS);
            // reclamation
            // sometimes it may have been calculated during parsing log
            if (event.getReclamation() == Constant.UNKNOWN_INT && total != null &&
                    total.getPreUsed() != Constant.UNKNOWN_INT && total.getPostUsed() != Constant.UNKNOWN_INT) {
                event.setReclamation(total.getPreUsed() - total.getPostUsed());
            }
            // promotion
            if (event.getPromotion() == Constant.UNKNOWN_INT
                    && event.hasPromotion() && event.getEventType() != G1_MIXED_GC
                    && young != null && total != null) {
                // notice: g1 young mixed gc should have promotion, but we have no way to know it exactly
                long youngReduction = young.getMemoryReduction();
                long totalReduction = total.getMemoryReduction();
                if (youngReduction != Constant.UNKNOWN_INT && totalReduction != Constant.UNKNOWN_INT) {
                    long promotion = youngReduction - totalReduction;
                    if (humongous != null && humongous.getMemoryReduction() != Constant.UNKNOWN_INT) {
                        promotion -= humongous.getMemoryReduction();
                    }
                    event.setPromotion(promotion);
                }
            }
            // allocation
            if (event.getAllocation() == Constant.UNKNOWN_INT &&
                    total != null && total.getPreUsed() != Constant.UNKNOWN_INT) {
                // As to concurrent event, allocation is composed of two parts: allocation between two adjacent events
                // and during event. If original allocation is not unknown, that value is allocation during event.
                event.setAllocation(zeroIfUnknownInt(event.getAllocation()) + total.getPreUsed() - lastTotalMemory);
                lastTotalMemory = total.getPostUsed();
            }
        }
    }

    private long zeroIfUnknownInt(long x) {
        return x == Constant.UNKNOWN_INT ? 0 : x;
    }

    private void calculateEventMemoryItems(GCEvent event) {
        if (event.hasPhases()) {
            for (GCEvent phase : event.getPhases()) {
                calculateEventMemoryItems(phase);
            }
        }

        if (event.getMemoryItems() == null) {
            return;
        }
        gcCollectionEvents.add(event);

        // hack: Survivor capacity of g1 is not printed in jdk8. Make it equal to pre used so that
        // we can calculate young and old capacity
        if (event.getMemoryItem(SURVIVOR) != null &&
                event.getMemoryItem(SURVIVOR).getTotal() == Constant.UNKNOWN_INT) {
            event.getMemoryItem(SURVIVOR).setTotal(event.getMemoryItem(SURVIVOR).getPreUsed());
        }

        //case 1: know eden and survivor, calculate young
        GCMemoryItem young = event.getMemoryItemOrEmptyObject(EDEN)
                .merge(event.getMemoryItem(SURVIVOR));
        young.setArea(YOUNG);
        event.setMemoryItem(event.getMemoryItemOrEmptyObject(YOUNG)
                .updateIfAbsent(young), true);

        //case 2: know young and old, calculate heap
        GCMemoryItem heap = event.getMemoryItemOrEmptyObject(YOUNG)
                .merge(event.getMemoryItem(OLD))
                .mergeIfPresent(event.getMemoryItem(HUMONGOUS));
        heap.setArea(HEAP);
        event.setMemoryItem(event.getMemoryItemOrEmptyObject(HEAP)
                .updateIfAbsent(heap), true);

        //case 3: know old and heap, calculate young
        young = event.getMemoryItemOrEmptyObject(HEAP)
                .subtract(event.getMemoryItem(OLD))
                .subtractIfPresent(event.getMemoryItem(HUMONGOUS));
        young.setArea(YOUNG);
        event.setMemoryItem(event.getMemoryItemOrEmptyObject(YOUNG)
                .updateIfAbsent(young), true);

        //case 4: know young and heap, calculate old
        GCMemoryItem old = event.getMemoryItemOrEmptyObject(HEAP)
                .subtract(event.getMemoryItem(YOUNG))
                .subtractIfPresent(event.getMemoryItem(HUMONGOUS));
        old.setArea(OLD);
        event.setMemoryItem(event.getMemoryItemOrEmptyObject(OLD)
                .updateIfAbsent(old), true);
    }

    private void filterInvalidEvents() {
        // Sometimes the given log is just a part of the complete log. This may lead to some incomplete events at
        // beginning or end of this log. Such event at beginning is likely to have been dealt by parser, so here we try
        // to deal with the last event
        if (gcEvents.get(gcEvents.size() - 1).getEndTime() == Constant.UNKNOWN_DOUBLE) {
            gcEvents.remove(gcEvents.size() - 1);
        }
    }

    protected static List<GCEventType> calcAllEventTypes(GCCollectorType collector) {
        return GCEventType.getAllEventTypes().stream()
                .filter(e -> e.getGcs().contains(collector))
                .collect(Collectors.toList());
    }

    protected static List<GCEventType> calcPauseEventTypes(GCCollectorType collector) {
        return GCEventType.getAllEventTypes().stream()
                .filter(e -> e.getGcs().contains(collector) && e.getPause() == GCPause.PAUSE)
                .collect(Collectors.toList());
    }

    protected static List<GCEventType> calcMainPauseEventTypes(GCCollectorType collector) {
        return GCEventType.getAllEventTypes().stream()
                .filter(e -> e.getGcs().contains(collector) && e.isMainPauseEventType())
                .collect(Collectors.toList());
    }

    protected static List<GCEventType> calcParentEventTypes(GCCollectorType collector) {
        return Stream.of(YOUNG_GC, G1_MIXED_GC, CMS_CONCURRENT_MARK_SWEPT, G1_CONCURRENT_CYCLE, FULL_GC, ZGC_GARBAGE_COLLECTION)
                .filter(e -> e.getGcs().contains(collector))
                .collect(Collectors.toList());
    }

    protected abstract List<GCEventType> getAllEventTypes();

    protected abstract List<GCEventType> getPauseEventTypes();

    protected abstract List<GCEventType> getMainPauseEventTypes();

    protected abstract List<GCEventType> getParentEventTypes();

    protected abstract List<GCEventType> getImportantEventTypes();

    public GCEvent getLastEventWithCondition(Predicate<GCEvent> condition) {
        for (int i = allEvents.size() - 1; i >= 0; i--) {
            GCEvent event = allEvents.get(i);
            if (condition.test(event)) {
                return event;
            }
        }
        return null;
    }

    // mainly used in jdk8, where gcid may be missing
    public GCEvent getLastEventOfType(GCEventType... types) {
        List<GCEventType> typeList = Arrays.asList(types);
        return getLastEventWithCondition(event -> typeList.contains(event.getEventType()));
    }

    // mainly used in parser of jdk11, where gcid is always logged if tag includes gc
    public GCEvent getLastEventOfGCID(int gcid) {
        return getLastEventWithCondition(event -> event.getEventLevel() == GCEventLevel.EVENT && event.getGcid() == gcid);
    }

    public double getReferenceTimestamp() {
        return referenceTimestamp;
    }

    public void setReferenceTimestamp(double referenceTimestamp) {
        this.referenceTimestamp = referenceTimestamp;
    }

    public void setVmOptions(VmOptions vmOptions) {
        this.vmOptions = vmOptions;
    }

    public VmOptions getVmOptions() {
        return vmOptions;
    }

    private void calculateEventsInterval() {
        Map<GCEventType, Double> lastEndTime = new HashMap<>();
        for (GCEvent event : allEvents) {
            GCEventType eventType = event.getEventType();
            // regard mixed gc as young gc
            if (event.isYoungGC()) {
                eventType = YOUNG_GC;
            }
            if (lastEndTime.containsKey(eventType)) {
                event.setInterval(Math.max(0, event.getStartTime() - lastEndTime.get(eventType)));
            }
            lastEndTime.put(eventType, event.getEndTime());
        }
    }

    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        for (GCEvent event : gcEvents) {
            sb.append(event).append("\n");
            if (event.hasPhases()) {
                for (GCEvent phase : event.getPhases()) {
                    sb.append("         ").append(phase).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public boolean shouldAvoidFullGC() {
        return collectorType != SERIAL && collectorType != PARALLEL && collectorType != UNKNOWN;
    }

    public List<String> getGCDetails() {
        return gcEventsDetailCache;
    }

    public PageView<String> getGCDetails(PagingRequest pagingRequest, GCDetailFilter filter) {
        List<String> details = getGCDetails();
        List<String> filtered = new ArrayList<>();
        for (int i = 0; i < details.size(); i++) {
            if (!filter.isFiltered(gcEvents.get(i))) {
                filtered.add(details.get(i));
            }
        }
        List<String> result = new ArrayList<>();
        int firstIndex = (pagingRequest.getPage() - 1) * pagingRequest.getPageSize();
        int lastIndex = Math.min(firstIndex + pagingRequest.getPageSize(), filtered.size());
        for (int i = firstIndex; i < lastIndex; i++) {
            result.add(filtered.get(i));
        }
        return new PageView<>(pagingRequest, filtered.size(), result);
    }

    public List<String> getGCDetails(GCDetailFilter filter) {
        List<String> details = getGCDetails();
        List<String> filtered = new ArrayList<>();
        for (int i = 0; i < details.size(); i++) {
            if (!filter.isFiltered(gcEvents.get(i))) {
                filtered.add(details.get(i));
            }
        }
        return filtered;
    }

    public GCLogMetadata getGcModelMetadata() {
        return metadata;
    }

    private void calculateGcModelMetadata() {
        metadata = new GCLogMetadata();
        metadata.setCauses(gcEvents.stream()
                .map(GCEvent::getCause)
                .filter(Objects::nonNull)
                .map(GCCause::getName)
                .distinct()
                .collect(Collectors.toList()));
        metadata.setCollector(getCollectorType().toString());
        metadata.setLogStyle(getLogStyle().toString());
        metadata.setPauseless(isPauseless());
        metadata.setGenerational(isGenerational());
        metadata.setMetaspaceCapacityReliable(isMetaspaceCapacityReliable());
        metadata.setTimestamp(getReferenceTimestamp());
        metadata.setStartTime(getStartTime());
        metadata.setEndTime(getEndTime());
        metadata.setParentEventTypes(getParentEventTypes().stream().map(GCEventType::getName).collect(Collectors.toList()));
        metadata.setImportantEventTypes(getImportantEventTypes().stream().map(GCEventType::getName).collect(Collectors.toList()));
        metadata.setPauseEventTypes(getPauseEventTypes().stream().map(GCEventType::getName).collect(Collectors.toList()));
        metadata.setAllEventTypes(getAllEventTypes().stream().map(GCEventType::getName).collect(Collectors.toList()));
        metadata.setMainPauseEventTypes(getMainPauseEventTypes().stream().map(GCEventType::getName).collect(Collectors.toList()));
        metadata.setParallelGCThreads(getParallelThread());
        metadata.setConcurrentGCThreads(getConcurrentThread());
    }

    protected boolean isMetaspaceCapacityReliable() {
        return collectorType == ZGC;
    }

    public void setParallelThread(int parallelThread) {
        this.parallelThread = parallelThread;
    }

    public void setConcurrentThread(int concurrentThread) {
        this.concurrentThread = concurrentThread;
    }

    public int getParallelThread() {
        if (parallelThread == Constant.UNKNOWN_INT && vmOptions != null) {
            return vmOptions.<Long>getOptionValue("ParallelGCThreads", Constant.UNKNOWN_LONG).intValue();
        }
        return parallelThread;
    }

    public int getConcurrentThread() {
        if (concurrentThread == Constant.UNKNOWN_INT && vmOptions != null) {
            return vmOptions.<Long>getOptionValue("ConcGCThreads", Constant.UNKNOWN_LONG).intValue();
        }
        return concurrentThread;
    }

    @Data
    @NoArgsConstructor
    @ToString
    public static class GCDetailFilter {
        private String eventType;
        private String gcCause;
        //in ms
        private double logTimeLow;
        private double logTimeHigh;
        private double pauseTimeLow;

        public GCDetailFilter(String eventType, String gcCause, Double logTimeLow, Double logTimeHigh, Double pauseTimeLow) {
            this.eventType = eventType;
            this.gcCause = gcCause;
            this.logTimeLow = logTimeLow == null ? -Double.MAX_VALUE : logTimeLow;
            this.logTimeHigh = logTimeHigh == null ? Double.MAX_VALUE : logTimeHigh;
            this.pauseTimeLow = pauseTimeLow == null ? -Double.MAX_VALUE : pauseTimeLow;
        }

        public boolean isFiltered(GCEvent event) {
            return event.getEventType() == SAFEPOINT ||
                    !((eventType == null || eventType.equals(event.getEventType().getName()))
                            && (gcCause == null || gcCause.equals(event.getCause().getName()))
                            && (logTimeLow <= event.getEndTime() && event.getEndTime() <= logTimeHigh)
                            && (pauseTimeLow <= event.getPause()));
        }
    }

}
