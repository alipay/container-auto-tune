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
import com.alipay.autotuneservice.dao.TuneParamTrialDataRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePoolInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTrialDataRecord;
import com.alipay.autotuneservice.model.common.TrailTuneTaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

/**
 * @author huangkaifei
 * @version : TuningParamTrialDataRepositoryImpl.java, v 0.1 2022年06月01日 1:38 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class TuneParamTrialDataRepositoryImpl extends BaseDao implements
                                                             TuneParamTrialDataRepository {

    @Override
    public TuningParamTrialDataRecord getTrialData(Integer pipelineId, TrailTuneTaskStatus status) {
        Condition condition = null;
        if (pipelineId != null && pipelineId > 0) {
            condition = Tables.TUNING_PARAM_TRIAL_DATA.PIPELINE_ID.eq(pipelineId);
        }
        if (status != null) {
            condition = condition.and(Tables.TUNING_PARAM_TRIAL_DATA.TASK_STATUS.eq(status.name()));
        }
        return condition == null ? null : mDSLContext.select().from(Tables.TUNING_PARAM_TRIAL_DATA)
            .where(condition).limit(1).fetchOneInto(TuningParamTrialDataRecord.class);
    }

    @Override
    public Integer updateTaskStatus(Integer pipelineId) {
        UpdateQuery<TuningParamTrialDataRecord> updateQuery = mDSLContext
            .updateQuery(Tables.TUNING_PARAM_TRIAL_DATA);
        updateQuery.addValue(Tables.TUNING_PARAM_TRIAL_DATA.TASK_STATUS,
            TrailTuneTaskStatus.CANCEL.name());
        updateQuery.addConditions(Tables.TUNING_PARAM_TRIAL_DATA.PIPELINE_ID.eq(pipelineId));
        return updateQuery.execute();
    }
}