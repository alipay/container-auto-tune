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
package com.alipay.autotuneservice.service.monitor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.monitor.AppBasicInfoVO;
import com.alipay.autotuneservice.controller.model.monitor.AppIndicatorVO;
import com.alipay.autotuneservice.controller.model.monitor.MetricVO;
import com.alipay.autotuneservice.controller.model.monitor.MetricVOS;
import com.alipay.autotuneservice.controller.model.monitor.PodIndicatorVO;
import com.alipay.autotuneservice.dao.JavaInfoRepository;
import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.TwatchInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JavaInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.monitor.MonitorService;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version MonitorServiceImpl.java, v 0.1 2022年10月17日 5:43 下午 huoyuqi
 */
@Service
@Slf4j
public class MonitorServiceImpl implements MonitorService {

    private final static Long TIME       = 10 * 60 * 1000L;
    private final static Long ONE_MINUTE = 1 * 60 * 1000L;
    private final static Long ONE_DAY    = 24 * 60 * 60 * 1000L;
    private final static Long THREE_DAY  = 3 * 24 * 60 * 60 * 1000L;
    private final static Long FIVE_DAY   = 5 * 24 * 60 * 60 * 1000L;
    private final static Long ONE_WEEK   = 7 * 24 * 60 * 60 * 1000L;

    @Autowired
    private JvmMonitorMetricRepository jvmMetricRepository;

    @Autowired
    private TwatchInfoRepository twatchInfoRepository;

    @Autowired
    private PodInfo podInfo;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private JavaInfoRepository javaInfoRepository;

    @Override
    public PodIndicatorVO getPodIndicators(Integer podId, String podName, String type, Long start, Long end) {

        //1.查询包含业务的container，并获取container相关信息
        PodIndicatorVO podIndicatorVO = new PodIndicatorVO();
        String appName = podName.substring(0, StringUtils.lastOrdinalIndexOf(podName, "-", 2));
        List<TwatchInfoDo> infoDo = twatchInfoRepository.findInfoByPod(podName);
        if (CollectionUtils.isNotEmpty(infoDo)) {
            Optional<TwatchInfoDo> first = infoDo.stream().filter(r -> StringUtils.isNotBlank(r.getContainerName())).findFirst();
            if (first.isPresent()) {
                podIndicatorVO = first.get().convertVO();
            }
        }
        podIndicatorVO.setAppName(appName);

        //2.获取集群名称
        PodInfoRecord podInfoRecord = podInfo.getByPodAndAT(podName, UserUtil.getAccessToken());
        if (podInfoRecord != null) {
            podIndicatorVO.setClusterName(podInfoRecord.getClusterName());
            podIndicatorVO.setUnicode(podInfoRecord.getUnicode());
            podIndicatorVO.setJvmParam(podInfoRecord.getPodJvm());
        }

        //3.获取javaInfo 相关信息 pid javaVersion jvm_home
        JavaInfoRecord javaInfoRecord = javaInfoRepository.findInfo(podName);
        if (javaInfoRecord != null) {
            podIndicatorVO.setPid(javaInfoRecord.getPid());
            podIndicatorVO.setVersion(javaInfoRecord.getVersion());
            podIndicatorVO.setJvmHome(javaInfoRecord.getJvmHome());
            podIndicatorVO.setClassPath(javaInfoRecord.getClassPath());
            podIndicatorVO.setLibraryPath(javaInfoRecord.getLibraryPath());
            podIndicatorVO.setOsVersion(javaInfoRecord.getOsVersion());
            podIndicatorVO.setOsArch(javaInfoRecord.getOsArch());
            if (javaInfoRecord.getInputArguments() != null) {
                podIndicatorVO.setJvmParam(javaInfoRecord.getInputArguments().replaceAll(" ", "").replaceAll(",", " ").replace("\"", "")
                        .replace("[", "").replace("]", ""));
            }
        }

        Long currentTime = System.currentTimeMillis();
        long endTime = null == end ? currentTime - ONE_MINUTE : end;
        long startTime = start == null ? endTime - TIME : start;
        //4.获取jvm相关指标
        MetricVOS metricVOS = getPodMetric(podName, type, startTime, endTime);
        //构建JvmJitTime
        if (CollectionUtils.isNotEmpty(metricVOS.getJvmJitTime()) && metricVOS.getJvmJitTime().get(0) != null) {
            podIndicatorVO.setJvmJitTime((long) metricVOS.getJvmJitTime().get(0).getValue().intValue());
        }
        metricVOS.getJvmJitTime().removeAll(metricVOS.getJvmJitTime());
        metricVOS = judgeTime(metricVOS, startTime, endTime);

        //判断时间是否大于一天小于三天，10分钟一个点； 大于三天小于5天  20分钟一个点； 大于5天小于7天30分钟一个点
        podIndicatorVO.setMetricVOS(metricVOS);

        //todo RT QPS
        podIndicatorVO.setMonitorRQ(Boolean.FALSE);
        return podIndicatorVO;
    }

