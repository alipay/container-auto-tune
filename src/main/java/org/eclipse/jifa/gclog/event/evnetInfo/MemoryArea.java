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
package org.eclipse.jifa.gclog.event.evnetInfo;

public enum MemoryArea {
    EDEN,
    SURVIVOR,
    YOUNG,
    OLD,
    HUMONGOUS,
    HEAP, //young + old + humongous
    METASPACE, // also represents perm
    MEMORY_AREA_COUNT;

    public static MemoryArea getMemoryArea(String name) {
        if (name == null) {
            return null;
        }
        switch (name.trim().toLowerCase()) {
            case "young":
            case "parnew":
            case "defnew":
            case "psyounggen":
                return YOUNG;
            case "eden":
                return EDEN;
            case "survivor":
            case "survivors":
                return SURVIVOR;
            case "tenured":
            case "old":
            case "psoldgen":
            case "paroldgen":
            case "cms":
            case "ascms":
                return OLD;
            case "metaspace":
            case "perm":
                return METASPACE;
            case "humongous":
                return HUMONGOUS;
            case "total":
            case "heap":
                return HEAP;
            default:
                return null;
        }
    }

}
