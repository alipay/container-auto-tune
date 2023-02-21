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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_DOUBLE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpuTime {
    //unit is ms
    private double user = UNKNOWN_DOUBLE;
    private double sys = UNKNOWN_DOUBLE;
    private double real = UNKNOWN_DOUBLE;

    public String toString() {
        return String.format("User=%.2fs Sys=%.2fs Real=%.2fs", user / 1000, sys / 1000, real / 1000);
    }

}
