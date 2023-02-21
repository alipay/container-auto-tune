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
package com.alipay.autotuneservice.service.algorithmlab;


import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.SingleReport;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 打分服务，对风险评估结果进行打分
 * @author hongshu
 * @version ScoringSys.java, v 0.1 2022年12月06日 17:57 hongshu
 */
@Slf4j
public class ScoringSys {

    /**
     * score
     * 系数 = 100/（监测项*加权值）
     */
    public static final int PERFECT_MARK = 100;
    public static double buildScoreFromDiagnoseReport(DiagnosisReport diagnosisReport){
        List<SingleReport> reports = diagnosisReport.getReports();
        double weight = PERFECT_MARK/(reports.stream()
                .map(r -> ProblemMetricEnum.valueOf(r.getName()).getDangeLevelEnum().getMultiple())
                .mapToDouble(r->r).sum());
        double score =  reports.stream().filter(SingleReport::isNormal)
                .map(r -> ProblemMetricEnum.valueOf(r.getName()).getDangeLevelEnum().getMultiple())
                .mapToDouble(r->r)
                .sum()
                *weight;
        // 保留小数点后两位
        return ((int)score*PERFECT_MARK)/(PERFECT_MARK*1.0);
    }


}
