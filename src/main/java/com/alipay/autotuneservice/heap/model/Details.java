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
 * @version Summary.java, v 0.1 2022年01月13日 3:33 下午 t-rex
 */
@Data
public class Details {
    public String jvmInfo;

    public int identifierSize;

    public long creationDate;

    public int numberOfObjects;

    public int numberOfGCRoots;

    public int numberOfClasses;

    public int numberOfClassLoaders;

    public long usedHeapSize;

    public boolean generationInfoAvailable;

    public Details(String jvmInfo, int identifierSize, long creationDate, int numberOfObjects,
                   int numberOfGCRoots,
                   int numberOfClasses, int numberOfClassLoaders, long usedHeapSize,
                   boolean generationInfoAvailable) {
        this.jvmInfo = jvmInfo;
        this.identifierSize = identifierSize;
        this.creationDate = creationDate;
        this.numberOfObjects = numberOfObjects;
        this.numberOfGCRoots = numberOfGCRoots;
        this.numberOfClasses = numberOfClasses;
        this.numberOfClassLoaders = numberOfClassLoaders;
        this.usedHeapSize = usedHeapSize;
        this.generationInfoAvailable = generationInfoAvailable;
    }
}
