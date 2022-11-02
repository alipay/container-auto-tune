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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.JvmTuningRiskCenterRepository;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.JvmTuningRiskCenter;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.RiskCheckTask;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.repository.JvmMonitorMetricDataService;
import com.alipay.autotuneservice.service.riskcheck.entity.CheckType;
import com.alipay.autotuneservice.service.riskcheck.entity.MConsumer;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCheckEnum;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCheckParam;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCollector;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskTaskStatus;
import com.alipay.autotuneservice.util.LogUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RiskCheckHandler {

    @Autowired
    private JvmTuningRiskCenterRepository  jvmTuningRiskCenterRepository;

    @Autowired
    private JvmMonitorMetricDataService jvmMetricRepository;

    public RiskCheckEnum executeRiskCheck(RiskCheckTask riskCheckTask, MConsumer<RiskCheckEnum, RiskCollector, RiskTaskStatus> callBack) {
        log.info(LogUtil.scureLogFormat("executeRiskCheck start"));
        try {
            callBack.apply(null, null, RiskTaskStatus.EXECUTING);
            RiskCheckParam param = JSON.parseObject(riskCheckTask.getExecuteParam(), new TypeReference<RiskCheckParam>() {
            });
            Set<CheckType> checkTypes = param.getCheckTypes();
            Map<String, JvmMonitorMetricData> currentMonitor = findPodsMetric(param.getPodnames());
            if (CollectionUtils.isEmpty(currentMonitor)) {
                callBack.apply(RiskCheckEnum.UNKNOW, new RiskCollector("获取不到pod监控数据"), RiskTaskStatus.END);
                log.info("获取不到pod监控数据 结束评估 置为UNKNOW");
                return RiskCheckEnum.UNKNOW;
            }
            Map<String, JvmTuningRiskCenter> reference = jvmTuningRiskCenterRepository.find(param.getAppID(), checkTypes);
            if (CollectionUtils.isEmpty(reference)) {
                callBack.apply(RiskCheckEnum.UNKNOW, new RiskCollector("算法画像为空"), RiskTaskStatus.END);
                log.info("算法画像为空 结束评估 置为UNKNOW");
                return RiskCheckEnum.UNKNOW;
            }
            //存放每台pod的决策结果<podName,result>
            Map<String, RiskCheckEnum> pod_check_result = new HashMap<>();
            RiskCollector riskCollector = new RiskCollector(param.getCheckTypes());
            currentMonitor.entrySet().stream().forEach(entry -> {
                RiskCheckEnum pod_result = checkSinglePod(new ArrayList<>(checkTypes), reference, entry.getValue(), riskCollector::collector);
                if (pod_result.existRisk()) {
                    riskCollector.collector(entry.getKey());
                }
                pod_check_result.put(entry.getKey(), pod_result);
                log.info(LogUtil.scureLogFormat("checkSinglePod end %s - %s", entry.getKey(), pod_result));
            });
            log.info(LogUtil.scureLogFormat("executeRiskCheck end,detail %s", JSON.toJSONString(pod_check_result)));
            RiskCheckEnum result = summarizeResult(Lists.newArrayList(pod_check_result.values()));
            if (RiskCheckEnum.UNKNOW == result){
                callBack.apply(result, new RiskCollector("获取不到pod监控数据"), RiskTaskStatus.END);
                return result;
            }
            callBack.apply(result, riskCollector, RiskTaskStatus.END);
            return result;
        } catch (Exception e) {
            log.error(LogUtil.scureLogFormat("executeRiskCheck error"), e);
            callBack.apply(RiskCheckEnum.UNKNOW, new RiskCollector(e.getMessage()), RiskTaskStatus.END);
            return RiskCheckEnum.UNKNOW;
        }
    }

    /**
     * 检查单pod状态
     *
     * @param checkTypes      检查指标集合
     * @param reference       指标画像
     * @param pod_metricData  pod当前监控
     * @param risk_collection 存放风险详情
     * @return
     */
    private RiskCheckEnum checkSinglePod(List<CheckType> checkTypes,
                                         Map<String, JvmTuningRiskCenter> reference,
                                         JvmMonitorMetricData pod_metricData,
                                         BiConsumer<CheckType, RiskCheckEnum> callback) {
        Map<String, String> pod_monitor = JSONObject.parseObject(JSON.toJSONString(pod_metricData),
            new TypeReference<Map<String, String>>() {
            });
        if (StringUtils.isEmpty(pod_metricData.getPod()) || CollectionUtils.isEmpty(pod_monitor)) {
            return RiskCheckEnum.UNKNOW;
        }
        List<RiskCheckEnum> result = new ArrayList<>();
        for (int i = 0; i < checkTypes.size(); i++) {
            CheckType checkType = checkTypes.get(i);
            JvmTuningRiskCenter pod_reference = reference.get(checkType.getDesc());
            Double pod_current_value = Double.valueOf(pod_monitor.get(checkType.getDesc()));
            if (null == pod_reference || StringUtils.isEmpty(pod_monitor.get(checkType.getDesc()))) {
                log.info(LogUtil.scureLogFormat("checkSinglePod %s - %s",
                    JSON.toJSONString(pod_reference), pod_current_value));
                return RiskCheckEnum.UNKNOW;
            }
            if (pod_current_value.compareTo(pod_reference.getLowline()) < 0) {
                log.info(LogUtil.scureLogFormat("checkSinglePod lowrisk %s - %s - [%s]",
                    checkType.getDesc(), pod_current_value, JSON.toJSONString(pod_reference)));
                result.add(RiskCheckEnum.LOW_RISK);
                callback.accept(checkType, RiskCheckEnum.LOW_RISK);
                continue;
            }
            if (pod_current_value.compareTo(pod_reference.getUpline()) > 0) {
                log.info(LogUtil.scureLogFormat("checkSinglePod highrisk %s - %s - [%s]",
                    checkType.getDesc(), pod_current_value, JSON.toJSONString(pod_reference)));
                result.add(RiskCheckEnum.HIGH_RISK);
                callback.accept(checkType, RiskCheckEnum.HIGH_RISK);
            }
        }
        if (result.contains(RiskCheckEnum.HIGH_RISK)) {
            return RiskCheckEnum.HIGH_RISK;
        }
        if (result.contains(RiskCheckEnum.LOW_RISK)) {
            return RiskCheckEnum.LOW_RISK;
        }
        return RiskCheckEnum.NORMAL;
    }

    private RiskCheckEnum summarizeResult(List<RiskCheckEnum> decisions) {
        if (CollectionUtils.isEmpty(decisions)) {
            log.info(LogUtil.scureLogFormat("投票箱为空"));
            return RiskCheckEnum.UNKNOW;
        }
        List<RiskCheckEnum> high = decisions.stream().filter(
                decisionResultEnum -> decisionResultEnum == RiskCheckEnum.HIGH_RISK).collect(
                Collectors.toList());
        if (high.size() >= decisions.size() * 0.1) {
            return RiskCheckEnum.HIGH_RISK;
        }
        List<RiskCheckEnum> low = decisions.stream().filter(
                decisionResultEnum -> decisionResultEnum == RiskCheckEnum.LOW_RISK).collect(
                Collectors.toList());
        if (low.size() >= decisions.size() * 0.3) {
            return RiskCheckEnum.LOW_RISK;
        }
        List<RiskCheckEnum> unknow = decisions.stream().filter(
                decisionResultEnum -> decisionResultEnum == RiskCheckEnum.UNKNOW).collect(
                Collectors.toList());
        if (unknow.size() >= decisions.size() * 0.1) {
            log.info(LogUtil.scureLogFormat("部分机器监控数据为空-UNKNOW"));
            return RiskCheckEnum.UNKNOW;
        }
        return RiskCheckEnum.NORMAL;
    }

    /**
     * @return key   - podname
     * value - JvmMonitorMetricData
     */
    private Map<String, JvmMonitorMetricData> findPodsMetric(List<String> pods) {
        try {
            log.info(LogUtil.scureLogFormat("findPodsMetric 开始 获取pod监控数据 %s",pods));
            return pods.parallelStream().collect(Collectors.toMap(pod -> pod, jvmMetricRepository::getPodLatestOneMinuteJvmMetric, (k1, k2) -> k1, ConcurrentHashMap::new));
        } catch (Exception e) {
            log.error(LogUtil.scureLogFormat("findPodsMetric error"), e);
            return null;
        }
    }
}