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
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMonitorMetricDataRecord;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataConverter.java, v 0.1 2022年10月31日 8:36 PM huangkaifei Exp $
 */
public class JvmMonitorMetricDataConverter implements EntityConverter<JvmMonitorMetricDataRecord, JvmMonitorMetricData>{
    @Override
    public JvmMonitorMetricData serialize(JvmMonitorMetricDataRecord entity) {
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(entity.getData(), new TypeReference<JvmMonitorMetricData>(){});
    }

    @Override
    public JvmMonitorMetricDataRecord deserialize(JvmMonitorMetricData data) {
        if (data == null) {
            return null;
        }
        JvmMonitorMetricDataRecord record = new JvmMonitorMetricDataRecord();
        record.setAppName(data.getApp());
        record.setPodName(data.getPod());
        record.setGmtModified(data.getPeriod());
        record.setData(JSON.toJSONString(data));
        return record;
    }
}