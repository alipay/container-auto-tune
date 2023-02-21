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

import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.heap.model.HeapVO;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.service.algorithmlab.ScoringSys;
import com.alipay.autotuneservice.service.algorithmlab.TuneParamModel;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.BaseDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.BaseInfo;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.thread.model.ThreadVO;
import com.alipay.autotuneservice.util.TuneParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jifa.gclog.model.GCModel;

import java.util.ArrayList;
import java.util.List;

import static com.alipay.autotuneservice.service.algorithmlab.ScoringSys.PERFECT_MARK;

/**
 * @author hongshu
 * @version DiagnosisLab.java, v 0.1 2022年10月25日 17:57 hongshu
 */
@Slf4j
public class DiagnosisLab {

    /**
     * offline gclog diagnose
     * @param gcModel gc object from log
     * @return  diagnose report
     * Report:
     *  Xmx_Xms_DIFF
     *  MaxNewSize_NewSize_DIFF
     *  MetaSpaceSize_MaxMetaSpaceSize_DIFF
     *  SAFE_POINT_LONG
     *  YGC_TIME_GREAT
     *  FGC_COUNT_HIGH
     *  FGC_TIME_GREAT
     *  META_UTIL_HIGH
     *  OLD_UTIL_BURST
     *  BIG_OBJECT_PROMOTION
     *  STOP_THE_WORLD_LONG
     */
    public static DiagnosisReport gcLogDiagnosis(GCModel gcModel){
        DiagnosisReport diagnosisReport = DiagnosisReport.builder()
                .reports(new ArrayList<>())
                .build();
        LogDiagnosis.gcDiagnosis(diagnosisReport,gcModel);
        LogDiagnosis.recParamGen(diagnosisReport,gcModel);
        BaseDiagnosis.packConclusionReport(diagnosisReport);
        if(CollectionUtils.isEmpty(diagnosisReport.getReports())){
            diagnosisReport.setReports(null);
        }
        return diagnosisReport;
    }

    /**
     * THREAD_COUNT_HIGH
     * THREAD_DEADLOCK
     * @param threadVO
     * @return
     */
    public static DiagnosisReport threadLogDiagnosis(ThreadVO threadVO){
        DiagnosisReport diagnosisReport = DiagnosisReport.builder().reports(new ArrayList<>()).build();
        LogDiagnosis.threadDiagnosis(diagnosisReport,threadVO);
        BaseDiagnosis.packConclusionReport(diagnosisReport);
        if(CollectionUtils.isEmpty(diagnosisReport.getReports())){
            diagnosisReport.setReports(null);
        }
        return diagnosisReport;
    }


    /**
     * @param heapVO
     * @return
     */
    public static DiagnosisReport memLogDiagnosis(HeapVO heapVO){
        DiagnosisReport diagnosisReport = DiagnosisReport.builder().reports(new ArrayList<>()).build();
        LogDiagnosis.memDiagnosis(diagnosisReport,heapVO);
        BaseDiagnosis.packConclusionReport(diagnosisReport);
        if(CollectionUtils.isEmpty(diagnosisReport.getReports())){
            diagnosisReport.setReports(null);
        }
        return diagnosisReport;
    }




    /**
     * TODO 线程池的诊断
     * timing data diagnose
     * @param data
     * @param jvm
     * @return  diagnose report
     * Report:
     *  FGC_COUNT,FGC_TIME
     *  OLD_UTIL,HEAP_OLD_UTIL
     *  HEAP_MEMORY
     *  HEAP_META_IDLE
     *
     *  Xmx_Xms_DIFF
     *  MaxNewSize_NewSize_DIFF
     *  MetaSpaceSize_MaxMetaSpaceSize_DIFF
     *  SAFE_POINT_LONG
     *  YGC_TIME_GREAT
     *  FGC_COUNT_HIGH
     *  FGC_TIME_GREAT
     *  META_UTIL_HIGH
     *  OLD_UTIL_BURST
     *  BIG_OBJECT_PROMOTION
     *  STOP_THE_WORLD_LONG
     */
    public static DiagnosisReport timingDataDiagnosis(List<JvmMonitorMetricData> data, String jvm){
        if(CollectionUtils.isEmpty(data) || StringUtils.isEmpty(jvm)){
            return null;
        }
        GarbageCollector garbageCollector = GarbageCollector.matchGarbageCollectorByJvmOpt(jvm);
        if(GarbageCollector.UNKNOWN == garbageCollector){
            return null;
        }
        List<TuneParamModel> tuneParamModels = TuneParamUtil.convert2TuneParamModel(jvm);
        DiagnosisReport diagnosisReport = DiagnosisReport.builder()
                .baseInfo(BaseInfo.builder()
                        .gcCollectorType(BaseDiagnosis.toJifaGcType(garbageCollector))
                        .jvmOpt(jvm)
                        .build())
                .reports(new ArrayList<>())
                .build();

        TimingDataDiagnosis.cpuDiagnosis(diagnosisReport,data);
        TimingDataDiagnosis.memDiagnosis(diagnosisReport,data);
        TimingDataDiagnosis.diskDiagnosis(diagnosisReport,data);
        TimingDataDiagnosis.threadDiagnosis(diagnosisReport,data);
        TimingDataDiagnosis.networkDiagnosis(diagnosisReport,data);
        TimingDataDiagnosis.jvmAndGcDiagnosis(diagnosisReport,data,tuneParamModels,garbageCollector);
        TimingDataDiagnosis.codeDiagnosis(diagnosisReport,data);
        TimingDataDiagnosis.errorDiagnosis(diagnosisReport,data);
        TimingDataDiagnosis.businessDiagnosis(diagnosisReport,data);
        // 结果打分
        if(CollectionUtils.isNotEmpty(diagnosisReport.getReports())
            && diagnosisReport.getReports().stream().anyMatch(r->!r.isNormal())){
            diagnosisReport.setScore(ScoringSys.buildScoreFromDiagnoseReport(diagnosisReport));
            diagnosisReport.setProblemCount((int)diagnosisReport.getReports().stream().filter(r->!r.isNormal()).count());
            diagnosisReport.setTotalCount(diagnosisReport.getReports().size());
            BaseDiagnosis.packConclusionReport(diagnosisReport);
            BaseDiagnosis.packRecParamReport(diagnosisReport,jvm);
        }else{
            diagnosisReport.setScore(PERFECT_MARK);
        }
        return diagnosisReport;
    }


}
