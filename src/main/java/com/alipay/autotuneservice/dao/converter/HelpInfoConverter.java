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

import com.alipay.autotuneservice.dao.jooq.tables.records.HelpInfoRecord;
import com.alipay.autotuneservice.model.common.HelpInfo;
import com.alipay.autotuneservice.model.common.HelpInfo.HelpType;

/**
 * @author dutianze
 * @version HelpInfoConverter.java, v 0.1 2022年06月02日 11:57 dutianze
 */
public class HelpInfoConverter implements EntityConverter<HelpInfo, HelpInfoRecord> {

    @Override
    public HelpInfoRecord serialize(HelpInfo entity) {
        if (entity == null) {
            return null;
        }
        HelpInfoRecord record = new HelpInfoRecord();
        record.setId(entity.getId());
        record.setStep(entity.getStep());
        record.setStepTitle(entity.getTitle());
        record.setStepShowMessage(entity.getMessage());
        record.setStepTag(entity.getHelpType().name());
        record.setCreatedBy(entity.getCreatedBy());
        record.setUpdatedBy(entity.getUpdatedBy());
        record.setCreatedTime(entity.getCreatedTime());
        record.setUpdatedTime(entity.getUpdatedTime());
        return record;
    }

    @Override
    public HelpInfo deserialize(HelpInfoRecord record) {
        if (record == null) {
            return null;
        }
        HelpInfo helpInfo = new HelpInfo();
        helpInfo.setId(record.getId());
        helpInfo.setStep(record.getStep());
        helpInfo.setTitle(record.getStepTitle());
        helpInfo.setMessage(record.getStepShowMessage());
        helpInfo.setHelpType(HelpType.valueOf(record.getStepTag()));
        helpInfo.setCreatedBy(record.getCreatedBy());
        helpInfo.setUpdatedBy(record.getUpdatedBy());
        helpInfo.setCreatedTime(record.getCreatedTime());
        helpInfo.setUpdatedTime(record.getUpdatedTime());
        return helpInfo;
    }
}