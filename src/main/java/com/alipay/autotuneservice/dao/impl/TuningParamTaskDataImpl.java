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

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.TuningParamTaskData;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTaskDataRecord;
import com.alipay.autotuneservice.model.tune.TuneChangeDefinition;
import com.alipay.autotuneservice.model.tune.TuneTaskStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.InsertQuery;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenqu
 * @version : TunePlanRepository.java, v 0.1 2022年04月18日 15:41 chenqu Exp $
 */
@Service
public class TuningParamTaskDataImpl extends BaseDao implements TuningParamTaskData {

    @Override
    public TuningParamTaskDataRecord getData(Integer pipelineId) {
        return mDSLContext.select().from(Tables.TUNING_PARAM_TASK_DATA)
            .where(Tables.TUNING_PARAM_TASK_DATA.PIPELINE_ID.eq(pipelineId))
            .fetchOneInto(TuningParamTaskDataRecord.class);
    }

    @Override
    public void init(TuningParamTaskDataRecord record) {
        InsertQuery<TuningParamTaskDataRecord> insertQuery = mDSLContext
            .insertQuery(Tables.TUNING_PARAM_TASK_DATA);
        insertQuery.addRecord(record);
        insertQuery.execute();
    }

    @Override
    public void updateStatus(Integer pipelineId, TuneTaskStatus status) {
        UpdateQuery<TuningParamTaskDataRecord> updateQuery = mDSLContext
            .updateQuery(Tables.TUNING_PARAM_TASK_DATA);
        updateQuery.addValue(Tables.TUNING_PARAM_TASK_DATA.TASK_STATUS, status.name());
        updateQuery.addValue(Tables.TUNING_PARAM_TASK_DATA.PODS, "");
        updateQuery.addConditions(Tables.TUNING_PARAM_TASK_DATA.PIPELINE_ID.eq(pipelineId));
        updateQuery.execute();
    }

    @Override
    public void updateChangePod(List<TuneChangeDefinition> changePods, Integer pipelineId,
                                List<String> comparePods) {
        UpdateQuery<TuningParamTaskDataRecord> updateQuery = mDSLContext
            .updateQuery(Tables.TUNING_PARAM_TASK_DATA);
        updateQuery.addValue(Tables.TUNING_PARAM_TASK_DATA.PODS,
            JSONObject.toJSONString(changePods));
        updateQuery.addValue(Tables.TUNING_PARAM_TASK_DATA.TRIAL_START_TIME, LocalDateTime.now());
        updateQuery.addValue(Tables.TUNING_PARAM_TASK_DATA.MODIFY_TIME, LocalDateTime.now());
        if (CollectionUtils.isNotEmpty(comparePods)) {
            updateQuery.addValue(Tables.TUNING_PARAM_TASK_DATA.COMPARE_PODS,
                JSONObject.toJSONString(comparePods));
        }
        updateQuery.addConditions(Tables.TUNING_PARAM_TASK_DATA.PIPELINE_ID.eq(pipelineId));
        updateQuery.execute();
    }
}