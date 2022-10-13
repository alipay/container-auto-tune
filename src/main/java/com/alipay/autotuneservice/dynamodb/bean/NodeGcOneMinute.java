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
package com.alipay.autotuneservice.dynamodb.bean;

import lombok.Data;

/**
 * @author t-rex
 * @version NodeGcOneMinute.java, v 0.1 2022年02月18日 11:18 上午 t-rex
 */
@Data
public class NodeGcOneMinute {
    private String cluster;
    private Long   period;
    private String node;
    private double eden_used;
    private double eden_max;
    private double eden_capacity;
    private double old_used;
    private double old_max;
    private double old_capacity;
    private double meta_util;
    private double eden_util;
    private double old_util;
    private double meta_used;
    private double meta_max;
    private double meta_capacity;
    private double ygc_count;
    private double ygc_count_max;
    private double ygc_count_total;
    private double ygc_time;
    private double ygc_time_max;
    private double ygc_time_total;
    private double fgc_count;
    private double fgc_count_max;
    private double fgc_count_total;
    private double fgc_time;
    private double fgc_time_max;
    private double fgc_time_total;
}