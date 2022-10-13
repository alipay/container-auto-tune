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
package com.alipay.autotuneservice.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamAttributeEnum;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamItem;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsRequest;
import com.alipay.autotuneservice.model.tune.params.DecisionedTuneParam;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.util.TuneParamUtil.convert2TuneParamItem;
import static com.alipay.autotuneservice.util.TuneParamUtil.wrapUpdateTuneParamsWithParamName;

public class TuneParamUtilTest {

    @Test
    public void findTuneParamModelByParamName() {
        List<TuneParamItem> defaultTuneParamItem = getUpdatedTuneParamItem();
        DecisionedTuneParam decisionedTuneParam = new DecisionedTuneParam();
        decisionedTuneParam.setDecisionedTuneParamItems(defaultTuneParamItem);
        System.out.println(decisionedTuneParam.getDecisionedTuneParams());
    }

    @Test
    public void mergeUpdateTuneParamItem() {
        String defaultJvm = "xx";
        System.out.println("xx " + defaultJvm);
        List<TuneParamItem> defaultJvmList = convert2TuneParamItem(defaultJvm);
        System.out.println("xx" + printRawJVM(defaultJvmList));
        List<TuneParamItem> updatedJvmList = getUpdatedTuneParamItem();
        System.out.println("xx" + printRawJVM(wrapUpdateTuneParamsWithParamName(updatedJvmList)));

        List<TuneParamItem> tuneParamItems1 = TuneParamUtil.mergeUpdateTuneParamItem(
            defaultJvmList, wrapUpdateTuneParamsWithParamName(updatedJvmList));
        System.out.println("xx" + printRawJVM(tuneParamItems1));
        printParamSize(tuneParamItems1);
        printParamNames(tuneParamItems1);

    }

    private String printRawJVM(List<TuneParamItem> updatedJvmList){
        return updatedJvmList.stream().map(item -> item.getParamName()).collect(Collectors.joining(" "));
    }

    @Test
    public void mergeUpdateTuneParamItem1() {
        String defaultJvm = "xx";
        String recommendJvm = "xx";
        List<TuneParamItem> defaultJvmList = convert2TuneParamItem(defaultJvm);
        List<TuneParamItem> recommendJvmList = convert2TuneParamItem(recommendJvm);
        List<TuneParamItem> result = TuneParamUtil.mergeUpdateTuneParamItem(defaultJvmList,
            wrapUpdateTuneParamsWithParamName(recommendJvmList));
        printParamSize(result);
    }

    private void printParamSize(List<TuneParamItem> result) {
        System.out.println(JSON.toJSONString(result));
        System.out.println("new: " + filter(result, TuneParamAttributeEnum.NEW).size());
        System.out.println("replace: " + filter(result, TuneParamAttributeEnum.REPLACE).size());
        System.out.println("delete: " + filter(result, TuneParamAttributeEnum.DELETE).size());
        System.out.println("SAME: " + filter(result, TuneParamAttributeEnum.SAME).size());
    }

    private void printParamNames(List<TuneParamItem> result) {
        System.out.println(JSON.toJSONString(result));
        System.out.println("new: " + filter2(result, TuneParamAttributeEnum.NEW));
        System.out.println("replace: " + filter2(result, TuneParamAttributeEnum.REPLACE));
        System.out.println("delete: " + filter2(result, TuneParamAttributeEnum.DELETE));
        System.out.println("SAME: " + filter2(result, TuneParamAttributeEnum.SAME));
    }

    private String filter2(List<TuneParamItem> defaultJvmList,TuneParamAttributeEnum tuneParamAttributeEnum ){
        return defaultJvmList.stream().filter(item -> item.getAttributeEnum() == tuneParamAttributeEnum).map(item -> item.getParamName()).collect(
                Collectors.joining(" "));
    }

    private List<TuneParamItem> filter(List<TuneParamItem> defaultJvmList,TuneParamAttributeEnum tuneParamAttributeEnum ){
        return defaultJvmList.stream().filter(item -> item.getAttributeEnum() == tuneParamAttributeEnum).collect(Collectors.toList());
    }

    private String getAppDefaultJvm() {
        return "xx";
    }

    @Test
    public void print() {
        getUpdatedTuneParamItem();
    }

    private List<TuneParamItem> getUpdatedTuneParamItem() {
        String jsonStr = "xx";
        System.out.println(jsonStr);
        return JSON.parseObject(jsonStr, new TypeReference<List<TuneParamItem>>() {
        });
    }

    private List<TuneParamItem> getDefaultTuneParamItem() {
        String jsonStr = "xx";
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
}