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
import com.alipay.autotuneservice.dao.JavaInfoRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.JavaInfoRecord;
import com.alipay.autotuneservice.util.DateUtils;
import org.jooq.InsertQuery;
import org.springframework.stereotype.Service;

/**
 * @author dutianze
 * @version JvmOptsConfigServiceImpl.java, v 0.1 2022年03月16日 15:23 dutianze
 */
@Service
public class JavaInfoRepositoryImpl extends BaseDao implements JavaInfoRepository {

    @Override
    public void insert(JavaInfoRecord record) {
        record.setCreatedTime(DateUtils.now());
        record.setUpdatedTime(DateUtils.now());
        InsertQuery<JavaInfoRecord> insertQuery = mDSLContext.insertQuery(Tables.JAVA_INFO);
        insertQuery.addRecord(record);
        insertQuery.execute();
    }

    @Override
    public JavaInfoRecord findInfo(String hostName) {
        return mDSLContext.select()
                .from(Tables.JAVA_INFO)
                .where(Tables.JAVA_INFO.HOST_NAME.eq(hostName))
                .orderBy(Tables.JAVA_INFO.CREATED_TIME.desc())
                .limit(1)
                .fetchOneInto(JavaInfoRecord.class);
    }

    @Override
    public JavaInfoRecord findInfo(String appName, String hostName) {
        return mDSLContext.select()
                .from(Tables.JAVA_INFO)
                .where(Tables.JAVA_INFO.HOST_NAME.eq(hostName))
                .and(Tables.JAVA_INFO.APP_NAME.eq(appName))
                .orderBy(Tables.JAVA_INFO.CREATED_TIME.desc())
                .limit(1)
                .fetchOneInto(JavaInfoRecord.class);
    }
}