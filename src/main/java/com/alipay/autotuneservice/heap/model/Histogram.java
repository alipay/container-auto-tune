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
package com.alipay.autotuneservice.heap.model;

/**
 * @author t-rex
 * @version Histogram.java, v 0.1 2022年01月17日 2:52 下午 t-rex
 */

import com.alipay.autotuneservice.heap.util.SearchType;
import com.alipay.autotuneservice.heap.util.Searchable;
import com.alipay.autotuneservice.heap.util.SortTableGenerator;
import com.alipay.autotuneservice.heap.util.exception.ErrorUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.Map;

public interface Histogram {
    enum Grouping {

        BY_CLASS,

        BY_SUPERCLASS,

        BY_CLASSLOADER,

        BY_PACKAGE;
    }

    interface ItemType {
        int CLASS        = 1;
        int CLASS_LOADER = 2;
        int SUPER_CLASS  = 5;
        int PACKAGE      = 6;
    }

    @Data
    @NoArgsConstructor
    class Item implements Searchable {
        private static Map<String, Comparator<Item>> sortTable = new SortTableGenerator<Item>()
                .add("id", Item::getObjectId)
                .add("numberOfObjects", Item::getNumberOfObjects)
                .add("shallowSize", Item::getShallowSize)
                .add("retainedSize", Item::getRetainedSize)
                .build();
        public         long                          numberOfObjects;
        public         long                          shallowSize;
        public         long                          retainedSize;
        public         String                        label;
        public         int                           objectId;
        public         int                           type;

        private long numberOfYoungObjects;
        private long shallowSizeOfYoung;
        private long numberOfOldObjects;
        private long shallowSizeOfOld;

        public Item(int objectId, String label, int type, long numberOfObjects, long shallowSize,
                    long retainedSize, long numberOfYoungObjects, long shallowSizeOfYoung, long numberOfOldObjects,
                    long shallowSizeOfOld) {
            this.objectId = objectId;
            this.label = label;
            this.type = type;
            this.numberOfObjects = numberOfObjects;
            this.shallowSize = shallowSize;
            this.retainedSize = retainedSize;

            this.numberOfYoungObjects = numberOfYoungObjects;
            this.shallowSizeOfYoung = shallowSizeOfYoung;
            this.numberOfOldObjects = numberOfOldObjects;
            this.shallowSizeOfOld = shallowSizeOfOld;
        }

        public static Comparator<Item> sortBy(String field, boolean ascendingOrder) {
            return ascendingOrder ? sortTable.get(field) : sortTable.get(field).reversed();
        }

        @Override
        public Object getBySearchType(SearchType type) {
            switch (type) {
                case BY_NAME:
                    return getLabel();
                case BY_OBJ_NUM:
                    return getNumberOfObjects();
                case BY_RETAINED_SIZE:
                    return getRetainedSize();
                case BY_SHALLOW_SIZE:
                    return getShallowSize();
                default:
                    ErrorUtil.shouldNotReachHere();
            }
            return null;
        }
    }
}