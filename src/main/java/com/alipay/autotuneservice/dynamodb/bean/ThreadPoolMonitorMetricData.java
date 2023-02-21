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

import java.io.Serializable;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetric.java, v 0.1 2022年04月13日 4:08 PM huangkaifei Exp $
 */
@Data
public class ThreadPoolMonitorMetricData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String threadPoolName;
    private String appName;

    private String hostName;
    /**
     * 活动线程数
     */
    private int    activeCount;
    /**
     * 线程数
     */
    private int    poolSize;
    /**
     * 核心线程数
     */
    private int    corePoolSize;
    /**
     * 存活时间
     */
    private long   keepAliveTime;
    /**
     * 完成任务数
     */
    private long   completedTaskCount;
    /**
     * 最大时的线程数
     */
    private int    largestPoolSize;
    /**
     * 最大线程数
     */
    private int    maximumPoolSize;
    /**
     * 计划执行的任务数
     */
    private long   taskCount;
    /**
     * 堵塞队列长度
     */
    private long   blockQueue;
    /**
     * 空闲线程数
     */
    private long   idlePoolSize;
    /**
     * 拒绝次数
     */
    private long   rejectCount;
    /**
     * 时间
     */
    private long   period;
    /**
     * 天分区
     */
    private long   dt;

    public String getHostName() {
        return hostName;
    }

    public long getPeriod() {
        return this.period;
    }
}