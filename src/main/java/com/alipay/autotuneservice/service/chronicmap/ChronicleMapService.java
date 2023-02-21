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
package com.alipay.autotuneservice.service.chronicmap;

import com.google.common.collect.Lists;
import net.openhft.chronicle.map.ChronicleMap;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author huangkaifei
 * @version : ChronicleMapService.java, v 0.1 2022年11月08日 3:24 PM huangkaifei Exp $
 */
@Component
public class ChronicleMapService {

    private static final Map<String, LinkedList<Object>> CACHE_LIST_MAP = new ConcurrentHashMap<>();

    private static final ChronicleMap<String, Object> CACHE_MAP           = initInMemMap();
    /**
     * <key, <CreatedTimeStamp, ExpireTimeStamp>>
     */
    private static final Map<String, List>            KEY_CREATEDTIME_MAP = new ConcurrentHashMap<>();

    private static ChronicleMap<String, Object> initInMemMap() {
        if (CACHE_MAP == null) {
            return ChronicleMap
                    .of(String.class, Object.class)
                    // jvm里对应的名称
                    .name("CACHE_MAP")
                    // 必须要指定, 会动态扩容, 不指定会报异常
                    .entries(2_000_000L)
                    // 初始化map时设置的一个平均值, 必须要指定,不指定会报异常
                    .averageValue("")
                    // Key的平均大小, 以byte为单位必须要制定, 否则会报异常
                    .averageKeySize(256)
                    .create();
        }
        return CACHE_MAP;
    }

    /**
     * Get value by key
     *
     * @param key key with which the specified value is to be associated
     */
    public Object get(String key) {
        if (!CACHE_MAP.containsKey(key)) {
            return null;
        }
        if (!KEY_CREATEDTIME_MAP.containsKey(key)) {
            return null;
        }
        List<Long> timeList = KEY_CREATEDTIME_MAP.get(key);
        if (timeList.size() != 2) {
            // the timeout without configuration
            return CACHE_MAP.get(key);
        }
        if (System.currentTimeMillis() - timeList.get(0) >= timeList.get(1)) {
            //Key超时, 删除Key
            del(key);
            return null;
        }
        return CACHE_MAP.get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        if (!CACHE_MAP.containsKey(key)) {
            return null;
        }
        Object o = CACHE_MAP.get(key);
        return clazz.cast(o);
    }

    /**
     * Set value if key not exists, will return the value with specific key if key exists.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public Object set(String key, Object value) {
        return CACHE_MAP.putIfAbsent(key, value);
    }

    /**
     * set value by key
     *
     * @param key
     * @param value
     * @param expire
     */
    public void set(String key, Object value, long expire) {
        setNx(key, value, expire, TimeUnit.MILLISECONDS);
    }

    /**
     * Set value if key not exists, will return the value with specific key if key exists.
     *
     * @param key     key with which the specified value is to be associated
     * @param value   value to be associated with the specified key
     * @param timeout timeout
     * @param unit    time unit
     */
    public boolean setNx(String key, Object value, long timeout, TimeUnit unit) {
        if (!CACHE_MAP.containsKey(key)) {
            CACHE_MAP.put(key, value);
            KEY_CREATEDTIME_MAP.put(key, Lists.newArrayList(System.currentTimeMillis(), unit.toMillis(timeout)));
            return true;
        }
        return false;
    }

    /**
     * Set value if key exists
     *
     * @param key     key with which the specified value is to be associated
     * @param value   value to be associated with the specified key
     * @param timeout timeout
     * @param unit    time unit
     */
    public void setEx(String key, Object value, long timeout, TimeUnit unit) {
        if (CACHE_MAP.containsKey(key)) {
            CACHE_MAP.put(key, value);
            KEY_CREATEDTIME_MAP.put(key, Lists.newArrayList(System.currentTimeMillis(), unit.toMillis(timeout)));
        }
    }

    /**
     * remove key
     *
     * @param key key with which the specified value is to be associated
     */
    public void del(String key) {
        if (CACHE_MAP.containsKey(key)) {
            CACHE_MAP.remove(key);
        }
        if (KEY_CREATEDTIME_MAP.containsKey(key)) {
            KEY_CREATEDTIME_MAP.remove(key);
        }
        if(CACHE_LIST_MAP.containsKey(key)){
            CACHE_LIST_MAP.remove(key);
        }
    }

    /**
     * shutdown the ChronicleMap
     */
    public void shutdown() {
        CACHE_MAP.clear();
        CACHE_MAP.close();
        KEY_CREATEDTIME_MAP.clear();
    }

    public boolean exists(String key) {
        return CACHE_MAP.containsKey(key);
    }

    public void rpush(String key, Object value) {

        if (Objects.isNull(CACHE_LIST_MAP.get(key))) {
            LinkedList<Object> values = new LinkedList<>();
            values.add(value);
            CACHE_LIST_MAP.put(key, values);
            return;
        }
        CACHE_LIST_MAP.get(key).add(value);
    }

    public List<Object> lrange(String key) {
        return CACHE_LIST_MAP.get(key);
    }
}