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
package com.alipay.autotuneservice.dao.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.TuneEffectVO;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePlanRecord;
import com.alipay.autotuneservice.model.tune.TuneActionStatus;
import com.alipay.autotuneservice.model.tune.TuneParam;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.util.GsonUtil;

/**
 * @author dutianze
 * @version TunePlanConverter.java, v 0.1 2022年05月05日 16:05 dutianze
 */
public class TunePlanConverter implements EntityConverter<TunePlan, TunePlanRecord> {

    @Override
    public TunePlanRecord serialize(TunePlan entity) {
        if (entity == null) {
            return null;
        }
        TunePlanRecord record = new TunePlanRecord();
        record.setId(entity.getId());
        record.setHealthCheckId(entity.getHealthCheckId());
        record.setAccessToken(entity.getAccessToken());
        record.setAppId(entity.getAppId());
        record.setPlanName(entity.getPlanName());
        record.setPlanStatus(entity.getTunePlanStatus().name());
        record.setActionStatus(entity.getActionStatus().name());
        record.setPlanParam(GsonUtil.toJson(entity.getTuneParam()));
        record.setCreatedTime(entity.getCreatedTime());
        record.setUpdateTime(entity.getUpdateTime());
        record.setTuneEffect(JSONObject.toJSONString(entity.getTuneEffectVO()));
        record.setTuneStatus(null == entity.getTuneStatus() ? null : entity.getTuneStatus().name());
        return record;
    }

    @Override
    public TunePlan deserialize(TunePlanRecord record) {
        if (record == null) {
            return null;
        }
        return TunePlan
            .builder()
            .withId(record.getId())
            .withHealthCheckId(record.getHealthCheckId())
            .withAccessToken(record.getAccessToken())
            .withAppId(record.getAppId())
            .withPlanName(record.getPlanName())
            .withTunePlanStatus(TunePlanStatus.valueOf(record.getPlanStatus()))
            .withActionStatus(TuneActionStatus.valueOf(record.getActionStatus()))
            .withTuneParam(GsonUtil.fromJson(record.getPlanParam(), TuneParam.class))
            .withCreatedTime(record.getCreatedTime())
            .withUpdateTime(record.getUpdateTime())
            .withTuneEffectVO(
                JSON.parseObject(record.getTuneEffect(), new TypeReference<TuneEffectVO>() {
                }))
            .withPredictEffectVO(
                JSON.parseObject(record.getPredictEffect(), new TypeReference<TuneEffectVO>() {
                }))
            .withTuneStatus(
                null == record.getTuneStatus() ? null : TunePlanStatus.valueOf(record
                    .getTuneStatus())).build();
    }
}