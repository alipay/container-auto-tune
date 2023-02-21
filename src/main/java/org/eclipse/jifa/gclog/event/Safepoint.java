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
package org.eclipse.jifa.gclog.event;

import org.eclipse.jifa.gclog.model.GCEventType;
import org.eclipse.jifa.gclog.util.Constant;

public class Safepoint extends GCEvent {
    private double timeToEnter = Constant.UNKNOWN_DOUBLE;

    public Safepoint() {
        this.setEventType(GCEventType.SAFEPOINT);
    }

    public double getTimeToEnter() {
        return timeToEnter;
    }

    public void setTimeToEnter(double timeToEnter) {
        this.timeToEnter = timeToEnter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendStartTime(sb);
        sb.append(String.format("Total time for which application threads were stopped: " +
                "%.3f seconds, Stopping threads took: %.3f seconds", getDuration(), getTimeToEnter()));
        return sb.toString();
    }
}
