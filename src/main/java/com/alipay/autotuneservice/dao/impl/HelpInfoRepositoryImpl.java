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

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.HelpInfoRepository;
import com.alipay.autotuneservice.dao.converter.HelpInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.HelpInfoRecord;
import com.alipay.autotuneservice.model.common.HelpInfo;
import com.alipay.autotuneservice.model.common.HelpInfo.HelpType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version AgentHelpInfoImpl.java, v 0.1 2022年02月17日 11:30 dutianze
 */
@Service
public class HelpInfoRepositoryImpl extends BaseDao implements HelpInfoRepository {

    private final HelpInfoConverter converter = new HelpInfoConverter();

    @Override
    public List<HelpInfo> findByHelpType(HelpType helpType) {
        List<HelpInfoRecord> helpInfoRecords = mDSLContext.select()
                .from(Tables.HELP_INFO)
                .where(Tables.HELP_INFO.STEP_TAG.eq(helpType.name()))
                .orderBy(Tables.HELP_INFO.STEP.asc())
                .fetch()
                .into(HelpInfoRecord.class);
        return helpInfoRecords.stream().map(converter::deserialize).collect(Collectors.toList());
    }
}