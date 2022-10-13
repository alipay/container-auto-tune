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
import com.alipay.autotuneservice.dao.RiskCheckTaskRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.RiskCheckTask;
import com.alipay.autotuneservice.dao.jooq.tables.records.RiskCheckTaskRecord;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCheckEnum;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskTaskStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RiskCheckTaskRepositoryImpl extends BaseDao implements RiskCheckTaskRepository {

    @Override
    public Integer save(RiskCheckTask riskCheckTask) {
        RiskCheckTaskRecord record = mDSLContext.newRecord(Tables.RISK_CHECK_TASK);
        record.from(riskCheckTask);
        record.insert();
        return record.getId();
    }

    @Override
    public List<RiskCheckTask> find(LocalDateTime startTime, LocalDateTime endTime) {
        return mDSLContext.select().from(Tables.RISK_CHECK_TASK)
            .where(Tables.RISK_CHECK_TASK.EXECUTE_TIME.between(startTime, endTime))
            .and(Tables.RISK_CHECK_TASK.TASK_STATUS.eq(RiskTaskStatus.READY.name()))
            .fetchInto(RiskCheckTask.class);
    }

    @Override
    public List<RiskCheckTask> findByJobID(Integer jobID) {
        return mDSLContext.select().from(Tables.RISK_CHECK_TASK)
            .where(Tables.RISK_CHECK_TASK.JOB_ID.eq(jobID))
            .and(Tables.RISK_CHECK_TASK.TASK_STATUS.eq(RiskTaskStatus.END.name()))
            .fetchInto(RiskCheckTask.class);
    }

    @Override
    public void updateByTaskID(Integer taskID, RiskTaskStatus riskTaskStatus,
                               RiskCheckEnum riskCheckEnum, String riskMsg) {
        RiskCheckTaskRecord record = mDSLContext.newRecord(Tables.RISK_CHECK_TASK);
        record.setId(taskID);
        record.setTaskStatus(riskTaskStatus.name());
        if (riskCheckEnum != null) {
            record.setTaskResult(riskCheckEnum.name());
        }
        if (riskMsg != null) {
            record.setTaskRiskMsg(riskMsg);
        }
        record.update();
    }

    @Override
    public void updateByJobId(Integer jobID) {
        mDSLContext.update(Tables.RISK_CHECK_TASK)
            .set(Tables.RISK_CHECK_TASK.TASK_STATUS, RiskTaskStatus.INTERUPTE.name())
            .where(Tables.RISK_CHECK_TASK.JOB_ID.eq(jobID))
            .and(Tables.RISK_CHECK_TASK.TASK_STATUS.eq(RiskTaskStatus.READY.name())).execute();
    }

    @Override
    public void delete(LocalDateTime time) {
        mDSLContext.deleteFrom(Tables.RISK_CHECK_TASK)
            .where(Tables.RISK_CHECK_TASK.CREATE_TIME.lt(time)).execute();
    }
}
