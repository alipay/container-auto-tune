/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataRepositoryImpl.java, v 0.1 2022年10月31日 9:10 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class JvmMonitorMetricRepositoryImpl extends BaseDao implements JvmMonitorMetricRepository {

    @Override
    public void insertGCData(JvmMonitorMetricData jvmMonitorMetricData) {
        //插入表数据
        mDSLContext.insertInto(Tables.JVM_MONITOR_METRIC)
                .set(Tables.JVM_MONITOR_METRIC.CPUCOUNT, jvmMonitorMetricData.getCpuCount())
                .set(Tables.JVM_MONITOR_METRIC.SYSTEMCPULOAD, jvmMonitorMetricData.getSystemCpuLoad())
                .set(Tables.JVM_MONITOR_METRIC.PROCESSCPULOAD, jvmMonitorMetricData.getProcessCpuLoad())
                .set(Tables.JVM_MONITOR_METRIC.CLUSTER, jvmMonitorMetricData.getCluster())
                .set(Tables.JVM_MONITOR_METRIC.PERIOD, jvmMonitorMetricData.getPeriod())
                .set(Tables.JVM_MONITOR_METRIC.POD, jvmMonitorMetricData.getPod())
                .set(Tables.JVM_MONITOR_METRIC.DT, jvmMonitorMetricData.getDt())
                .set(Tables.JVM_MONITOR_METRIC.APPID, jvmMonitorMetricData.getAppId())
                .set(Tables.JVM_MONITOR_METRIC.APP, jvmMonitorMetricData.getApp())
                .set(Tables.JVM_MONITOR_METRIC.EDEN_USED, jvmMonitorMetricData.getEden_used())
                .set(Tables.JVM_MONITOR_METRIC.EDEN_MAX, jvmMonitorMetricData.getEden_max())
                .set(Tables.JVM_MONITOR_METRIC.EDEN_CAPACITY, jvmMonitorMetricData.getEden_capacity())
                .set(Tables.JVM_MONITOR_METRIC.EDEN_UTIL, jvmMonitorMetricData.getEden_util())
                .set(Tables.JVM_MONITOR_METRIC.OLD_USED, jvmMonitorMetricData.getOld_used())
                .set(Tables.JVM_MONITOR_METRIC.OLD_MAX, jvmMonitorMetricData.getOld_max())
                .set(Tables.JVM_MONITOR_METRIC.OLD_CAPACITY, jvmMonitorMetricData.getOld_capacity())
                .set(Tables.JVM_MONITOR_METRIC.OLD_UTIL, jvmMonitorMetricData.getOld_util())
                .set(Tables.JVM_MONITOR_METRIC.META_UTIL, jvmMonitorMetricData.getMeta_util())
                .set(Tables.JVM_MONITOR_METRIC.META_USED, jvmMonitorMetricData.getMeta_used())
                .set(Tables.JVM_MONITOR_METRIC.META_MAX, jvmMonitorMetricData.getMeta_max())
                .set(Tables.JVM_MONITOR_METRIC.META_CAPACITY, jvmMonitorMetricData.getMeta_capacity())
                .set(Tables.JVM_MONITOR_METRIC.JVM_MEM_UTIL, jvmMonitorMetricData.getJvm_mem_util())
                .set(Tables.JVM_MONITOR_METRIC.JVM_MEM_USED, jvmMonitorMetricData.getJvm_mem_used())
                .set(Tables.JVM_MONITOR_METRIC.JVM_MEM_MAX, jvmMonitorMetricData.getJvm_mem_max())
                .set(Tables.JVM_MONITOR_METRIC.JVM_MEM_CAPACITY, jvmMonitorMetricData.getJvm_mem_capacity())
                .set(Tables.JVM_MONITOR_METRIC.SYSTEM_MEM_UTIL, jvmMonitorMetricData.getSystem_mem_util())
                .set(Tables.JVM_MONITOR_METRIC.SYSTEM_MEM_USED, jvmMonitorMetricData.getSystem_mem_used())
                .set(Tables.JVM_MONITOR_METRIC.SYSTEM_MEM_MAX, jvmMonitorMetricData.getSystem_mem_max())
                .set(Tables.JVM_MONITOR_METRIC.SYSTEM_MEM_CAPACITY, jvmMonitorMetricData.getSystem_mem_capacity())
                .set(Tables.JVM_MONITOR_METRIC.YGC_COUNT, jvmMonitorMetricData.getYgc_count())
                .set(Tables.JVM_MONITOR_METRIC.YGC_TIME, jvmMonitorMetricData.getYgc_time())
                .set(Tables.JVM_MONITOR_METRIC.FGC_COUNT, jvmMonitorMetricData.getFgc_count())
                .set(Tables.JVM_MONITOR_METRIC.FGC_TIME, jvmMonitorMetricData.getFgc_time())
                .set(Tables.JVM_MONITOR_METRIC.S0C, jvmMonitorMetricData.getS0c())
                .set(Tables.JVM_MONITOR_METRIC.S1C, jvmMonitorMetricData.getS1c())
                .set(Tables.JVM_MONITOR_METRIC.S0U, jvmMonitorMetricData.getS0u())
                .set(Tables.JVM_MONITOR_METRIC.S1U, jvmMonitorMetricData.getS1u())
                .set(Tables.JVM_MONITOR_METRIC.EC, jvmMonitorMetricData.getEc())
                .set(Tables.JVM_MONITOR_METRIC.EU, jvmMonitorMetricData.getEu())
                .set(Tables.JVM_MONITOR_METRIC.OC, jvmMonitorMetricData.getOc())
                .set(Tables.JVM_MONITOR_METRIC.OU, jvmMonitorMetricData.getOu())
                .set(Tables.JVM_MONITOR_METRIC.MC, jvmMonitorMetricData.getMc())
                .set(Tables.JVM_MONITOR_METRIC.MU, jvmMonitorMetricData.getMu())
                .set(Tables.JVM_MONITOR_METRIC.CCSC, jvmMonitorMetricData.getCcsc())
                .set(Tables.JVM_MONITOR_METRIC.CCSU, jvmMonitorMetricData.getCcsu())
                .set(Tables.JVM_MONITOR_METRIC.YGC, jvmMonitorMetricData.getYgc())
                .set(Tables.JVM_MONITOR_METRIC.YGCT, jvmMonitorMetricData.getYgct())
                .set(Tables.JVM_MONITOR_METRIC.FGC, jvmMonitorMetricData.getFgc())
                .set(Tables.JVM_MONITOR_METRIC.FGCT, jvmMonitorMetricData.getFgct())
                .set(Tables.JVM_MONITOR_METRIC.GCT, jvmMonitorMetricData.getGct())
                .set(Tables.JVM_MONITOR_METRIC.NGCMN, jvmMonitorMetricData.getNgcmn())
                .set(Tables.JVM_MONITOR_METRIC.NGCMX, jvmMonitorMetricData.getNgcmx())
                .set(Tables.JVM_MONITOR_METRIC.NGC, jvmMonitorMetricData.getNgc())
                .set(Tables.JVM_MONITOR_METRIC.OGCMN, jvmMonitorMetricData.getOgcmn())
                .set(Tables.JVM_MONITOR_METRIC.OGCMX, jvmMonitorMetricData.getOgcmx())
                .set(Tables.JVM_MONITOR_METRIC.OGC, jvmMonitorMetricData.getOgc())
                .set(Tables.JVM_MONITOR_METRIC.MCMN, jvmMonitorMetricData.getMcmn())
                .set(Tables.JVM_MONITOR_METRIC.MCMX, jvmMonitorMetricData.getMcmx())
                .set(Tables.JVM_MONITOR_METRIC.CCSMN, jvmMonitorMetricData.getCcsmn())
                .set(Tables.JVM_MONITOR_METRIC.CCSMX, jvmMonitorMetricData.getCcsmx())
                .set(Tables.JVM_MONITOR_METRIC.CODECACHEUSED, jvmMonitorMetricData.getCodeCacheUsed())
                .set(Tables.JVM_MONITOR_METRIC.CODECACHEMAX, jvmMonitorMetricData.getCodeCacheMax())
                .set(Tables.JVM_MONITOR_METRIC.CODECACHEUTIL, jvmMonitorMetricData.getCodeCacheUtil())
                .returning()
                .fetchOne();
    }
}