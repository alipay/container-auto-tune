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

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.RiskCheckControlRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.RiskCheckControl;
import com.alipay.autotuneservice.dao.jooq.tables.records.RiskCheckControlRecord;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCheckEnum;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCheckParam;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskControlStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RiskCheckControlRepositoryImpl extends BaseDao implements RiskCheckControlRepository {

    @Override
    public Integer save(RiskCheckParam riskCheckParam, String traceID) {
        RiskCheckControlRecord record = mDSLContext.newRecord(Tables.RISK_CHECK_CONTROL);
        record.setAppId(riskCheckParam.getAppID());
        record.setAppName(riskCheckParam.getAppName());
        record.setCheckTime(riskCheckParam.getCheckTime());
        record.setStatus(RiskControlStatus.EXECUTING.name());
        record.setTraceId(traceID);
        record.setCreateTime(LocalDateTime.now());
        record.setRiskbegintime(LocalDateTime.now().plusMinutes(riskCheckParam.getCheckOffset()));
        record.setCheckResult(RiskCheckEnum.EMPTY.name());
        record.insert();
        return record.getId();
    }

    @Override
    public void update(Integer id, List<Integer> taskIds) {
        RiskCheckControlRecord record = mDSLContext.newRecord(Tables.RISK_CHECK_CONTROL);
        record.setId(id);
        record.setTaskIds(JSON.toJSONString(taskIds));
        record.update();
    }

    @Override
    public void update(Integer id, RiskCheckEnum riskCheckEnum,
                       RiskControlStatus riskControlStatus, String msg, LocalDateTime riskEndTime) {
        RiskCheckControlRecord record = mDSLContext.newRecord(Tables.RISK_CHECK_CONTROL);
        record.setId(id);
        record.setCheckResult(riskCheckEnum.name());
        record.setStatus(riskControlStatus.name());
        if (!StringUtils.isEmpty(msg)) {
            record.setRiskMsg(msg);
        }
        if (null != riskEndTime) {
            record.setRiskendtime(riskEndTime);
        }
        record.update();
    }

    @Override
    public List<RiskCheckControl> find(LocalDateTime startTime, LocalDateTime endTime) {
        return mDSLContext.select().from(Tables.RISK_CHECK_CONTROL)
            .where(Tables.RISK_CHECK_CONTROL.CREATE_TIME.between(startTime, endTime))
            .and(Tables.RISK_CHECK_CONTROL.STATUS.eq(RiskControlStatus.EXECUTING.name()))
            .fetchInto(RiskCheckControl.class);
    }

    @Override
    public RiskCheckControl find(String traceID) {
        return mDSLContext.select().from(Tables.RISK_CHECK_CONTROL)
            .where(Tables.RISK_CHECK_CONTROL.TRACE_ID.eq(traceID)).fetchOne()
            .into(RiskCheckControl.class);
    }

    @Override
    public void delete(LocalDateTime time) {
        mDSLContext.deleteFrom(Tables.RISK_CHECK_CONTROL)
            .where(Tables.RISK_CHECK_CONTROL.CREATE_TIME.lt(time)).execute();
    }
}
