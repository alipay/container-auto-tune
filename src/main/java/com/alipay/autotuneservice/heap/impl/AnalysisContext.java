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
package com.alipay.autotuneservice.heap.impl;

import com.alipay.autotuneservice.heap.model.Model;
import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.IResultTree;
import org.eclipse.mat.query.refined.RefinedTable;
import org.eclipse.mat.snapshot.ISnapshot;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AnalysisContext {

    final ISnapshot snapshot;

    volatile SoftReference<ClassLoaderExplorerData> classLoaderExplorerData = new SoftReference<>(null);

    volatile SoftReference<DirectByteBufferData> directByteBufferData = new SoftReference<>(null);

    volatile SoftReference<LeakReportData> leakReportData = new SoftReference<>(null);

    AnalysisContext(ISnapshot snapshot) {
        this.snapshot = snapshot;
    }

    static class ClassLoaderExplorerData {

        IResultTree result;

        // classloader object Id -> record
        Map<Integer, Object> classLoaderIdMap;

        List<?> items;

        int definedClasses;

        int numberOfInstances;
    }

    static class DirectByteBufferData {
        static final String OQL =
                "SELECT s.@displayName as label, s.position as position, s.limit as limit, s.capacity as " +
                        "capacity FROM java.nio.DirectByteBuffer s where s.cleaner != null";

        static final Map<String, Object> ARGS = new HashMap<>(1);

        static {
            ARGS.put("queryString", OQL);
        }

        RefinedTable resultContext;

        Model.DirectByteBuffer.Summary summary;

        public String label(Object row) {
            return (String) resultContext.getColumnValue(row, 0);
        }

        public int position(Object row) {
            return (Integer) resultContext.getColumnValue(row, 1);
        }

        public int limit(Object row) {
            return (Integer) resultContext.getColumnValue(row, 2);
        }

        public int capacity(Object row) {
            return (Integer) resultContext.getColumnValue(row, 3);
        }

    }

    static class LeakReportData {
        IResult result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AnalysisContext that = (AnalysisContext) o;
        return Objects.equals(snapshot, that.snapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snapshot);
    }
}
