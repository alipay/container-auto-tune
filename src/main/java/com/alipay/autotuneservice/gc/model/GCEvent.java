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
package com.alipay.autotuneservice.gc.model;

import com.alipay.autotuneservice.gc.util.DateHelper;

import java.util.Iterator;
import java.util.Objects;

/**
 * The GCEvent is the type of event that contains memory (preused, postused, total) and
 * pause information.
 *
 * <p>Date: Jan 30, 2002</p>
 * <p>Time: 5:05:43 PM</p>
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
 */
public class GCEvent extends AbstractGCEvent<GCEvent> {

    /**
     * store references to related/inferred events
     */
    private GCEvent young;
    private GCEvent tenured;
    private GCEvent perm;

    public GCEvent() {
    }

    public GCEvent(double timestamp, int preUsed, int postUsed, int total, double pause, Type type) {
        this.setTimestamp(timestamp);
        this.setPreUsed(preUsed);
        this.setPostUsed(postUsed);
        this.setTotal(total);
        this.setPause(pause);
        this.setType(type);
    }

    @Override
    public void add(GCEvent event) {
        super.add(event);

        setReferencedEvent(event);
    }

    private void setReferencedEvent(GCEvent event) {
        switch (event.getExtendedType().getGeneration()) {
            case YOUNG:
                young = young == null ? event : young.cloneAndMerge(event);
                break;
            case TENURED:
                tenured = tenured == null ? event : tenured.cloneAndMerge(event);
                break;
            case PERM:
                perm = perm == null ? event : perm.cloneAndMerge(event);
                break;
            // ALL and OTHER are never read
            case ALL:
                break;
            case OTHER:
                break;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        GCEvent[] events = {young, tenured, perm};
        young = null;
        tenured = null;
        perm = null;

        GCEvent clonedEvent = (GCEvent) super.clone();

        Iterator<GCEvent> eventIterator = clonedEvent.details();
        while (eventIterator.hasNext()) {
            clonedEvent.setReferencedEvent(eventIterator.next());
        }

        young = events[0];
        tenured = events[1];
        perm = events[2];

        return clonedEvent;
    }

    @Override
    public GCEvent cloneAndMerge(AbstractGCEvent<GCEvent> otherEvent) {
        return (GCEvent) super.cloneAndMerge(otherEvent);
    }

    /**
     * Returns information on young generation. If it was not present in the gc log, but
     * tenured was, it is inferred from there (with -XX:+PrintGCDetails). Otherwise it is
     * <code>null</code> (without -XX:+PrintGCDetails).
     *
     * @return Information on young generation if possible, <code>null</code> otherwise.
     */
    public GCEvent getYoung() {
        if (young == null) {
            if (tenured != null) {
                young = new GCEvent();
                young.setTimestamp(tenured.getTimestamp());
                young.setPreUsed(getPreUsed() - tenured.getPreUsed());
                young.setPostUsed(getPostUsed() - tenured.getPostUsed());
                young.setTotal(getTotal() - tenured.getTotal());
                young.setPause(tenured.getPause());
            }
        }

        return young;
    }

    /**
     * Returns information on young generation. If it was not present in the gc log, but
     * tenured was, it is inferred from there (with -XX:+PrintGCDetails). Otherwise it
     * is <code>null</code> (without -XX:+PrintGCDetails).
     *
     * @return Information on young generation if possible, <code>null</code> otherwise.
     */
    public GCEvent getTenured() {
        if (tenured == null) {
            if (young != null) {
                tenured = new GCEvent();
                tenured.setTimestamp(young.getTimestamp());
                tenured.setPreUsed(getPreUsed() - young.getPreUsed());
                tenured.setPostUsed(getPostUsed() - young.getPostUsed());
                tenured.setTotal(getTotal() - young.getTotal());
                tenured.setPause(young.getPause());
            }
        }

        return tenured;
    }

    /**
     * Returns information on perm generation. If it was not present in the gc log,
     * <code>null</code> will be returned, because the values cannot be inferred.
     *
     * @return Information on perm generation or <code>null</code> if not present.
     */
    public GCEvent getPerm() {
        return perm;
    }

    public void toStringBuffer(StringBuffer sb) {
        if (getDatestamp() != null) {
            sb.append("[").append(DateHelper.formatDate(getDatestamp())).append("]");
        }
        sb.append("[").append(getTimestamp()).append("]");
        if (getNumber() >= 0) {
            sb.append(" GC(").append(getNumber()).append(")");
        }
        sb.append(" [").append(getExtendedType() != null ? getExtendedType().getName() : ExtendedType.UNDEFINED);
        if (details != null) {
            sb.append(' ');
            for (AbstractGCEvent event : details) {
                event.toStringBuffer(sb);
            }
            sb.append(' ');
        } else {
            sb.append(": ");
        }
        sb.append(getPreUsed());
        sb.append("K->");
        sb.append(getPostUsed());
        sb.append("K(");
        sb.append(getTotal());
        sb.append("K), ");
        sb.append(getPause());
        sb.append(" secs]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof GCEvent)) { return false; }
        if (!super.equals(o)) { return false; }
        GCEvent gcEvent = (GCEvent) o;
        return getPreUsed() == gcEvent.getPreUsed() &&
                getPostUsed() == gcEvent.getPostUsed() &&
                getTotal() == gcEvent.getTotal();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPreUsed(), getPostUsed(), getTotal());
    }
}
