/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dynamodb.bean;

import lombok.Data;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetric.java, v 0.1 2022年04月13日 4:08 PM huangkaifei Exp $
 */
@Data
public class JvmMonitorMetricData {

    private long    id;
    //cpu核数
    private long    cpuCount;
    //系统cpu利用率
    private double  systemCpuLoad;
    //用户cpu利用率
    private double  processCpuLoad;
    //集群名
    private String  cluster;
    private long    period;
    /**
     * pod name
     */
    private String  pod;//node 名
    private long    dt;//天分区
    /**
     * app Id
     **/
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
    // jvm_mem
    private double  jvm_mem_util;
    private double  jvm_mem_used;
    private double  jvm_mem_max;
    private double  jvm_mem_capacity;
    // system_mem
    private double  system_mem_util;
    private double  system_mem_used;
    private double  system_mem_max;
    private double  system_mem_capacity;
    // ygc
    private long    ygc_count;
    private double  ygc_time;
    // fgc
    private long    fgc_count;
    private double  fgc_time;
    //----
    //第一个幸存区大小
    private double  s0c;
    //第二个幸存区大小
    private double  s1c;
    //第一个幸存区使用大小
    private double  s0u;
    //第二个幸存区使用大小
    private double  s1u;
    //eden区大小
    private double  ec;
    //eden区使用大小
    private double  eu;
    //老年代大小
    private double  oc;
    //老年代使用大小
    private double  ou;
    //方法区大小
    private double  mc;
    //方法区使用大小
    private double  mu;
    //压缩类空间大小
    private double  ccsc;
    //压缩类空间使用大小
    private double  ccsu;
    //年轻代垃圾回收次数
    private int     ygc;
    //年轻代垃圾回收消耗时间
    private double  ygct;
    //老年代垃圾回收次数
    private int     fgc;
    //老年代回收消耗时间
    private double  fgct;
    //垃圾回收消耗总时间
    private double  gct;
    //新生代最小容量
    private double  ngcmn;
    //新生代最大容量
    private double  ngcmx;
    //当前新生代容量
    private double  ngc;
    //老年代最小容量
    private double  ogcmn;
    //老年代最大容量
    private double  ogcmx;
    //当前老年代大小
    private double  ogc;
    //最小元数据容量
    private double  mcmn;
    //最大元数据容量
    private double  mcmx;
    //最小压缩类空间大小
    private double  ccsmn;
    //最大压缩类空间大小
    private double  ccsmx;
    //代码缓冲区使用大小
    private long    codeCacheUsed;
    //代码缓冲区最大
    private long    codeCacheMax;
    //代码缓冲区使用率
    private double  codeCacheUtil;

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