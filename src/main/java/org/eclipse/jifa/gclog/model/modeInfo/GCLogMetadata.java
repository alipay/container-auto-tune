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
package org.eclipse.jifa.gclog.model.modeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_DOUBLE;
import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_INT;

/**
 * This class provides some necessary information to the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GCLogMetadata {
    private String collector;
    private String logStyle;
    private double startTime = UNKNOWN_DOUBLE;
    private double endTime = UNKNOWN_DOUBLE;
    private double timestamp = UNKNOWN_DOUBLE;
    private boolean generational = true;
    private boolean pauseless = false;
    private boolean metaspaceCapacityReliable = false;
    private int parallelGCThreads = UNKNOWN_INT;
    private int concurrentGCThreads = UNKNOWN_INT;
    private List<String> parentEventTypes;
    private List<String> importantEventTypes;
    private List<String> pauseEventTypes;
    private List<String> mainPauseEventTypes;
    private List<String> allEventTypes;
    private List<String> causes;
}
