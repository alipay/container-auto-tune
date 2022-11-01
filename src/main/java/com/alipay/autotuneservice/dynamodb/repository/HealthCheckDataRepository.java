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

import com.alipay.autotuneservice.dynamodb.bean.HealthCheckData;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataService.java, v 0.1 2022年06月22日 3:02 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class HealthCheckDataRepository {

    private static final String HEALCTH_CHECK_KEY = "jvm_risk_statistic_problem";

    private final NosqlService  nosqlService;

    public HealthCheckDataRepository(NosqlService nosqlService) {
        this.nosqlService = nosqlService;
    }

    /**
     * get jvm problem by date
     * @param dt   date, format: yyyyMMdd
     * @return
     */
    public List<HealthCheckData> getJvmProblemPerDay(String dt) {
        return this.nosqlService.queryByPkIndex(HEALCTH_CHECK_KEY, dt, "dt", dt,
            HealthCheckData.class);
    }

    public List<HealthCheckData> getJvmProblemByAppIdPerDay(String dt, Integer appId) {
        return this.nosqlService.queryByPkSkLongIndex(HEALCTH_CHECK_KEY, dt, "dt", dt, "app_id",
            (long) appId, HealthCheckData.class);
    }

    /**
     * insert one data
     * @param healthCheckData
     */
    public void insert(HealthCheckData healthCheckData) {
        try {
            this.nosqlService.insert(healthCheckData, HEALCTH_CHECK_KEY);
        } catch (Exception e) {
            log.error("insert healthCheckData error, data:{}, error:{}", healthCheckData, e);
            throw e;
        }
    }

}