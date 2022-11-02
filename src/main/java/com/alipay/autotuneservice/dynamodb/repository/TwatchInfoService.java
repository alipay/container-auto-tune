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
package com.alipay.autotuneservice.dynamodb.repository;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.TwatchInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.alipay.autotuneservice.util.AgentConstant.TWATCH_TABLE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author huangkaifei
 * @version : TwatchInfoService.java, v 0.1 2022年04月19日 1:00 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class TwatchInfoService {

    @Autowired
    private NosqlService nosqlService;

    @Autowired
    private TwatchInfoRepository twatchInfoRepository;

    public List<TwatchInfoDo> getAllTwatchInfoBasedPods(List<PodInfoRecord> podsList) {
        final List<TwatchInfoDo> list = Collections.synchronizedList(new ArrayList<>());
        try {
            if (CollectionUtils.isEmpty(podsList)) {
                return null;
            }
            //去重
            podsList = podsList.stream().collect(
                    collectingAndThen(toCollection(() -> new TreeSet<>(comparing(s -> s.getPodName()))), ArrayList::new));
            CountDownLatch countDownLatch = new CountDownLatch(podsList.size());
            podsList.stream().filter(Objects::nonNull)
                    .filter(o -> StringUtils.isNotBlank(o.getPodName()))
                    .parallel()
                    .forEach(item -> findTwatchAndSave(item.getPodName(), list, countDownLatch));
            // 获取所有最新的twatchInfo信息
            countDownLatch.await(20, TimeUnit.SECONDS);
            log.info("getAllTwatchInfo res={}", JSON.toJSONString(list));
            return list;
        } catch (Exception e) {
            log.info("getAllTwatchInfo occurs an error.", e);
            return list;
        }
    }

    public void findTwatchAndSave(String podName, List<TwatchInfoDo> list,
                                  CountDownLatch countDownLatch) {
        try {
            List<TwatchInfoDo> infoByPod = findInfoByPod(podName);
            if (!CollectionUtils.isEmpty(infoByPod)) {
                list.add(infoByPod.get(0));
            }
        } catch (Exception e) {
            log.info("buildTwatchInfo podName={} occurs an error.", podName, e);
        } finally {
            countDownLatch.countDown();
        }
    }

    public List<TwatchInfoDo> findInfoByPod(String podName) {
        return twatchInfoRepository.findInfoByPod(podName);
    }

    public TwatchInfoDo findOneByPod(String podName) {
        List<TwatchInfoDo> list = findInfoByPod(podName);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public List<TwatchInfoDo> findInfoByAgent(String agentName) {
        return twatchInfoRepository.findInfoByAgent(agentName);
    }

    public List<TwatchInfoDo> findInfoByContainerId(String containerId) {
        return twatchInfoRepository.findByContainerId(containerId);
    }

    @Deprecated
    private List<TwatchInfoDo> getInfos(String indexName, String key, String value) {
        List<TwatchInfoDo> twatchInfoDos = nosqlService.queryByPkIndex(TWATCH_TABLE, indexName, key, value, TwatchInfoDo.class);
        twatchInfoDos.sort(comparing(TwatchInfoDo::getDtPeriod));
        Collections.reverse(twatchInfoDos);
        return twatchInfoDos;
    }

    public void insert(TwatchInfoDo infoDo) {
        try {
            twatchInfoRepository.insert(infoDo);
        } catch (Exception e) {
            log.error("insert TwatchInfoDo occurs an error", e);
        }
    }

    public List<TwatchInfoDo> listAll() {
        return twatchInfoRepository.listAll();
    }
}