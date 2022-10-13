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

import com.alipay.autotuneservice.controller.model.BatchGroupVO;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.TuneLogInfo;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TuneActionStatus;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.service.BatchGroupService;
import com.alipay.autotuneservice.service.riskcheck.RiskCheckService;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : BatchGroupService.java, v 0.1 2022年05月24日 14:09 chenqu Exp $
 */
@Service
@Slf4j
public class BatchGroupServiceImpl implements BatchGroupService {

    private final static Map<Integer, Double> BATCH_MAP = ImmutableMap.of(1, 0.1, 2, 0.3, 3, 0.6,
                                                            4, 1d);

    @Autowired
    private PodInfo                           podInfo;
    @Autowired
    private TuneLogInfo                       tuneLogInfo;
    @Autowired
    private RiskCheckService                  riskCheckService;
    @Autowired
    private TunePlanRepository                tunePlanRepository;

    @Override
    public BatchGroupVO generateBatchGroup(TunePipeline pipeline) {
        BatchGroupVO batchGroupVO = new BatchGroupVO(pipeline.getPipelineId());
        batchGroupVO.setTuneStage(pipeline.getStage());
        //构建GroupDetail
        Map<Integer, Double> batchMap = pipeline.getContext().getBatchMap();
        if (MapUtils.isEmpty(batchMap)) {
            batchMap = BATCH_MAP;
        }
        List<BatchGroupVO.GroupDetail> tmpList = Lists.newArrayList();
        List<PodInfoRecord> podInfoRecords = podInfo.getByAppId(pipeline.getAppId());
        //获取总机器数
        Integer totalNum = podInfoRecords.size();
        //获取jvm机器数
        long jvmMarketId = pipeline.getContext().getMetaData().getJvmMarketId();
        List<PodInfoRecord> jvmPodInfoRecords = podInfo.getByAllPodByAppId(pipeline.getAppId()).stream().filter(podInfoRecord -> {
            String jvm = podInfoRecord.getPodJvm();
            return StringUtils.contains(jvm, UserUtil.getTuneJvmConfig((int) jvmMarketId));
        }).collect(Collectors.toList());
        Integer jvmTotalNum = jvmPodInfoRecords.size();
        Map<Integer, MetaData> batchMetaDataMap = pipeline.getContext().getBatchMeatMap();
        //组织数据
        batchMap.forEach((batchNo, percent) -> {
            AtomicLong atomicLong = new AtomicLong();
            BatchGroupVO.GroupDetail groupDetail = convertGroup(pipeline, batchNo, percent, totalNum, jvmTotalNum, batchMetaDataMap,
                    (s) -> {
                        atomicLong.set(s);
                        return Boolean.TRUE;
                    });
            Integer targetRestartNo = groupDetail.getTargetRestartNo();
            if (CollectionUtils.isNotEmpty(jvmPodInfoRecords) && jvmPodInfoRecords.size() < targetRestartNo) {
                jvmPodInfoRecords.forEach(successPod -> {
                    BatchGroupVO.PodDetail podDetail = new BatchGroupVO.PodDetail();
                    podDetail.setSuccessPodName(successPod.getPodName());
                    podDetail.setFinishTime(DateUtils.asTimestamp(successPod.getCreatedTime()));
                    podDetail.setCreateTime(atomicLong.get());
                });
            }
            if (CollectionUtils.isNotEmpty(jvmPodInfoRecords) && jvmPodInfoRecords.size() >= targetRestartNo) {
                List<PodInfoRecord> successPods = jvmPodInfoRecords.subList(0, targetRestartNo);
                successPods.forEach(successPod -> {
                    BatchGroupVO.PodDetail podDetail = new BatchGroupVO.PodDetail();
                    podDetail.setSuccessPodName(successPod.getPodName());
                    podDetail.setFinishTime(DateUtils.asTimestamp(successPod.getCreatedTime()));
                    podDetail.setCreateTime(atomicLong.get());
                    groupDetail.getSuccessPodNames().add(podDetail);
                });
                //按照时间排序
                if (CollectionUtils.isNotEmpty(successPods)) {
                    PodInfoRecord podInfoRecord = successPods.get((successPods.size() - 1));
                    groupDetail.setRestartEndTime(DateUtils.asTimestamp(podInfoRecord.getCreatedTime()));
                }
                groupDetail.setGroupStatus(BatchGroupVO.GroupStatus.FINISH);
            }
            tmpList.add(groupDetail);
        });
        if (CollectionUtils.isEmpty(tmpList)) {
            return batchGroupVO;
        }
        //tmpList排序
        batchGroupVO.setBatchGroup(tmpList.stream().sorted(Comparator.comparing(BatchGroupVO.GroupDetail::getBatchNo))
                .collect(Collectors.toList()));
        //获取进度
        if (CollectionUtils.isNotEmpty(batchGroupVO.getBatchGroup())) {
            List<BatchGroupVO.GroupDetail> batchGroup = batchGroupVO.getBatchGroup();
            int totalRate = batchGroup.size();
            batchGroup = batchGroup.stream().filter(batch -> batch.getGroupStatus() == BatchGroupVO.GroupStatus.FINISH).collect(
                    Collectors.toList());
            int nowRate = batchGroup.size();
            batchGroupVO.setTuneRate((double) nowRate / (double) totalRate * 100);
        }
        //判断整个PIPELINE状态
        Integer tunePlanId = pipeline.getContext().getTunePlanId();
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePlanId);
        if (tunePlan != null && TuneActionStatus.MANUAL == tunePlan.getActionStatus()) {
            Optional optional = batchGroupVO.getBatchGroup().stream()
                    .filter(batchGroup -> batchGroup.getGroupStatus() != BatchGroupVO.GroupStatus.FINISH)
                    .filter(batchGroup -> batchGroup.getGroupStatus() != BatchGroupVO.GroupStatus.WAITING)
                    .findAny();
            if (!optional.isPresent()) {
                batchGroupVO.setPipelineStatus(BatchGroupVO.PipelineStatus.PAUSE);
                batchGroupVO.setShowDesc("wait to continue");
            }
        }
        Optional optional = batchGroupVO.getBatchGroup().stream().filter(
                batchGroup -> batchGroup.getGroupStatus() != BatchGroupVO.GroupStatus.FINISH).findAny();
        if (!optional.isPresent()) {
            batchGroupVO.setPipelineStatus(BatchGroupVO.PipelineStatus.FINISH);
            batchGroupVO.setShowDesc("the pipeline is finish");
        }
        optional = batchGroupVO.getBatchGroup().stream().filter(
                batchGroup -> batchGroup.getGroupStatus() == BatchGroupVO.GroupStatus.CHECKING).findAny();
        if (optional.isPresent()) {
            batchGroupVO.setPipelineStatus(BatchGroupVO.PipelineStatus.RUNNING);
            batchGroupVO.setShowDesc("the pipeline is running");
        }
        return batchGroupVO;
    }

    private BatchGroupVO.GroupDetail convertGroup(TunePipeline pipeline, Integer batchNo,
                                                  double percent, Integer totalNum,
                                                  Integer jvmTotalNum,
                                                  Map<Integer, MetaData> batchMetaDataMap,
                                                  Predicate<Long> predicate) {
        BatchGroupVO.GroupDetail groupDetail = new BatchGroupVO.GroupDetail();
        groupDetail.setBatchNo(batchNo);
        int targetRestartNo = (int) (percent * totalNum);
        targetRestartNo = targetRestartNo <= 0 ? 1 : targetRestartNo;
        groupDetail.setTargetRestartNo(targetRestartNo);
        jvmTotalNum = (jvmTotalNum >= targetRestartNo) ? targetRestartNo : jvmTotalNum;
        groupDetail.setNowRestartNo(jvmTotalNum);
        //获取分批信息
        TuneLogInfoRecord record = new TuneLogInfoRecord();
        record.setPipelineId(pipeline.getPipelineId());
        record.setAppId(pipeline.getAppId());
        record.setJvmMarketId((int) pipeline.getContext().getMetaData().getJvmMarketId());
        record.setBatchNo(batchNo);
        //获取risk决策ID
        TuneLogInfoRecord tuneLogInfoRecord = tuneLogInfo.findRecord(record);
        if (tuneLogInfoRecord == null) {
            groupDetail.setGroupStatus(BatchGroupVO.GroupStatus.WAITING);
            return groupDetail;
        }
        predicate.test(DateUtils.asTimestamp(tuneLogInfoRecord.getCreatedTime()));
        String actionDesc = tuneLogInfoRecord.getActionDesc();
        if (StringUtils.isEmpty(actionDesc)) {
            groupDetail.setGroupStatus(BatchGroupVO.GroupStatus.CHECKING);
            if (targetRestartNo <= 1) {
                groupDetail.setGroupStatus(BatchGroupVO.GroupStatus.FINISH);
            }
        }
        if (StringUtils.equals(actionDesc, "SUCCESS")) {
            groupDetail.setGroupStatus(BatchGroupVO.GroupStatus.FINISH);
            groupDetail.setNowRestartNo(targetRestartNo);
            if (tuneLogInfoRecord.getChangetTime() != null) {
                groupDetail.setRestartEndTime(DateUtils.asTimestamp(tuneLogInfoRecord
                    .getChangetTime()));
            }
        }
        if (batchMetaDataMap.containsKey(batchNo)) {
            MetaData metaData = batchMetaDataMap.get(batchNo);
            long replicas = metaData.getReplicas();
            groupDetail.setTargetRestartNo((int) replicas);
            if (jvmTotalNum >= replicas) {
                groupDetail.setGroupStatus(BatchGroupVO.GroupStatus.FINISH);
            }
        }
        groupDetail.setRestartBeginTime(DateUtils.asTimestamp(tuneLogInfoRecord.getCreatedTime()));
        String riskTraceId = tuneLogInfoRecord.getRiskTraceId();
        groupDetail.setRiskTraceId(riskTraceId);
        if (StringUtils.isNotEmpty(riskTraceId)) {
            //进行诊断数据组织
            try {
                groupDetail.setCheckResponse(riskCheckService.getRiskCheckResult(riskTraceId));
            } catch (Exception e) {
                log.error("convertGroup risk is error", e);
            }
        }
        return groupDetail;
    }
}