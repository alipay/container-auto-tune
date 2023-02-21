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
package com.alipay.autotuneservice.service.algorithmlab;

import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DangeLevelEnum;
import lombok.Getter;

import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.BUSINESS;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.CODE;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.CPU;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.DISK;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.ERROR;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.GC;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.JVM;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.MEM;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.NETWORK;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisEnum.THREAD;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisServiceEnum.COST;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisServiceEnum.PERFORMANCE;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisServiceEnum.SAFETY;
import static com.alipay.autotuneservice.service.algorithmlab.DiagnosisServiceEnum.STABILITY;
import static com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DangeLevelEnum.DISASTER;
import static com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DangeLevelEnum.WARN;

/**
 * @author hognshu
 * @version HealthCheckEnum.java, v 0.1 2022年10月26日 11:17 下午 hognshu
 */
@Getter
public enum ProblemMetricEnum {

    /**
     * container metrics
     */
    /**
     * CPU
     */
    // "CPU使用率高"
    CPU_UTIL_HIGH("C0001", "CPU_UTIL_HIGH:max>65% && avg>40%", "cpu usage is high", CPU, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "CPU使用率低"
    CPU_UTIL_LOW("C0001", "CPU_UTIL_LOW:max<45% && avg<30%", "cpu usage is low", CPU, COST, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "CPU使用率突增"
    CPU_UTIL_BURST("C0001", "CPU_UTIL_BURST:increase 30% in 5m", "cpu usage sudden increase", CPU, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "CPU使用率突降"
    CPU_UTIL_REDUCE("C0001", "CPU_UTIL_REDUCE:decrease 30% in 5m", "cpu usage sudden decrease", CPU, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),

    // 用户cpu使用
    // 用户CPU使用率高
    CPU_USER_UTIL_HIGH("C0001", "CPU_USER_UTIL_HIGH:use_cpu/(use+sys)>90%", "cpu usage is low", CPU, COST, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 用户CPU使用率低
    CPU_USER_UTIL_LOW("C0001", "CPU_USER_UTIL_HIGH:use_cpu/(use+sys)<60%", "cpu usage is low", CPU, COST, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 用户CPU使用率突增
    CPU_USER_UTIL_BURST("C0001", "CPU_USER_UTIL_BURST:increase 30% in 5m", "cpu usage sudden increase", CPU, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 用户CPU使用率突降
    CPU_USER_UTIL_REDUCE("C0001", "CPU_USER_UTIL_REDUCE:decrease 30% in 5m", "user cpu use normal", CPU, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),

    /**
     * mem
     */
    // "MEM使用率高"
    MEM_UTIL_HIGH("C0001", "MEM_UTIL_HIGH:max>90% && avg>60%", "memory usage is high", MEM, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "MEM使用率低"
    MEM_UTIL_LOW("C0001", "MEM_UTIL_LOW:max<50% && avg<30%", "memory usage is low", MEM, COST, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "MEM使用率突增"
    MEM_UTIL_BURST("C0001", "MEM_UTIL_BURST:increase 30% in 30s", "memory usage sudden increase", MEM, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "MEM使用率突降"
    MEM_UTIL_REDUCE("C0001", "MEM_UTIL_REDUCE:decrease 30% in 30s", "memory usage sudden decrease", MEM, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 内存泄漏检测
    MEM_LEAK("C0001", "MEM_UTIL_REDUCE:mem increase continuously in statistical period", "there is a mem leak", MEM, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 内存碎片率高
    MEM_FRAGMENT_HIGH("C0001", "MEM_FRAGMENT_HIGH:max rate>15%", "memory fragmentation rate high", MEM, PERFORMANCE, "reason", "Please check your code logic or db or third-party interface", WARN),
    // TLB miss high
    MEM_TLB_MISS_HIGH("C0001", "MEM_TLB_MISS_HIGH:hit miss rate>85%", "TLB miss high", MEM, PERFORMANCE, "reason", "Please check your code logic or db or third-party interface", WARN),
    // swap partition
    MEM_SWAP_OPEN("C0001", "MEM_SWAP_OPEN:swap partition is open", "swap is closed or not", MEM, PERFORMANCE, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 内存缓冲区是否合理
    MEM_CACHE_ERROR("C0001", "MEM_CACHE_ERROR:cache page size < 2M", "memory cache use reasonable", MEM, PERFORMANCE, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 虚拟内存使用是否合理
    MEM_VIRTUAL_ERROR("C0001", "MEM_CACHE_ERROR:cache_size/total_memory  > 100%", "memory virtual use reasonable", MEM, PERFORMANCE, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 大对象
    MEM_BIG_OBJ("C0001", "MEM_BIG_OBJ:object_size > 400M", "big object exist", MEM, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // 对象数过多
    MEM_OBJ_COUNT_GREAT("C0001", "MEM_OBJ_COUNT_GREAT:single object count > 1 millions", "too many items", MEM, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),


    /**
     * disk
      */
    DISK_IO_UTIL_HIGH("JG004", "DISK_IO_UTIL_HIGH:disk_cpu usage/sys_cpu>30%", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    DISK_IO_UTIL_LOW("JG004", "DISK_IO_UTIL_LOW:disk io idle>80%", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    DISK_IO_UTIL_BURST("JG004", "DISK_IO_UTIL_BURST:increase 30% in 5m", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    DISK_IO_UTIL_REDUCE("JG004", "DISK_IO_UTIL_REDUCE:decrease 30% in 5m", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    DISK_UTIL_HIGH("JG004", "DISK_UTIL_HIGH:disk usage max>90% && avg>60%", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    DISK_UTIL_LOW("JG004", "DISK_UTIL_LOW:disk usage max<50% && avg<30%", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    DISK_FAILURE("JG004", "DISK_FAILURE:hirq_cpu max > 50%", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),

    /**
     * thread
      */
    THREAD_COUNT_HIGH("JG004", "THREAD_COUNT_HIGH:thread count>2k", "there is a thread deadlock", THREAD, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    THREAD_COUNT_BURST("JG004", "THREAD_COUNT_BURST:thread count increase 30% in 30s", "there is a thread deadlock", THREAD, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    THREAD_BLOCK_BURST("JG004", "THREAD_BLOCK_BURST:thread block increase 30% in 30s", "there is a thread deadlock", THREAD, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    THREAD_DEADLOCK("JG004", "THREAD_DEADLOCK:thread deadLock>1", "there is a thread deadlock", THREAD, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    THREAD_POOL_COUNT_HIGH("JG004", "THREAD_POOL_COUNT_HIGH:thread pool count>60", "there is a thread deadlock", THREAD, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    THREAD_POOL_DISCARD_P_MODE("JG004", "THREAD_POOL_COUNT_HIGH:DiscardPolicy mode count>1", "there is a thread deadlock", THREAD, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),

    /**
     * network
     */
    NET_TCP_CON_HIGH("JG004", "NET_TCP_CON_HIGH:tcp connections count>20K", "tcp connection is high", NETWORK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    NET_TCP_CON_BURST("JG004", "NET_TCP_CON_BURST:tcp connections increase 30% in 30s", "tcp connection is high", NETWORK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    NET_TCP_CON_REDUCE("JG004", "NET_TCP_CON_REDUCE:decrease 30% in 30s", "tcp connection is high", NETWORK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    NET_TIME_WAIT_HIGH("JG004", "NET_TIME_WAIT_HIGH:time wait>500", "tcp connection is high", NETWORK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    NET_TCP_RE_TRANS_HIGH("JG004", "NET_TCP_RE_TRANS_HIGH:re_trans rate>30%", "tcp connection is high", NETWORK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    NET_IO_UTIL_BURST("JG004", "NET_IO_UTIL_BURST:increase 30% in 5m", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    NET_IO_UTIL_REDUCE("JG004", "NET_IO_UTIL_REDUCE:decrease 30% in 5m", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    NET_IO_UTIL_HIGH("JG004", "NET_IO_UTIL_HIGH:net io usage/sys_cpu>30%", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    NET_IO_UTIL_LOW("JG004", "NET_IO_UTIL_LOW:net io idle>80%", "disk  usage is high", DISK, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),



    /**
     * gc
     */
    // "YGC次数高"
    YGC_COUNT_HIGH("JG004", "YGC_COUNT_HIGH:young gc count>10/min", "YGC count is high", GC, PERFORMANCE, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "YGC次数", "YGC次数正常", "检测YGC次数增涨过多"
    YGC_COUNT_BURST("JG001", "YGC_COUNT_BURST:increase 30% in 5m", "YGC count anomaly", GC, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    // "YGC时间", "YGC耗时正常", "检测出YGC时间过长"
    // 减少年轻代空间大小，减小堆空间大小，
    YGC_TIME_GREAT("JG002", "YGC_TIME_GREAT:young gc time>1s", "YGC takes a long time", GC, STABILITY, "reason", "If the CMS algorithm is used, the size of the Eden area can be appropriately reduced or G1 can be used; " +
            "if G1 is used, please adjust the size of -XX:MaxGCPauseMillis", WARN),
    // "FGC次数高"
    FGC_COUNT_HIGH("JG004", "FGC_COUNT_HIGH:full gc count>2 per hour", "FGC count is high", GC, STABILITY, "reason", "Please check the metaspace, old generation size, object promotion rate, and whether there is a large object promotion in order", DISASTER),
    // "FGC次数", "FGC次数正常", "检测FGC次数增涨过多"
    FGC_COUNT_BURST("JG003", "FGC_COUNT_BURST:increase 30% in 5m", "FGC count anomaly", GC, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    // "FGC时间", "FGC耗时正常", "检测出FGC时间过长"
    FGC_TIME_GREAT("JG004", "FGC_TIME_GREAT:full gc time>3s", "FGC takes a long time", GC, STABILITY, "reason", "Please check the most time-consuming steps such as parallel collection failure or long STW time events, etc", WARN),
    // "HEAP使用率高"
    HEAP_UTIL_HIGH("JG004", "HEAP_UTIL_HIGH:heap util max>90%", "heap usage is high", GC, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "HEAP使用率低"
    HEAP_UTIL_LOW("C0001", "HEAP_UTIL_LOW:heap util max<50%", "heap usage is low", GC, COST, "reason", "If you want to save resources, you can reduce the heap space appropriately", WARN),
    // "HEAP使用率突增"
    HEAP_UTIL_BURST("C0001", "HEAP_UTIL_BURST:increase 30% in 1m", "heap usage sudden increase", GC, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "old区使用率", "使用率正常", "检测出使用率过频"
    OLD_UTIL_HIGH("JG004", "OLD_UTIL_HIGH:old util avg>60%", "old usage is high", GC, STABILITY, "reason", "Please increase the old generation,watch out zombie data", WARN),
    // "old 空间使用", "使用old空间合理", "使用old空间不合理"
    OLD_UTIL_LOW("JG004", "OLD_UTIL_LOW:old util max<60%", "old  usage is low", GC, COST, "reason", "If you want to save resources, you can reduce the old generation appropriately", WARN),
    // "HEAP使用率突增"
    OLD_UTIL_BURST("C0001", "OLD_UTIL_BURST:increase 40% in 1m", "old usage sudden increase", GC, STABILITY, "reason", "If qps does not increase suddenly, maybe there is a big object promotion", WARN),
    // "young区使用率", "使用率正常", "检测出使用率过频"
    YOUNG_UTIL_HIGH("JG004", "normalDesc", "young usage is high", GC, STABILITY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    // "young空间使用", "使用young空间合理", "使用young空间不合理JVM
    YOUNG_UTIL_LOW("JG004", "YOUNG_UTIL_LOW:young gc interval > 10m", "young  usage is low", GC, COST, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    // "young使用率突增"
    YOUNG_UTIL_BURST("C0001", "YOUNG_UTIL_BURST:young gc interval reduce 30% in 5m", "young usage sudden increase", GC, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "垃圾回收器", "使用回收器合理", "使用回收器异常"
    GC_TYPE_UNREASONABLE("JG004", "GC_TYPE_UNREASONABLE:gc throughput or rt not match", "Collector usage is unreasonable", GC, STABILITY, "reason", "Switching to G1 may be a better choice", WARN),
    // "大对象晋升"
    BIG_OBJECT_PROMOTION("JG004", "BIG_OBJECT_PROMOTION:one promotion size > 500M", "big object promotion from eden to old", GC, PERFORMANCE, "reason", "please adjust your young generation or troubleshoot your code to determine the cause of large objects", WARN),


    /**
     * JVM
     */
    // "meta空间使用", "使用meta空间合理", "使用meta空间不合理"
    // "进入安全点时间长"
    SAFE_POINT_LONG("JG004", "SAFE_POINT_LONG:enter safe point time max > 50ms", "safe point time is high", JVM, PERFORMANCE, "reason", "Please pay attention to the thread that enters Safepoint slowly, modify the code such as int loop", DISASTER),
    META_UTIL_LOW("JG004", "META_UTIL_LOW:meta util max<40%", "metaspace usage is low", JVM, COST, "reason", "If you want to save resources, you can reduce the metaspace appropriately", WARN),
    // "HEAP使用率突增"
    META_UTIL_BURST("C0001", "META_UTIL_BURST:increase 30% in 1m", "metaspace usage sudden increase", JVM, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // "old区使用率", "使用率正常", "检测出使用率过频"
    META_UTIL_HIGH("JG004", "META_UTIL_HIGH:meta util max>80%", "metaspace usage is high", JVM, STABILITY, "reason", "Please increase the metaspace", WARN),
    // "堆内存溢出"
    OUT_OF_MEMORY("JG004", "OUT_OF_MEMORY:oom exception total>1", "There is a heap memory overflow", JVM, STABILITY, "reason", "Memory leaks in code or increase heap space", WARN),
    // "java版本低"
    JDK_VERSION_LOW("JG004", "JDK_VERSION_LOW:jdk version<1.7", "Jdk version is low", JVM, PERFORMANCE, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    // "MaxGCPauseMillis配置不合理"
    MAX_GC_PAUSE_MILLIS_UNREASONABLE("JG004", "MAX_GC_PAUSE_MILLIS_UNREASONABLE:pause time>rt", "MaxGCPauseMillis is unreasonable", JVM, PERFORMANCE, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    // "-Xmx/-Xms配置不同"
    Xmx_Xms_DIFF("JG004", "Xmx_Xms_DIFF:xmx diff with xms", "-Xmx/-Xms is different", JVM, PERFORMANCE, "reason", "Keep -Xmx/-Xms the same may reduce the allocation and release of operating system resources", WARN),
    // "-Xmx/-Xms配置不同"
    MaxNewSize_NewSize_DIFF("JG004", "MaxNewSize_NewSize_DIFF:MaxNewSize diff with NewSize", "-XX:MaxNewSize /-XX:NewSize is different", JVM, PERFORMANCE, "reason", "Keep -XX:MaxNewSize /-XX:NewSize the same may reduce the overhead of young generation adjustments", WARN),
    // "-Xmx/-Xms配置不同"
    MetaSpaceSize_MaxMetaSpaceSize_DIFF("JG004", "MetaSpaceSize_MaxMetaSpaceSize_DIFF", "-XX:MetaSpaceSize/-XX:MaxMetaSpaceSize is different", JVM, PERFORMANCE, "reason", "Keep -XX:MetaSpaceSize/-XX:MaxMetaSpaceSize the same may reduce the overhead of metaspace on jvm adjustments", WARN),
    // "PrintGCApplicationStoppedTime配置不合理"
    PrintGCApplicationStoppedTime_LOSS("JG004", "PrintGCApplicationStoppedTime: loss", "PrintGCApplicationStoppedTime should be config", JVM, COST, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    // "stop the world时间太大"
    STOP_THE_WORLD_LONG("JG004", "STOP_THE_WORLD_LONG: stw>1s", "stop the world time too long", JVM, STABILITY, "reason", "Please check GenCollectForAllocation or SafePoint time", WARN),
    // "ReservedCodeCacheSize配置太小"
    ReservedCodeCacheSize_SMALL("JG004", "ReservedCodeCacheSize_SMALL: usage>80%", "ReservedCodeCacheSize too small", JVM, PERFORMANCE, "reason", "Increase the size of the code cache by param reservedCodeCacheSize", WARN),
    // "SurvivorRatio配置不合理"
    SurvivorRatio_SMALL("JG004", "SurvivorRatio_SMALL: ygc frequently and no fgc in 2day", "SurvivorRatio config not reasonable", JVM, PERFORMANCE, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),
    // "G1ReservePercent配置不合理"
    G1ReservePercent_SMALL("JG004", "G1ReservePercent_SMALL: fgc > 0", "G1ReservePercent config not reasonable", JVM, PERFORMANCE, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),



    /**
     * business metrics
     */
    // RT
    BUS_RT_HIGH("B0001", "BUS_RT_HIGH: rt(default) > 500ms", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_RT_BURST("B0001", "BUS_RT_BURST:rt increase 30% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_RT_REDUCE("B0001", "BUS_RT_REDUCE:rt decrease 30% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // QPS
    BUS_QPS_BURST("B0001", "BUS_QPS_BURST:qps increase 30% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_QPS_REDUCE("B0001", "BUS_QPS_REDUCE:qps decrease 30% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // SUCCESS RATE
    BUS_SUCCESS_RATE_LOW("B0001", "BUS_SUCCESS_RATE_LOW: rate(default) < 99%", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_SUCCESS_RATE_BURST("B0001", "BUS_SUCCESS_RATE_BURST: rate increase 3% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_SUCCESS_RATE_REDUCE("B0001", "BUS_SUCCESS_RATE_REDUCE: rate decrease 3% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    // INDICATOR  业务指标
    BUS_INDICATOR_ERROR_HIGH("B0001", "BUS_INDICATOR_ERROR_HIGH: business error > 3/s", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_INDICATOR_ERROR_BURST("B0001", "BUS_INDICATOR_ERROR_BURST: error count increase 30% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_INDICATOR_ERROR_REDUCE("B0001", "BUS_INDICATOR_ERROR_REDUCE: error count decrease 30% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_INDICATOR_BURST("B0001", "BUS_INDICATOR_BURST: count increase 30% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    BUS_INDICATOR_REDUCE("B0001", "BUS_INDICATOR_REDUCE: count decrease 30% in 5m", "RT takes a long time", BUSINESS, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),

    /**
     * code metric
     */
    CODE_JAR_EXPIRED("C0001", "CODE_JAR_EXPIRED: expired jar depend > 0", "error sudden increase", CODE, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    CODE_JAR_SEC_BREACH("C0001", "CODE_JAR_SEC_BREACH: security hole jar depend > 0", "error sudden increase", CODE, SAFETY, "reason", "Please check your code logic or db or third-party interface", WARN),
    CODE_JAR_CONFLICT("JG004", "CODE_JAR_CONFLICT: version diff in pom and depend > 0", "disk  usage is high", CODE, SAFETY, "reason", "If qps does not increase suddenly, there may be a problem with the system", WARN),


    /**
     * error
     */
    // "error突增"
    ERROR_BURST("C0001", "ERROR_BURST: error increase 30% in 5m", "error sudden increase", ERROR, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    ERROR_COUNT_HIGH("C0001", "ERROR_COUNT_HIGH: error > 5/s", "error sudden increase", ERROR, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),
    THIRD_PARTY_ERROR_HIGH("C0001", "THIRD_PARTY_ERROR_HIGH: third party error > 3/min", "error sudden increase", ERROR, STABILITY, "reason", "Please check your code logic or db or third-party interface", WARN),





    /**
     * 未知类型枚举
     */
    UNKNOWN("unknown", "normalDesc", "unknown problem", DiagnosisEnum.UNKNOWN, STABILITY, "reason", "unknown problem, please contact customer xiaomi", WARN),
    ;
    /**
     * container metrics， Cxxxx
     * jvm metrics, Jxxxx,
     * jvm-gc JGxxx
     * jvm-oth JOxxx
     * business metrics Bxxxx
     */
    private final String code;
    private final String desc;
    private final String problemDesc;

    private final DiagnosisEnum groupType;

    private final DiagnosisServiceEnum serviceType;

    private final String reason;
    /**
     * expert experience for problem
     */
    private final String expert;
    
    private final DangeLevelEnum dangeLevelEnum;
    

    ProblemMetricEnum(String code, String desc, String problemDesc, DiagnosisEnum groupType,
                      DiagnosisServiceEnum serviceType, String reason, String expert, DangeLevelEnum dangeLevelEnum) {
        this.code = code;
        this.desc = desc;
        this.problemDesc = problemDesc;
        this.groupType = groupType;
        this.serviceType = serviceType;
        this.reason = reason;
        this.expert = expert;
        this.dangeLevelEnum = dangeLevelEnum;
    }


}