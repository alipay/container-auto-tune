/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.AppInfoVO;
import com.alipay.autotuneservice.controller.model.K8sAccessTokenModel;
import com.alipay.autotuneservice.controller.model.PodProcessInfo;
import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.MeterMetaInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.NodeInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.ContainerProcessInfo;
import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.infrastructure.rpc.model.PsResponse;
import com.alipay.autotuneservice.model.agent.ContainerMetric;
import com.alipay.autotuneservice.model.common.AppModel;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.NodeModel;
import com.alipay.autotuneservice.model.common.NodeStatus;
import com.alipay.autotuneservice.model.statistics.StatisticsResponse;
import com.alipay.autotuneservice.meter.model.MeterMetric;
import com.alipay.autotuneservice.service.AppInfoService;
import com.amazonaws.regions.Regions;
import com.auto.tune.client.GcMetricsGrpc;
import com.auto.tune.client.MemoryMetricsGrpc;
import com.auto.tune.client.MetricsGrpcRequest;
import com.auto.tune.client.SystemCommonGrpc;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dutianze
 * @version ConvertUtils.java, v 0.1 2022年02月17日 16:51 dutianze
 */
@Slf4j
public class ConvertUtils {

    /**
     * S3 下载的字节流转 file
     *
     * @param buf
     * @param filePath
     * @param fileName
     * @return
     */
    public static File convert2MeterMetaRecord(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public static AppInfoVO convert2MeterMetaRecord(AppInfoRecord record) {
        AppInfoVO appInfoVO = new AppInfoVO();
        appInfoVO.setAppName(record.getAppName());
        appInfoVO.setAppId(record.getId());
        appInfoVO.setJvmBaseLine(record.getAppDefaultJvm());
        appInfoVO.setNodeCount(JSON.parseObject(record.getNodeIds(), new TypeReference<List<Integer>>() {}).size());
        return appInfoVO;
    }

    public static <T> Stream<T> ofNullable(Collection<T> list) {
        return list == null ? Stream.empty() : list.stream();
    }

    public static NodeModel convert2NodeModel(NodeInfoRecord record) {
        if (record == null) {
            return null;
        }
        NodeModel nodeModel = new NodeModel();
        nodeModel.setId(record.getId());
        nodeModel.setIp(record.getIp());
        nodeModel.setNodeName(record.getNodeName());
        nodeModel.setNodeStatus(NodeStatus.valueOf(record.getStatus()));
        nodeModel.setCreatedTime(record.getCreatedTime());
        nodeModel.setNodeTags(record.getNodeTags());
        nodeModel.setAccessToken(record.getAccessToken());
        return nodeModel;
    }

    public static List<NodeModel> convert2NodeModels(Collection<NodeInfoRecord> records) {
        return ofNullable(records).map(ConvertUtils::convert2NodeModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<AppModel> convert2ClusterModels(List<AppInfoRecord> clusterInfoRecords) {
        return ofNullable(clusterInfoRecords).map(ConvertUtils::convert2ClusterModel).collect(Collectors.toList());
    }

    public static AppModel convert2ClusterModel(AppInfoRecord record) {
        if (record == null) {
            return null;
        }
        AppModel appModel = new AppModel();
        appModel.setId(record.getId());
        appModel.setAppName(record.getAppName());
        appModel.setAppAsName(record.getAppAsName());
        appModel.setAppDesc(record.getAppDesc());
        appModel.setCreatedTime(record.getCreatedTime());
        appModel.setUpdatedTime(record.getUpdatedTime());
        appModel.setStatus(AppStatus.valueOf(record.getStatus()));
        return appModel;
    }

    public static K8sAccessTokenInfoRecord convert2K8sAccessTokenInfoRecord(K8sAccessTokenModel k8sAccessTokenModel) {
        if (Objects.isNull(k8sAccessTokenModel)) {
            return null;
        }
        K8sAccessTokenInfoRecord record = new K8sAccessTokenInfoRecord();
        record.setCreateTime(LocalDateTime.now());
        record.setUpdatedTime(LocalDateTime.now());
        record.setAccessToken(k8sAccessTokenModel.getAccessToken());
        record.setAccessKeyId(k8sAccessTokenModel.getAccessKeyId());
        record.setSecretAccessKey(k8sAccessTokenModel.getSecretAccessKey());
        record.setClusterId(k8sAccessTokenModel.getClusterId());
        record.setClusterName(k8sAccessTokenModel.getClusterName());
        record.setEndpoint(k8sAccessTokenModel.getEndpoint());
        record.setCer(k8sAccessTokenModel.getCer());
        try {
            record.setRegion(Regions.fromName(k8sAccessTokenModel.getRegion()).name());
        } catch (Exception e) {
            record.setRegion(k8sAccessTokenModel.getRegion());
        }
        return record;
    }

    public static K8sAccessTokenModel convert2K8sAccessTokenModel(K8sAccessTokenInfoRecord record) {
        if (Objects.isNull(record)) {
            return null;
        }
        K8sAccessTokenModel k8sAccessTokenModel = new K8sAccessTokenModel();
        k8sAccessTokenModel.setAccessKeyId(record.getAccessKeyId());
        k8sAccessTokenModel.setAccessToken(record.getAccessToken());
        k8sAccessTokenModel.setSecretAccessKey(record.getSecretAccessKey());
        k8sAccessTokenModel.setCer(record.getCer());
        k8sAccessTokenModel.setEndpoint(record.getEndpoint());
        k8sAccessTokenModel.setClusterName(record.getClusterName());
        k8sAccessTokenModel.setRegion(record.getRegion());
        return k8sAccessTokenModel;
    }

    public static NodeInfoRecord convert2NodeInfoRecord(NodeModel nodeModel) {
        if (nodeModel == null) {
            return null;
        }
        NodeInfoRecord record = new NodeInfoRecord();
        record.setId(nodeModel.getId());
        record.setIp(nodeModel.getIp());
        record.setStatus(nodeModel.getNodeStatus().name());
        record.setCreatedTime(nodeModel.getCreatedTime());
        record.setNodeTags(nodeModel.getNodeTags());
        record.setAccessToken(nodeModel.getAccessToken());
        return record;
    }

    public static JvmMonitorMetricData convert2JvmMonitorMetricData(MetricsGrpcRequest request, AppInfoService appInfoService) {
        Preconditions.checkArgument(request != null, "MetricsGrpcRequest can not be null.");
        Preconditions.checkArgument(StringUtils.isNotBlank(request.getSystemCommon().getAccessToken()), "AccessToken can not be empty.");
        JvmMonitorMetricData gcMetaData = JvmMonitorMetricData.builder().build();
        // system common
        SystemCommonGrpc systemCommon = request.getSystemCommon();
        gcMetaData.setPeriod(DateUtils.truncate2Minute(systemCommon.getTimestamp()));
        try {
            String appName = systemCommon.getHostname().substring(0, StringUtils.lastOrdinalIndexOf(systemCommon.getHostname(), "-", 2));
            gcMetaData.setApp(appName);
            String accessToken = systemCommon.getAccessToken().equals("1") ? "IOeiob2AI9n_YBjvjy04krmS5pe0xeEt"
                    : systemCommon.getAccessToken();
            AppInfoRecord appInfoRecord = appInfoService.getByAppAndATAndNamespace(appName, accessToken, systemCommon.getNamespace());
            gcMetaData.setAppId(appInfoRecord.getId());
            gcMetaData.setCluster(appInfoRecord.getClusterName());
        } catch (Exception e) {
            log.error("convert2JvmMonitorMetricData - insert appId or cluster occurs an error.", e);
        }
        gcMetaData.setPod(systemCommon.getHostname());
        try {
            long dt = Long.parseLong(DateUtils.formatTimestampToStr(System.currentTimeMillis(), DateTimeFormatter.ofPattern("yyyyMMdd")));
            gcMetaData.setDt(dt);
        } catch (Exception e) {
            log.error("convert2dt occurs an error.", e);
        }
        // ygc
        GcMetricsGrpc ygcMetric = request.getYoungGcMetric();
        gcMetaData.setYgc_count(ygcMetric.getGcCount());
        gcMetaData.setYgc_time(ygcMetric.getGcTime());
        // fgc
        GcMetricsGrpc fgcMetric = request.getFullGcMetric();
        gcMetaData.setFgc_count(fgcMetric.getGcCount());
        gcMetaData.setFgc_time(fgcMetric.getGcTime());
        // eden area
        MemoryMetricsGrpc edenMemMetric = request.getEdenMemoryMetric();
        gcMetaData.setEden_capacity(edenMemMetric.getCapacity());
        gcMetaData.setEden_util(edenMemMetric.getUtil());
        gcMetaData.setEden_max(edenMemMetric.getMax());
        gcMetaData.setEden_used(edenMemMetric.getUsed());
        // old are
        MemoryMetricsGrpc oldMemMetric = request.getOldMemoryMetric();
        gcMetaData.setOld_capacity(oldMemMetric.getCapacity());
        gcMetaData.setOld_max(oldMemMetric.getMax());
        gcMetaData.setOld_used(oldMemMetric.getUsed());
        gcMetaData.setOld_util(oldMemMetric.getUtil());
        // metaspace area
        MemoryMetricsGrpc metaMemMetric = request.getMetaMemoryMetric();
        gcMetaData.setMeta_capacity(metaMemMetric.getCapacity());
        gcMetaData.setMeta_max(metaMemMetric.getMax());
        gcMetaData.setMeta_used(metaMemMetric.getUsed());
        gcMetaData.setMeta_util(metaMemMetric.getUtil());
        return gcMetaData;
    }

    public static List<ContainerProcessInfo> convert2ContainerProcessInfos(Integer appId, ContainerMetric processMetric) {
        if (processMetric == null || StringUtils.isBlank(processMetric.getProcessInfo())) {
            return null;
        }
        PsResponse processInfo = JSON.parseObject(processMetric.getProcessInfo(), new TypeReference<PsResponse>() {});
        String[][] processes = processInfo.getProcesses();
        List<ContainerProcessInfo> list = new ArrayList<>();
        Stream.of(processes).forEach(item -> {
            if (!ArrayUtils.isEmpty(item)) {
                list.add(buildContainerProcessInfo(processMetric.getMonitorTime(), appId, processMetric.getPodName(),
                        processMetric.getContainerId(), item));
            }
        });
        return list;
    }

    @Deprecated
    public static List<ContainerProcessInfo> convert2ContainerProcessInfos(Integer appId, String podName, String containerId,
                                                                           String result) {
        if (StringUtils.isBlank(containerId) || StringUtils.isBlank(result)) {
            log.info("convert2ContainerProcessInfos containerId or result is empty.");
            return null;
        }
        PsResponse processInfo = JSON.parseObject(result, new TypeReference<PsResponse>() {});
        String[][] processes = processInfo.getProcesses();
        List<ContainerProcessInfo> list = new ArrayList<>();
        Stream.of(processes).forEach(item -> {
            if (!ArrayUtils.isEmpty(item)) {
                list.add(buildContainerProcessInfo(222222L, appId, podName, containerId, item));
            }
        });
        return list;
    }

    private static ContainerProcessInfo buildContainerProcessInfo(final Long monitorTime, Integer appId, String podName, String containerId,
                                                                  String[] item) {
        try {
            ContainerProcessInfo cp = new ContainerProcessInfo();
            cp.setPodName(podName);
            cp.setContainerId(containerId);
            cp.setGmtCreated(DateUtils.truncate2Minute(monitorTime));
            cp.setUSER(item[0]);
            cp.setPID(Long.parseLong(item[1]));
            cp.setCPU(Double.parseDouble(item[2]));
            cp.setMEM(Double.parseDouble(item[3]));
            cp.setVSZ(Long.parseLong(item[4]));
            cp.setRSS(Long.parseLong(item[5]));
            cp.setTTY(item[6]);
            cp.setSTAT(item[7]);
            cp.setSTART(item[8]);
            cp.setTIME(item[9]);
            cp.setCOMMAND(item[10]);
            cp.setAppId(appId);
            return cp;
        } catch (Exception e) {
            log.error("buildContainerProcessInfo containerId={} occurs an error.", containerId, e);
            return null;
        }
    }

    @Deprecated
    public static ContainerStatistics convert2ContainerStat(Integer appId, String podName, String containerId, String statsResponse) {
        if (StringUtils.isBlank(statsResponse)) {
            log.info("convert2ContainerStat - statsResponse is empty, so will skip.");
            return null;
        }
        try {
            String s = statsResponse.replaceAll("\\\\", "");
            StatisticsResponse statistics = JSONObject.parseObject(s.substring(1, s.length() - 1),
                    new TypeReference<StatisticsResponse>() {});
            ContainerStatistics containerStat = new ContainerStatistics();
            containerStat.setPodName(podName);
            containerStat.setContainerId(containerId);
            containerStat.setGmtCreated(System.currentTimeMillis());
            // set cpu info
            containerStat.setCpuTotalUsage(statistics.getCpuStats().getCpuUsage().getTotalUsage());
            containerStat.setSystemCpuUsage(statistics.getCpuStats().getSystemCpuUsage());
            containerStat.setPrecpuTotalUsage(statistics.getPreCpuStats().getCpuUsage().getTotalUsage());
            containerStat.setPrecpuSystemCpuUsage(statistics.getPreCpuStats().getSystemCpuUsage());
            containerStat.setOnlineCpus(statistics.getCpuStats().getOnlineCpus());
            containerStat.setCpuUsageRate(containerStat.getCpuUsageRate());
            // set mem info
            containerStat.setMemLimit(statistics.getMemoryStats().getLimit());
            containerStat.setMemUsage(statistics.getMemoryStats().getUsage());
            containerStat.setMemMaxUsage(statistics.getMemoryStats().getMaxUsage());
            containerStat.setMemCache(statistics.getMemoryStats().getStats().getCache());
            containerStat.setMemUsageRate(containerStat.getMemUsageRate());
            containerStat.setAppId(appId);
            //containerStat.setRawValue(jsonStr);
            return containerStat;
        } catch (Exception e) {
            log.error("convert2ContainerStat for containerId={} occurs an error.", containerId, e);
            return null;
        }
    }

    public static ContainerStatistics convert2ContainerStats(Integer appId, ContainerMetric statsMetric) {
        if (statsMetric == null) {
            log.info("convert2ContainerStats - statsResponse is empty, so will skip.");
            return null;
        }
        try {
            String res = statsMetric.getStatistics();
            String s = res.replaceAll("\\\\", "");
            StatisticsResponse statistics = JSONObject.parseObject(s, new TypeReference<StatisticsResponse>() {});
            ContainerStatistics containerStat = new ContainerStatistics();
            containerStat.setPodName(statsMetric.getPodName());
            containerStat.setContainerId(statsMetric.getContainerId());
            containerStat.setGmtCreated(DateUtils.truncate2Minute(statsMetric.getMonitorTime()));
            // set cpu info
            containerStat.setCpuTotalUsage(statistics.getCpuStats().getCpuUsage().getTotalUsage());
            containerStat.setSystemCpuUsage(statistics.getCpuStats().getSystemCpuUsage());
            containerStat.setPrecpuTotalUsage(statistics.getPreCpuStats().getCpuUsage().getTotalUsage());
            containerStat.setPrecpuSystemCpuUsage(statistics.getPreCpuStats().getSystemCpuUsage());
            containerStat.setOnlineCpus(statistics.getCpuStats().getOnlineCpus());
            containerStat.setCpuUsageRate(containerStat.getCpuUsageRate());
            // set mem info
            containerStat.setMemLimit(statistics.getMemoryStats().getLimit());
            containerStat.setMemUsage(statistics.getMemoryStats().getUsage());
            containerStat.setMemMaxUsage(statistics.getMemoryStats().getMaxUsage());
            containerStat.setMemCache(statistics.getMemoryStats().getStats().getCache());
            containerStat.setMemUsageRate(containerStat.getMemUsageRate());
            containerStat.setAppId(appId);
            return containerStat;
        } catch (Exception e) {
            log.error("convert2ContainerStat for containerId={} occurs an error.", statsMetric.getContainerId(), e);
            return null;
        }
    }

    public static List<PodProcessInfo> convert2PodProcessInfoList(List<ContainerProcessInfo> cpInfo) {
        if (CollectionUtils.isEmpty(cpInfo)) {
            return Lists.newArrayList();
        }
        List<PodProcessInfo> resList = new ArrayList<>();
        cpInfo.stream().forEach(item -> {
            try {
                resList.add(PodProcessInfo.builder().pid(item.getPID()).command(item.getCOMMAND()).build());
            } catch (Exception e) {
                log.error("convert2PodProcessInfoList occurs an error.", e);
            }
        });
        return resList;
    }

    public static List<PodProcessInfo> convert2PodProcessInfoListV2(String processInfo) {
        try {
            if (StringUtils.isEmpty(processInfo)) {
                return null;
            }
            PsResponse psProcessInfo = JSON.parseObject(processInfo, new TypeReference<PsResponse>() {});
            String[][] processes = psProcessInfo.getProcesses();
            return Stream.of(processes)
                    .map(item -> {
                        if (!ArrayUtils.isEmpty(item)) {
                            try {
                                return PodProcessInfo.builder().pid(Long.parseLong(item[1])).command(item[10]).build();
                            } catch (Exception e) {
                            }
                        }
                        return null;
                    })
                    .filter(item -> item != null && item.getCommand().startsWith("java -jar"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("convert2PodProcessInfoListV2 processInfo={} occurs an error", processInfo, e);
            return Lists.newArrayList();
        }
    }

    public static List<PodProcessInfo> convert2PodProcessInfo(String processInfo) {
        log.info("convert2PodProcessInfo processInfo={}", processInfo);
        if (StringUtils.isBlank(processInfo)) {
            return Lists.newArrayList();
        }
        if (!StringUtils.contains(processInfo, "PID")) {
            return Lists.newArrayList();
        }
        String[] split = processInfo.replaceAll("\"", "").split("\\\\n");
        return Stream.of(split).map(String::trim).filter(item -> !item.startsWith("UID")).map(
                item -> {
                    String[] s1 = item.split("\\s+");
                    if (s1.length >= 4) {
                        return PodProcessInfo.builder().pid(Long.parseLong(s1[1])).command(convert2String(s1, 7)).build();
                    }
                    return null;
                }).filter(item -> item != null && item.getCommand().startsWith("java -jar")).collect(Collectors.toList());
    }

    private static String convert2String(String[] arr, int startIndex) {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i >= startIndex) {
                stb.append(arr[i] + " ");
            }
        }
        return stb.toString();
    }



    public static MeterMetaInfoRecord convert2MeterMetaRecord(MeterMeta meterMeta) {
        MeterMetaInfoRecord record = new MeterMetaInfoRecord();
        record.setAppId(meterMeta.getAppId());
        record.setMeterDomain(meterMeta.getMeterDomain());
        record.setMeterName(meterMeta.getMeterName());
        record.setMeterMetrics(JSON.toJSONString(meterMeta.getMetricList()));
        //record.setMeterEnable(meterMeta.isMeterEnable());
        return record;
    }

    public static MeterMeta convertByMeterRecord(MeterMetaInfoRecord record){
        if (record == null) {
            return null;
        }
        return MeterMeta.builder()
                .meterName(record.getMeterName())
                .appId(record.getAppId())
                .meterDomain(record.getMeterDomain())
                .metricList(convert2MeterMetaList(record.getMeterMetrics()))
                .meterEnable(true)
                .build();
    }

    public static List<MeterMetric> convert2MeterMetaList(String metricList){
        if (StringUtils.isEmpty(metricList)) {
            return Lists.newArrayList();
        }
        try {
            return JSONObject.parseObject(metricList, new TypeReference<List<MeterMetric>>(){});
        }catch (Exception e) {
            return Lists.newArrayList();
        }
    }
}