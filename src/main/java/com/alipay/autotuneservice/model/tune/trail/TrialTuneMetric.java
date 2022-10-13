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
package com.alipay.autotuneservice.model.tune.trail;

import lombok.Data;

/**
 * @author huangkaifei
 * @version : TrialTuneMetric.java, v 0.1 2022年06月01日 3:04 PM huangkaifei Exp $
 */
@Data
public class TrialTuneMetric {

    private double fgc_count = 0.0;
    private double fgc_time  = 0.0;
    private double ygc_time  = 0.0;
    private double ygc_count = 0.0;

    private long   fgcCountMax4Refer;
    private long   fgcCountMax4Trail;

    private double fgcTimeMax4Refer;
    private double fgcTimeMax4Trail;

    private long   ygcCountMax4Refer;
    private long   ygcCountMax4Trial;

    private double ygcTimeMax4Refer;
    private double ygcTimeMax4Trail;

    private double heapUsed4Refer;
    private double heapUsed4Trail;
}