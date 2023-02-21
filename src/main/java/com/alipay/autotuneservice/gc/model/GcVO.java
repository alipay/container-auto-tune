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
package com.alipay.autotuneservice.gc.model;

import com.alipay.autotuneservice.controller.model.diagnosis.SeriousProblem;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jifa.gclog.diagnoser.GlobalDiagnoser.GlobalAbnormalInfo;
import org.eclipse.jifa.gclog.model.GCTimeStamp;
import org.eclipse.jifa.gclog.model.modeInfo.VmOptions;
import org.eclipse.jifa.gclog.vo.MemoryStatistics;
import org.eclipse.jifa.gclog.vo.MemoryStatistics.MemoryStatisticsItem;
import org.eclipse.jifa.gclog.vo.ObjectStatistics;
import org.eclipse.jifa.gclog.vo.PauseStatistics;
import org.eclipse.jifa.gclog.vo.PhaseStatistics;
import org.eclipse.jifa.gclog.vo.PhaseStatistics.ParentStatisticsInfo;
import org.eclipse.jifa.gclog.vo.PhaseStatistics.PhaseStatisticItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version GcVO.java, v 0.1 2022年10月25日 3:58 下午 huoyuqi
 */
@Data
public class GcVO {

    /**
     * 1.诊断问题
     */
    private GlobalAbnormalInfo globalAbnormalInfo;

    /**
     * 诊断问题时间轴
     */
    private List<SeriousProblem> seriousProblems;

    /**
     * 诊断报告
     */
    private DiagnosisReport diagnosisReport;

    /**
     * 1.1 总暂停时间
     */
    private Double totalPauseTime;

    /**
     * 1.2 总吞吐量
     */
    private Double totalThroughput;

    /**
     * 2.timeGrapData 时间图
     */
    private Map<String, List<Object[]>> timeGraphDate;

    /**
     * 2.2 timeGraph
     */
    private Map<String, List<GCTimeStamp>> timeGraph;

    /**
     * 3.1暂停信息1
     */
    private PauseStatistics pauseStatistics;

    /**
     * 3.2暂停信息
     */
    private Map<String, int[]> pauseDistribution;

    /**
     * 4.堆元空间统计
     */
    private MemoryStatistics memoryStatistics;

    private List<MemoryStatisticVO> memoryStatisticVO;

    /**
     * 5.GC 阶段和原因
     */
    private PhaseStatistics phaseStatistics;

    /**
     * GC 原因详情
     */
    List<GcCauseVO> gcCauseVOS;

    /**
     * GC Phase
     */
    List<GcCauseVO> gcPhaseVOS;

    /**
     * 6.对象统计
     */
    private ObjectStatistics objectStatistics;

    /**
     * 7.jvm参数展示
     */
    private VmOptions vmOptions;

    /**
     * 8.GC basicInfo
     */
    private GCBasicVO gcBasicVO;

    private Long storageInfoId;

    public GcVO(GlobalAbnormalInfo globalAbnormalInfo, Map<String, List<Object[]>> timeGraphDate, Map<String, List<GCTimeStamp>> timeGraph,
                PauseStatistics pauseStatistics, Map<String, int[]> pauseDistribution, MemoryStatistics memoryStatistic,
                PhaseStatistics phaseStatistics, ObjectStatistics objectStatistics, VmOptions vmOptions, GCBasicVO gcBasicVO, Long id,
                DiagnosisReport diagnosisReport) {
        this.globalAbnormalInfo = globalAbnormalInfo;
        this.timeGraphDate = timeGraphDate;
        this.timeGraph = convert2TimeGraph(timeGraph, gcBasicVO.getSearchStartTime(), gcBasicVO.getSearchEndTime());
        this.pauseStatistics = pauseStatistics;
        this.pauseDistribution = pauseDistribution;
        this.memoryStatistics = memoryStatistic;
        this.memoryStatisticVO = construct2MemoryStatisticVO(memoryStatistic);
        this.phaseStatistics = phaseStatistics;
        this.gcCauseVOS = convert2GcCauseVO(phaseStatistics, "cause");
        this.gcPhaseVOS = convert2GcCauseVO(phaseStatistics, "phase");
        this.objectStatistics = objectStatistics;
        this.vmOptions = vmOptions;
        this.gcBasicVO = gcBasicVO;
        this.storageInfoId = id;
        this.diagnosisReport = diagnosisReport;
        this.totalThroughput = pauseStatistics.getThroughput();
        this.totalPauseTime = constructTotalPauseTime(timeGraph);
        this.seriousProblems = convertSeriousProblem(globalAbnormalInfo, gcBasicVO.getStartTime());
    }

