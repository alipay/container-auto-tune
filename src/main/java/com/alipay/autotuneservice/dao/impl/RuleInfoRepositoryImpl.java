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
import com.alipay.autotuneservice.dao.RuleInfoRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.RuleInfoRecord;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author huoyuqi
 * @version RuleInfoRepositoryImpl.java, v 0.1 2023年01月05日 7:44 下午 huoyuqi
 */
@Repository
public class RuleInfoRepositoryImpl extends BaseDao implements RuleInfoRepository {

    @Override
    public List<RuleInfoRecord> selectByAlarmType(AlarmType type) {
        return mDSLContext.select()
                .from(Tables.RULE_INFO)
                .where(Tables.RULE_INFO.RULE_TYPE.eq(type.name()))
                .fetchInto(RuleInfoRecord.class);
    }

    @Override
    public RuleInfoRecord selectByRuleName(String ruleName) {
        return mDSLContext.select()
                .from(Tables.RULE_INFO)
                .where(Tables.RULE_INFO.RULE_NAME.eq(ruleName))
                .fetchOneInto(RuleInfoRecord.class);
    }

    @Override
    public RuleInfoRecord selectById(Integer id) {
        return mDSLContext.select()
                .from(Tables.RULE_INFO)
                .where(Tables.RULE_INFO.ID.eq(id))
                .fetchOneInto(RuleInfoRecord.class);
    }

}