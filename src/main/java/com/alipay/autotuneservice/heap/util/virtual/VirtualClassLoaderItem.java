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
package com.alipay.autotuneservice.heap.util.virtual;

import com.alipay.autotuneservice.heap.impl.AnalysisException;
import com.alipay.autotuneservice.heap.model.DominatorTree;
import com.alipay.autotuneservice.heap.util.UseAccessor;
import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.query.Bytes;
import org.eclipse.mat.query.IStructuredResult;
import org.eclipse.mat.snapshot.ISnapshot;

import static com.alipay.autotuneservice.heap.model.JavaObject.typeOf;

@UseAccessor
public class VirtualClassLoaderItem extends DominatorTree.ClassLoaderItem {
    static final int COLUMN_LABEL    = 0;
    static final int COLUMN_OBJECTS  = 1;
    static final int COLUMN_SHALLOW  = 2;
    static final int COLUMN_RETAINED = 3;
    static final int COLUMN_PERCENT  = 4;

    transient final ISnapshot         snapshot;
    transient final IStructuredResult results;
    transient final Object            e;

    public VirtualClassLoaderItem(final ISnapshot snapshot, final IStructuredResult results, final Object e) {
        this.snapshot = snapshot;
        this.results = results;
        this.e = e;
        this.objectId = results.getContext(e).getObjectId();
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public int getObjectId() {
        return objectId;
    }

    @Override
    public int getObjectType() {
        try {
            return typeOf(snapshot.getObject(objectId));
        } catch (SnapshotException se) {
            throw new AnalysisException(se);
        }
    }

    @Override
    public boolean isGCRoot() {
        return snapshot.isGCRoot(objectId);
    }

    @Override
    public String getLabel() {
        return (String) results.getColumnValue(e, COLUMN_LABEL);
    }

    @Override
    public long getObjects() {
        Object value = results.getColumnValue(e, COLUMN_OBJECTS);
        if (value != null) {
            return (Integer) value;
        } else {
            return 0;
        }
    }

    @Override
    public long getShallowSize() {
        return ((Bytes) results.getColumnValue(e, COLUMN_SHALLOW)).getValue();
    }

    @Override
    public long getRetainedSize() {
        return ((Bytes) results.getColumnValue(e, COLUMN_RETAINED)).getValue();
    }

    @Override
    public double getPercent() {
        return (Double) results.getColumnValue(e, COLUMN_PERCENT);
    }
}