    private Double constructTotalPauseTime(Map<String, List<GCTimeStamp>> timeGraph) {
        if (CollectionUtils.isNotEmpty(timeGraph.get("Pause GC Duration"))) {
            return (double) timeGraph.get("Pause GC Duration").stream().map(GCTimeStamp::getBefore).count();
        }
        return 0.0;
    }

    private List<SeriousProblem> convertSeriousProblem(GlobalAbnormalInfo globalAbnormalInfo, long timestamp) {
        List<SeriousProblem> resultList = new ArrayList<>();
        if (globalAbnormalInfo != null && globalAbnormalInfo.getSeriousProblems() != null) {
            if (CollectionUtils.isNotEmpty(globalAbnormalInfo.getSeriousProblems().get("heapMemoryFullGC"))) {
                resultList = globalAbnormalInfo.getSeriousProblems().get("heapMemoryFullGC").stream()
                        .map(item -> new SeriousProblem((long) (item + timestamp), "FGC")).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(globalAbnormalInfo.getSeriousProblems().get("longYoungGCPause"))) {
                resultList.addAll(globalAbnormalInfo.getSeriousProblems().get("longYoungGCPause").stream()
                        .map(item -> new SeriousProblem((long) (item + timestamp), "YGC")).collect(Collectors.toList()));
            }
        }

        return resultList;
    }

    private List<MemoryStatisticVO> construct2MemoryStatisticVO(MemoryStatistics memoryStatistics) {
        List<MemoryStatisticVO> memoryStatisticVOS = new ArrayList<>();

        MemoryStatisticsItem young = memoryStatistics.getYoung();
        memoryStatisticVOS.add(new MemoryStatisticVO("young", young.getCapacityAvg(), young.getUsedMax(), young.getUsedAvgAfterFullGC(),
                young.getUsedAvgAfterOldGC()));

        MemoryStatisticsItem old = memoryStatistics.getOld();
        memoryStatisticVOS.add(new MemoryStatisticVO("old", old.getCapacityAvg(), old.getUsedMax(), old.getUsedAvgAfterFullGC(),
                old.getUsedAvgAfterOldGC()));

        MemoryStatisticsItem heap = memoryStatistics.getHeap();
        memoryStatisticVOS.add(new MemoryStatisticVO("heap", heap.getCapacityAvg(), heap.getUsedMax(), heap.getUsedAvgAfterFullGC(),
                heap.getUsedAvgAfterFullGC()));

        MemoryStatisticsItem metaspace = memoryStatistics.getMetaspace();
        memoryStatisticVOS.add(new MemoryStatisticVO("metaspace", metaspace.getCapacityAvg(), metaspace.getUsedMax(),
                metaspace.getUsedAvgAfterFullGC(), metaspace.getUsedAvgAfterOldGC()));

        return memoryStatisticVOS;
    }

