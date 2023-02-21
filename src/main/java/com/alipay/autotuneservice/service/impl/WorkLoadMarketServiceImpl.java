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
import com.alipay.autotuneservice.controller.model.WorkLoadMarketVO;
import com.alipay.autotuneservice.dao.TuneLogInfo;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.service.WorkloadService;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version WorkLoadMarketServiceImpl.java, v 0.1 2022年10月26日 6:47 下午 huoyuqi
 */
@Slf4j
@Service
public class WorkLoadMarketServiceImpl implements WorkloadService {

    @Autowired
    private TunePlanRepository tunePlanRepository;

    @Autowired
    private TunePipelineRepository tunePipelineRepository;

    @Autowired
    private TuneLogInfo tuneLogInfo;

    @Override
    public WorkLoadMarketVO getWorkLoadMarket(Long start, Long end) {
        //1.获取调优计划 获取应用的数量 获取pod数量 获取节省费用 调优次数
        Long nowTime = System.currentTimeMillis();
        Long startTime = start == null ? nowTime - 7 * 24 * 60 * 60 * 1000L : start;
        Long endTime = end == null ? nowTime : end;
        WorkLoadMarketVO workLoadMarketVO = new WorkLoadMarketVO();
        List<TunePlan> tunePlanList = tunePlanRepository.findByTime(startTime, endTime);
        if (CollectionUtils.isEmpty(tunePlanList)) {
            return null;
        }
        workLoadMarketVO.setTuneTimes(tunePlanList.size());

        //2.获取到调优应用数量
        Set<TunePlan> tunePlanSet = new TreeSet<>(Comparator.comparing(TunePlan::getAppId));
        tunePlanSet.addAll(tunePlanList);
        workLoadMarketVO.setOptimizedWorkloads(tunePlanSet.size());

        //3.获取调优pod的数量
        List<Integer> tunePlanIds = tunePlanList.stream().map(TunePlan::getId).collect(Collectors.toList());
        List<TunePipeline> tunePipelines = tunePipelineRepository.findByPlanIds(tunePlanIds);
        if (CollectionUtils.isEmpty(tunePipelines)) {
            workLoadMarketVO.setOptimizedPods(0);
        }
        List<Integer> tunePipelineIds = tunePipelines.stream().map(TunePipeline::getPipelineId).collect(Collectors.toList());
        List<TuneLogInfoRecord> tuneLogInfoRecords = tuneLogInfo.findRecordByPipelineIds(tunePipelineIds, "DELETE");
        workLoadMarketVO.setOptimizedPods(tuneLogInfoRecords.size());
        //4.获取调优的cost
        double totalIncome = tunePlanList.stream()
                .filter(tunePlan -> tunePlan.getTuneEffectVO() != null && tunePlan.getTuneEffectVO().getTotalIncome() != null
                        && tunePlan.getTuneEffectVO().getTotalIncome() > 0.0)
                .mapToDouble(plan -> plan.getTuneEffectVO().getTotalIncome()).sum();
        workLoadMarketVO.setTotalIncome(totalIncome);

        //5.获取每日调优的app数量
        workLoadMarketVO.setOptimizedAppMap(getTimeMap(tunePlanList, null, startTime, endTime));

        //6.获取每日调优的pod数量
        workLoadMarketVO.setOptimizedPodMap(getTimeMap(null, tuneLogInfoRecords, startTime, endTime));

        return workLoadMarketVO;
    }

    private Map<String, Long> getTimeMap(List<TunePlan> tunePlanList, List<TuneLogInfoRecord> tuneLogInfoRecords, Long start, Long end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<Object, Long> timeSumMap = CollectionUtils.isEmpty(tunePlanList) && CollectionUtils.isNotEmpty(tuneLogInfoRecords) ?
                tuneLogInfoRecords.stream().collect(
                        Collectors.groupingBy(item -> item.getCreatedTime().format(formatter), Collectors.counting())) :
                tunePlanList.stream()
                        .collect(Collectors.groupingBy(item -> item.getCreatedTime().format(formatter), Collectors.counting()));

        Map<String, Long> timeMap = new LinkedHashMap<>();
        //遍历给定的日期期间的每一天
        for (int i = 0; !Duration.between(DateUtils.asLocalData(start).plusDays(i), DateUtils.asLocalData(end)).isNegative(); i++) {
            //添加日期
            String time = DateUtils.asLocalData(start).plusDays(i).format(formatter);
            timeMap.put(time, timeSumMap.getOrDefault(time, 0L));
            System.out.println(time);
        }

        System.out.println(JSON.toJSONString(timeMap));
        return timeMap;
    }

}