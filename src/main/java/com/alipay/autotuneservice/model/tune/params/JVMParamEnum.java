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
package com.alipay.autotuneservice.model.tune.params;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangkaifei
 * @version : JVMParamEnum.java, v 0.1 2022年05月17日 9:28 PM huangkaifei Exp $
 */
public enum JVMParamEnum {
    Xms("-Xms", "初始堆大小"),

    Xmx("-Xmx", "最大堆大小"),

    Xss("-Xss", "每个线程的堆栈大小"),

    Xmn("-Xmn", "年轻代大小(1.4or lator)整个JVM内存大小=年轻代大小 + 年老代大小 + 持久代大小。持久代一般固定大小为64m，所以增大年轻代后，将会减小年老代大小。此值对系统性能影响较大，Sun官方推荐配置为整个堆的3/8"),

    MetaSpace("-XX:MetaspaceSize", "Metaspace 空间初始大小"),

    MaxMetaSpace("-XX:MaxMetaspaceSize", "Metaspace 最大值，默认不限制大小"),

    NewSize("-XX:NewSize", "新生代初始内存的大小,应该小于-Xms的值"),

    MaxNewSize("-XX:MaxNewSize", "表示新生代可被分配的内存的最大上限"),

    PermSize("-XX:PermSize", "表示非堆区初始内存分配大小,设置持久代(perm gen)初始值"),

    MaxPermSize("-XX:MaxPermSize", "表示对非堆区分配的内存的最大上限"),

    InitiatingHeapOccupancyPercent("-XX:InitiatingHeapOccupancyPercent",
            "设置触发标记周期的 Java 堆占用率阈值,就是说当使用内存占到堆总大小的InitiatingHeapOccupancyPercent%的时候，G1将开始并发标记阶段"),

    G1HeapRegionSize("-XX:G", "设置的 G1 区域的大小"),

    UseZenGC("-XX:+UseZenGC", "使用ZenGC，本质上就是G1，做了一些优化"),

    ZenGCElasticHeap("-XX:+ZenGCElasticHeap", "可以实现了通过动态调整可回收部分Java堆的大小，从而达到减少内存使用的目的"),

    UseWisp("-XX:+UseWisp", "开启 wisp 协程功能"),

    D__sofa__rpc_min_pool_size_tr("-D__sofa__rpc_min_pool_size_tr", "核心线程数大小"),

    Drpc_pool_queue_size_tr("-Drpc_pool_queue_size_tr=197", "线程池队列长度"),

    UseParNewGC("-XX:+UseParNewGC", "Parallel是并行的意思，ParNew收集器是Serial收集器的多线程版本，使用这个参数后会在新生代进行并行回收，老年代仍旧使用串行回收"),

    UseParallelGC("-XX:+UseParallelGC", "代表新生代使用Parallel收集器，老年代使用串行收集器"),

    UseConcMarkSweepGC("-XX:+UseConcMarkSweepGC", "Concurrent Mark Sweep 并发标记清除，即使用CMS收集器"),

    UseCMSCompactAtFullCollection("-XX:+UseCMSCompactAtFullCollection", "Full GC后，进行一次整理，整理过程是独占的，会引起停顿时间变长。仅在使用CMS收集器时生效"),

    CMSInitiatingOccupancyFraction("-XX:CMSInitiatingOccupancyFraction", "指在使用CMS收集器的情况下，老年代使用了指定阈值的内存时，触发FullGC"),

    server("-server",
            "启动模式,-Server模式启动时，速度较慢，但是一旦运行起来后，性能将会有很大的提升;JVM如果不显式指定是-Server模式还是-client模式，JVM能够根据下列原则进行自动判断（适用于Java5版本或者Java以上版本）"),

    Dce_monitor_interval("-Dce_monitor_interval", "ce 监控日志打印速度(次/ms)"),

    MaxDirectMemorySize("-XX:MaxDirectMemorySize",
            "JVM堆内存大小可以通过-Xmx来设置，同样的direct ByteBuffer可以通过-XX:MaxDirectMemorySize来设置，此参数的含义是当Direct ByteBuffer分配的堆外内存到达指定大小后，即触发Full GC。"),

    ParallelGCThreads("-XX:ParallelGCThreads", "这个参数是指定并行GC 线程的数量，一般最好和CPU 核心数量相当。"),

    PrintGCDetails("-XX:+PrintGCDetails", "用于打印输出详细的GC收集日志的信息"),

    verbose("-verbose:gc", "表示输出虚拟机中GC的详细情况"),

    G1("-XX:+UseG1GC", "G1垃圾回收器"),

    JVM_MARKET_ID("-DJvmMarketId", "jvm参数版本号"),

    UNKNOWN("unKnown", "未知参数");

    @Getter
    private String fullName;

    @Getter
    private String desc;

    JVMParamEnum(String fullName, String desc) {
        this.fullName = fullName;
        this.desc = desc;
    }

