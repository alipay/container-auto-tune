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
package com.alipay.autotuneservice.base.cache;

import java.util.LinkedHashMap;

/**
 * 按先进先出策略更替缓存记录的本地缓存。
 *
 * @author chenqu
 * @version : FIFOLocalCache.java, v 0.1 2021年12月30日 17:52 chenqu Exp $
 */
public class FIFOLocalCache<K, V> extends LocalCache<K, V> {

    private static final float defaultLoadFactor = 0.75f;

    public FIFOLocalCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Illegal capacity: " + capacity);
        }
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, defaultLoadFactor);
    }

    @Override
    protected void eliminate() {
        cache.entrySet().iterator().next();
        cache.entrySet().iterator().remove();
    }

    public static LocalCache<String, String> getInstance() {
        return FIFOLocalCacheHolder.INSTANCE;
    }

    private static class FIFOLocalCacheHolder {
        private static final LocalCache<String, String> INSTANCE = new FIFOLocalCache<>(100);
    }
}
