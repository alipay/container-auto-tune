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
package com.alipay.autotuneservice.model.report;

import com.alipay.autotuneservice.model.rule.RuleType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dutianze
 * @version ReportModel.java, v 0.1 2022年02月21日 17:09 dutianze
 */
@Data
public class ReportModel<T> {

    private Integer          id;
    private String           accessToken;
    private String           taskPipelineId;
    private String           targetObject;
    private TargetDeployType targetDeployType;
    private RuleType         targetRuleType;
    private ReportType       reportType;
    private List<String>     reportExecuteStep;
    private ReportRequest    reportRequest;
    private ReportRecommend  reportRecommend;
    private ReportResponse   reportResponse;
    private String           reportFileUrl;
    private ReportStatus     reportStatus;
    private LocalDateTime    createdTime;
    private T                report;

}