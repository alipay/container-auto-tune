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
 * @author huangkaifei
 * @version : JvmMonitorMetric.java, v 0.1 2022年04月13日 4:08 PM huangkaifei Exp $
 */
@Data
public class JvmMonitorMetricData {

    private String  cluster;       //集群名
    private long    period;
    /**
     * pod name
     */
    private String  pod;           //node 名
    private long    dt;            //天分区
    /** app Id **/
    private Integer appId;
    /**
     * app name
     **/
    private String  app;

    // eden
    private double  eden_used;
    private double  eden_max;
    private double  eden_capacity;
    private double  eden_util;

    // old
    private double  old_used;
    private double  old_max;
    private double  old_capacity;
    private double  old_util;

    // metaspace
    private double  meta_util;
    private double  meta_used;
    private double  meta_max;
    private double  meta_capacity;

    // ygc
    private long    ygc_count;
    //    private double ygc_count_max;
    //    private double ygc_count_total;
    private double  ygc_time;
    //    private double ygc_time_max;
    //    private double ygc_time_total;

    // fgc
    private long    fgc_count;
    //    private double fgc_count_max;

    //    private double fgc_count_total;
    private double  fgc_time;

    //    private double fgc_time_max;
    //    private double fgc_time_total;

    public long getDt() {
        return dt;
    }

    public String getCluster() {
        return this.cluster;
    }

    public long getPeriod() {
        return this.period;
    }

    public String getPod() {
        return pod;
    }

    /**
     * Get heap size
     *
     * @return heap size MB
     */
    public double getHeapUsed() {
        // 当前获取不到S0, S1, 先用heap_used = eden + old
        return (eden_used + old_used) / 1024d / 1024d;
    }
}