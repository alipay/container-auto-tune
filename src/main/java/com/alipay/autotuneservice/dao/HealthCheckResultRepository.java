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
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckResultRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author huoyuqi
 * @version HealthCheckInfo.java, v 0.1 2022年04月25日 7:15 下午 huoyuqi
 */
public interface HealthCheckResultRepository {

    Integer insert(Integer appId, String accessToken, String createBy, LocalDateTime createTime, LocalDateTime updateTime, String createMode,
                   String status, String problem, String report, String reportDetail);

    HealthCheckResultRecord selectById(Integer healthCheckId);

    List<HealthCheckResultRecord> selectByIds(List<Integer> healthIds);

    List<HealthCheckResultRecord> findByAppId(Integer appId);

    HealthCheckResultRecord findFirstByAppId(Integer appId);

    HealthCheckResultRecord findFirstByAppIdAndStatus(Integer appId, String status);

    List<HealthCheckResultRecord> findByAppIdAndStatus(Integer appId, String status);

    void update(Integer id, LocalDateTime updateTime, String status, String problem, String report, String reportDetail);

    HealthCheckResultRecord findFirst();
}