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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.HealthCheckResultRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckResultRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.exception.ResourceNotFoundException;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.service.ExpertService;
import com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.SingleReport;
import com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.ExpertEvalItem;
import com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.ExpertEvalResult;
import com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.ExpertEvalResultType;
import com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.Trend;
import com.alipay.autotuneservice.util.GsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.util.UserUtil.TUNE_JVM_APPEND;

/**
 * @author dutianze
 * @version ExpertServiceImpl.java, v 0.1 2022年04月26日 17:09 dutianze
 */
@Service
public class ExpertServiceImpl implements ExpertService {

    @Autowired
    private AppInfoRepository appInfoRepository;

    @Autowired
    private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired
    private PodInfo podInfo;

    @Override
    public ExpertEvalResult eval(Integer appId) {
        AppInfo appInfo = appInfoRepository.findById(appId);
        if (appInfo == null) {
            throw new ResourceNotFoundException(ResultCode.APP_NOT_FOUND);
        }
        String jvmConfig = appInfo.getAppDefaultJvm();
        // 查询最近一次的健康检查风险项
        HealthCheckResultRecord checkResultRecord = healthCheckResultRepository.findFirstByAppId(appInfo.getId());
        if (StringUtils.isNotEmpty(checkResultRecord.getProbleam())) {
            List<SingleReport> reports = GsonUtil.fromJsonList(checkResultRecord.getProbleam(), SingleReport.class);
            List<ProblemMetricEnum> problems = reports.stream().map(r -> ProblemMetricEnum.valueOf(r.getName())).collect(
                    Collectors.toList());
            if (CollectionUtils.isNotEmpty(problems)) {
                // 获取内存规格
                PodInfoRecord podInfoRecord = podInfo.findOneRunningPodByAppId(appInfo.getId());
                // 精度损失，但向下取整不影响调参
                int spec = podInfoRecord.getMemLimit() / 1024;
                List<ExpertEvalItem> items = Trend.relatedParamSuggest(problems, jvmConfig, !jvmConfig.contains(TUNE_JVM_APPEND), spec);
                if (CollectionUtils.isNotEmpty(items)) {
                    List<ExpertEvalItem> itemTmp = items.stream()
                            .filter(s -> StringUtils.isNotEmpty(s.getTarget()) && StringUtils.isNotEmpty(s.getValue()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(itemTmp)) {
                        return packExpertEvalResult(itemTmp);
                    }
                }
                throw new ServerException(ResultCode.EXPERT_KNOWLEDGE_NOT_FOUND);
            }
        }
        return null;
    }

    private ExpertEvalResult packExpertEvalResult(List<ExpertEvalItem> items) {
        ExpertEvalResult.ExpertEvalResultBuilder expertEvalResult = ExpertEvalResult.builder().evalList(items);
        if (items.stream().anyMatch(r -> r.getParam().contains("-Xms") || r.getParam().contains("-Xmx"))) {
            return expertEvalResult.type(ExpertEvalResultType.COST.getValue()).build();
        }
        return expertEvalResult.type(ExpertEvalResultType.PERF.getValue()).build();
    }
}