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
/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ThreadpoolMonitorMetricData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String  hostName;
    private Long    period;
    private Integer activeCount;
    private String  appName;
    private Long    blockQueue;
    private Long    completedTaskCount;
    private Integer corePoolSize;
    private Long    dt;
    private Integer idlePoolSize;
    private Long    keepAliveTime;
    private Integer largestPoolSize;
    private Integer maxiMumPoolSize;
    private Integer poolSize;
    private Long    rejectCount;
    private Long    taskCount;
    private String  threadPoolName;

    public ThreadpoolMonitorMetricData() {}

    public ThreadpoolMonitorMetricData(ThreadpoolMonitorMetricData value) {
        this.id = value.id;
        this.hostName = value.hostName;
        this.period = value.period;
        this.activeCount = value.activeCount;
        this.appName = value.appName;
        this.blockQueue = value.blockQueue;
        this.completedTaskCount = value.completedTaskCount;
        this.corePoolSize = value.corePoolSize;
        this.dt = value.dt;
        this.idlePoolSize = value.idlePoolSize;
        this.keepAliveTime = value.keepAliveTime;
        this.largestPoolSize = value.largestPoolSize;
        this.maxiMumPoolSize = value.maxiMumPoolSize;
        this.poolSize = value.poolSize;
        this.rejectCount = value.rejectCount;
        this.taskCount = value.taskCount;
        this.threadPoolName = value.threadPoolName;
    }

    public ThreadpoolMonitorMetricData(
        Integer id,
        String  hostName,
        Long    period,
        Integer activeCount,
        String  appName,
        Long    blockQueue,
        Long    completedTaskCount,
        Integer corePoolSize,
        Long    dt,
        Integer idlePoolSize,
        Long    keepAliveTime,
        Integer largestPoolSize,
        Integer maxiMumPoolSize,
        Integer poolSize,
        Long    rejectCount,
        Long    taskCount,
        String  threadPoolName
    ) {
        this.id = id;
        this.hostName = hostName;
        this.period = period;
        this.activeCount = activeCount;
        this.appName = appName;
        this.blockQueue = blockQueue;
        this.completedTaskCount = completedTaskCount;
        this.corePoolSize = corePoolSize;
        this.dt = dt;
        this.idlePoolSize = idlePoolSize;
        this.keepAliveTime = keepAliveTime;
        this.largestPoolSize = largestPoolSize;
        this.maxiMumPoolSize = maxiMumPoolSize;
        this.poolSize = poolSize;
        this.rejectCount = rejectCount;
        this.taskCount = taskCount;
        this.threadPoolName = threadPoolName;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.ID</code>. 唯一ID;唯一ID
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.ID</code>. 唯一ID;唯一ID
     */
    public ThreadpoolMonitorMetricData setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.HOST_NAME</code>. host name
     */
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.HOST_NAME</code>. host name
     */
    public ThreadpoolMonitorMetricData setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.PERIOD</code>. 日期
     */
    public Long getPeriod() {
        return this.period;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.PERIOD</code>. 日期
     */
    public ThreadpoolMonitorMetricData setPeriod(Long period) {
        this.period = period;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.ACTIVE_COUNT</code>. activeCount
     */
    public Integer getActiveCount() {
        return this.activeCount;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.ACTIVE_COUNT</code>. activeCount
     */
    public ThreadpoolMonitorMetricData setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.APP_NAME</code>. appName
     */
    public String getAppName() {
        return this.appName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.APP_NAME</code>. appName
     */
    public ThreadpoolMonitorMetricData setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.BLOCK_QUEUE</code>. blockQueue
     */
    public Long getBlockQueue() {
        return this.blockQueue;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.BLOCK_QUEUE</code>. blockQueue
     */
    public ThreadpoolMonitorMetricData setBlockQueue(Long blockQueue) {
        this.blockQueue = blockQueue;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.COMPLETED_TASK_COUNT</code>. completedTaskCount
     */
    public Long getCompletedTaskCount() {
        return this.completedTaskCount;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.COMPLETED_TASK_COUNT</code>. completedTaskCount
     */
    public ThreadpoolMonitorMetricData setCompletedTaskCount(Long completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.CORE_POOL_SIZE</code>. corePoolSize
     */
    public Integer getCorePoolSize() {
        return this.corePoolSize;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.CORE_POOL_SIZE</code>. corePoolSize
     */
    public ThreadpoolMonitorMetricData setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.DT</code>. dt
     */
    public Long getDt() {
        return this.dt;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.DT</code>. dt
     */
    public ThreadpoolMonitorMetricData setDt(Long dt) {
        this.dt = dt;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.IDLE_POOL_SIZE</code>. idlePoolSize
     */
    public Integer getIdlePoolSize() {
        return this.idlePoolSize;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.IDLE_POOL_SIZE</code>. idlePoolSize
     */
    public ThreadpoolMonitorMetricData setIdlePoolSize(Integer idlePoolSize) {
        this.idlePoolSize = idlePoolSize;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.KEEP_ALIVE_TIME</code>. keepAliveTime
     */
    public Long getKeepAliveTime() {
        return this.keepAliveTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.KEEP_ALIVE_TIME</code>. keepAliveTime
     */
    public ThreadpoolMonitorMetricData setKeepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.LARGEST_POOL_SIZE</code>. largestPoolSize
     */
    public Integer getLargestPoolSize() {
        return this.largestPoolSize;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.LARGEST_POOL_SIZE</code>. largestPoolSize
     */
    public ThreadpoolMonitorMetricData setLargestPoolSize(Integer largestPoolSize) {
        this.largestPoolSize = largestPoolSize;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.MAXI_MUM_POOL_SIZE</code>. maximumPoolSize
     */
    public Integer getMaxiMumPoolSize() {
        return this.maxiMumPoolSize;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.MAXI_MUM_POOL_SIZE</code>. maximumPoolSize
     */
    public ThreadpoolMonitorMetricData setMaxiMumPoolSize(Integer maxiMumPoolSize) {
        this.maxiMumPoolSize = maxiMumPoolSize;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.POOL_SIZE</code>. poolSize
     */
    public Integer getPoolSize() {
        return this.poolSize;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.POOL_SIZE</code>. poolSize
     */
    public ThreadpoolMonitorMetricData setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.REJECT_COUNT</code>. rejectCount
     */
    public Long getRejectCount() {
        return this.rejectCount;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.REJECT_COUNT</code>. rejectCount
     */
    public ThreadpoolMonitorMetricData setRejectCount(Long rejectCount) {
        this.rejectCount = rejectCount;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.TASK_COUNT</code>. taskCount
     */
    public Long getTaskCount() {
        return this.taskCount;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.TASK_COUNT</code>. taskCount
     */
    public ThreadpoolMonitorMetricData setTaskCount(Long taskCount) {
        this.taskCount = taskCount;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.THREAD_POOL_NAME</code>.
     */
    public String getThreadPoolName() {
        return this.threadPoolName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.THREAD_POOL_NAME</code>.
     */
    public ThreadpoolMonitorMetricData setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ThreadpoolMonitorMetricData (");

        sb.append(id);
        sb.append(", ").append(hostName);
        sb.append(", ").append(period);
        sb.append(", ").append(activeCount);
        sb.append(", ").append(appName);
        sb.append(", ").append(blockQueue);
        sb.append(", ").append(completedTaskCount);
        sb.append(", ").append(corePoolSize);
        sb.append(", ").append(dt);
        sb.append(", ").append(idlePoolSize);
        sb.append(", ").append(keepAliveTime);
        sb.append(", ").append(largestPoolSize);
        sb.append(", ").append(maxiMumPoolSize);
        sb.append(", ").append(poolSize);
        sb.append(", ").append(rejectCount);
        sb.append(", ").append(taskCount);
        sb.append(", ").append(threadPoolName);

        sb.append(")");
        return sb.toString();
    }
}
