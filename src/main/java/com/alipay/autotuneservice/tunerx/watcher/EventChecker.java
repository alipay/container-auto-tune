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
package com.alipay.autotuneservice.tunerx.watcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.*;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.JvmMarketInfo;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.TuningParamTaskData;
import com.alipay.autotuneservice.model.pipeline.TuneContext;
import com.alipay.autotuneservice.model.pipeline.TuneEvent;
import com.alipay.autotuneservice.model.pipeline.TuneEventType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.service.ConfigInfoService;
import com.alipay.autotuneservice.service.TuneEffectService;
import com.alipay.autotuneservice.service.TuneParamService;
import com.alipay.autotuneservice.service.pipeline.TuneEventProducer;
import com.alipay.autotuneservice.service.pipeline.TunePipelineService;
import com.alipay.autotuneservice.service.riskcheck.RiskCheckService;
import com.alipay.autotuneservice.tunepool.TuneProcessor;
import com.alipay.autotuneservice.util.BaseLineParam;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : EventChecker.java, v 0.1 2022年04月18日 16:31 chenqu Exp $
 */
@Slf4j
public abstract class EventChecker {

    private static final String           DEFAULT_KEY = "java_opts_base";
    protected TunePipeline                tunePipeline;
    @Getter
    private ApplicationContext            applicationContext;
    private TuneEventProducer             tuneEventProducer;
    protected TuningParamTaskData         tuningParamTaskData;
    protected AppInfoRepository           appInfoRepository;
    protected JvmMarketInfo               jvmMarketInfo;
    protected ConfigInfoService           configInfoService;
    protected PodInfo                     podInfo;
    protected TuneProcessor               tuneProcessor;
    protected TunePlanRepository          tunePlanRepository;
    protected TuneLogInfo                 tuneLogInfo;
    protected RiskCheckService            riskCheckService;
    protected TunePipelineService         tunePipelineService;
    protected TuneParamService            tuneParamService;
    protected TuneEffectService           tuneEffectService;
    protected TunePipelinePhaseRepository tunePipelinePhaseRepository;

    public EventChecker(ApplicationContext applicationContext, TunePipeline tunePipeline) {
        this.applicationContext = applicationContext;
        this.tunePipeline = tunePipeline;
        if (applicationContext != null) {
            this.tuneEventProducer = (TuneEventProducer) applicationContext
                .getBean("tuneEventProducer");
            this.tuningParamTaskData = (TuningParamTaskData) applicationContext
                .getBean("tuningParamTaskDataImpl");
            this.appInfoRepository = (AppInfoRepository) applicationContext
                .getBean("appInfoRepositoryImpl");
            this.jvmMarketInfo = (JvmMarketInfo) applicationContext.getBean("jvmMarketInfoImpl");
            this.configInfoService = (ConfigInfoService) applicationContext
                .getBean("configInfoServiceImpl");
            this.podInfo = (PodInfo) applicationContext.getBean("podInfoImpl");
            this.tuneProcessor = (TuneProcessor) applicationContext.getBean("tuneProcessor");
            this.tunePlanRepository = (TunePlanRepository) applicationContext
                .getBean("tunePlanRepositoryImpl");
            this.tuneLogInfo = (TuneLogInfo) applicationContext.getBean("tuneLogInfoImpl");
            this.riskCheckService = (RiskCheckService) applicationContext
                .getBean("riskCheckService");
            this.tunePipelineService = (TunePipelineService) applicationContext
                .getBean("tunePipelineServiceImpl");
            this.tuneParamService = (TuneParamService) applicationContext
                .getBean("tuneParamServiceImpl");
            this.tuneEffectService = (TuneEffectService) applicationContext
                .getBean("tuneEffectServiceImpl");
            this.tunePipelinePhaseRepository = (TunePipelinePhaseRepository) applicationContext
                .getBean("tunePipelinePhaseRepositoryImpl");
        }
    }

    public boolean check() {
        try {
            log.info("check is begin");
            return doCheck();
        } catch (Exception ex) {
            log.error("check is error", ex);
            return Boolean.FALSE;
        }
    }

    public void submit() {
        log.info("submit is begin");
        this.submitNext();
    }

    public abstract TuneStage tuneStage();

    public abstract boolean doCheck();

    public abstract void submitNext();

    protected void submitEvent(Integer pipelineId, TuneEventType eventType, TuneContext tuneContext) {
        log.info("submitEvent, pipelineId:{} eventType:{}", pipelineId, eventType);
        TuneEvent tuneEvent = new TuneEvent(pipelineId, eventType);
        tuneEvent.setContext(tuneContext);
        tuneEventProducer.send(tuneEvent);
    }

    protected String generateJvm(String trialParams, String defaultJvm) {
        Map<String, Map<String, String>> jvmConfigMap = JSON.parseObject(trialParams,
            new TypeReference<Map<String, Map<String, String>>>() {
            });
        Set<Map.Entry<String, Map<String, String>>> sets = jvmConfigMap.entrySet();
        for (Map.Entry<String, Map<String, String>> entry : sets) {
            List<BaseLineParam.Param> params = new ArrayList<>();
            BaseLineParam.Param param = new BaseLineParam.Param();
            param.setConfigParam(entry.getValue());
            param.setOrigin(defaultJvm);
            param.setConfigKey(DEFAULT_KEY);
            switch (entry.getKey()) {
                case "APPEND":
                case "UPDATE":
                    param.setProcessType(BaseLineParam.Param.ProcessType.ADD);
                    break;
                case "DELETE":
                    param.setProcessType(BaseLineParam.Param.ProcessType.DELETE);
                    break;
                default:
                    break;
            }
            if (StringUtils.isNotEmpty(param.getOrigin())) {
                params.add(param);
            }
            BaseLineParam baseLineParam = new BaseLineParam();
            baseLineParam.setParams(params);
            Map<String, String> result = baseLineParam.buildParam();
            defaultJvm = result.get(DEFAULT_KEY);
        }
        return defaultJvm;
    }

    protected String invokeJvm(String jvm) {
        if (jvm.contains(UserUtil.TUNE_JVM_APPEND)) {
            String[] arrays = jvm.split(" ");
            List<String> filterArrs = Arrays.stream(arrays).filter(arr -> !arr.contains(UserUtil.TUNE_JVM_APPEND)).collect(
                    Collectors.toList());
            jvm = String.join(" ", filterArrs);
        }
        return jvm;
    }
}