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
import com.alipay.autotuneservice.service.algorithmlab.BaseAlgorithm;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum;
import com.alipay.autotuneservice.service.algorithmlab.TuneParamModel;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.BaseDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.CpuDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.JvmDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.MemDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis.ThreadDiagnosis;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hongshu
 * @version GcLogDiagnosis.java, v 0.1 2022年11月10日 17:57 hongshu
 */
@Slf4j
public class TimingDataDiagnosis extends BaseDiagnosis {

    public static void cpuDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        // 用户cpu使用
        List<Double> processCpuLoad = data.stream().map(JvmMonitorMetricData::getProcessCpuLoad)
                .map(r -> r/100).collect(Collectors.toList());
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_USER_UTIL_BURST,
                () -> CpuDiagnosis.cpuUserUtilBurst(processCpuLoad));
        if(CpuDiagnosis.cpuUserUtilHigh(processCpuLoad)){
            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_USER_UTIL_HIGH);
        }else {
            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_USER_UTIL_LOW,
                    () -> CpuDiagnosis.cpuUserUtilLow(processCpuLoad));
        }

        // 系统cpu使用
        List<Double> sysCpuLoad = data.stream().map(JvmMonitorMetricData::getSystemCpuLoad)
                .map(r -> r/100).collect(Collectors.toList());
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_UTIL_BURST,
                () -> CpuDiagnosis.cpuUtilBurst(sysCpuLoad));
        if(CpuDiagnosis.cpuUtilHigh(sysCpuLoad)){
            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_UTIL_HIGH);
        }else {
            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_UTIL_LOW,
                    () -> CpuDiagnosis.cpuUtilLow(sysCpuLoad));
        }

        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_UTIL_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_UTIL_REDUCE, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_USER_UTIL_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CPU_USER_UTIL_REDUCE, () -> false);
    }

    public static void memDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        // 内存使用
        List<Double> sysMemUtil = data.stream().map(JvmMonitorMetricData::getSystem_mem_util)
                .map(r -> r/100).collect(Collectors.toList());
        if(MemDiagnosis.memUtilHigh(sysMemUtil)){
            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_HIGH);
        }else {
            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_LOW,
                    () -> MemDiagnosis.memUtilLow(sysMemUtil));
        }
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_BURST, () -> MemDiagnosis.memUtilBurst(sysMemUtil));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_BURST, () -> MemDiagnosis.memFragmentBurst(sysMemUtil));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_BURST, () -> MemDiagnosis.memTLBRate(sysMemUtil));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_BURST, () -> MemDiagnosis.memSwapOpen(false));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_BURST, () -> MemDiagnosis.memCacheError(sysMemUtil));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_BURST, () -> MemDiagnosis.memVirtualError(sysMemUtil));
    }

    public static void diskDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.DISK_UTIL_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.DISK_IO_UTIL_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.DISK_IO_UTIL_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.DISK_IO_UTIL_REDUCE, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.DISK_FAILURE, () -> false);
    }

    public static void threadDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        List<Double> threadCount = data.stream().map(r -> (double)r.getThreadCount())
                .collect(Collectors.toList());
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THREAD_COUNT_HIGH,
                () -> ThreadDiagnosis.threadCountHigh(threadCount));

        List<Long> deadLock = data.stream().map(JvmMonitorMetricData::getDeadLockedCount)
                .collect(Collectors.toList());
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THREAD_DEADLOCK,
                () -> ThreadDiagnosis.threadDeadlock(deadLock));

        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THREAD_COUNT_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THREAD_BLOCK_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THREAD_POOL_COUNT_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THREAD_POOL_DISCARD_P_MODE, () -> false);
    }

    public static void networkDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_TCP_CON_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_TCP_CON_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_TCP_CON_REDUCE, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_TIME_WAIT_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_TCP_RE_TRANS_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_IO_UTIL_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_IO_UTIL_LOW, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_IO_UTIL_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.NET_IO_UTIL_REDUCE, () -> false);
    }

    public static void jvmAndGcDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data,
                                         List<TuneParamModel> tuneParamModels, GarbageCollector garbageCollector) {
        // Xmx_Xms_DIFF MaxNewSize_NewSize_DIFF MetaSpaceSize_MaxMetaSpaceSize_DIFF
        configCheck(diagnosisReport,tuneParamModels);
        // FGC_COUNT_HIGH  FGC_TIME_GREAT HEAP_UTIL_LOW
        fgcCheck(diagnosisReport,data);
        // YGC_TIME_GREAT
        youngcCheck(diagnosisReport,data);
        // META_UTIL_HIGH META_UTIL_LOW
        metaCheck(diagnosisReport,data);
        // OLD_UTIL_BURST OLD_UTIL_HIGH OLD_UTIL_LOW
        gcMemoryCheck(diagnosisReport,data);

        // other check
        // GC_TYPE_UNREASONABLE ReservedCodeCacheSize_SMALL
        otherGcCheck(diagnosisReport,data,garbageCollector);
    }
    public static void codeDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CODE_JAR_SEC_BREACH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CODE_JAR_EXPIRED, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.CODE_JAR_CONFLICT, () -> false);
    }
    public static void errorDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.ERROR_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.ERROR_COUNT_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.THIRD_PARTY_ERROR_HIGH, () -> false);
    }
    public static void businessDiagnosis(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_RT_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_RT_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_RT_REDUCE, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_QPS_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_QPS_REDUCE, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_SUCCESS_RATE_LOW, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_SUCCESS_RATE_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_SUCCESS_RATE_REDUCE, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_INDICATOR_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_INDICATOR_REDUCE, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_INDICATOR_ERROR_HIGH, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_INDICATOR_ERROR_BURST, () -> false);
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.BUS_INDICATOR_ERROR_REDUCE, () -> false);
    }

    private static void otherGcCheck(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data, GarbageCollector garbageCollector) {
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.GC_TYPE_UNREASONABLE,
                () -> {
                    Optional<Double> cap = data.stream().map(JvmMonitorMetricData::getJvm_mem_capacity)
                            .filter(r -> r>0).findAny();
                    return cap.isPresent() && JvmDiagnosis.gcTypeUnreasonable(garbageCollector, cap.get());
                });
        // codecache util
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.ReservedCodeCacheSize_SMALL,
                () -> JvmDiagnosis.reservedCodeCacheSizeSmall(
                        data.stream().map(JvmMonitorMetricData::getCodeCacheUtil).collect(Collectors.toList())));
    }

    private static void gcMemoryCheck(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {

        List<Double> oldUtil = data.stream().map(JvmMonitorMetricData::getOld_util).collect(Collectors.toList());
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.OLD_UTIL_BURST,
                () -> JvmDiagnosis.oldUtilBurst(oldUtil));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.OLD_UTIL_HIGH,
                () -> JvmDiagnosis.oldUtilHigh(oldUtil));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.OLD_UTIL_LOW,
                () -> JvmDiagnosis.oldUtilLow(oldUtil));
    }

    private static void metaCheck(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        List<Double> metaUtil = data.stream().map(JvmMonitorMetricData::getMeta_util).collect(Collectors.toList());
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.META_UTIL_HIGH,
                () -> JvmDiagnosis.metaUtilHigh(metaUtil));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MEM_UTIL_LOW,
                () -> JvmDiagnosis.metaUtilLow(metaUtil));
    }

    private static void fgcCheck(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        List<Double> fTime = data.stream().map(JvmMonitorMetricData::getFgc_time).filter(r -> r>0).collect(Collectors.toList());
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.FGC_TIME_GREAT,
                () -> JvmDiagnosis.fgcTimeGreat(fTime));

        List<Double> fCount = data.stream().map(JvmMonitorMetricData::getFgc_time).filter(r -> r>0).collect(Collectors.toList());
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.FGC_COUNT_HIGH,
                () -> BaseAlgorithm.sum(fCount)>JvmDiagnosis.FGC_COUNT_SUM_HIGH_THRESHOLD || JvmDiagnosis.fgcCountHigh(fCount));
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.HEAP_UTIL_LOW,
                () -> BaseAlgorithm.sum(fCount)<=JvmDiagnosis.FGC_COUNT_SUM_LOW_THRESHOLD && fCount.size()<=3);
    }

    private static void youngcCheck(DiagnosisReport diagnosisReport, List<JvmMonitorMetricData> data) {
        packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.YGC_TIME_GREAT,
                () -> JvmDiagnosis.ygcTimeGreat(
                        data.stream().map(JvmMonitorMetricData::getYgc_time).collect(Collectors.toList())));
    }

    /**
     * 检查jvm配置参数是否正常
     * @param diagnosisReport
     * @param tuneParamModels
     */
    private static void configCheck(DiagnosisReport diagnosisReport, List<TuneParamModel> tuneParamModels) {
        if(CollectionUtils.isNotEmpty(tuneParamModels)){
            Map<String,String> paramMap = Stream.of("-Xms","-Xmx","-XX:MetaspaceSize","-XX:MaxMetaspaceSize","-XX:NewSize","-XX:MaxNewSize")
                    .collect(Collectors.toMap(k -> k, v -> ""));

            tuneParamModels.stream().filter(r -> paramMap.containsKey(r.getParamName()))
                            .forEach(r -> paramMap.put(r.getParamName(),r.getParamVal()));

            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.Xmx_Xms_DIFF,
                    () -> !paramMap.get("-Xms").equals(paramMap.get("-Xmx")));

            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MetaSpaceSize_MaxMetaSpaceSize_DIFF,
                    () -> !paramMap.get("-XX:MetaspaceSize").equals(paramMap.get("-XX:MaxMetaspaceSize")));

            packSingleReportWithDefault(diagnosisReport,ProblemMetricEnum.MaxNewSize_NewSize_DIFF,
                    () -> !paramMap.get("-XX:NewSize").equals(paramMap.get("-XX:MaxNewSize")));
        }
    }
}
