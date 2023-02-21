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
package org.eclipse.jifa.common.cache;

import com.google.common.cache.CacheBuilder;
import org.eclipse.jifa.common.JifaException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

class Cache {

    private final com.google.common.cache.Cache<CacheKey, Object> cache;

    public Cache() {
        cache = CacheBuilder
                .newBuilder()
                .softValues()
                .recordStats()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    @SuppressWarnings("unchecked")
    public <V> V load(CacheKey key, Callable<V> loader) {
        try {
            return (V) cache.get(key, loader);
        } catch (ExecutionException e) {
            throw new JifaException(e);
        }
    }

    static class CacheKey {

        Method method;

        Object[] args;

        CacheKey(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            CacheKey cacheKey = (CacheKey) o;
            return method.equals(cacheKey.method) && Arrays.equals(args, cacheKey.args);
        }

        @Override
        public int hashCode() {
            int hash = method.hashCode();
            return hash * 31 ^ Arrays.hashCode(args);
        }
    }
}
