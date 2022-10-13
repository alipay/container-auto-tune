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

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.MeterMetaInfoRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.MeterMetaInfoRecord;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : MeterMetaInfoRepositoryImpl.java, v 0.1 2022年08月23日 8:16 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class MeterMetaInfoRepositoryImpl extends BaseDao implements MeterMetaInfoRepository {

    @Override
    public boolean saveOrUpdate(MeterMeta meterMeta) {
        if (meterMeta == null) {
            return false;
        }
        try {
            MeterMetaInfoRecord record = ConvertUtils.convert2MeterMetaRecord(meterMeta);
            MeterMetaInfoRecord select = select(meterMeta.getAppId(), meterMeta.getMeterName());
            if (select != null) {
                mDSLContext
                    .update(Tables.METER_META_INFO)
                    .set(Tables.METER_META_INFO.METER_DOMAIN, record.getMeterDomain())
                    .set(Tables.METER_META_INFO.METER_METRICS, record.getMeterMetrics())
                    .where(Tables.METER_META_INFO.APP_ID.eq(meterMeta.getAppId()))
                    .and(
                        Tables.METER_META_INFO.METER_NAME.equalIgnoreCase(meterMeta.getMeterName()))
                    .execute();
                return true;
            }
            mDSLContext.insertInto(Tables.METER_META_INFO)
                .set(Tables.METER_META_INFO.METER_NAME, record.getMeterName())
                .set(Tables.METER_META_INFO.METER_DOMAIN, record.getMeterDomain())
                .set(Tables.METER_META_INFO.METER_METRICS, record.getMeterMetrics())
                .set(Tables.METER_META_INFO.APP_ID, record.getAppId()).execute();
            return true;
        } catch (Exception e) {
            log.error("save MeterMetaInfoRecord occurs an error.", e);
            return false;
        }
    }

    @Override
    public List<MeterMeta> listAppMeters(Integer appId) {
        if(appId == null || appId <=0 ){
            return Lists.newArrayList();
        }
        List<MeterMetaInfoRecord> list = mDSLContext.select()
                .from(Tables.METER_META_INFO)
                .where(Tables.METER_META_INFO.APP_ID.eq(appId))
                .fetch()
                .into(MeterMetaInfoRecord.class);
        return Optional.ofNullable(list).orElse(Lists.newArrayList()).stream()
                .map(ConvertUtils::convertByMeterRecord)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<MeterMeta> listAppMeters() {
        List<MeterMetaInfoRecord> list = mDSLContext.select()
                .from(Tables.METER_META_INFO)
                .fetch()
                .into(MeterMetaInfoRecord.class);
        return Optional.ofNullable(list).orElse(Lists.newArrayList()).stream()
                .map(ConvertUtils::convertByMeterRecord)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public MeterMetaInfoRecord select(Integer appId, String meterName) {
        Condition condition = Tables.METER_META_INFO.APP_ID.in(appId).and(
            Tables.METER_META_INFO.METER_NAME.equalIgnoreCase(meterName));
        return mDSLContext.select().from(Tables.METER_META_INFO).where(condition)
            .and(Tables.METER_META_INFO.METER_NAME.eq(meterName)).limit(1)
            .fetchOneInto(MeterMetaInfoRecord.class);
    }

    @Override
    public Boolean deleteAppMeter(Integer appId, String meterName) {
        if (appId == null || appId <= 0) {
            return false;
        }
        try {
            Condition condition = Tables.METER_META_INFO.APP_ID.in(appId).and(
                Tables.METER_META_INFO.METER_NAME.equalIgnoreCase(meterName));
            mDSLContext.deleteFrom(Tables.METER_META_INFO).where(condition).execute();
            return true;
        } catch (Exception e) {
            log.error("deleteAppMeter occurs an error.", e);
            return false;
        }
    }
}