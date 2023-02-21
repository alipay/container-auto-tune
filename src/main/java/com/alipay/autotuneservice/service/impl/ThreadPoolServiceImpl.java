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
import com.alipay.autotuneservice.controller.model.ThreadPoolMonitorVO;
import com.alipay.autotuneservice.controller.model.monitor.MetricVO;
import com.alipay.autotuneservice.dao.ThreadPoolMonitorRepository;
import com.alipay.autotuneservice.dynamodb.bean.ThreadPoolMonitorMetricData;
import com.alipay.autotuneservice.service.ThreadPoolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huoyuqi
 * @version ThreadPoolServiceImpl.java, v 0.1 2022年12月06日 10:58 上午 huoyuqi
 */
@Slf4j
@Service
public class ThreadPoolServiceImpl implements ThreadPoolService {

    private final static Long                        TWENTY_MINUTE = 20 * 60 * 1000L;
    @Autowired
    private              ThreadPoolMonitorRepository threadPoolMonitorRepository;

    @Override
    public ThreadPoolMonitorVO monitorThreadPool(String workLoadName, String threadPoolName, Long start, Long end) {

        Long currentTime = System.currentTimeMillis();
        long endTime = null == end ? currentTime : end;
        long startTime = start == null ? endTime - TWENTY_MINUTE : start;

        List<ThreadPoolMonitorMetricData> dataList = threadPoolMonitorRepository.queryRange(workLoadName, threadPoolName, startTime,
                endTime);
        log.info("dataList is: {}, workLoadName: {}, threadPoolName: {}, startTime: {}, end: {}", JSON.toJSONString(dataList), workLoadName,
                threadPoolName, startTime, endTime);
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }

        //构造ThreadPoolMonitorVO
        ThreadPoolMonitorVO threadPool = new ThreadPoolMonitorVO();
        dataList.forEach(item -> {
            threadPool.getActiveCount().add(
                    new MetricVO(item.getPeriod(), (double) item.getActiveCount(), "ThreadPool", "ActiveCount", ""));
            threadPool.getPoolSize().add(new MetricVO(item.getPeriod(), (double) item.getPoolSize(), "ThreadPool", "PoolSize", ""));
            threadPool.getCorePoolSize().add(
                    new MetricVO(item.getPeriod(), (double) item.getCorePoolSize(), "ThreadPool", "CorePoolSize", ""));
            threadPool.getLargestPoolSize().add(
                    new MetricVO(item.getPeriod(), (double) item.getLargestPoolSize(), "ThreadPool", "LargestPoolSize", ""));
            threadPool.getMaximumPoolSize().add(
                    new MetricVO(item.getPeriod(), (double) item.getMaximumPoolSize(), "ThreadPool", "MaximumPoolSize", ""));
            threadPool.getBlockQueue().add(new MetricVO(item.getPeriod(), (double) item.getBlockQueue(), "ThreadPool", "BlockQueue", ""));
            threadPool.getIdlePoolSize().add(
                    new MetricVO(item.getPeriod(), (double) item.getIdlePoolSize(), "ThreadPool", "IdlePoolSize", ""));
            threadPool.getRejectCount().add(
                    new MetricVO(item.getPeriod(), (double) item.getRejectCount(), "ThreadPool", "RejectCount", ""));
        });
        return threadPool;
    }

}