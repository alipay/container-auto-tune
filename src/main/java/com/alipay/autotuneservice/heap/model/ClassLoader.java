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

import lombok.Data;

/**
 * @author t-rex
 * @version ClassLoader.java, v 0.1 2022年01月17日 8:43 下午 t-rex
 */
public interface ClassLoader {

    @Data
   public class Item {

        public int objectId;

        public String prefix;

        public String label;

        public boolean classLoader;

        public boolean hasParent;

        public int definedClasses;

        public int numberOfInstances;
    }

    @Data
    class Summary {

        public int totalSize;

        public int definedClasses;

        public int numberOfInstances;
    }
}