    private List<GcCauseVO> convert2GcCauseVO(PhaseStatistics phaseStatistics, String type) {
        return phaseStatistics.getParents().stream().map(item ->
                childExist(item, type) ? new GcCauseVO(item.getSelf().getName(), item.getSelf().getCount(), item.getSelf().getIntervalAvg(),
                        item.getSelf().getIntervalMin(), item.getSelf().getDurationAvg(), item.getSelf().getDurationMax(),
                        item.getSelf().getDurationTotal(),
                        StringUtils.equals("cause", type) ? addIndex(item.getCauses()) : addIndex(item.getPhases()))
                        : new GcCauseVO(item.getSelf().getName(), item.getSelf().getCount(), item.getSelf().getIntervalAvg(),
                                item.getSelf().getIntervalMin(), item.getSelf().getDurationAvg(), item.getSelf().getDurationMax(),
                                item.getSelf().getDurationTotal(), null)
        ).collect(Collectors.toList());
    }

    /**
     * 前端展示要唯一index
     */
    private List<PhaseStatisticItem> addIndex(List<PhaseStatisticItem> statisticItems) {
        return statisticItems.stream().peek(item -> item.setUnicode(constructUnicode())).collect(Collectors.toList());
    }

    private String constructUnicode() {
        return System.currentTimeMillis() + "" + (Math.random() * 9 + 1) * 100000;
    }

    private Boolean childExist(ParentStatisticsInfo parent, String type) {
        return StringUtils.equals("cause", type) ? CollectionUtils.isNotEmpty(parent.getCauses()) : CollectionUtils.isNotEmpty(
                parent.getPhases());
    }

    private Map<String, List<GCTimeStamp>> convert2TimeGraph(Map<String, List<GCTimeStamp>> timeGraph, Long start, Long end) {
        return timeGraph.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> addType(e.getValue(), e.getKey(), start, end)));
    }

    private List<GCTimeStamp> addType(List<GCTimeStamp> gcTimeStamps, String type, Long start, Long end) {
        List<GCTimeStamp> gcTimeStampLis = gcTimeStamps.stream().sorted(Comparator.comparing(GCTimeStamp::getTime)).collect(
                Collectors.toList());
        if (start != null && end != null) {
            gcTimeStampLis = gcTimeStamps.stream().filter(p -> p.getTime() >= start && p.getTime() <= end).collect(Collectors.toList());
        }

        switch (type) {
            case "Full GC":
            case "Young GC":
                return gcTimeStampLis.stream().peek(item -> item.setType(type)).collect(Collectors.toList());
            case "reclamation":
                return gcTimeStampLis.stream().peek(item -> {
                    item.setBefore(item.getBefore() / 1024 / 1024);
                    item.setType("Allocated Object Size");
                }).collect(Collectors.toList());
            case "youngCapacity":
            case "oldCapacity":
            case "metaspaceUsed":
                return gcTimeStampLis.stream().peek(item -> {
                    item.setBefore(item.getBefore() / 1024 / 1024);
                    item.setType("Allocated Space");
                }).collect(Collectors.toList());
            case "Young Gen":
            case "Old Gen":
            case "metaSpace":
                List<GCTimeStamp> gcTimeStampList = new ArrayList<>();
                gcTimeStampLis.stream().forEach(item -> {
                    GCTimeStamp gcTimeStamp = new GCTimeStamp(item.getBefore(), item.getTime(), "Before GC");
                    GCTimeStamp afGcTimeStamp = new GCTimeStamp(item.getAfter(), item.getTime(), "After GC");
                    gcTimeStampList.add(gcTimeStamp);
                    gcTimeStampList.add(afGcTimeStamp);
                });
                return gcTimeStampList;
            case "promotion":
                return gcTimeStampLis.stream().peek(item -> {
                    item.setBefore(item.getBefore() / 1024 / 1024);
                    item.setType("Promoted(Young->Old) Object Size");
                }).collect(Collectors.toList());
            case "metaAllocated":
                return gcTimeStampLis.stream().peek(item -> {
                    item.setType("Allocated Space");
                }).collect(Collectors.toList());
            default:
                return gcTimeStampLis;
        }
    }

}