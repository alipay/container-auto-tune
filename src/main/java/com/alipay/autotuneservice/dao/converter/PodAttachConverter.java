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

import com.alipay.autotuneservice.dao.jooq.tables.records.PodAttachRecord;
import com.alipay.autotuneservice.model.common.PodAttach;
import com.alipay.autotuneservice.model.common.PodAttachStatus;
import com.alipay.autotuneservice.util.DateUtils;

/**
 * @author dutianze
 * @version PodAttachConverter.java, v 0.1 2022年06月17日 11:43 dutianze
 */
public class PodAttachConverter implements EntityConverter<PodAttach, PodAttachRecord> {

    @Override
    public PodAttachRecord serialize(PodAttach entity) {
        if (entity == null) {
            return null;
        }
        PodAttachRecord record = new PodAttachRecord();
        record.setId(entity.getId());
        record.setAccessToken(entity.getAccessToken());
        record.setPodId(entity.getPodId());
        record.setAttachStatus(entity.getStatus().name());
        record.setCreatedTime(entity.getCreatedTime());
        record.setUpdatedTime(entity.getUpdatedTime());
        return record;
    }

    @Override
    public PodAttach deserialize(PodAttachRecord record) {
        if (record == null) {
            return null;
        }
        PodAttachStatus podAttachStatus = PodAttachStatus.valueOf(record.getAttachStatus());
        PodAttach entity = new PodAttach();
        entity.setId(record.getId());
        entity.setStatus(podAttachStatus);
        entity.setAccessToken(record.getAccessToken());
        entity.setPodId(record.getPodId());
        entity.setCreatedTime(record.getCreatedTime());
        entity.setUpdatedTime(record.getUpdatedTime());
        if (podAttachStatus.equals(PodAttachStatus.INSTALLING)
            && DateUtils.now().minusMinutes(3).isAfter(record.getCreatedTime())) {
            entity.setStatus(PodAttachStatus.NOT_INSTALLED);
        }
        return entity;
    }
}