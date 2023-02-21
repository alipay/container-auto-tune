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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Key2ValueListMap<K, V> {
    private Map<K, List<V>> map;

    public Key2ValueListMap(Map<K, List<V>> map) {
        this.map = map;
    }

    public Key2ValueListMap() {
        map = new HashMap<>();
    }

    public void put(K key, V value) {
        List<V> list = map.getOrDefault(key, null);
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        list.add(value);
    }

    public List<V> get(K key) {
        return map.getOrDefault(key, null);
    }

    public Map<K, List<V>> getInnerMap() {
        return map;
    }
}
