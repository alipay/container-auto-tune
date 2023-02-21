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
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMonitorMetricRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.ThreadpoolMonitorMetricDataRecord;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.ThreadPoolMonitorMetricData;
import com.alipay.autotuneservice.service.chronicmap.ChronicleMapService;
import com.alipay.autotuneservice.util.AgentConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : JvmMonitorMetricDataRepositoryImpl.java, v 0.1 2022年10月31日 9:10 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class JvmMonitorMetricRepositoryImpl extends BaseDao implements JvmMonitorMetricRepository {

    @Autowired
    private ChronicleMapService redisClient;

    @Autowired
    private PodInfo podInfo;

    @Override
    public void insertGCData(JvmMonitorMetricData jvmMonitorMetricData) {
        //插入表数据
        log.info("start insertGCData");
        mDSLContext.insertInto(Tables.JVM_MONITOR_METRIC)
                .set(Tables.JVM_MONITOR_METRIC.CPUCOUNT, jvmMonitorMetricData.getCpuCount())
                .set(Tables.JVM_MONITOR_METRIC.SYSTEMCPULOAD, jvmMonitorMetricData.getSystemCpuLoad())
                .set(Tables.JVM_MONITOR_METRIC.PROCESSCPULOAD, jvmMonitorMetricData.getProcessCpuLoad())
                .set(Tables.JVM_MONITOR_METRIC.WAITCPULOAD, jvmMonitorMetricData.getWaitCpuLoad())
                .set(Tables.JVM_MONITOR_METRIC.CPULOAD, jvmMonitorMetricData.getCpuLoad())
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
                .set(Tables.JVM_MONITOR_METRIC.SAFEPOINTCOUNT, jvmMonitorMetricData.getSafePointCount())
                .set(Tables.JVM_MONITOR_METRIC.SAFEPOINTTIME, jvmMonitorMetricData.getSafePointTime())
                .set(Tables.JVM_MONITOR_METRIC.THREADCOUNT, jvmMonitorMetricData.getThreadCount())
                .set(Tables.JVM_MONITOR_METRIC.PEAKTHREADCOUNT, jvmMonitorMetricData.getPeakThreadCount())
                .set(Tables.JVM_MONITOR_METRIC.DAEMONTHREADCOUNT, jvmMonitorMetricData.getDaemonThreadCount())
                .set(Tables.JVM_MONITOR_METRIC.DEADLOCKEDCOUNT, jvmMonitorMetricData.getDeadLockedCount())
                .set(Tables.JVM_MONITOR_METRIC.TOTALLOADEDCLASSCOUNT, jvmMonitorMetricData.getTotalLoadedClassCount())
                .set(Tables.JVM_MONITOR_METRIC.LOADEDCLASSCOUNT, jvmMonitorMetricData.getLoadedClassCount())
                .set(Tables.JVM_MONITOR_METRIC.UNLOADEDCLASSCOUNT, jvmMonitorMetricData.getUnloadedClassCount())
                .set(Tables.JVM_MONITOR_METRIC.JVMJITTIME, jvmMonitorMetricData.getJvmJitTime())
                .execute();
    }

    @Override
    public List<JvmMonitorMetricData> queryByPodNameAndDt(String podName, long dt) {
        try {
            List<JvmMonitorMetricRecord> records =  mDSLContext.select()
                    .from(Tables.JVM_MONITOR_METRIC)
                    .where(Tables.JVM_MONITOR_METRIC.POD.eq(podName))
                    .and(Tables.JVM_MONITOR_METRIC.PERIOD.eq(dt))
                    .fetchInto(JvmMonitorMetricRecord.class);
            return records.stream().map(this::covert2JvmMonitorMetricData).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("queryByPodNameAndDt for podName={}, dt={} occurs an error", podName, dt, e);
            return Lists.newArrayList();
        }
    }

    @Override
    public List<JvmMonitorMetricData> queryByPodName(String partitionKey, Long start, Long end) {
        try {

            List<JvmMonitorMetricRecord> dataRecords = mDSLContext.select()
                    .from(Tables.JVM_MONITOR_METRIC)
                    .where(Tables.JVM_MONITOR_METRIC.POD.eq(partitionKey))
                    .and(Tables.JVM_MONITOR_METRIC.PERIOD.between(start, end))
                    .fetchInto(JvmMonitorMetricRecord.class);
            return dataRecords.stream().map(this::covert2JvmMonitorMetricData).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("queryByPodName podName={}", partitionKey);
            return org.apache.commons.compress.utils.Lists.newArrayList();
        }
    }

    @Override
    public JvmMonitorMetricData getPodLatestOneMinuteJvmMetric(String nodeName) {
        try {
            return null;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public List<JvmMonitorMetricData> getPodJvmMetric(String podName, long start, long end) {
        try {
            List<JvmMonitorMetricRecord> dataRecords = mDSLContext.select()
                    .from(Tables.JVM_MONITOR_METRIC)
                    .where(Tables.JVM_MONITOR_METRIC.POD.eq(podName))
                    .and(Tables.JVM_MONITOR_METRIC.PERIOD.between(start, end))
                    .fetchInto(JvmMonitorMetricRecord.class);
            return dataRecords.stream().map(this::covert2JvmMonitorMetricData).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getPodJvmMetric for podName={} occurs an error.", podName, e);
            return Lists.newArrayList();
        }
    }

    private JvmMonitorMetricData covert2JvmMonitorMetricData(JvmMonitorMetricRecord r) {
        JvmMonitorMetricData metricData = new JvmMonitorMetricData();
        metricData.setCpuCount(r.getCpucount());
        metricData.setSystemCpuLoad(r.getSystemcpuload());
        metricData.setProcessCpuLoad(r.getProcesscpuload());
        metricData.setWaitCpuLoad(r.getWaitcpuload());
        metricData.setCpuLoad(r.getCpuload());
        metricData.setCluster(r.getCluster());
        metricData.setPeriod(r.getPeriod());
        metricData.setPod(r.getPod());
        metricData.setAppId(r.getAppid());
        metricData.setApp(r.getApp());
        metricData.setEden_used(r.getEdenUsed());
        metricData.setEden_max(r.getEdenMax());
        metricData.setEden_capacity(r.getEdenCapacity());
        metricData.setEden_util(r.getEdenUtil());
        metricData.setOld_util(r.getOldUtil());
        metricData.setOld_max(r.getOldMax());
        metricData.setOld_used(r.getOldUsed());
        metricData.setOld_capacity(r.getOldCapacity());
        metricData.setMeta_util(r.getMetaUtil());
        metricData.setMeta_used(r.getMetaUsed());
        metricData.setMeta_max(r.getMetaMax());
        metricData.setMeta_capacity(r.getMetaCapacity());
        metricData.setJvm_mem_util(r.getJvmMemUtil());
        metricData.setJvm_mem_used(r.getJvmMemUsed());
        metricData.setJvm_mem_max(r.getJvmMemMax());
        metricData.setJvm_mem_capacity(r.getJvmMemCapacity());
        metricData.setSystem_mem_util(r.getSystemMemUtil());
        metricData.setSystem_mem_used(r.getSystemMemUsed());
        metricData.setSystem_mem_max(r.getSystemMemMax());
        metricData.setSystem_mem_capacity(r.getSystemMemCapacity());
        metricData.setYgc_count(r.getYgcCount());
        metricData.setYgc_time(r.getYgcTime());
        metricData.setFgc_count(r.getFgcCount());
        metricData.setS0c(r.getS0c());
        metricData.setS1c(r.getS1c());
        metricData.setS0u(r.getS0u());
        metricData.setS1u(r.getS1u());
        metricData.setEc(r.getEc());
        metricData.setEu(r.getEu());
        metricData.setOc(r.getOc());
        metricData.setOu(r.getOu());
        metricData.setMc(r.getMc());
        metricData.setMu(r.getMu());
        metricData.setCcsc(r.getCcsc());
        metricData.setCcsu(r.getCcsu());
        metricData.setYgc(r.getYgc());
        metricData.setYgct(r.getYgct());
        metricData.setFgc(r.getFgc());
        metricData.setFgct(r.getFgct());
        metricData.setGct(r.getGct());
        metricData.setNgcmn(r.getNgcmn());
        metricData.setNgcmx(r.getNgcmx());
        metricData.setNgc(r.getNgc());
        metricData.setOgc(r.getOgc());
        metricData.setOgcmn(r.getOgcmn());
        metricData.setOgcmx(r.getOgcmx());
        metricData.setMcmn(r.getMcmn());
        metricData.setMcmx(r.getMcmx());
        metricData.setCcsmn(r.getCcsmn());
        metricData.setCcsmx(r.getCcsmx());
        metricData.setCodeCacheUsed(r.getCodecacheused());
        metricData.setCodeCacheMax(r.getCodecachemax());
        metricData.setCodeCacheUtil(r.getCodecacheutil());
        metricData.setSafePointCount(r.getSafepointcount());
        metricData.setSafePointTime(r.getSafepointtime());
        metricData.setThreadCount(r.getThreadcount());
        metricData.setPeakThreadCount(r.getPeakthreadcount());
        metricData.setDaemonThreadCount(r.getDaemonthreadcount());
        metricData.setDeadLockedCount(r.getDeadlockedcount());
        metricData.setTotalLoadedClassCount(r.getTotalloadedclasscount());
        metricData.setLoadedClassCount(r.getLoadedclasscount());
        metricData.setUnloadedClassCount(r.getUnloadedclasscount());
        metricData.setJvmJitTime(r.getJvmjittime());
        return metricData;
    }

    @Override
    public List<JvmMonitorMetricData> getAppJvmMetric(Integer appId, long start, long end, String rangeKey, String aggregateType,
                                                      List<String> groupBy, String... aggregateFields) {
        try {
            List<PodInfoRecord> records = podInfo.getByAppId(appId);
            if (CollectionUtils.isEmpty(records)) {
                return null;
            }
            String podName = records.get(0).getPodName();
            List<JvmMonitorMetricRecord> jvmMonitorMetricRecord =  mDSLContext.select()
                    .from(Tables.JVM_MONITOR_METRIC)
                    .where(Tables.JVM_MONITOR_METRIC.POD.eq(podName))
                    .and(Tables.JVM_MONITOR_METRIC.PERIOD.between(start, end))
                    .fetchInto(JvmMonitorMetricRecord.class);
            return jvmMonitorMetricRecord.stream().map(this::covert2JvmMonitorMetricData).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getPodJvmMetric for appId={} occurs an error.", appId, e);
            return Lists.newArrayList();
        }
    }

    @Override
    public void insertThreadPoolData(List<ThreadpoolMonitorMetricDataRecord> metrics) {
        if (CollectionUtils.isEmpty(metrics)) {
            log.info("insertGCData failed due to input insertThreadPoolData is null.");
            return;
        }
        try {
            metrics.parallelStream().forEach(metric -> mDSLContext.insertInto(Tables.THREADPOOL_MONITOR_METRIC_DATA)
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.HOST_NAME, metric.getHostName())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.THREAD_POOL_NAME, metric.getThreadPoolName())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.APP_NAME, metric.getAppName())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.ACTIVE_COUNT, metric.getActiveCount())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.POOL_SIZE, metric.getPoolSize())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.CORE_POOL_SIZE, metric.getCorePoolSize())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.KEEP_ALIVE_TIME, metric.getKeepAliveTime())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.COMPLETED_TASK_COUNT, metric.getCompletedTaskCount())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.LARGEST_POOL_SIZE, metric.getLargestPoolSize())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.MAXI_MUM_POOL_SIZE, metric.getMaxiMumPoolSize())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.TASK_COUNT, metric.getTaskCount())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.BLOCK_QUEUE, metric.getBlockQueue())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.IDLE_POOL_SIZE, metric.getIdlePoolSize())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.REJECT_COUNT, metric.getRejectCount())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.PERIOD, metric.getPeriod())
                    .set(Tables.THREADPOOL_MONITOR_METRIC_DATA.DT, metric.getDt())
                    .returning()
                    .fetchOne());
        } catch (Exception e) {
            log.error("insert insertThreadPoolData occurs an error.", e);
        }
    }

    @Override
    public void initThreadPoolCache(List<ThreadPoolMonitorMetricData> metrics) {
        if (CollectionUtils.isEmpty(metrics)) {
            return;
        }
        String appName = metrics.get(0).getAppName();
        String hostName = metrics.get(0).getHostName();
        String threadPoolKey = AgentConstant.generateThreadPoolKey(appName, hostName);
        redisClient.del(threadPoolKey);
        //存储
        metrics.parallelStream().forEach(threadpool -> redisClient.rpush(threadPoolKey, threadpool));
    }
}