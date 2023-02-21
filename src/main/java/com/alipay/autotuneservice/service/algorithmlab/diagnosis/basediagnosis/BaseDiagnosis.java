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
package com.alipay.autotuneservice.service.algorithmlab.diagnosis.basediagnosis;

import com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum;
import com.alipay.autotuneservice.service.algorithmlab.DiagnosisServiceEnum;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.SingleReport;
import com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.ExpertEvalItem;
import com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.Trend;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.util.UserUtil.TUNE_JVM_APPEND;

/**
 * @author hongshu
 * @version GcLogDiagnosis.java, v 0.1 2022年11月10日 17:57 hongshu
 */
@Slf4j
public class BaseDiagnosis {
    /**
     * 仅组装异常的问题数据
     * @param diagnosisReport
     * @param proEnum
     */
    protected static void packSingleReportWithDefault(DiagnosisReport diagnosisReport, ProblemMetricEnum proEnum) {
        diagnosisReport.getReports()
                .add(SingleReport.builder()
                        .name(proEnum.name())
                        .desc(proEnum.getDesc())
                        .expert(proEnum.getExpert())
                        .dangerLevel(proEnum.getDangeLevelEnum().name())
                        .reason(proEnum.getReason())
                        .normal(false)
                        .build());
    }

    /**
     * 组装正常和异常的数据
     * @param diagnosisReport
     * @param proEnum
     * @param supplier
     */
    protected static void packSingleReportWithDefault(DiagnosisReport diagnosisReport, ProblemMetricEnum proEnum, BooleanSupplier supplier) {
        diagnosisReport.getReports()
                .add(SingleReport.builder()
                        .name(proEnum.name())
                        .desc(proEnum.getDesc())
                        .expert(proEnum.getExpert())
                        .dangerLevel(proEnum.getDangeLevelEnum().name())
                        .reason(proEnum.getReason())
                        .normal(!supplier.getAsBoolean())
                        .build());
    }

    public static void packModReport(DiagnosisReport report) {
        LinkedHashMap<String, List<SingleReport>> defMod = new LinkedHashMap<>();
        LinkedHashMap<String,List<SingleReport>>  groupMod = new LinkedHashMap<>();
        Arrays.stream(DiagnosisServiceEnum.values()).filter(r -> r!=DiagnosisServiceEnum.UNKNOWN).forEach(r -> groupMod.put(r.name(),new ArrayList<>()));
        Arrays.stream(DiagnosisEnum.values()).filter(r -> r!=DiagnosisEnum.UNKNOWN).forEach(r -> defMod.put(r.name(),new ArrayList<>()));
        report.getReports().forEach(r -> {
            ProblemMetricEnum pEnum = ProblemMetricEnum.valueOf(r.getName());
            groupMod.get(pEnum.getServiceType().name()).add(r);
            defMod.get(pEnum.getGroupType().name()).add(r);
        });
        report.setDefModReports(defMod);
        report.setGroupModReports(groupMod);
    }

    public static void packConclusionReport(DiagnosisReport diagnosisReport){
        if(CollectionUtils.isNotEmpty(diagnosisReport.getReports())){
            String prefix = "Problems level: ";
            StringBuilder sb = new StringBuilder();
            diagnosisReport.getReports().stream()
                    .filter(r -> !r.isNormal())
                    .map(r -> ProblemMetricEnum.valueOf(r.getName()).getDangeLevelEnum().name())
                    .distinct().forEach(r -> {
                        sb.append(r);
                        sb.append(",");
                    });
            StringBuilder detail = new StringBuilder();
            Objects.requireNonNull(diagnosisReport.getReports()).stream()
                    .filter(r -> !r.isNormal())
                    .collect(Collectors.groupingBy(r->ProblemMetricEnum.valueOf(r.getName()).getDangeLevelEnum().name(),
                            Collectors.mapping(SingleReport::getName, Collectors.joining(", ", "[", "]"))))
                    .forEach((s,t) -> detail.append("\n").append(s).append(": ").append(t));
            diagnosisReport.setConclusions(prefix + sb.substring(0, sb.toString().length() - 1) + detail);
        }
    }

    public static void packRecParamReport(DiagnosisReport diagnosisReport, String jvmConfig) {
        if(StringUtils.isNotEmpty(jvmConfig)){
            Map<String,String> recParams = new HashMap<>();
            Consumer<ProblemMetricEnum> problemMetricEnumConsumer = r -> {
                List<ExpertEvalItem> items = Trend.relatedParamSuggest(Lists.newArrayList(r), jvmConfig, !jvmConfig.contains(TUNE_JVM_APPEND), 0);
                StringBuilder sb = new StringBuilder();
                if (CollectionUtils.isNotEmpty(items)) {
                    Objects.requireNonNull(items).stream()
                            .collect(Collectors.groupingBy(s -> s.getType().name(), Collectors.mapping(ExpertEvalItem::getParam, Collectors.joining(", ", "[", "]"))))
                            .forEach((s, t) -> sb.append("\n\t").append(s).append(" : ").append(t));
                    recParams.put("Problem " + r.name(), sb.toString());
                }
            };
            diagnosisReport.getReports().stream()
                    .filter(r->!r.isNormal())
                    .map(r -> ProblemMetricEnum.valueOf(r.getName()))
                    .forEach(problemMetricEnumConsumer);
            diagnosisReport.setRecParams(recParams);
        }
    }

    public static GCCollectorType toJifaGcType(GarbageCollector garbageCollector) {
        switch(garbageCollector.name()){
            case "CMS_GARBAGE_COLLECTOR":
                return GCCollectorType.CMS;
            case "G1_GARBAGE_COLLECTOR":
                return GCCollectorType.G1;
            default :
                return GCCollectorType.UNKNOWN;
        }
    }

    public static GarbageCollector toTamestroGcType(GCCollectorType gcCollectorType) {
        switch(gcCollectorType.name()){
            case "CMS":
                return GarbageCollector.CMS_GARBAGE_COLLECTOR;
            case "G1":
                return GarbageCollector.G1_GARBAGE_COLLECTOR;
            default :
                return GarbageCollector.UNKNOWN;
        }
    }
}
