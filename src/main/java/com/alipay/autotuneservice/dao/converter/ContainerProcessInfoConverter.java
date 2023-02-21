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
import com.alipay.autotuneservice.dao.jooq.tables.records.ContainerProcessInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerProcessInfo;

/**
 * @author huangkaifei
 * @version : ContainerProcessInfoConverter.java, v 0.1 2022年10月31日 8:37 PM huangkaifei Exp $
 */
public class ContainerProcessInfoConverter implements EntityConverter<ContainerProcessInfoRecord, ContainerProcessInfo>{

    @Override
    public ContainerProcessInfo serialize(ContainerProcessInfoRecord entity) {
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(entity.getData(), new TypeReference<ContainerProcessInfo>(){});
    }

    @Override
    public ContainerProcessInfoRecord deserialize(ContainerProcessInfo data) {
        if (data == null) {
            return null;
        }
        ContainerProcessInfoRecord record = new ContainerProcessInfoRecord();
        record.setAppId(Long.valueOf(data.getAppId()));
        record.setPodName(data.getPodName());
        record.setGmtModified(data.getGmtCreated());
        record.setData(JSON.toJSONString(data));
        return record;
    }
}