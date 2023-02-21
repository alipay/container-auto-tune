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
package com.alipay.autotuneservice.service.algorithmlab.diagnosis;

import com.alipay.autotuneservice.heap.model.HeapVO;
import com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.BaseDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.JvmDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.MemDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.thread.model.ThreadVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jifa.gclog.event.Safepoint;
import org.eclipse.jifa.gclog.event.TimedEvent;
import org.eclipse.jifa.gclog.event.evnetInfo.GCMemoryItem;
import org.eclipse.jifa.gclog.event.evnetInfo.MemoryArea;
import org.eclipse.jifa.gclog.model.GCEventType;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.model.modeInfo.VmOptions;
import org.eclipse.jifa.gclog.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.ThreadDiagnosis.THREAD_COUNT_HIGH_MAX_THRESHOLD;
import static com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.ThreadDiagnosis.THREAD_DEADLOCK_HIGH_THRESHOLD;

/**
 * @author hongshu
 * @version GcLogDiagnosis.java, v 0.1 2022年11月10日 17:57 hongshu
 */
@Slf4j
public class LogDiagnosis extends BaseDiagnosis {
    public static void gcDiagnosis(DiagnosisReport diagnosisReport, GCModel gcModel) {
        // Xmx_Xms_DIFF MaxNewSize_NewSize_DIFF MetaSpaceSize_MaxMetaSpaceSize_DIFF
        configCheck(diagnosisReport,gcModel);
        // SAFE_POINT_LONG
        safePointCheck(diagnosisReport,gcModel);
        // YGC_TIME_GREAT
        youngcCheck(diagnosisReport,gcModel);
        // FGC_COUNT_HIGH  FGC_TIME_GREAT
        fgcCheck(diagnosisReport,gcModel);
        // META_UTIL_HIGH
        metaCheck(diagnosisReport,gcModel);
        // OLD_UTIL_BURST BIG_OBJECT_PROMOTION
        oldEdenCheck(diagnosisReport,gcModel);
        // STOP_THE_WORLD_LONG
        stopTheWorldCheck(diagnosisReport,gcModel);
        //GC_TYPE_UNREASONABLE OUT_OF_MEMORY
        otherGcCheck(diagnosisReport,gcModel);

    }

