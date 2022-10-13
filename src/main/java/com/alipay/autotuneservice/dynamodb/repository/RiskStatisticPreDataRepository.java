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

import com.alipay.autotuneservice.dynamodb.bean.RiskStatisticPreData;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fangxueyang
 * @version RiskCheckPreDataRepository.java, v 0.1 2022年08月15日 18:12 hongshu
 */
@Slf4j
@Service
public class RiskStatisticPreDataRepository {

    public static final String RISK_CHECK_PRE_DATA_TABLE = "RiskStatisticPreData";

    private final NosqlService nosqlService;

    public RiskStatisticPreDataRepository(NosqlService nosqlService) {
        this.nosqlService = nosqlService;
    }

    /**
     * get pre_process data by date
     * @param dt   date, format: yyyyMMdd
     * @return
     */
    public List<RiskStatisticPreData> getPreDataPerDay(String dt) {
        return this.nosqlService.queryByPkIndex(RISK_CHECK_PRE_DATA_TABLE, dt, "dt", dt,
            RiskStatisticPreData.class);
    }

    /**
     * get pre_process data by date and app
     * @param dt   date, format: yyyyMMdd
     * @return
     */
    public List<RiskStatisticPreData> getPreDataDayAndApp(String dt, Integer appId) {
        return this.nosqlService.queryByPkSkLongIndex(RISK_CHECK_PRE_DATA_TABLE, dt, "dt", dt,
            "appId", (long) appId, RiskStatisticPreData.class);
    }

    public List<RiskStatisticPreData> getPreDataRange(Integer appId, String start, String end) {
        try {
            return nosqlService.queryRange(RISK_CHECK_PRE_DATA_TABLE, "pod", appId, "dt", start,
                end, RiskStatisticPreData.class);
        } catch (Exception e) {
            log.error("getPodJvmMetric for appId={} occurs an error.", appId, e);
            return Lists.newArrayList();
        }
    }

    /**
     * insert one data
     * @param riskCheckPreData
     */
    public void insert(RiskStatisticPreData riskCheckPreData) {
        try {
            this.nosqlService.insert(riskCheckPreData, RISK_CHECK_PRE_DATA_TABLE);
        } catch (Exception e) {
            log.error("insert riskCheckPreData error, data:{}, error:{}", riskCheckPreData, e);
            throw e;
        }
    }
}