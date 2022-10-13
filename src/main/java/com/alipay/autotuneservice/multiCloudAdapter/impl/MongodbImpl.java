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
package com.alipay.autotuneservice.multiCloudAdapter.impl;

import com.alipay.autotuneservice.configuration.InjectLoader;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.nosql.aliyun.StandardMongoProvider;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author huoyuqi
 * @version MongodbImpl.java, v 0.1 2022年07月13日 10:18 上午 huoyuqi
 */
@Slf4j
@InjectLoader(loadType = "aliyun")
public class MongodbImpl implements NosqlService {

    private StandardMongoProvider standardMongoProvider;

    public MongodbImpl() {
        try {
            this.standardMongoProvider = new StandardMongoProvider();
        } catch (Exception e) {
            //do noting
            log.error("MongodbImpl - init failed.", e);
        }
    }

    public StandardMongoProvider getStandardMongoProvider() {
        return standardMongoProvider;
    }

    @Override
    public <T> List<T> queryByPkIndex(String tableName, String indexName, String key, String value,
                                      Class<T> clazz) {
        return standardMongoProvider.query().withTableName(tableName).withQueryParam(key, value)
            .queryWithParam(clazz);
    }

    @Override
    public <T> List<T> queryByPkSkIndex(String tableName, String indexName, String key,
                                        String value, String key1, Long value1, Class<T> clazz) {
        return standardMongoProvider.query().withTableName(tableName).withQueryParam(key, value)
            .withQueryParam(key1, String.valueOf(value1)).queryWithParam(clazz);
    }

    @Override
    public <T> List<T> queryByPkSkLongIndex(String tableName, String indexName, String key,
                                            String value, String key1, Long value1, Class<T> clazz) {
        return standardMongoProvider.query().withTableName(tableName).withQueryParam(key, value)
            .withQueryParam(key1, value1).queryWithParam(clazz);
    }

    @Override
    public <T> List<T> queryRange(String tableName, String key, String value, String rangeKey,
                                  Long start, Long end, Class<T> clazz) {
        return standardMongoProvider.query().withTableName(tableName).withQueryParam(key, value)
            .withRangeKey(rangeKey).withFrom(start).withTo(end).queryWithRange(clazz);
    }

    @Override
    public <T> List<T> queryRange(String tableName, String key, Integer value, String rangeKey,
                                  String start, String end, Class<T> clazz) {
        return standardMongoProvider.query().withTableName(tableName).withQueryParam(key, value)
            .withRangeKey(rangeKey).withFrom(start).withTo(end).queryWithRange(clazz);
    }

    @Override
    public <T> void insert(T t, String tableName) {
        standardMongoProvider.insert(tableName, t);
    }

    @Override
    public <T> void batchInsert(List<T> t, String tableName) {
        standardMongoProvider.batchInsert(t, tableName);
    }

}