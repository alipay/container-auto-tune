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
import com.alipay.autotuneservice.dao.ContainerProcessInfoRepository;
import com.alipay.autotuneservice.dynamodb.bean.ContainerProcessInfo;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author huangkaifei
 * @version : ContainerProcessInfoService.java, v 0.1 2022年04月18日 3:07 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class ContainerProcessInfoService {

    private static final String TABLE_NAME        = "ContainerProcessInfo";
    private static final String CONTAINERID_INDEX = "containerId-index";
    private static final String PARTITION_KEY     = "containerId";

    @Autowired
    private ContainerProcessInfoRepository containerProcessInfoRepository;

    /**
     * Batch insert ContainerProcessInfo object
     *
     * @param list
     */
    public void batchInsertProcessInfo(List<ContainerProcessInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            log.info("batchInsertProcessInfo - input list is empty. pls check.");
            return;
        }
        list.stream().filter(Objects::nonNull).forEach(this::insertProcessInfo);
    }

    /**
     * Insert ContainerProcessInfo object
     *
     * @param item
     */
    @Async("dynamoDBTaskExecutor")
    public void insertProcessInfo(ContainerProcessInfo item) {
        if (Objects.isNull(item)) {
            log.info("insertProcessInfo - input is null, pls check.");
            return;
        }
        try {
            containerProcessInfoRepository.insert(item);
            //nosqlService.insert(item, TABLE_NAME);
        } catch (Exception e) {
            log.error("insertProcessInfo for item={} occurs an error.", JSON.toJSONString(item), e);
        }
    }

    /**
     * Query ContainerProcessInfo by containerId
     *
     * @param containerId
     * @return
     */
    public List<ContainerProcessInfo> queryProcessInfos(String containerId) {
        return containerProcessInfoRepository.queryProcessInfos(containerId);
        //return nosqlService.queryByPkIndex(TABLE_NAME, CONTAINERID_INDEX, "containerId",
        //    containerId, ContainerProcessInfo.class);
    }

    /**
     * find ContainerProcessInfo by containerId
     *
     * @param containerId
     * @param pid
     * @return
     */
    public ContainerProcessInfo findProcessInfo(String containerId, long pid) {
        return Optional.ofNullable(queryProcessInfos(containerId))
                .orElse(Lists.newArrayList())
                .stream()
                .filter(Objects::nonNull)
                .filter(item -> pid == item.getPID())
                .findFirst().orElse(null);

    }

    /**
     * save processes info for container
     *
     * @param appId
     * @param syncActionResult
     */
    public void saveProcessInfos(Integer appId, String podName, String containerId,
                                 String syncActionResult) {
        try {
            List<ContainerProcessInfo> list = Lists.newArrayList();
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            batchInsertProcessInfo(list);
        } catch (Exception e) {
            log.error("saveProcessInfos occurs an error.", e);
        }
    }

}