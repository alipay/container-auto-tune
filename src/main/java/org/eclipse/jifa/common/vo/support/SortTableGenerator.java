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
package org.eclipse.jifa.common.vo.support;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SortTableGenerator<T> {

    private final Map<String, Comparator<T>> table;

    public SortTableGenerator() {
        this.table = new HashMap<>();
    }

    public Map<String, Comparator<T>> build() {
        return table;
    }

    public SortTableGenerator<T> add(String key, Comparator<T> comp) {
        table.put(key, comp);
        return this;
    }

    public <U extends Comparable<? super U>> SortTableGenerator<T> add(String key, Function<T, ? extends U> val) {
        table.put(key, Comparator.comparing(val));
        return this;
    }
}
