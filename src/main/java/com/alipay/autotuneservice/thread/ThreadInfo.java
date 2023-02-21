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
package com.alipay.autotuneservice.thread;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dutianze
 * @version ThreadInfo.java, v 0.1 2022年01月06日 14:37 dutianze
 */
@Data
public class ThreadInfo {
    public String               name;
    public String               groupName;
    public boolean              daemon;
    public int                  number;
    public int                  osPriority;
    public int                  priority;
    public long                 threadId;
    public long                 nativeId;
    public long                 conditionPointer;
    public Long                 ownLock;
    public String               onwLockDetails;
    public String               state;
    public long                 waitFor;
    public String               waitForDetails;
    public String               state0;

    public String               lock;
    public String               waitToLock;
    public ThreadInfo           relateThread;
    public Integer              index;


    public List<StacktraceItem> stacktrace = new ArrayList<>();
    public List<String>         stackTraces = new ArrayList<>();

    public              long   filteredTo = -1;
    public static final String RUNNABLE   = "RUNNABLE";

    public long getIdentity() {
        return nativeId;
    }

    public void addStacktraceItem(StacktraceItem stacktraceItem) {
        this.stacktrace.add(stacktraceItem);
    }

    //public boolean isRunnable() {
    //    return state.equalsIgnoreCase(RUNNABLE);
    //}
}