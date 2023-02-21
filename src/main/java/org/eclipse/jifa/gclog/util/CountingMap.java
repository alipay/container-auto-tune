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
package org.eclipse.jifa.gclog.util;

import java.util.HashMap;
import java.util.Map;

public class CountingMap<T> {
    private Map<T, Integer> map = new HashMap<>();

    public void put(T key) {
        put(key, 1);
    }

    public void put(T key, int n) {
        map.put(key, map.getOrDefault(key, 0) + n);
    }

    public boolean containKey(T key) {
        return map.containsKey(key);
    }

    public int get(T key) {
        return map.getOrDefault(key, 0);
    }
}
