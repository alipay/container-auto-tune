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
import com.alipay.autotuneservice.dao.JvmOptsConfig;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmOptsConfigRecord;
import com.alipay.autotuneservice.util.DateUtils;
import org.jooq.InsertQuery;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

/**
 * @author dutianze
 * @version JvmOptsConfigServiceImpl.java, v 0.1 2022年03月16日 15:23 dutianze
 */
@Service
public class JvmOptsConfigImpl extends BaseDao implements JvmOptsConfig {

    @Override
    public JvmOptsConfigRecord findById(Long id) {
        return mDSLContext.select().from(Tables.JVM_OPTS_CONFIG)
            .where(Tables.JVM_OPTS_CONFIG.ID.eq(id)).limit(1)
            .fetchOneInto(JvmOptsConfigRecord.class);
    }

    @Override
    public void insert(JvmOptsConfigRecord record) {
        record.setCreateTime(DateUtils.now());
        record.setUpdateTime(DateUtils.now());
        InsertQuery<JvmOptsConfigRecord> insertQuery = mDSLContext
            .insertQuery(Tables.JVM_OPTS_CONFIG);
        insertQuery.addRecord(record);
        insertQuery.execute();
    }

    @Override
    public void updateById(JvmOptsConfigRecord record) {
        UpdateQuery<JvmOptsConfigRecord> updateQuery = mDSLContext
            .updateQuery(Tables.JVM_OPTS_CONFIG);
        if (record.getJvmOpt() != null) {
            updateQuery.addValue(Tables.JVM_OPTS_CONFIG.JVM_OPT, record.getJvmOpt());
        }
        updateQuery.addValue(Tables.JVM_OPTS_CONFIG.UPDATE_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.JVM_OPTS_CONFIG.ID.eq(record.getId()));
        updateQuery.execute();
    }
}