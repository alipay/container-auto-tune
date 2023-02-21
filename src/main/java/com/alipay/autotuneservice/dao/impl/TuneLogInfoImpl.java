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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.TuneLogInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.model.tune.TuneChangeDefinition;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.InsertQuery;
import org.jooq.Record;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huoyuqi
 * @version PodInfoImpl.java, v 0.1 2022年04月18日 7:14 下午 huoyuqi
 */
@Service
@Slf4j
public class TuneLogInfoImpl extends BaseDao implements TuneLogInfo {
    @Override
    public void insertPodInfo(TuneLogInfoRecord record) {
        record.setCreatedTime(DateUtils.now());
        InsertQuery<TuneLogInfoRecord> insertQuery = mDSLContext.insertQuery(Tables.TUNE_LOG_INFO);
        insertQuery.addRecord(record);
        insertQuery.execute();

    }

    @Override
    public void updateRiskTraceId(Integer tuneId, String traceId) {
        UpdateQuery<TuneLogInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_LOG_INFO);
        updateQuery.addValue(Tables.TUNE_LOG_INFO.RISK_TRACE_ID, traceId);
        updateQuery.addConditions(Tables.TUNE_LOG_INFO.ID.eq(tuneId));
        updateQuery.execute();
    }

    @Override
    public void updateChangePodInfo(TuneLogInfoRecord record, List<TuneChangeDefinition> changeDefinitions) {
        TuneLogInfoRecord result = findRecord(record);
        if (result == null) {
            record.setAction("BATCH");
            insertPodInfo(record);
            return;
        }
        if (CollectionUtils.isNotEmpty(changeDefinitions)) {
            //执行update
            if (StringUtils.isEmpty(result.getBatchPods())) {
                record.setBatchPods(JSONObject.toJSONString(changeDefinitions));
            } else {
                List<TuneChangeDefinition> definitions = JSON.parseObject(result.getBatchPods(),
                        new TypeReference<List<TuneChangeDefinition>>() {});
                definitions.addAll(changeDefinitions);
                record.setBatchPods(JSONObject.toJSONString(definitions));
            }
        }
        //update
        log.info(String.format("id=[%s],batchPods=[%s]", result.getId(), record.getBatchPods()));
        UpdateQuery<TuneLogInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.TUNE_LOG_INFO);
        if (StringUtils.isNotEmpty(record.getBatchPods())) {
            updateQuery.addValue(Tables.TUNE_LOG_INFO.BATCH_PODS, record.getBatchPods());
        }
        if (StringUtils.isNotEmpty(record.getActionDesc())) {
            updateQuery.addValue(Tables.TUNE_LOG_INFO.ACTION_DESC, record.getActionDesc());
        }
        if (record.getBatchRatio() != null) {
            updateQuery.addValue(Tables.TUNE_LOG_INFO.BATCH_RATIO, record.getBatchRatio());
        }
        if (record.getBatchTotalNum() != null) {
            updateQuery.addValue(Tables.TUNE_LOG_INFO.BATCH_TOTAL_NUM, record.getBatchTotalNum());
        }
        updateQuery.addConditions(Tables.TUNE_LOG_INFO.ID.eq(result.getId()));
        updateQuery.execute();
    }

    @Override
    public TuneLogInfoRecord findRecord(TuneLogInfoRecord record) {
        Record result = mDSLContext.select().from(Tables.TUNE_LOG_INFO)
                .where(Tables.TUNE_LOG_INFO.PIPELINE_ID.eq(record.getPipelineId()))
                .and(Tables.TUNE_LOG_INFO.APP_ID.eq(record.getAppId()))
                .and(Tables.TUNE_LOG_INFO.JVM_MARKET_ID.eq(record.getJvmMarketId()))
                .and(Tables.TUNE_LOG_INFO.BATCH_NO.eq(record.getBatchNo()))
                .fetchOne();
        if (result != null) {
            return result.into(TuneLogInfoRecord.class);
        }
        return null;
    }

    @Override
    public List<TuneLogInfoRecord> findRecordByPipeline(Integer pipelineId, String action) {
        return mDSLContext.select()
                .from(Tables.TUNE_LOG_INFO)
                .where(Tables.TUNE_LOG_INFO.PIPELINE_ID.eq(pipelineId))
                .and(Tables.TUNE_LOG_INFO.ACTION.eq(action))
                .orderBy(Tables.TUNE_LOG_INFO.ID.desc())
                .fetchInto(TuneLogInfoRecord.class);

    }

    @Override
    public List<TuneLogInfoRecord> findRecordByPipelineIds(List<Integer> pipelineIds, String action) {
        return mDSLContext.select()
                .from(Tables.TUNE_LOG_INFO)
                .where(Tables.TUNE_LOG_INFO.PIPELINE_ID.in(pipelineIds))
                .and(Tables.TUNE_LOG_INFO.ACTION.eq(action))
                .orderBy(Tables.TUNE_LOG_INFO.ID.desc())
                .fetchInto(TuneLogInfoRecord.class);
    }
}