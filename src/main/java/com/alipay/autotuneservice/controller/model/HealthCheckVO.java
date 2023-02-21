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
package com.alipay.autotuneservice.controller.model;

import com.alipay.autotuneservice.model.common.HealthCheckStatus;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.SingleReport;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author huoyuqi
 * @version HealthCheckVO.java, v 0.1 2022年04月27日 4:00 下午 huoyuqi
 */
@Data
public class HealthCheckVO {

    /**
     * 健康项检查id
     */
    private int id;

    /**
     * 应用名称id
     */
    private int appId;

    /**
     * 健康检查项分数
     */
    private int score;

    /**
     * 健康检查异常项数
     */
    private int checkNum;

    /**
     * 一级分类
     */
    private List<String> titles;

    /**
     * 二级分类
     */
    private LinkedHashMap<String,List<SingleReport>> contents;

    /**
     * 二级分类
     */
    private LinkedHashMap<String,List<SingleReport>> groupContents;

    private DiagnosisReport report;

    /**
     * 检测完成数量
     */
    private int checkedNum;

    /**
     * 检测时间
     */
    private Long checkTime;

    /**
     * 参照检查开始时间
     */
    private Long checkStartTime;

    /**
     * 参照检查结束时间
     */
    private Long checkEndTime;

    /**
     * 状态
     */
    private HealthCheckStatus status;
}


