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
package org.eclipse.jifa.gclog.model;

import org.eclipse.jifa.common.util.ErrorUtil;
import org.eclipse.jifa.gclog.model.modeInfo.GCCollectorType;

public class GCModelFactory {
    public static GCModel getModel(GCCollectorType collectorType) {
        switch (collectorType) {
            case G1:
                return new G1GCModel();
            case CMS:
                return new CMSGCModel();
            case SERIAL:
                return new SerialGCModel();
            case PARALLEL:
                return new ParallelGCModel();
            case ZGC:
                return new ZGCModel();
            case UNKNOWN:
                return new UnknownGCModel();
            default:
                ErrorUtil.shouldNotReachHere();
        }
        return null;
    }
}
