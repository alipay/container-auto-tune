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
 * @author t-rex
 * @version GCEventUJL.java, v 0.1 2022年01月20日 11:15 上午 t-rex
 */
public class GCEventUJL extends GCEvent {

    @Override
    public boolean isFull() {
        // assumption AbstractGCEvent does not hold here; only the event type itself can tell
        return getExtendedType().getGeneration().equals(Generation.ALL);
    }

    @Override
    public Generation getGeneration() {
        // assumption in AbstractGCEvent concerning "has information about several generations"
        // -> "has collected objects from several generations" is not correct for unified jvm logging events
        // they usually seem to hold information about several generations, as soon as heap details are logged
        if (generation == null) {
            generation = getExtendedType() != null ? getExtendedType().getGeneration() : null;
        }

        return generation == null ? Generation.YOUNG : generation;
    }

    @Override
    public void addPhase(AbstractGCEvent<?> phase) {
        super.addPhase(phase);

        // If it is a stop-the-world event, increase pause time for parent GC event
        if (Concurrency.SERIAL.equals(phase.getExtendedType().getConcurrency())) {
            setPause(getPause() + phase.getPause());
        }
    }
}