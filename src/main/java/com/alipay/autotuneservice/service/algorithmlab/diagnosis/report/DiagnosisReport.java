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
package com.alipay.autotuneservice.service.algorithmlab.diagnosis.report;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongshu
 * @version DiagnosisReport.java, v 0.1 2022年10月31日 17:57 hongshu
 */
@Slf4j
@Data
@Builder
public class DiagnosisReport {

    private double score;

    private int problemCount;

    private int totalCount;

    private BaseInfo baseInfo;

    private List<SingleReport> reports;

    private String conclusions;

    private boolean checked;

    /**
     * 推荐的待修改参数
     * type：{@link com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.ExpertEvalItem }
     * {ADD:[Metaspace,PrintLog],DELETE:[UseParamNew},
     */
    private Map<String,String> recParams;

    private LinkedHashMap<String,List<SingleReport>> defModReports;

    private LinkedHashMap<String,List<SingleReport>> groupModReports;

}
