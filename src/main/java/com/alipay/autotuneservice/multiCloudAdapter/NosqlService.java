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
package com.alipay.autotuneservice.multiCloudAdapter;

import java.util.List;

/**
 * @author huoyuqi
 * @version NosqlService.java, v 0.1 2022年07月13日 9:22 上午 huoyuqi
 */
public interface NosqlService {

    /**
     * 根据 一个值进行查询
     *
     * @param tableName 表名称
     * @param indexName 索引名称
     * @param key       键key
     * @param value     值
     * @param clazz     类型
     * @param <T>
     * @return
     */
    <T> List<T> queryByPkIndex(String tableName, String indexName, String key, String value,
                               Class<T> clazz);

    <T> List<T> queryByPkSkIndex(String tableName, String indexName, String key, String value,
                                 String key1, Long value1, Class<T> clazz);

    <T> List<T> queryByPkSkLongIndex(String tableName, String indexName, String key, String value,
                                     String key1, Long value1, Class<T> clazz);

    <T> List<T> queryRange(String tableName, String key, String value, String rangeKey, Long start,
                           Long end, Class<T> clazz);

    <T> List<T> queryRange(String tableName, String key, Integer value, String rangeKey,
                           String start, String end, Class<T> clazz);

    <T> void insert(T t, String tableName);

    <T> void batchInsert(List<T> t, String tableName);

}