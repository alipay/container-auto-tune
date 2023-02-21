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

import com.alipay.autotuneservice.heap.util.SearchType;
import com.alipay.autotuneservice.heap.util.Searchable;
import com.alipay.autotuneservice.heap.util.SortTableGenerator;
import com.alipay.autotuneservice.heap.util.exception.ErrorUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;
import java.util.Map;

/**
 * @author t-rex
 * @version DominatorTree.java, v 0.1 2022年01月17日 11:07 上午 t-rex
 */
public interface DominatorTree {
    interface ItemType {
        int CLASS        = 1;
        int CLASS_LOADER = 2;
        int SUPER_CLASS  = 5;
        int PACKAGE      = 6;
    }

    enum Grouping {

        NONE,

        BY_CLASS,

        BY_CLASSLOADER,

        BY_PACKAGE;
    }

    @Data
    class Item {
        public String label;

        public String suffix;

        public int objectId;

        public int objectType;

        public boolean gCRoot;

        public long shallowSize;

        public long retainedSize;

        public double percent;

        public boolean isObjType = true;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class ClassLoaderItem extends Item implements Searchable {
        private static Map<String, Comparator<ClassLoaderItem>> sortTable =
                new SortTableGenerator<ClassLoaderItem>()
                        .add("id", ClassLoaderItem::getObjectId)
                        .add("shallowHeap", ClassLoaderItem::getShallowSize)
                        .add("retainedHeap", ClassLoaderItem::getRetainedSize)
                        .add("percent", ClassLoaderItem::getPercent)
                        .add("Objects", ClassLoaderItem::getObjects)
                        .build();

        public long objects;

        public static Comparator<ClassLoaderItem> sortBy(String field, boolean ascendingOrder) {
            return ascendingOrder ? sortTable.get(field) : sortTable.get(field).reversed();
        }

        @Override
        public Object getBySearchType(SearchType type) {
            switch (type) {
                case BY_NAME:
                    return getLabel();
                case BY_PERCENT:
                    return getPercent();
                case BY_OBJ_NUM:
                    return getObjects();
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

    @Data
    @EqualsAndHashCode(callSuper = true)
    class ClassItem extends Item implements Searchable {
        private static Map<String, Comparator<ClassItem>> sortTable = new SortTableGenerator<ClassItem>()
                .add("id", ClassItem::getObjectId)
                .add("shallowHeap", ClassItem::getShallowSize)
                .add("retainedHeap", ClassItem::getRetainedSize)
                .add("percent", ClassItem::getPercent)
                .add("Objects", ClassItem::getObjects)
                .build();
        private        int                                objects;

        public static Comparator<ClassItem> sortBy(String field, boolean ascendingOrder) {
            return ascendingOrder ? sortTable.get(field) : sortTable.get(field).reversed();
        }

        @Override
        public Object getBySearchType(SearchType type) {
            switch (type) {
                case BY_NAME:
                    return getLabel();
                case BY_PERCENT:
                    return getPercent();
                case BY_OBJ_NUM:
                    return getObjects();
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

    @Data
    @EqualsAndHashCode(callSuper = true)
    class DefaultItem extends Item implements Searchable {
        private static Map<String, Comparator<DefaultItem>> sortTable = new SortTableGenerator<DefaultItem>()
                .add("id", DefaultItem::getObjectId)
                .add("shallowHeap", DefaultItem::getShallowSize)
                .add("retainedHeap", DefaultItem::getRetainedSize)
                .add("percent", DefaultItem::getPercent)
                .build();

        public static Comparator<DefaultItem> sortBy(String field, boolean ascendingOrder) {
            return ascendingOrder ? sortTable.get(field) : sortTable.get(field).reversed();
        }

        @Override
        public Object getBySearchType(SearchType type) {
            switch (type) {
                case BY_NAME:
                    return getLabel();
                case BY_PERCENT:
                    return getPercent();
                case BY_OBJ_NUM:
                    return null;
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

    @Data
    @EqualsAndHashCode(callSuper = true)
    class PackageItem extends Item implements Searchable {
        private static Map<String, Comparator<PackageItem>> sortTable = new SortTableGenerator<PackageItem>()
                .add("id", PackageItem::getObjectId)
                .add("shallowHeap", PackageItem::getShallowSize)
                .add("retainedHeap", PackageItem::getRetainedSize)
                .add("percent", PackageItem::getPercent)
                .add("Objects", PackageItem::getObjects)
                .build();
        private        long                                 objects;

        public static Comparator<PackageItem> sortBy(String field, boolean ascendingOrder) {
            return ascendingOrder ? sortTable.get(field) : sortTable.get(field).reversed();
        }

        @Override
        public Object getBySearchType(SearchType type) {
            switch (type) {
                case BY_NAME:
                    return getLabel();
                case BY_PERCENT:
                    return getPercent();
                case BY_OBJ_NUM:
                    return getObjects();
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
