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
import com.alipay.autotuneservice.dao.jooq.tables.records.ContainerStatisticsRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;

/**
 * @author huangkaifei
 * @version : ContainerStatisticConverter.java, v 0.1 2022年10月31日 8:38 PM huangkaifei Exp $
 */
public class ContainerStatisticConverter implements EntityConverter<ContainerStatisticsRecord, ContainerStatistics> {

    @Override
    public ContainerStatistics serialize(ContainerStatisticsRecord entity) {
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(entity.getData(), new TypeReference<ContainerStatistics>() {});
    }

    @Override
    public ContainerStatisticsRecord deserialize(ContainerStatistics data) {
        if (data == null) {
            return null;
        }
        ContainerStatisticsRecord record = new ContainerStatisticsRecord();
        record.setAppId(data.getAppId());
        record.setPodName(data.getPodName());
        record.setGmtModified(data.getGmtCreated());
        record.setData(JSON.toJSONString(data));
        return record;
    }
}