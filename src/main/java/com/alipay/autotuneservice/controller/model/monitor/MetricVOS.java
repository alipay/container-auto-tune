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
package com.alipay.autotuneservice.controller.model.monitor;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huoyuqi
 * @version MetricVOS.java, v 0.1 2022年11月14日 4:51 下午 huoyuqi
 */
@Data
public class MetricVOS {

    //CPU

    /**
     * 系统cpu利用率
     */
    private List<MetricVO> systemCpuLoads = new ArrayList<>();

    /**
     * 用户cpu利用率
     */
    private List<MetricVO> processCpuLoads = new ArrayList<>();

    /**
     * 空闲cpu利用率
     */
    private List<MetricVO> waitCpuLoads = new ArrayList<>();

    /**
     * cpu 当前使用率
     */
    private List<MetricVO> cpuLoad = new ArrayList<>();

    /**
     * cpu核数
     */
    private List<MetricVO> cpuCounts = new ArrayList<>();

    //jvm_mem
    /**
     * jvm_mem使用
     */
    private List<MetricVO> jvmMemUtils = new ArrayList<>();

    /**
     * jvm_mem 使用大小
     */
    private List<MetricVO> jvmMemUses = new ArrayList<>();

    /**
     * jvm_mem 使用最大值
     */
    private List<MetricVO> jvmMemMax = new ArrayList<>();

    /**
     * jvm_mem 容量
     */
    private List<MetricVO> jvmMemCapacity = new ArrayList<>();

    //system_mem
    /**
     * system_mem 使用比例
     */
    private List<MetricVO> systemMemUtils = new ArrayList<>();

    /**
     * system_mem 使用量
     */
    private List<MetricVO> systemMemUses = new ArrayList<>();

    /**
     * system_mem 使用最大值
     */
    private List<MetricVO> systemMemMax = new ArrayList<>();

    /**
     * system_mem 使用容量
     */
    private List<MetricVO> systemMemCapacity = new ArrayList<>();


    //新生代大小
    /**
     * 新生代最小容量
     */
    private List<MetricVO> ngcmn = new ArrayList<>();

    /**
     * 新生代最大容量
     */
    private List<MetricVO> ngcmx = new ArrayList<>();

    /**
     * 当前新生代容量
     */
    private List<MetricVO> ngc = new ArrayList<>();

    /**
     * 生存0区大小
     */
    private List<MetricVO> soc = new ArrayList<>();

    /**
     * 生存1区大小
     */
    private List<MetricVO> sc = new ArrayList<>();

    /**
     * 生存0区使用
     */
    private List<MetricVO> sou = new ArrayList<>();

    /**
     * 生存1区大小
     */
    private List<MetricVO> su = new ArrayList<>();

    /**
     * 伊甸区大小
     */
    private List<MetricVO> ec = new ArrayList<>();

    /**
     * 伊甸区使用
     */
    private List<MetricVO> eu = new ArrayList<>();


    // 老年代大小
    /**
     * 老年代大小
     */
    private List<MetricVO> oc = new ArrayList<>();

    /**
     * 老年代使用
     */
    private List<MetricVO> ou = new ArrayList<>();

    /**
     * 老年代最小容量
     */
    private List<MetricVO> ogcmn = new ArrayList<>();

    /**
     * 老年代最大容量
     */
    private List<MetricVO> ogcmx = new ArrayList<>();

    //元空间大小与使用
    /**
     * 元空间大小
     */
    private List<MetricVO> mc = new ArrayList<>();

    /**
     * 元空间使用
     */
    private List<MetricVO> mu = new ArrayList<>();

    /**
     * 最小元空间容量
     */
    private List<MetricVO> mcmn = new ArrayList<>();

    /**
     * 最大元空间容量
     */
    private List<MetricVO> mcmx = new ArrayList<>();

    //压缩类空间
    /**
     * 压缩类空间大小
     */
    private List<MetricVO> ccsc = new ArrayList<>();

    /**
     * 压缩类使用
     */
    private List<MetricVO> ccsu = new ArrayList<>();

    /**
     * 最小压缩类空间大小
     */
    private List<MetricVO> ccsmn = new ArrayList<>();

    /**
     * 最大压缩类空间大小
     */
    private List<MetricVO> ccsmx = new ArrayList<>();

    //代码缓冲区大小
    /**
     * 代码缓存区使用
     */
    private List<MetricVO> codeCacheUses = new ArrayList<>();

    /**
     * 代码缓冲区最大
     */
    private List<MetricVO> codeCacheMax = new ArrayList<>();

    /**
     * 代码缓冲区使用率
     */
    private List<MetricVO> codeCacheUtils = new ArrayList<>();


    //GC相关参数
    /**
     * fgc_Count
     */
    private List<MetricVO> fgcCounts = new ArrayList<>();

    /**
     * fgc_Time
     */
    private List<MetricVO> fgcTimes = new ArrayList<>();

    /**
     * ygc_Count
     */
    private List<MetricVO> ygcCounts = new ArrayList<>();

    /**
     * ygc_Time
     */
    private List<MetricVO> ygcTimes = new ArrayList<>();

    /**
     * gc_Time
     */
    private List<MetricVO> gcTimes = new ArrayList<>();

    // safePoint相关参数
    /**
     * safePoint 时间
     */
    private List<MetricVO> safePointTime = new ArrayList<>();

    /**
     * safePoint 次数
     */
    private List<MetricVO> safePointCount = new ArrayList<>();

    //线程相关参数
    /**
     * 活动线程数
     */
    private List<MetricVO> threadCount = new ArrayList<>();

    /**
     * 活动峰值线程数
     */
    private List<MetricVO> peakThreadCount = new ArrayList<>();

    /**
     * 守护线程数
     */
    private List<MetricVO> daemonThreadCount = new ArrayList<>();

    /**
     * 死锁线程数
     */
    private List<MetricVO> deadLockedCount = new ArrayList<>();


    //类相关参数
    /**
     * 已加载类总数
     */
    private List<MetricVO> totalLoadedClassCount = new ArrayList<>();

    /**
     * 已加载当前类数量
     */
    private List<MetricVO> loadedClassCount = new ArrayList<>();

    /**
     *  已卸载类总数
     */
    private List<MetricVO> unloadedClassCount = new ArrayList<>();


    /**
     * cpuList
     */
    private List<MetricVO> cpuLists = new ArrayList<>();

    /**
     * mem limit
     */
    private List<MetricVO> memLimit = new ArrayList<>();

    /**
     * mem usage
     */
    private List<MetricVO> memUsage = new ArrayList<>();

    /**
     * mem cache
     */
    private List<MetricVO> memCache = new ArrayList<>();

    /**
     * mem totalUsage
     */
    private List<MetricVO> memTotalUsage = new ArrayList<>();

    /**
     * major memory page faults
     */
    private List<MetricVO> pgMajFault = new ArrayList<>();

    /**
     * 热编译时间
     */
    private List<MetricVO> jvmJitTime = new ArrayList<>();
}