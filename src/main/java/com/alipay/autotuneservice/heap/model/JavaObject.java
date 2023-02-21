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

import com.alipay.autotuneservice.heap.util.SortTableGenerator;
import lombok.Data;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IClassLoader;
import org.eclipse.mat.snapshot.model.IObject;

import java.util.Comparator;
import java.util.Map;

/**
 * @author t-rex
 * @version JavaObject.java, v 0.1 2022年01月14日 11:59 上午 t-rex
 */
@Data
public  class JavaObject {

    public static final int CLASS_TYPE = 1;

    public static final int CLASS_LOADER_TYPE = 2;

    public static final int ARRAY_TYPE = 3;

    public static final int                                 NORMAL_TYPE = 4;
    // FIXME: can we generate these code automatically?
    public static       Map<String, Comparator<JavaObject>> sortTable   = new SortTableGenerator<JavaObject>()
            .add("id", JavaObject::getObjectId)
            .add("shallowHeap", JavaObject::getShallowSize)
            .add("retainedHeap", JavaObject::getRetainedSize)
            .add("label", JavaObject::getLabel)
            .build();
    public              int                                 objectId;
    public String prefix;
    public String label;
    public String suffix;
    public long shallowSize;
    public long retainedSize;
    public boolean hasInbound;
    public boolean hasOutbound;
    public int objectType;
    public boolean gCRoot;

    public static Comparator<JavaObject> sortBy(String field, boolean ascendingOrder) {
        return ascendingOrder ? sortTable.get(field) : sortTable.get(field).reversed();
    }

    /**
     * 工具方法
     */
    public static int typeOf(IObject object) {
        if (object instanceof IClass) {
            return JavaObject.CLASS_TYPE;
        }

        if (object instanceof IClassLoader) {
            return JavaObject.CLASS_LOADER_TYPE;
        }

        if (object.getClazz().isArrayType()) {
            return JavaObject.ARRAY_TYPE;
        }
        return JavaObject.NORMAL_TYPE;
    }
}