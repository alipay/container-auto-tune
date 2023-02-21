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
package com.alipay.autotuneservice.service.alarmManger.model;

import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.dao.NotifyRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.gc.service.GcLogAnalysisService;
import com.alipay.autotuneservice.service.notification.NoticeDefAction;
import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version AlarmContext.java, v 0.1 2022年12月26日 10:37 上午 huoyuqi
 */
@Data
public class AlarmContext {

    /**
     * 规则是否生效
     */
    private AlarmStatus alarmStatus;

    /**
     * 联合类型
     */
    private CombinationType combinationType;

    /**
     * 规则数量
     */
    private List<RuleModel> ruleModels;

    /**
     * 动作类型
     */
    private List<ActionEnum> actionEnums;

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 单个规则
     */
    private RuleModel ruleModel;

    /**
     * 当前时间
     */
    private Long currentTime;

    /**
     * alarmId
     */
    private Long alarmId;

    /**
     * alarmNotice id name
     */
    private List<AlarmNoticeModel> alarmNotices;

    /**
     * 融合规则判断结果
     */
    private JudgeActionExecuteModel judgeActionExecuteModel;

    /**
     * podInfoRecord
     */
    private transient PodInfoRecord podInfoRecord;

    /**
     * jvmMonitorMetricDataRepository
     */
    private transient JvmMonitorMetricRepository jvmMonitorMetricRepository;

    /**
     * GcLogAnalysisService;
     */
    private transient GcLogAnalysisService gcLogAnalysisService;

    /**
     * NoticeDefAction
     */
    private transient NoticeDefAction noticeDefAction;

    /**
     * NotifyRepository
     */
    private transient NotifyRepository notifyRepository;

}