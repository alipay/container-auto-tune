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

/**
 * G1GcEvent may contain lots of detail information which provide information about the different steps of the garbage collection. It is not
 * just information about different generations as with e.g. CMS collector.
 *
 * @author t-rex
 * @version G1GcEvent.java, v 0.1 2021年12月29日 4:14 下午 t-rex
 */
public class G1GcEvent extends GCEvent {

    @Override
    public String getTypeAsString() {
        return getExtendedType().getName();
    }
}