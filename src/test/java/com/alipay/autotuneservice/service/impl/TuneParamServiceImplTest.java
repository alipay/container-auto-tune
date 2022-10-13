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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.agent.twatch.model.PodHealthIndexEnum;
import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import com.alipay.autotuneservice.controller.model.tuneparam.AppTuneParamsVO;
import com.alipay.autotuneservice.controller.model.tuneparam.SubmitTuneParamsRequest;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamAttributeEnum;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamItem;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamVO;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsRequest;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsVO;
import com.alipay.autotuneservice.model.tune.params.DecisionedTuneParam;
import com.alipay.autotuneservice.model.tune.params.TuneParamUpdateStatus;
import com.alipay.autotuneservice.service.AgentInvokeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class TuneParamServiceImplTest {
    @Autowired
    private TuneParamServiceImpl tuneParamService;
    @Autowired
    private AgentInvokeService   agentInvokeService;

    @Test
    public void getPod() {
        String str = "xx";
        String podHealthIndex = agentInvokeService.getPodHealthIndex("xx",
            PodHealthIndexEnum.IS_TUNE_AGENT_INSTALL);
        if (Boolean.parseBoolean(podHealthIndex)) {
            System.out.println(podHealthIndex);

        }
    }

    @Test
    public void getTuneParams() {
        try {
            Integer appId = 0;
            Integer pipelineId = 0;
            AppTuneParamsVO tuneParams = tuneParamService.getTuneParams(appId, pipelineId);
            System.out.println("xx" + JSON.toJSONString(tuneParams));
        } catch (Exception e) {
        }
    }

    @Test
    public void getDecisionedTuneParams() {
        try {
            Integer appId = 0;
            Integer pipelineId = 0;
            DecisionedTuneParam decisionedTuneParams = tuneParamService.getDecisionedTuneParams(
                appId, pipelineId);
            System.out.println("result=" + JSON.toJSONString(decisionedTuneParams));
        } catch (Exception e) {
        }
    }

    @Test
    public void updateTuneParams() {
        UpdateTuneParamsVO updateTuneParamsVO = tuneParamService
            .updateTuneParams(buildUpdateTuneParamsRequest());
        System.out.println(" updateTuneParamsVO=" + JSON.toJSONString(updateTuneParamsVO));
    }

    private UpdateTuneParamsRequest buildUpdateTuneParamsRequest() {
        String json = "xx";
        return JSON.parseObject(json, new TypeReference<UpdateTuneParamsRequest>() {
        });
    }

    @Test
    public void submitTuneParam() {
        Boolean aBoolean = tuneParamService.submitTuneParam(0, 0, buildSubmitRequest());
        System.out.println(aBoolean);
    }

    @Test
    public void queryTuneParamStatus() {
        TuneParamUpdateStatus updateStatus = tuneParamService.queryTuneParamStatus(0, 0);
        System.out.println(updateStatus);
    }

    private String getAppDefaultJvm() {
        return "xx";
    }

    private SubmitTuneParamsRequest buildSubmitRequest() {
        SubmitTuneParamsRequest submitTuneParamsRequest = SubmitTuneParamsRequest.builder().build();
        submitTuneParamsRequest.setTuneParamItems(getUpdatedTuneParamItem());
        return submitTuneParamsRequest;
    }

    private List<TuneParamItem> getUpdatedTuneParamItem() {
        String jsonStr = "xx";
        return JSON.parseObject(jsonStr, new TypeReference<List<TuneParamItem>>() {
        });
    }

    private List<TuneParamItem> getDefaultTuneParamItem() {
        String jsonStr = "xx";
        System.out.println("getDefaultTuneParamItem res=" + jsonStr);
        return JSON.parseObject(jsonStr, new TypeReference<List<TuneParamItem>>() {
        });
    }

    private UpdateTuneParamsRequest buildUpdateTuneParamsRequest(Integer appId, Integer pipelineId) {
        UpdateTuneParamsRequest updateTuneParamsRequest = new UpdateTuneParamsRequest();
        updateTuneParamsRequest.setAppId(appId);
        updateTuneParamsRequest.setPipelineId(pipelineId);
        updateTuneParamsRequest.setUpdatedTuneParamItems(getUpdatedTuneParamItem());

        return updateTuneParamsRequest;
    }

    private SubmitTuneParamsRequest buildSubmitTuneParamsRequest() {
        String strJson = "xx";

        return JSON.parseObject(strJson, new TypeReference<SubmitTuneParamsRequest>() {
        });
    }

    private AppTuneParamsVO mockData(Integer appId, Integer pipelineId) {
        AppTuneParamsVO appTuneParamsVO = new AppTuneParamsVO();
        appTuneParamsVO.setAppId(appId);
        appTuneParamsVO.setPipelineId(pipelineId);
        List<TuneConfig> tuneGroups = new ArrayList<>();
        tuneGroups.add(buildTuneConfig(0, 0.1));
        tuneGroups.add(buildTuneConfig(2, 0.3));
        tuneGroups.add(buildTuneConfig(3, 0.6));
        tuneGroups.add(buildTuneConfig(4, 1));
        appTuneParamsVO.setTuneGroups(tuneGroups);
        String jsonStr = "xx";
        List<TuneParamVO> tuneParamVOList = JSON.parseObject(jsonStr, new TypeReference<List<TuneParamVO>>() {});

        appTuneParamsVO.setNewParamNum(tuneParamVOList.stream().filter(item -> item.getAttributeEnum() == TuneParamAttributeEnum.NEW)
                .collect(
                        Collectors.toList()).size());
        appTuneParamsVO.setReplaceParamNum(tuneParamVOList.stream().filter(
                item -> item.getAttributeEnum() == TuneParamAttributeEnum.REPLACE).collect(
                Collectors.toList()).size());
        appTuneParamsVO.setDelParamNum(tuneParamVOList.stream().filter(item -> item.getAttributeEnum() == TuneParamAttributeEnum.DELETE)
                .collect(
                        Collectors.toList()).size());
        return appTuneParamsVO;
    }

    private TuneConfig buildTuneConfig(int number, double percent) {
        TuneConfig tuneConfig = new TuneConfig();
        tuneConfig.setPercent(percent);
        return tuneConfig;
    }

    @Test
    void submitAutoTuneParam() {
        String defaultJvm = "xx";
        String recommend = "xx";
        tuneParamService.submitAutoTuneParam(0, 0, defaultJvm, recommend);
    }
}