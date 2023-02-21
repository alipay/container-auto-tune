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
package org.eclipse.jifa.gclog.diagnoser;

import java.util.HashMap;
import java.util.Map;

public class AbnormalType {
    public static String I18N_PREFIX = "jifa.gclog.diagnose.abnormal.";
    private static Map<String, AbnormalType> name2Type = new HashMap<>();

    // order these members by their general importance
    // Whenever a new type is added, add its default suggestions to DefaultSuggestionGenerator

    // Ultra
    public static AbnormalType OUT_OF_MEMORY = new AbnormalType("outOfMemory");
    public static AbnormalType ALLOCATION_STALL = new AbnormalType("allocationStall");
    public static AbnormalType METASPACE_FULL_GC = new AbnormalType("metaspaceFullGC");
    public static AbnormalType HEAP_MEMORY_FULL_GC = new AbnormalType("heapMemoryFullGC");

    //High
    public static AbnormalType LONG_YOUNG_GC_PAUSE = new AbnormalType("longYoungGCPause");
    public static AbnormalType SYSTEM_GC = new AbnormalType("systemGC");

    public static AbnormalType LAST_TYPE = new AbnormalType("lastType");


    private String name;
    private int ordinal;

    private AbnormalType(String name) {
        this.name = name;
        ordinal = name2Type.size();
        name2Type.put(name, this);
    }

    public static AbnormalType getType(String name) {
        return name2Type.getOrDefault(name, null);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
