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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 存放Container的stat信息, e.g cpu, 内存
 *
 * @author huangkaifei
 * @version : ContainerStat.java, v 0.1 2022年04月19日 11:43 PM huangkaifei Exp $
 */
@Data
public class ContainerStatistics {
    private String containerId;
    private String podName;
    private long   gmtCreated;

    /**
     * cpu
     **/
    // cpu_stats.cpu_usage.total_usage
    private long   cpuTotalUsage;
    // cpu_stats.system_cpu_usage
    private long   systemCpuUsage;
    // precpu_stats.cpu_usage.total_usage
    private long   precpuTotalUsage;
    //  precpu_stats.system_cpu_usage
    private long   precpuSystemCpuUsage;
    //  cpu_stats.online_cpus
    private long   onlineCpus;
    // cpu使用率
    private double cpuUsageRate;

    /**
     * memory unit: MB
     **/
    // memory_stats.limit
    private long   memLimit;
    // memory_stats.usage
    private long   memUsage;
    // memory_stats.limit
    private long   memMaxUsage;
    // memory_stats.stats.cache
    private long   memCache;
    private double memUsageRate;
    private long   appId;

    public String getContainerId() {
        return containerId;
    }

    public long getGmtCreated() {
        return gmtCreated;
    }

    /**
     * Getter
     **/

    public double getCpuUsageRate() {
        return calCpuUsageRate();
    }

    public double getMemUsageRate() {
        return calMemUsageRate();
    }

    /**
     * Memory Setter
     **/
    private static final long MEM_2_MB = 1024l * 1024l;

    public void setMemLimit(long memLimit) {
        this.memLimit = memLimit / MEM_2_MB;
    }

    public void setMemUsage(long memUsage) {
        this.memUsage = memUsage / MEM_2_MB;
    }

    public void setMemMaxUsage(long memMaxUsage) {
        this.memMaxUsage = memMaxUsage / MEM_2_MB;
    }

    public void setMemCache(long memCache) {
        this.memCache = memCache / MEM_2_MB;
    }

    /**
     * calculate cpu usage with percent unit
     *
     * ● cpu_delta = cpu_stats.cpu_usage.total_usage - precpu_stats.cpu_usage.total_usage
     * ● system_cpu_delta = cpu_stats.system_cpu_usage - precpu_stats.system_cpu_usage
     * ● number_cpus = length(cpu_stats.cpu_usage.percpu_usage) or cpu_stats.online_cpus
     * ● CPU usage % = (cpu_delta / system_cpu_delta) * number_cpus * 100.0
     *
     * @return
     */
    private double calCpuUsageRate() {
        BigDecimal cpuDelta = new BigDecimal(this.cpuTotalUsage - this.precpuTotalUsage);
        BigDecimal systemCpuDelta = new BigDecimal(this.systemCpuUsage - this.precpuSystemCpuUsage);
        try {
            return cpuDelta.divide(systemCpuDelta, 5, RoundingMode.HALF_DOWN).doubleValue()
                   * this.onlineCpus * 100;
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * calculate the usage of memory
     * ● used_memory = memory_stats.usage - memory_stats.stats.cache
     * ● available_memory = memory_stats.limit
     * ● Memory usage % = (used_memory / available_memory) * 100.0
     *
     * @return
     */
    private double calMemUsageRate() {
        BigDecimal used_memory = new BigDecimal(this.memUsage - this.memCache);
        BigDecimal memLimit = new BigDecimal(this.memLimit);
        try {
            return used_memory.divide(memLimit, 5, RoundingMode.HALF_DOWN).doubleValue() * 100;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public String generateResourceKey() {
        return String.format("POD_RESOURCE_%s_%s", this.podName, this.containerId);
    }

    public String getResourceValue() {
        return String.format("%s_CPUCORE_%s_MEMLIMIT_%s", this.podName, this.onlineCpus,
            this.memLimit);
    }

    /** 获取使用cpu的核数  **/
    public double getUsedCpuCores() {
        return this.cpuUsageRate / 100;
    }
}