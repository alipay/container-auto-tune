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
package com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend;

import com.alipay.autotuneservice.model.tune.params.JVMParamEnum;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.service.algorithmlab.JvmOptionMeta;
import com.alipay.autotuneservice.service.algorithmlab.ProblemMetricEnum;
import com.alipay.autotuneservice.service.algorithmlab.TuneParamModel;
import com.alipay.autotuneservice.service.algorithmlab.template.TemplateLab;
import com.alipay.autotuneservice.util.TuneParamUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.TuneTrendExpert.CMS_TUNE_TREND;
import static com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.TuneTrendExpert.COMMON_TUNE_TREND;
import static com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend.TuneTrendExpert.G1_TUNE_TREND;

/**
 * @author hongshu
 * @version TuneLab.java, v 0.1 2022年10月31日 17:57 hongshu
 */
@Slf4j
@Data
public class Trend {

    /**
     * 根据异常问题提供相关参数及调节趋势
     * @param problemMetricEnums
     */
    public static List<ExpertEvalItem> relatedParamSuggest(List<ProblemMetricEnum> problemMetricEnums, String jvm,
                                                           boolean combine, int memCapacity){
        GarbageCollector garbageCollector = GarbageCollector.matchGarbageCollectorByJvmOpt(jvm);
        if(GarbageCollector.UNKNOWN == garbageCollector){
            return null;
        }
        Map<String,TuneParamModel> tuneParamModelMap = TuneParamUtil.convert2TuneParamModel(jvm).stream()
                .collect(Collectors.toMap(TuneParamModel::getParamName,r->r));

        // TODO 与算法确定 1，有些需要往上调，有些需要往下调，该如何处理   2，基于模版，第一次补充了一版参数该如何处理
        if(CollectionUtils.isNotEmpty(problemMetricEnums)){
            if(combine){
                List<ExpertEvalItem> items = combineBaseTemplate(garbageCollector,jvm,memCapacity);
                if(CollectionUtils.isNotEmpty(items)){
                    // 当前先按照有新增项，第一轮调参按照新增项实验
                    return items;
                }
            }
            List<ExpertEvalItem> items = problemMetricEnums.stream().map(r -> {
                List<ExpertJvmPlan> expertJvmPlansTmp = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(COMMON_TUNE_TREND.get(r))){
                    expertJvmPlansTmp.addAll(COMMON_TUNE_TREND.get(r));
                }
                if(GarbageCollector.G1_GARBAGE_COLLECTOR==garbageCollector && CollectionUtils.isNotEmpty(G1_TUNE_TREND.get(r))){
                    expertJvmPlansTmp.addAll(G1_TUNE_TREND.get(r));
                }else if(GarbageCollector.CMS_GARBAGE_COLLECTOR==garbageCollector && CollectionUtils.isNotEmpty(CMS_TUNE_TREND.get(r))){
                    expertJvmPlansTmp.addAll(CMS_TUNE_TREND.get(r));
                }
                return  expertJvmPlansTmp;
            }).flatMap(Collection::stream).map(r -> {
                TuneParamModel tuneParamModel = tuneParamModelMap.getOrDefault(r.getJvmOpts(), JvmOptionMeta.JVM_DEFAULT_VALUES.get(r.getJvmOpts()));
                String value = tuneParamModel==null ? "" : tuneParamModel.getParamVal();
                String target = tuneParamModel==null ? "" : tuneParamModel.getParamName()+tuneParamModel.getOperator()+tuneParamModel.getParamVal();
                return new ExpertEvalItem(target, value, r.getJvmOpts(), r.getExpertEvalType());
            }).collect(Collectors.toList());

            // 专家经验过滤处理
            if(CollectionUtils.isNotEmpty(items)){
                return filterExpertItem(items);
            }
        }
        return null;
    }

    private static List<ExpertEvalItem> filterExpertItem(List<ExpertEvalItem> items) {
        Map<String, ExpertEvalItem> itemMap = new HashMap<>(items.size());
        items.forEach(item -> itemMap.put(item.getParam(), item));
        return new ArrayList<>(itemMap.values());
    }

    /**
     * 根据默认模版参数，填充当前jvm配置
     * @param garbageCollector
     * @param jvm
     * @param memCapacity
     */
    private static List<ExpertEvalItem>  combineBaseTemplate(GarbageCollector garbageCollector, String jvm, int memCapacity) {
        Map<JVMParamEnum, TuneParamModel> target = TemplateLab.buildEssParams(garbageCollector,memCapacity);
        List<String> elements = Arrays.asList(jvm.split(" "));
        elements.forEach(r -> {
            JVMParamEnum jvmParamEnum = JVMParamEnum.match(r);
            if(jvmParamEnum!=null && target.containsKey(jvmParamEnum)){
                target.remove(jvmParamEnum);
            }
        });
        // 根据不存在的参数，生成专家经验建议
        if(MapUtils.isNotEmpty(target)){
            return target.values().stream().map(r -> {
                String value = r.getParamVal();
                String opt = r.getParamName()+r.getOperator()+r.getParamVal();
                return new ExpertEvalItem(opt, value, r.getParamName(), ExpertEvalType.APPEND);
            }).collect(Collectors.toList());
        }
        return null;
    }

}
