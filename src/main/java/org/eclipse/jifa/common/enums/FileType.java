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
package org.eclipse.jifa.common.enums;

import static org.eclipse.jifa.common.util.ErrorUtil.shouldNotReachHere;

public enum FileType {
    HEAP_DUMP("heap-dump"),

    METASPACE_DUMP("metaspace-dump"),

    GC_LOG("gc-log"),

    // cannot remove it now since zprofiler use it
    STACK_DUMP("stack-dump"),

    THREAD_DUMP("thread-dump"),
    ;

    private String tag;

    FileType(String tag) {
        this.tag = tag;
    }

    public static FileType getByTag(String tag) {
        for (FileType type : FileType.values()) {
            if (type.tag.equals(tag)) {
                return type;
            }
        }
        return shouldNotReachHere();
    }

    public String getTag() {
        return tag;
    }
}


