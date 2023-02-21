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

import com.alipay.autotuneservice.dao.jooq.tables.records.MeterMetricInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.MeterMetricInfo;

/**
 * @author huangkaifei
 * @version : MeterMetricInfoConverter.java, v 0.1 2022年11月01日 2:38 PM huangkaifei Exp $
 */
public class MeterMetricInfoConverter implements EntityConverter<MeterMetricInfoRecord, MeterMetricInfo> {

    @Override
    public MeterMetricInfo serialize(MeterMetricInfoRecord entity) {
        if (entity == null) {
            return null;
        }
        return MeterMetricInfo.builder()
                .metricName(entity.getMetricName())
                .dt(entity.getDt())
                .appId(entity.getAppId())
                .appName(entity.getAppName())
                .gmtCreated(entity.getGmtCreated())
                .meterVendor(entity.getMetricVendor())
                .data(entity.getData())
                .build();
    }

    @Override
    public MeterMetricInfoRecord deserialize(MeterMetricInfo data) {
        if (data == null) {
            return null;
        }
        MeterMetricInfoRecord record = new MeterMetricInfoRecord();
        record.setMetricName(data.getMetricName());
        record.setMetricVendor(data.getMeterVendor());
        record.setAppId(data.getAppId());
        record.setAppName(data.getAppName());
        record.setDt(data.getDt());
        record.setData(data.getData());
        record.setGmtCreated(data.getGmtCreated());
        return record;
    }
}