    private MetricVOS judgeTime(MetricVOS metricVOS, long startTime, long endTime) {
        Long searchTime = endTime - startTime;
        Class<? extends Object> cls = metricVOS.getClass();
        Field[] fields = cls.getDeclaredFields();

        if (searchTime >= ONE_DAY && searchTime < THREE_DAY) {
            return aggregationTime(metricVOS, fields, 10);
        }

        if (searchTime >= THREE_DAY && searchTime < FIVE_DAY) {
            return aggregationTime(metricVOS, fields, 20);
        }

        if (searchTime >= FIVE_DAY && searchTime <= ONE_WEEK) {
            return aggregationTime(metricVOS, fields, 30);
        }

        if (searchTime > ONE_WEEK) {
            throw new RuntimeException("input time over one week");
        }
        return metricVOS;
    }

    private MetricVOS aggregationTime(MetricVOS metricVOS, Field[] fields, Integer partition) {
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                List<MetricVO> metricVOList = JSON.parseObject(JSON.toJSONString(field.get(metricVOS)),
                        new TypeReference<List<MetricVO>>() {});
                List<List<MetricVO>> result = Lists.partition(metricVOList, partition);
                field.set(metricVOS, result.stream().map(item -> new MetricVO(item.get(0).getTime(),
                        item.stream().mapToDouble(MetricVO::getValue).average().orElse(0.0), item.get(0).getCombinationType(),
                        item.get(0).getMetricType(), item.get(0).getUnit())).collect(Collectors.toList()));
            }
            return metricVOS;
        } catch (Exception e) {
            log.info("judgeTime occurs an error", e);
            return metricVOS;
        }
    }

    @Override
    public AppIndicatorVO getAppIndicators(Integer appId, String type, Long start, Long end) {
        Long currentTime = System.currentTimeMillis();
        long endTime = null == end ? currentTime - ONE_MINUTE : end;
        long startTime = start == null ? endTime - TIME : start;
        // construct app basicInfo
        AppInfoRecord record = appInfoService.selectById(appId);
        if (null == record) {
            log.error("getAppInfoRecord is null in db, appId: {}", appId);
            return null;
        }

        // construct monitor appMetric
        List<PodInfoRecord> podList = podInfo.getByAppId(appId);
        if (CollectionUtils.isEmpty(podList)) {
            log.error("getAppIndicators find pod in db is null, appId: {}", appId);
            return null;
        }
        MetricVOS metricVOS = getAppJvmMetrics(appId, type, startTime, endTime, "period", "avg", Collections.singletonList("period"),
                "period", "systemCpuLoad", "processCpuLoad", "system_mem_capacity", "system_mem_used", "system_mem_max", "jvm_mem_util",
                "fgc", "fgct", "ygc", "ygct");
        log.info("AppJvmMetric is: {}", JSON.toJSONString(metricVOS));
        metricVOS = judgeTime(metricVOS, startTime, endTime);
        return new AppIndicatorVO(record.getAppName(), metricVOS);
    }

    @Override
    public AppBasicInfoVO getAppBasicInfo(Integer appId) {
        AppInfoRecord record = appInfoService.selectById(appId);
        if (null == record) {
            log.error("getAppInfoRecord is null in db, appId: {}", appId);
            return null;
        }
        return new AppBasicInfoVO(record.getAppName(), record.getNamespace(), DateUtils.asTimestamp(record.getCreatedTime()));
    }

    private MetricVOS getAppJvmMetrics(Integer appId, String type, Long start, Long end, String rangeKey, String aggregateType,
                                       List<String> groupBy, String... aggregateFields) {
        List<JvmMonitorMetricData> appJvmMetrics = jvmMetricRepository.getAppJvmMetric(appId, start, end, rangeKey, aggregateType,
                groupBy, aggregateFields);
        return getAppJvmMetric(appJvmMetrics, type);
    }

    private MetricVOS getPodMetric(String podName, String type, Long startTime, Long endTime) {
        Long start = System.currentTimeMillis();
        List<JvmMonitorMetricData> metaData = jvmMetricRepository.getPodJvmMetric(podName, startTime, endTime);
        log.info("jvmMonitorMetricData is:{}", JSON.toJSONString(metaData));
        log.info("getPodMetric duration time is：{}", System.currentTimeMillis() - start);
        return getPodJvmMetric(metaData, type);
    }

    private MetricVOS getAppJvmMetric(List<JvmMonitorMetricData> jvmMetrics, String type) {
        MetricVOS metricVOS = new MetricVOS();
        if (CollectionUtils.isNotEmpty(jvmMetrics)) {
            jvmMetrics.forEach(item -> {
                        try {
                            Long time = item.getPeriod();
                            //1.传入存在时只获取当前类型的  2.不存在时获取所有类型
                            boolean flag = StringUtils.isNotEmpty(type);

                            if (!flag || StringUtils.equals(type, "CPU")) {
                                metricVOS.getSystemCpuLoads().add(new MetricVO(time, item.getSystemCpuLoad(), "CPU", "SystemCpuLoads",
                                        "%"));
                                metricVOS.getProcessCpuLoads().add(new MetricVO(time, item.getProcessCpuLoad(), "CPU", "ProcessCpuLoads",
                                        "%"));
                            }

                            //SYS_M
                            if (!flag || StringUtils.equals(type, "SystemMem")) {
                                metricVOS.getSystemMemCapacity().add(
                                        new MetricVO(time, item.getSystem_mem_capacity(), "SystemMem", "SystemMemCapacity", "M"));
                                metricVOS.getSystemMemMax().add(new MetricVO(time, item.getSystem_mem_max(), "SystemMem", "SystemMemMax",
                                        "M"
                                ));
                                metricVOS.getSystemMemUses().add(new MetricVO(time, item.getSystem_mem_used(), "SystemMem", "SystemMemUsed",
                                        "M"
                                ));
                            }

                            //Jvm_M
                            if (!flag || StringUtils.equals(type, "JvmMemUtil")) {
                                metricVOS.getJvmMemUtils().add(new MetricVO(time, item.getJvm_mem_util(), null, "JvmMemUtil", "%"));
                            }

                            //GC回收时间 FGC、YGC
                            if (!flag || StringUtils.equals(type, "FgcCount")) {
                                metricVOS.getFgcCounts().add(new MetricVO(time, (double) item.getFgc_count(), null, "FgcCount", ""));
                            }
                            if (!flag || StringUtils.equals(type, "FgcTime")) {
                                metricVOS.getFgcTimes().add(new MetricVO(time, item.getFgc_time(), null, "FgcTime", "MS"));
                            }
                            if (!flag || StringUtils.equals(type, "YgcCount")) {
                                metricVOS.getYgcCounts().add(new MetricVO(time, (double) item.getYgc_count(), null, "YgcCount", ""));
                            }
                            if (!flag || StringUtils.equals(type, "YgcTime")) {
                                metricVOS.getYgcTimes().add(new MetricVO(time, item.getYgc_time(), null, "YgcTime", "MS"));
                            }
                        } catch (Exception e) {
                            log.error("getIndicators occurs an error", e);
                        }
                    }
            );
            return ascMetrics(metricVOS);
        }
        return metricVOS;
    }

    private MetricVOS getPodJvmMetric(List<JvmMonitorMetricData> jvmMetrics, String type) {

        MetricVOS metricVOS = new MetricVOS();
        if (CollectionUtils.isNotEmpty(jvmMetrics)) {
            jvmMetrics.forEach(item -> {
                        try {
                            Long time = item.getPeriod();
                            //1.传入存在时只获取当前类型的  2.不存在时获取所有类型
                            boolean flag = StringUtils.isNotEmpty(type);

                            metricVOS.getJvmJitTime().add(new MetricVO(time, (double) item.getJvmJitTime(), null, "JvmJitTime", "MS"));
                            //CPU
                            if (!flag || StringUtils.equals(type, "CpuCore")) {
                                metricVOS.getCpuCounts().add(new MetricVO(time, (double) item.getCpuCount(), null, "CpuCore", "C"));
                            }
                            if (!flag || StringUtils.equals(type, "CPU")) {
                                metricVOS.getSystemCpuLoads().add(new MetricVO(time, item.getSystemCpuLoad(), "CPU", "SystemCpuLoad",
                                        "%"));
                                metricVOS.getProcessCpuLoads().add(new MetricVO(time, item.getProcessCpuLoad(), "CPU", "ProcessCpuLoad",
                                        "%"));
                                metricVOS.getWaitCpuLoads().add(new MetricVO(time, item.getWaitCpuLoad(), "CPU", "WaitCpuLoad", "%"));
                                metricVOS.getCpuLoad().add(new MetricVO(time, item.getCpuLoad(), "CPU", "CpuLoad", "%"));
                            }

                            //SYS_M
                            if (!flag || StringUtils.equals(type, "SystemMem")) {
                                metricVOS.getSystemMemCapacity().add(
                                        new MetricVO(time, item.getSystem_mem_capacity(), "SystemMem", "SystemMemCapacity", "M"));
                                metricVOS.getSystemMemMax().add(new MetricVO(time, item.getSystem_mem_max(), "SystemMem", "SystemMemMax",
                                        "M"
                                ));
                                metricVOS.getSystemMemUses().add(new MetricVO(time, item.getSystem_mem_used(), "SystemMem", "SystemMemUsed",
                                        "M"
                                ));
                            }
                            if (!flag || StringUtils.equals(type, "SystemMemUtil")) {
                                metricVOS.getSystemMemUtils().add(new MetricVO(time, item.getSystem_mem_util(), null, "SystemMemUtil",
                                        "%"));
                            }

                            //JVM_M
                            if (!flag || StringUtils.equals(type, "JvmMemUtil")) {
                                metricVOS.getJvmMemUtils().add(new MetricVO(time, item.getJvm_mem_util(), null, "JvmMemUtil", "%"));
                            }
                            if (!flag || StringUtils.equals(type, "JvmMem")) {
                                metricVOS.getJvmMemMax().add(new MetricVO(time, item.getJvm_mem_max(), "JvmMem", "JvmMemMax", "M"));
                                metricVOS.getJvmMemUses().add(new MetricVO(time, item.getJvm_mem_used(), "JvmMem", "JvmMemUsed", "M"));
                                metricVOS.getJvmMemCapacity().add(new MetricVO(time, item.getJvm_mem_capacity(), "JvmMem", "JvmMemCapacity",
                                        "M"));
                            }

                            //新生代大小
                            if (!flag || StringUtils.equals(type, "New Generation")) {
                                metricVOS.getNgcmn().add(new MetricVO(time, item.getNgcmn(), "New Generation", "Ngcmn", "M"));
                                metricVOS.getNgcmx().add(new MetricVO(time, item.getNgcmx(), "New Generation", "Ngcmx", "M"));
                                metricVOS.getNgc().add(new MetricVO(time, item.getNgc(), "New Generation", "Ngc", "M"));
                            }

                            //幸存区大小使用
                            if (!flag || StringUtils.equals(type, "Survivor")) {
                                metricVOS.getSoc().add(new MetricVO(time, item.getS0c(), "Survivor", "S0c", "M"));
                                metricVOS.getSc().add(new MetricVO(time, item.getS1c(), "Survivor", "S1c", "M"));
                                metricVOS.getSou().add(new MetricVO(time, item.getS0u(), "Survivor", "S0u", "M"));
                                metricVOS.getSu().add(new MetricVO(time, item.getS1u(), "Survivor", "S1u", "M"));
                            }

                            //伊甸区大小使用
                            if (!flag || StringUtils.equals(type, "Eden")) {
                                metricVOS.getEc().add(new MetricVO(time, item.getEc(), "Eden", "Ec", "M"));
                                metricVOS.getEu().add(new MetricVO(time, item.getEu(), "Eden", "Eu", "M"));
                            }

                            //老年代大小使用
                            if (!flag || StringUtils.equals(type, "Old Generation")) {
                                metricVOS.getOc().add(new MetricVO(time, item.getOc(), "Old Generation", "Oc", "M"));
                                metricVOS.getOu().add(new MetricVO(time, item.getOu(), "Old Generation", "Ou", "M"));
                                metricVOS.getOgcmn().add(new MetricVO(time, item.getOgcmn(), "Old Generation", "Ogcmn", "M"));
                                metricVOS.getOgcmx().add(new MetricVO(time, item.getOgcmx(), "Old Generation", "Ogcmx", "M"));
                            }

                            //元空间大小使用
                            if (!flag || StringUtils.equals(type, "Metaspace")) {
                                metricVOS.getMc().add(new MetricVO(time, item.getMc(), "Metaspace", "Mc", "M"));
                                metricVOS.getMu().add(new MetricVO(time, item.getMu(), "Metaspace", "Mu", "M"));
                                metricVOS.getMcmn().add(new MetricVO(time, item.getMcmn(), "Metaspace", "Mcmn", "M"));
                                metricVOS.getMcmx().add(new MetricVO(time, item.getMcmx(), "Metaspace", "Mcmx", "M"));
                            }

                            //压缩类空间大小使用
                            if (!flag || StringUtils.equals(type, "CompressedClassSpace")) {
                                metricVOS.getCcsc().add(new MetricVO(time, item.getCcsc(), "CompressedClassSpace", "Ccsc", "M"));
                                metricVOS.getCcsu().add(new MetricVO(time, item.getCcsu(), "CompressedClassSpace", "Ccsu", "M"));
                                metricVOS.getCcsmn().add(new MetricVO(time, item.getCcsmn(), "CompressedClassSpace", "Ccmn", "M"));
                                metricVOS.getCcsmx().add(new MetricVO(time, item.getCcsmn(), "CompressedClassSpace", "Ccmx", "M"));
                            }

                            //GC回收时间 FGC、YGC
                            if (!flag || StringUtils.equals(type, "FgcCount")) {
                                metricVOS.getFgcCounts().add(new MetricVO(time, (double) item.getFgc_count(), null, "FgcCount", ""));
                            }
                            if (!flag || StringUtils.equals(type, "FgcTime")) {
                                metricVOS.getFgcTimes().add(new MetricVO(time, item.getFgc_time(), null, "FgcTime", "MS"));
                            }
                            if (!flag || StringUtils.equals(type, "YgcCount")) {
                                metricVOS.getYgcCounts().add(new MetricVO(time, (double) item.getYgc_count(), null, "YgcCount", ""));
                            }
                            if (!flag || StringUtils.equals(type, "YgcTime")) {
                                metricVOS.getYgcTimes().add(new MetricVO(time, item.getYgc_time(), null, "YgcTime", "MS"));
                            }
                            if (!flag || StringUtils.equals(type, "GcTime")) {
                                metricVOS.getGcTimes().add(new MetricVO(time, item.getYgc_time(), null, "GcTime", "MS"));
                            }

                            //代码缓冲区大小
                            if (!flag || StringUtils.equals(type, "CodeCache")) {
                                metricVOS.getCodeCacheMax().add(new MetricVO(time, (double) item.getCodeCacheMax(), "CodeCache",
                                        "CodeCacheMax", "M"));
                                metricVOS.getCodeCacheUses().add(new MetricVO(time, (double) item.getCodeCacheUsed(), "CodeCache",
                                        "CodeCacheUsed", "M"));
                            }
                            if (!flag || StringUtils.equals(type, "CodeCacheUtil")) {
                                metricVOS.getCodeCacheUtils().add(new MetricVO(time, item.getCodeCacheUtil(), null, "CodeCacheUtil", "%"));
                            }

                            //SafePoint
                            if (!flag || StringUtils.equals(type, "SafePointCount")) {
                                metricVOS.getSafePointCount().add(
                                        new MetricVO(time, (double) item.getSafePointCount(), null, "SafePointCount", ""));
                            }
                            if (!flag || StringUtils.equals(type, "SafePointTime")) {
                                metricVOS.getSafePointTime().add(new MetricVO(time, (double) item.getSafePointTime(), null,
                                        "SafePointTime", "MS"));
                            }

                            //线程相关
                            if (!flag || StringUtils.equals(type, "Thread")) {
                                metricVOS.getThreadCount().add(new MetricVO(time, (double) item.getThreadCount(), "Thread", "ThreadCount"
                                        , ""));
                                metricVOS.getDaemonThreadCount().add(
                                        new MetricVO(time, (double) item.getDaemonThreadCount(), "Thread", "DaemonThreadCount", ""));
                                metricVOS.getPeakThreadCount().add(
                                        new MetricVO(time, (double) item.getPeakThreadCount(), "Thread", "PeakThreadCount", ""));
                                metricVOS.getDeadLockedCount().add(
                                        new MetricVO(time, (double) item.getDeadLockedCount(), "Thread", "DeadLockedCount", ""));
                            }

                            //类相关
                            if (!flag || StringUtils.equals(type, "Class")) {
                                metricVOS.getTotalLoadedClassCount().add(
                                        new MetricVO(time, (double) item.getTotalLoadedClassCount(), "Class", "TotalLoadedClassCount", ""));
                                metricVOS.getLoadedClassCount().add(
                                        new MetricVO(time, (double) item.getLoadedClassCount(), "Class", "getLoadedClassCount", ""));
                                metricVOS.getUnloadedClassCount().add(
                                        new MetricVO(time, (double) item.getUnloadedClassCount(), "Class", "UnloadedClassCount", ""));
                            }

                        } catch (Exception e) {
                            log.error("getIndicators occurs an error", e);
                        }
                    }
            );
        }

        return ascMetrics(metricVOS);
    }

    //对参数进行升序排序
    private MetricVOS ascMetrics(MetricVOS metricVOS) {
        Class<? extends Object> cls = metricVOS.getClass();
        Field[] fields = cls.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                List<MetricVO> metricVOList = JSON.parseObject(JSON.toJSONString(field.get(metricVOS)),
                        new TypeReference<List<MetricVO>>() {});
                field.set(metricVOS, metricVOList.stream().sorted(Comparator.comparing(MetricVO::getTime)).collect(Collectors.toList()));
            }
            return metricVOS;
        } catch (Exception e) {
            log.info("judgeTime occurs an error", e);
            return null;
        }
    }

}