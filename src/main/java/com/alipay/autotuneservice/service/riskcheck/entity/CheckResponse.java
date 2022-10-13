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
package com.alipay.autotuneservice.service.riskcheck.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class CheckResponse {

    /**
     * 执行状态
     */
    private RiskControlStatus status;

    /**
     * 检测结果
     */
    private RiskCheckEnum     result;

    /**
     * 如果有风险，就代表风险详情
     * 如果没有风险，忽律即可
     */
    private RiskCollector     riskDetail;

    /**
     * 开始检测的时间
     */
    private String            riskBeginTime;

    /**
     * 结束时间
     */
    private String            riskEndTime;

    public CheckResponse(RiskControlStatus status, RiskCheckEnum result, RiskCollector riskDetail,
                         LocalDateTime riskBeginTime, LocalDateTime riskEndTime) {
        this.status = status;
        this.result = result;
        this.riskDetail = riskDetail;
        this.riskBeginTime = riskBeginTime.format(DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.riskEndTime = riskEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
