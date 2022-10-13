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
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.JvmTuningRiskCenterRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.JvmTuningRiskCenter;
import com.alipay.autotuneservice.service.riskcheck.entity.CheckType;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JvmTuningRiskCenterRepositoryImpl extends BaseDao implements
                                                              JvmTuningRiskCenterRepository {

    @Override
    public Map<String, JvmTuningRiskCenter> find(Integer appID, Set<CheckType> metrci) {
        Set<String> collection = metrci.stream().map(CheckType::getDesc).collect(Collectors.toSet());
        for (int i = 1; i < 6; i++) {
            String dt = new SimpleDateFormat("yyyyMMdd").format(DateUtils.addDays(new Date(), -1 * i));
            Map<String, JvmTuningRiskCenter> result = mDSLContext.select().from(Tables.JVM_TUNING_RISK_CENTER).where(Tables.JVM_TUNING_RISK_CENTER.APPID.eq(appID))
                    .and(Tables.JVM_TUNING_RISK_CENTER.METRIC.in(collection))
                    .and(Tables.JVM_TUNING_RISK_CENTER.SUCESS.eq("True"))
                    .and(Tables.JVM_TUNING_RISK_CENTER.DT.eq(dt))
                    .fetchMap(Tables.JVM_TUNING_RISK_CENTER.METRIC, JvmTuningRiskCenter.class);
            if (!CollectionUtils.isEmpty(result)) {
                return result;
            }
        }
        return null;
    }

    @Override
    public void delete(LocalDateTime time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dt = df.format(time);
        mDSLContext.deleteFrom(Tables.JVM_TUNING_RISK_CENTER)
            .where(Tables.JVM_TUNING_RISK_CENTER.DT.lt(dt)).execute();
    }
}