    private static void otherGcCheck(DiagnosisReport diagnosisReport, GCModel gcModel) {
        if(CollectionUtils.isNotEmpty(gcModel.getAllEvents())){
            long heapSize = gcModel.getAllEvents().stream().filter(e->e.getMemoryItem(MemoryArea.HEAP)!=null)
                            .map(r -> r.getMemoryItem(MemoryArea.HEAP).getTotal())
                            .filter(r -> r!=Constant.UNKNOWN_INT).findAny().get();
            if(JvmDiagnosis.gcTypeUnreasonable(toTamestroGcType(gcModel.getCollectorType()),heapSize)){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.GC_TYPE_UNREASONABLE);
            }
        }
        if(CollectionUtils.isNotEmpty(gcModel.getOoms())){
            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.OUT_OF_MEMORY);
        }
    }

    private static void stopTheWorldCheck(DiagnosisReport diagnosisReport, GCModel gcModel) {
        if(CollectionUtils.isNotEmpty(gcModel.getAllEvents())
                && gcModel.getAllEvents().stream()
                .anyMatch(r -> r.getDuration()> JvmDiagnosis.STOP_THE_WORLD_LONG_THRESHOLD)){
            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.OLD_UTIL_BURST);
        }
    }

    private static void oldEdenCheck(DiagnosisReport diagnosisReport, GCModel gcModel) {
        // 大对象晋升
        if(CollectionUtils.isNotEmpty(gcModel.getGcEvents())){
            List<GCMemoryItem> gcMemoryItems = new ArrayList<>();
            // old util burst
            List<Double> oldUtil = gcModel.getGcEvents().stream()
                    .map(r -> r.getMemoryItem(MemoryArea.OLD))
                    .filter(Objects::nonNull)
                    .filter(r -> r.getPreUsed()!=Constant.UNKNOWN_LONG
                            && r.getPostUsed()!=Constant.UNKNOWN_LONG
                            && r.getTotal()!=Constant.UNKNOWN_LONG)
                    .map(r -> {
                        if(CollectionUtils.isEmpty(gcMemoryItems) && (r.getPostUsed()-r.getPreUsed()> JvmDiagnosis.OLD_PROMOTION_BURST_THRESHOLD)){
                            gcMemoryItems.add(r);
                        }
                       return  (double)(r.getPostUsed()-r.getPreUsed())/r.getTotal();
                    }).collect(Collectors.toList());
            if(JvmDiagnosis.oldUtilBurst(oldUtil)){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.OLD_UTIL_BURST);
            }
            if(CollectionUtils.isNotEmpty(gcMemoryItems)){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BIG_OBJECT_PROMOTION);
            }
        }
    }

    private static void metaCheck(DiagnosisReport diagnosisReport, GCModel gcModel) {
        if(gcModel.getVmOptions()!=null){
            Long maxMetaspaceSize = gcModel.getVmOptions().getOptionValue("MaxMetaspaceSize");
            String[] dataTypes = new String[] {"metaspaceUsed"};
            Map<String, List<Object[]>> timeGraphData = gcModel.getTimeGraphData(dataTypes);
            if(timeGraphData!=null && CollectionUtils.isNotEmpty(timeGraphData.get(dataTypes[0]))){
                List<Double> metaUtilData = timeGraphData.get(dataTypes[0]).stream()
                        .map(r -> (double)r[1]/maxMetaspaceSize).collect(Collectors.toList());
                if(JvmDiagnosis.metaUtilHigh(metaUtilData)){
                    packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.META_UTIL_HIGH);
                }
            }
        }
    }

    private static void fgcCheck(DiagnosisReport diagnosisReport, GCModel gcModel) {
        if(CollectionUtils.isNotEmpty(gcModel.getGcEvents())){
            // full gc time
            List<Double> fTime = gcModel.getGcEvents().stream()
                    .filter(gcEvent->gcEvent.getEventType()== GCEventType.FULL_GC)
                    .map(TimedEvent::getDuration).collect(Collectors.toList());
            if(JvmDiagnosis.ygcTimeGreat(fTime)){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.FGC_TIME_GREAT);
            }
            if(fTime.size() > JvmDiagnosis.FGC_COUNT_SUM_HIGH_THRESHOLD){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.FGC_COUNT_HIGH);
            }
        }
    }



    private static void youngcCheck(DiagnosisReport diagnosisReport, GCModel gcModel) {
        if(CollectionUtils.isNotEmpty(gcModel.getGcEvents())){
            // young gc time
            List<Double> yTime = gcModel.getGcEvents().stream()
                    .filter(gcEvent->gcEvent.getEventType()== GCEventType.YOUNG_GC)
                    .map(TimedEvent::getDuration).collect(Collectors.toList());
            if(JvmDiagnosis.ygcTimeGreat(yTime)){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.YGC_TIME_GREAT);
            }
        }
    }

    /**
     * 统计进入安全点的时长是否存在异常
     * @param diagnosisReport
     * @param gcModel
     */
    private static void safePointCheck(DiagnosisReport diagnosisReport, GCModel gcModel) {
        if(CollectionUtils.isNotEmpty(gcModel.getSafepoints())){
            if(JvmDiagnosis.safePointLong(gcModel.getSafepoints().stream().map(Safepoint::getTimeToEnter).collect(Collectors.toList()))){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.SAFE_POINT_LONG);
            }
        }
    }

    /**
     * 检查jvm配置参数是否正常
     * @param diagnosisReport
     * @param gcModel
     */
    private static void configCheck(DiagnosisReport diagnosisReport, GCModel gcModel) {
        if(gcModel.getVmOptions()!=null){
            VmOptions vmOptions = gcModel.getVmOptions();
            Long xmx = vmOptions.getOptionValue("Xmx");
            Long xms = vmOptions.getOptionValue("Xms");
            Long metaspaceSize = vmOptions.getOptionValue("MetaspaceSize");
            Long maxMetaspaceSize = vmOptions.getOptionValue("MaxMetaspaceSize");
            Long newSize = vmOptions.getOptionValue("NewSize");
            Long maxNewSize = vmOptions.getOptionValue("MaxNewSize");

            if(xms!=null && !xms.equals(xmx)){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.Xmx_Xms_DIFF);
            }
            if(metaspaceSize!=null && !metaspaceSize.equals(maxMetaspaceSize)){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MetaSpaceSize_MaxMetaSpaceSize_DIFF);
            }
            if(newSize!=null && !newSize.equals(maxNewSize)){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MaxNewSize_NewSize_DIFF);
            }
        }
    }


    public static void threadDiagnosis(DiagnosisReport diagnosisReport, ThreadVO threadVO) {
        if(threadVO!=null && threadVO.getTotalThreadVO()!=null){
            if(threadVO.getTotalThreadVO().getTotalCount()>THREAD_COUNT_HIGH_MAX_THRESHOLD){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THREAD_COUNT_HIGH);
            }
        }
        if(threadVO!=null && threadVO.getDeadLockVOS()!=null){
            if(threadVO.getDeadLockVOS().size()>THREAD_DEADLOCK_HIGH_THRESHOLD){
                packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THREAD_DEADLOCK);
            }
        }
    }

    public static void memDiagnosis(DiagnosisReport diagnosisReport, HeapVO heapVO) {
        if(heapVO!=null){
            // 大对象检测
            if(heapVO.getDetails()!=null){
                long heapSize = heapVO.getDetails().getUsedHeapSize();
                if(CollectionUtils.isNotEmpty(heapVO.getBigObjects())){
                    heapVO.getBigObjects().stream()
                            .filter(r -> !r.getLabel().contains("java.lang.String") && !r.getLabel().contains("int[]"))
                            .filter(r -> MemDiagnosis.memBigObjeckCheck(r.getValue(),heapSize))
                            .findFirst()
                            .ifPresent(e -> packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_BIG_OBJ));
                }
            }
            // 对象数检测
            if(CollectionUtils.isNotEmpty(heapVO.getClassView().getData())){
                heapVO.getClassView().getData().stream()
                        .filter(r -> MemDiagnosis.memObjNumCheck(r.getNumberOfObjects()))
                        .findFirst()
                        .ifPresent(e -> packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_OBJ_COUNT_GREAT));
            }
        }

    }

    public static void recParamGen(DiagnosisReport diagnosisReport, GCModel gcModel) {
        if(gcModel.getVmOptions()!=null && CollectionUtils.isNotEmpty(diagnosisReport.getReports())){
            String jvmConfig = gcModel.getVmOptions().getOriginalOptionString();
            BaseDiagnosis.packRecParamReport(diagnosisReport,jvmConfig);
        }
    }
}
