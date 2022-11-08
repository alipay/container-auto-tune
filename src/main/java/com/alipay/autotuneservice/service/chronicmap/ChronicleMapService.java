/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service.chronicmap;

import com.google.common.collect.Lists;
import net.openhft.chronicle.map.ChronicleMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author huangkaifei
 * @version : ChronicleMapService.java, v 0.1 2022年11月08日 3:24 PM huangkaifei Exp $
 */
@Component
public class ChronicleMapService {

    private static final ChronicleMap<String, Object> CACHE_MAP           = initInMemMap();
    /**
     * <key, <CreatedTimeStamp, ExpireTimeStamp>>
     */
    private static final Map<String, List>            KEY_CREATEDTIME_MAP = new ConcurrentHashMap<>();

    private static ChronicleMap<String, Object> initInMemMap() {
        if (CACHE_MAP == null) {
            return ChronicleMap
                    .of(String.class, Object.class)
                    .name("CACHE_MAP") // jvm里对应的名称
                    .entries(100) // 必须要指定, 会动态扩容, 不指定会报异常
                    .averageValue("") // 初始化map时设置的一个平均值, 必须要指定,不指定会报异常
                    .averageKeySize(256) // Key的平均大小, 以byte为单位必须要制定, 否则会报异常
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
    }

    /**
     * shutdown the ChronicleMap
     */
    public void shutdown() {
        CACHE_MAP.clear();
        CACHE_MAP.close();
        KEY_CREATEDTIME_MAP.clear();
    }
}