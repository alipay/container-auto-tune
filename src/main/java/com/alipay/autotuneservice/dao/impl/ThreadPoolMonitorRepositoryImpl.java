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
import com.alipay.autotuneservice.dao.ThreadPoolMonitorRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppLogRecord;
import com.alipay.autotuneservice.dynamodb.bean.ThreadPoolMonitorMetricData;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huoyuqi
 * @version ThreadPoolMonitorRepositoryImpl.java, v 0.1 2023年01月31日 5:53 下午 huoyuqi
 */
@Service
public class ThreadPoolMonitorRepositoryImpl extends BaseDao implements ThreadPoolMonitorRepository {
    @Override
    public List<ThreadPoolMonitorMetricData> queryRange(String hostName, String threadPoolName, Long start, Long end) {
        return mDSLContext.select().from(Tables.THREADPOOL_MONITOR_METRIC_DATA)
                .where(Tables.THREADPOOL_MONITOR_METRIC_DATA.HOST_NAME.eq(hostName)
                        .and(Tables.THREADPOOL_MONITOR_METRIC_DATA.THREAD_POOL_NAME.eq(threadPoolName))
                        .and(Tables.THREADPOOL_MONITOR_METRIC_DATA.PERIOD.between(start, end)))
                .fetchInto(ThreadPoolMonitorMetricData.class);
    }
}