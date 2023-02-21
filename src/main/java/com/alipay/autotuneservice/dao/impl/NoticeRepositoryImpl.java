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
import com.alipay.autotuneservice.dao.NoticeRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.NoticeRecord;
import com.alipay.autotuneservice.model.notice.NoticeType;
import org.springframework.stereotype.Service;

/**
 * @author huoyuqi
 * @version NoticeRepositoryImpl.java, v 0.1 2022年10月20日 2:23 下午 huoyuqi
 */
@Service
public class NoticeRepositoryImpl extends BaseDao implements NoticeRepository {

    @Override
    public NoticeRecord selectByTypeAndToken(NoticeType noticeType, String token) {
        return mDSLContext.select()
                .from(Tables.NOTICE)
                .where(Tables.NOTICE.NOTICE_TYPE.eq(noticeType.name()))
                .and(Tables.NOTICE.ACCESS_TOKEN.eq(token))
                .limit(1)
                .fetchOneInto(NoticeRecord.class);
    }

    @Override
    public void insertOrUpdate(NoticeRecord record) {
        mDSLContext.insertInto(Tables.NOTICE)
                .set(Tables.NOTICE.NOTICE_TYPE, record.getNoticeType())
                .set(Tables.NOTICE.ACCESS_TOKEN, record.getAccessToken())
                .set(Tables.NOTICE.ACCEPT, record.getAccept())
                .set(Tables.NOTICE.NOTICE_STATUS, record.getNoticeStatus())
                .onDuplicateKeyUpdate()
                .set(Tables.NOTICE.ACCEPT, record.getAccept())
                .set(Tables.NOTICE.NOTICE_STATUS, record.getNoticeStatus())
                .returning()
                .fetch();
    }

}