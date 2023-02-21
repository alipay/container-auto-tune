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
import com.alipay.autotuneservice.dao.BaseLineInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.BaseLine;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.BaseLineRecord;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huoyuqi
 * @version BaseLineInfoImpl.java, v 0.1 2022年08月29日 5:39 下午 huoyuqi
 */

@Slf4j
@Service
public class BaseLineInfoImpl extends BaseDao implements BaseLineInfo {
    @Override
    public BaseLineRecord getByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.BASE_LINE)
                .where(Tables.BASE_LINE.APP_ID.eq(appId))
                .orderBy(Tables.BASE_LINE.VERSION.desc())
                .limit(1)
                .fetchOneInto(BaseLineRecord.class);
    }

    @Override
    public List<BaseLineRecord> selectByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.BASE_LINE)
                .where(Tables.BASE_LINE.APP_ID.eq(appId))
                .orderBy(Tables.BASE_LINE.VERSION.desc())
                .fetchInto(BaseLineRecord.class);
    }

    @Override
    public List<BaseLineRecord> getByAppIdAndPipelineId(Integer appId, Integer pipelineId) {
        return mDSLContext.select()
                .from(Tables.BASE_LINE)
                .where(Tables.BASE_LINE.APP_ID.eq(appId))
                .and(Tables.BASE_LINE.PIPELINE_ID.eq(pipelineId))
                .orderBy(Tables.BASE_LINE.VERSION.desc())
                .fetchInto(BaseLineRecord.class);
    }

    @Override
    public BaseLineRecord getByJvmMarketId(Integer jvmMarketId) {
        return mDSLContext.select()
                .from(Tables.BASE_LINE)
                .where(Tables.BASE_LINE.JVM_MARKET_ID.eq(jvmMarketId))
                .orderBy(Tables.BASE_LINE.VERSION.desc())
                .limit(1)
                .fetchOneInto(BaseLineRecord.class);
    }

    @Override
    public List<BaseLineRecord> getByJvmMarketId(List<Integer> jvmMarketIds) {
        return mDSLContext.select()
                .from(Tables.BASE_LINE)
                .where(Tables.BASE_LINE.JVM_MARKET_ID.in(jvmMarketIds))
                .orderBy(Tables.BASE_LINE.VERSION.desc())
                .fetchInto(BaseLineRecord.class);
    }

    @Override
    public BaseLineRecord getByAppIdVersion(Integer appId, Integer version) {
        return mDSLContext.select()
                .from(Tables.BASE_LINE)
                .where(Tables.BASE_LINE.APP_ID.eq(appId))
                .and(Tables.BASE_LINE.VERSION.eq(version))
                .orderBy(Tables.BASE_LINE.VERSION.desc())
                .limit(1)
                .fetchOneInto(BaseLineRecord.class);
    }

    @Override
    public void insert(BaseLineRecord record) {
        mDSLContext.insertInto(Tables.BASE_LINE)
                .set(Tables.BASE_LINE.APP_ID, record.getAppId())
                .set(Tables.BASE_LINE.PIPELINE_ID, record.getPipelineId())
                .set(Tables.BASE_LINE.JVM_MARKET_ID, record.getJvmMarketId())
                .set(Tables.BASE_LINE.VERSION, record.getVersion())
                .set(Tables.BASE_LINE.CREATED_TIME, DateUtils.now())
                .returning()
                .fetch();
    }
}