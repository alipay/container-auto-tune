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
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.configVO.*;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.ConfigInfoRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.ConfigInfoRecord;
import org.jooq.Condition;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ConfigInfoRepositoryImpl extends BaseDao implements ConfigInfoRepository {
    @Override
    public Integer save(ConfigInfoVO configInfoVO) {
        ConfigInfoRecord record = assembly(configInfoVO);
        record.insert();
        return record.getId();
    }

    @Override
    public Integer update(ConfigInfoVO configInfoVO) {

        return mDSLContext
            .update(Tables.CONFIG_INFO)
            .set(
                Tables.CONFIG_INFO.AUTO_TUNE,
                null == configInfoVO.getAutoTune() ? "false" : Boolean.toString(configInfoVO
                    .getAutoTune()))
            .set(
                Tables.CONFIG_INFO.TUNE_TIME_TAG,
                null == configInfoVO.getTuneTimeTag() ? "false" : Boolean.toString(configInfoVO
                    .getTuneTimeTag()))
            .set(Tables.CONFIG_INFO.TUNE_PRIMARY_TIME,
                JSON.toJSONString(configInfoVO.getTunePrimaryTime()))
            .set(
                Tables.CONFIG_INFO.AUTO_DISPATCH,
                null == configInfoVO.getAutoDispatch() ? "false" : Boolean.toString(configInfoVO
                    .getAutoDispatch()))
            .set(Tables.CONFIG_INFO.TUNE_GROUP_CONFIG,
                JSON.toJSONString(configInfoVO.getTuneGroupConfig()))
            .set(
                Tables.CONFIG_INFO.RISK_SWITCH,
                null == configInfoVO.getRiskSwitch() ? "false" : Boolean.toString(configInfoVO
                    .getRiskSwitch()))
            .set(Tables.CONFIG_INFO.ADVANCED_SETUP,
                JSON.toJSONString(configInfoVO.getAdvancedSetup()))
            .set(Tables.CONFIG_INFO.TIME_ZONE, configInfoVO.getTimeZone())
            .set(Tables.CONFIG_INFO.OPERATE_TIME, configInfoVO.getOperateTime())
            .where(Tables.CONFIG_INFO.APP_ID.eq(configInfoVO.getAppId())).execute();
    }

    @Override
    public ConfigInfoVO findConfigByAPPID(Integer appid) {
        ConfigInfoRecord record = mDSLContext.select().from(Tables.CONFIG_INFO)
            .where(Tables.CONFIG_INFO.APP_ID.eq(appid)).fetchAnyInto(ConfigInfoRecord.class);
        if (null != record) {
            ConfigInfoVO vo = new ConfigInfoVO();
            vo.setAppId(record.getAppId());
            vo.setAutoTune("true".equals(record.getAutoTune()));
            vo.setTuneTimeTag("true".equals(record.getTuneTimeTag()));
            vo.setTunePrimaryTime(JSON.parseObject(record.getTunePrimaryTime(),
                new TypeReference<Map<WeekEnum, List<TimeHHmm>>>() {
                }));
            vo.setAutoDispatch("true".equals(record.getAutoDispatch()));
            vo.setTuneGroupConfig(JSON.parseObject(record.getTuneGroupConfig(),
                new TypeReference<List<TuneConfig>>() {
                }));
            vo.setRiskSwitch("true".equals(record.getRiskSwitch()));
            vo.setAdvancedSetup(JSON.parseObject(record.getAdvancedSetup(),
                new TypeReference<List<RiskIndictor>>() {
                }));
            vo.setTimeZone(record.getTimeZone());
            return vo;
        }
        return new ConfigInfoVO();
    }

    @Override
    public List<ConfigInfoRecord> batchFindConfigByAppIds(List<Integer> appIds) {
        Condition condition = Tables.CONFIG_INFO.APP_ID.in(appIds).and(
            Tables.CONFIG_INFO.AUTO_TUNE.eq("true"));
        return mDSLContext.select().from(Tables.CONFIG_INFO).where(condition)
            .groupBy(Tables.CONFIG_INFO.APP_ID).fetchInto(ConfigInfoRecord.class);
    }

    private ConfigInfoRecord assembly(ConfigInfoVO configInfoVO) {
        ConfigInfoRecord record = mDSLContext.newRecord(Tables.CONFIG_INFO);
        record.setAppId(configInfoVO.getAppId());
        record.setAutoTune(null == configInfoVO.getAutoTune() ? "false" : Boolean
            .toString(configInfoVO.getAutoTune()));
        record.setTuneTimeTag(null == configInfoVO.getTuneTimeTag() ? "false" : Boolean
            .toString(configInfoVO.getTuneTimeTag()));
        record.setTunePrimaryTime(JSON.toJSONString(configInfoVO.getTunePrimaryTime()));
        record.setAutoDispatch(null == configInfoVO.getAutoDispatch() ? "false" : Boolean
            .toString(configInfoVO.getAutoDispatch()));
        record.setTuneGroupConfig(JSON.toJSONString(configInfoVO.getTuneGroupConfig()));
        record.setRiskSwitch(null == configInfoVO.getRiskSwitch() ? "false" : Boolean
            .toString(configInfoVO.getRiskSwitch()));
        record.setAdvancedSetup(JSON.toJSONString(configInfoVO.getAdvancedSetup()));
        record.setTimeZone(configInfoVO.getTimeZone());
        record.setOperateTime(configInfoVO.getOperateTime());
        return record;
    }
}