    public static final List<JVMParamEnum> GC_COLLECTORS        = ImmutableList.of(G1, UseZenGC, UseConcMarkSweepGC);
    public static final List<JVMParamEnum> INITIATING_OCCUPANCY = ImmutableList.of(CMSInitiatingOccupancyFraction,
            InitiatingHeapOccupancyPercent);

    public static final List<JVMParamEnum> X_TYPE_PARAM               = ImmutableList.of(Xms, Xmx, Xss, Xmn);
    public static final List<JVMParamEnum> EQUAL_SIGN_TYPE_TYPE_PARAM = ImmutableList.of(MetaSpace, MaxMetaSpace, PermSize, MaxPermSize,
            NewSize, MaxNewSize, InitiatingHeapOccupancyPercent, JVM_MARKET_ID);
    public static final List<JVMParamEnum> PLUS_SIGN_TYPE_TYPE_PARAM  = ImmutableList.of(UseZenGC, UseWisp, UseParallelGC, UseZenGC, G1,
            PrintGCDetails);

    // ---------------------------- param order ------------------------------------------- //
    private static final Integer SERVER_TYPE_PARAM_ORDER          = 1;
    // 规定-X类型调优参数为第二level，顺序为200： -Xms, Xmn, Xss ..
    private static final Integer X_TYPE_PARAM_ORDER               = 200;
    // 规定-XX类型调优参数为第三level，顺序为300： -XX:xx ...
    private static final Integer XX_TYPE_PARAM_ORDER              = 300;
    // 规定 -XX:+类型为第四level， 顺序为400
    private static final Integer XX_PLUS_TYPE_PARAM_ORDER         = 400;
    private static final Integer GC_COLLECTORS_PARAM_ORDER        = 500;
    private static final Integer INITIATING_OCCUPANCY_PARAM_ORDER = 600;
    private static final Integer UNKNOWN_ORDER_TYPE_PARAM_ORDER   = 1000;

    private static final Map<JVMParamEnum, Integer> TUNE_PARAM_ORDER_MAP = new ConcurrentHashMap<>();

    static {
        Arrays.asList(values()).forEach(item -> {
            if (item == server) {
                TUNE_PARAM_ORDER_MAP.put(item, SERVER_TYPE_PARAM_ORDER);
                return;
            }
            if (X_TYPE_PARAM.contains(item)) {
                TUNE_PARAM_ORDER_MAP.put(item, X_TYPE_PARAM_ORDER);
                return;
            }
            if (EQUAL_SIGN_TYPE_TYPE_PARAM.contains(item)) {
                TUNE_PARAM_ORDER_MAP.put(item, XX_TYPE_PARAM_ORDER);
                return;
            }
            if (PLUS_SIGN_TYPE_TYPE_PARAM.contains(item)) {
                TUNE_PARAM_ORDER_MAP.put(item, XX_PLUS_TYPE_PARAM_ORDER);
                return;
            }
            if (GC_COLLECTORS.contains(item)) {
                TUNE_PARAM_ORDER_MAP.put(item, GC_COLLECTORS_PARAM_ORDER);
                return;
            }
            if (INITIATING_OCCUPANCY.contains(item)) {
                TUNE_PARAM_ORDER_MAP.put(item, INITIATING_OCCUPANCY_PARAM_ORDER);
                return;
            }
            // 未知顺序设置顺序为1000
            TUNE_PARAM_ORDER_MAP.put(item, UNKNOWN_ORDER_TYPE_PARAM_ORDER);
        });
    }

    public static JVMParamEnum valueOfFullName(String param) {
        for (JVMParamEnum o : JVMParamEnum.values()) {
            if (o.getFullName().equals(param)) {
                return o;
            }
        }
        throw new UnsupportedOperationException(String.format("input param=%s is now unsupported.", param));
    }

    public static JVMParamEnum collectorMatcher(String param) {
        JVMParamEnum paramDesEnum = valueOfFullName(param);
        if (GC_COLLECTORS.contains(paramDesEnum)) {
            return paramDesEnum;
        }
        return UNKNOWN;
    }

    public static Map<JVMParamEnum, String> initiatingOccupancyMatcher(String param) {

        JVMParamEnum paramDesEnum = valueOfFullName(param.split("=")[0]);
        if (INITIATING_OCCUPANCY.contains(paramDesEnum)) {
            Map<JVMParamEnum, String> res = new HashMap<>();
            res.put(paramDesEnum, param.split("=")[1]);
            return res;
        }
        return null;
    }

    public static JVMParamEnum match(String jvmOption) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(jvmOption), "jvmOption can not be empty.");
            for (JVMParamEnum jvmParamEnum : values()) {
                if (jvmOption.startsWith(String.format("%s=", jvmParamEnum.fullName))) {
                    return jvmParamEnum;
                }
                if (jvmOption.startsWith(jvmParamEnum.fullName)) {
                    return jvmParamEnum;
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return UNKNOWN;
    }

    public static int getParamOrder(String param) {
        return TUNE_PARAM_ORDER_MAP.get(match(param));
    }
}