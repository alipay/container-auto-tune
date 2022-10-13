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
package com.alipay.autotuneservice.service.riskcheck;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.RiskCheckControlRepository;
import com.alipay.autotuneservice.dao.RiskCheckTaskRepository;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.RiskCheckControl;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.RiskCheckTask;
import com.alipay.autotuneservice.service.riskcheck.entity.*;
import com.alipay.autotuneservice.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@Service
public class RiskCheckService {

    @Autowired
    private RiskCheckTaskRepository    riskCheckTaskRepository;

    @Autowired
    private RiskCheckControlRepository riskCheckControlRepository;

    @Autowired
    private AppInfoRepository          appInfoRepository;

    /**
     * 提交风险检测任务
     */
    public String submitRiskCheckJob(RiskCheckParam riskCheckParam) {
        String traceID = SystemUtil.generateDecisionId();
        try {
            if (StringUtils.isEmpty(riskCheckParam.getAppName())) {
                riskCheckParam.setAppName(appInfoRepository.getAppName(riskCheckParam.getAppID()));
            }
            Integer jobID = riskCheckControlRepository.save(riskCheckParam, traceID);
            List<Integer> taskIds = LongStream.range(1, riskCheckParam.getCheckTime() + 1).mapToObj(item -> {
                RiskCheckTask riskCheckTask = createRiskCheckTask(item, jobID, riskCheckParam, traceID);
                return riskCheckTaskRepository.save(riskCheckTask);
            }).collect(Collectors.toList());
            riskCheckControlRepository.update(jobID, taskIds);
        } catch (Exception e) {
            log.error("submitRiskCheckJob error    ", e);
        }
        return traceID;
    }

    /**
     * 获取 风险 检测结果
     */
    public CheckResponse getRiskCheckResult(String traceID) {
        RiskCheckControl riskCheckControl = riskCheckControlRepository.find(traceID);
        log.info("getRiskCheckResult {} - {}", traceID, JSON.toJSONString(riskCheckControl));
        RiskControlStatus status = RiskControlStatus.valueOf(riskCheckControl.getStatus());
        RiskCheckEnum riskCheckEnum = RiskCheckEnum.valueOf(riskCheckControl.getCheckResult());
        log.info("{} {}", traceID, JSON.toJSONString(riskCheckControl));
        return new CheckResponse(status, riskCheckEnum, JSON.parseObject(
            riskCheckControl.getRiskMsg(), new TypeReference<RiskCollector>() {
            }), riskCheckControl.getRiskbegintime(), riskCheckControl.getRiskendtime());
    }

    private RiskCheckTask createRiskCheckTask(Long i, Integer jobID, RiskCheckParam riskCheckParam,
                                              String traceID) {
        RiskCheckTask riskCheckTask = new RiskCheckTask();
        riskCheckTask.setJobId(jobID);
        riskCheckTask.setExecuteTime(LocalDateTime.now().plusMinutes(
            riskCheckParam.getCheckOffset() * i));
        riskCheckTask.setExecuteParam(JSON.toJSONString(riskCheckParam));
        riskCheckTask.setTaskStatus(RiskTaskStatus.READY.name());
        riskCheckTask.setTaskTraceId(String.format("%s_%s", traceID, i));
        riskCheckTask.setCreateTime(LocalDateTime.now());
        return riskCheckTask;
    }
}
