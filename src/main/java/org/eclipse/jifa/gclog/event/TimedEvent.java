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

import org.eclipse.jifa.gclog.vo.TimeRange;

import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_DOUBLE;

public class TimedEvent {
    // We assume that start time always exists. We will refuse to analyze logs that does not print any time,
    // and will add a suitable start time to events that does not have a start time in log.
    // Unit of all time variables is ms.
    protected double startTime = UNKNOWN_DOUBLE;
    // Real time duration of event. The duration may not exist, and we should always check its existence when using.
    private double duration = UNKNOWN_DOUBLE;

    public double getStartTime() {
        return startTime;
    }

    public double getDuration() {
        return duration;
    }

    public double getEndTime() {
        if (getStartTime() != UNKNOWN_DOUBLE && getDuration() != UNKNOWN_DOUBLE) {
            return getStartTime() + getDuration();
        } else {
            return UNKNOWN_DOUBLE;
        }
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public TimedEvent(double startTime, double duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public TimedEvent(double startTime) {
        this.startTime = startTime;
    }

    public TimedEvent() {
    }

    public TimeRange toTimeRange() {
        if (duration != UNKNOWN_DOUBLE) {
            return new TimeRange(getStartTime(), getEndTime());
        } else {
            return new TimeRange(getStartTime(), getStartTime());
        }
    }

    public static TimedEvent newByStartEnd(double start, double end) {
        return new TimedEvent(start, end - start);
    }
}
