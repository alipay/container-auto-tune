/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.baseLine.BaseLineContentVO;
import com.alipay.autotuneservice.controller.model.baseLine.BaseLineVO;
import com.alipay.autotuneservice.controller.model.baseLine.HistoryBaseLineVO;
import com.alipay.autotuneservice.controller.model.baseLine.JvmDateVO;
import com.alipay.autotuneservice.controller.model.baseLine.PodLineVO;
import com.alipay.autotuneservice.controller.model.tuneparam.AppTuneParamsVO;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamItem;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.BaseLineInfo;
import com.alipay.autotuneservice.dao.JvmMarketInfo;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.TuneParamInfoRepository;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.BaseLineRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMarketInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.service.BaseLineService;
import com.alipay.autotuneservice.service.TuneParamService;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.TuneParamUtil;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version BaseLineServiceImpl.java, v 0.1 2022年08月09日 10:58 上午 huoyuqi
 */
@Service
@Slf4j
public class BaseLineServiceImpl implements BaseLineService {

    @Autowired
    private AppInfoRepository       appInfoRepository;
    @Autowired
    private TuneParamService        tuneParamService;
    @Autowired
    private TunePipelineRepository  tunePipelineRepository;
    @Autowired
    private TuneParamInfoRepository tuneParamInfoRepository;
    @Autowired
    private JvmMarketInfo           jvmMarketInfo;
    @Autowired
    private PodInfo                 podInfo;
    @Autowired
    private BaseLineInfo            baseLineInfo;
    @Autowired
    private TunePlanRepository      tunePlanRepository;

    @Override
    public List<JvmDateVO> getJvmDate(Integer appId) {
        List<BaseLineRecord> records = baseLineInfo.selectByAppId(appId);
        if (CollectionUtils.isNotEmpty(records)) {
            //todo 追加版本号
            return records.stream().map(
                    t -> new JvmDateVO(t.getId(), t.getJvmMarketId(), DateUtils.asTimestamp(t.getCreatedTime()),
                            convertVersion(t.getVersion()))).collect(
                    Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public BaseLineVO getJvm(Integer appId) {
        // check app in pipeline
        Optional<TunePipeline> tunePipeline = getTunePipeline(appId);
        //应用自带默认参数  param jvmMarketId time version
        AppInfoRecord appInfoRecord = getAppInfoRecord(appId);
        List<TuneParamItem> defaultTuneParamItem = TuneParamUtil.convert2TuneParamItem(appInfoRecord.getAppDefaultJvm());
        long jvmMarketId = getJvmMarketId(appInfoRecord.getAppDefaultJvm());
        Integer defaultVersion = 0;
        Long defaultTime = DateUtils.asTimestamp(appInfoRecord.getUpdatedTime());
        if (jvmMarketId != 0) {
            BaseLineRecord record = baseLineInfo.getByJvmMarketId((int) jvmMarketId);
            if (record != null) {
                defaultVersion = record.getVersion();
                defaultTime = DateUtils.asTimestamp(record.getCreatedTime());
            }
        }
        // 是否在流程中的状态
        Boolean status = Boolean.FALSE;
        BaseLineContentVO newVersion = new BaseLineContentVO();
        //在流程中 流程param jvmMarketId time version
        if (tunePipeline.isPresent()) {
            status = Boolean.TRUE;
            BaseLineRecord baseLineRecord = baseLineInfo.getByAppId(appId);
            if (baseLineRecord == null) {
                throw new RuntimeException("baseLineRecord is null");
            }
            List<TuneParamItem> tuneParam = TuneParamUtil.convert2TuneParamItem(
                    jvmMarketInfo.getJvmInfo(baseLineRecord.getJvmMarketId()).getJvmConfig());
            newVersion = new BaseLineContentVO(tuneParam.stream().map(TuneParamItem::getOriginTuneParam).collect(Collectors.toList()),
                    convertVersion(baseLineRecord.getVersion()), DateUtils.asTimestamp(baseLineRecord.getCreatedTime()));
        }
        BaseLineContentVO previousVersion = new BaseLineContentVO(
                defaultTuneParamItem.stream().map(TuneParamItem::getOriginTuneParam).collect(Collectors.toList()),
                convertVersion(defaultVersion), defaultTime);
        if (status.equals(Boolean.TRUE)) {
            Integer pipelineId = tunePipeline.get().getPipelineId();
            TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePipeline.get().getTunePlanId());
            if (null == tunePlan) {
                throw new ServerException(ResultCode.NO_DATA_IN_DB);
            }
            return new BaseLineVO(Boolean.TRUE, newVersion, previousVersion, appId, pipelineId, tunePlan.getPlanName());
        }
        return new BaseLineVO(Boolean.FALSE, newVersion, previousVersion, appId, null, null);
    }

    @Override
    public List<HistoryBaseLineVO> getHistoryJvm(Integer appId) {
        log.info("getHistoryJvm enter");
        List<BaseLineRecord> baseLineRecords = baseLineInfo.selectByAppId(appId);
        if (CollectionUtils.isEmpty(baseLineRecords)) {
            return null;
        }
        // 获取应用的jvmMarketIds
        List<Integer> jvmMarketIds = new ArrayList<>();
        List<Integer> pipelineIds = new ArrayList<>();
        baseLineRecords.forEach(p -> {
            jvmMarketIds.add(p.getJvmMarketId());
            pipelineIds.add(p.getPipelineId());
        });
        // 获取jvm参数 并构建jvmMap
        List<JvmMarketInfoRecord> jvmMarketInfoRecords = jvmMarketInfo.getJvmInfo(jvmMarketIds);
        if (CollectionUtils.isEmpty(jvmMarketInfoRecords)) {
            return null;
        }
        HashMap<Integer, List<String>> jvmIdMap = new HashMap<>();
        jvmMarketInfoRecords.forEach(record -> jvmIdMap.put(record.getId(), TuneParamUtil.convert2TuneParamItem(record.getJvmConfig())
                .stream().map(TuneParamItem::getOriginTuneParam).collect(Collectors.toList())));
        //获取tunePlan 创建人 planName status  tunePipeline->tunePlan
        List<TunePipeline> tunePipelines = tunePipelineRepository.batchFindPipelinesByPipelines(pipelineIds);
        if (tunePipelines.isEmpty()) {
            return null;
        }
        HashMap<Integer, Integer> planPipeLineIdMap = new HashMap<>();
        tunePipelines.forEach(t -> planPipeLineIdMap.put(t.getTunePlanId(), t.getPipelineId()));
        List<Integer> tunePlanIds = tunePipelines.stream().map(TunePipeline::getTunePlanId).collect(Collectors.toList());
        List<TunePlan> tunePlans = tunePlanRepository.batchFindTunePlanByPipelineId(tunePlanIds);
        HashMap<Integer, TunePlan> pipeLinePlanMap = new HashMap<>();
        tunePlans.forEach(tunePlan -> pipeLinePlanMap.put(planPipeLineIdMap.get(tunePlan.getId()), tunePlan));

        //获取历史版本
        List<HistoryBaseLineVO> vos = new ArrayList<>();
        baseLineRecords.forEach(record -> {
            try {
                Integer pipelineId = record.getPipelineId();
                String version = convertVersion(record.getVersion());
                Integer jvmMarketId = record.getJvmMarketId();
                TunePlan tunePlan = pipeLinePlanMap.get(pipelineId);
                //todo tunePlan追加一列 createBy
                vos.add(new HistoryBaseLineVO(pipelineId, appId, tunePlan.getTunePlanStatus(), tunePlan.getPlanName(), null,
                        DateUtils.asTimestamp(record.getCreatedTime()), version,
                        jvmMarketId, jvmIdMap.get(jvmMarketId), Boolean.TRUE));
            } catch (Exception e) {
                log.warn("getHistoryJvm occurs an error jvmMarketId:{}, pipelineId:{}", record.getJvmMarketId(), record.getPipelineId());
            }

        });
        return CollectionUtils.isNotEmpty(vos) ? vos : null;
    }

    @Override
    public AppTuneParamsVO getCompare(Integer appId, Integer currentMarketId, Integer historyMarketId, String version) {
        //默认值情况下
        if (StringUtils.isEmpty(version)) {
            throw new RuntimeException("version must not be null");
        }
        Integer compareVersion = convertToVersion(version);
        if (null == historyMarketId) {
            if ((compareVersion - 1) != 0) {
                BaseLineRecord baseLineRecord = baseLineInfo.getByAppIdVersion(appId, compareVersion - 1);
                if (baseLineRecord != null) {
                    historyMarketId = baseLineRecord.getJvmMarketId();
                }
            }
        }

        //默认值情况下
        if (null == historyMarketId) {
            throw new RuntimeException("historyMarketId is null, compare over");
        }

        //当前和历史对比
        List<TuneParamItem> historyTuneParamItem = getJvmConfig(historyMarketId);
        List<TuneParamItem> currentTuneParamItem = getJvmConfig(currentMarketId);
        List<TuneParamItem> tuneParamItems = TuneParamUtil.mergeUpdateTuneParamItem(historyTuneParamItem, currentTuneParamItem);
        AppTuneParamsVO appTuneParamsVO = new AppTuneParamsVO();
        appTuneParamsVO.setTuneParamItems(tuneParamItems);
        appTuneParamsVO.countParamNums();
        appTuneParamsVO.setCompareVersion(convertVersion(compareVersion - 1));
        appTuneParamsVO.setAppId(appId);
        return appTuneParamsVO;
    }

    @Override
    public List<PodLineVO> getPodLine(Integer appId) {
        //默认jvm
        AppInfoRecord appInfoRecord = getAppInfoRecord(appId);
        //应用默认jvm
        String defaultJvm = appInfoRecord.getAppDefaultJvm();
        if (StringUtils.isEmpty(defaultJvm)) {
            return null;
        }
        long defaultJvmMarketId = getJvmMarketId(defaultJvm);
        String defaultVersion = defaultJvmMarketId == 0 ? "V0" : convertVersion(
                baseLineInfo.getByJvmMarketId((int) defaultJvmMarketId).getVersion());
        List<String> defaultJvmDetail = TuneParamUtil.convert2TuneParamItem(defaultJvm).stream().map(TuneParamItem::getOriginTuneParam)
                .collect(Collectors.toList());
        AppTag appTag = null;
        if (StringUtils.isNotEmpty(appInfoRecord.getAppTag())) {
            appTag = JSON.parseObject(appInfoRecord.getAppTag(), new TypeReference<AppTag>() {});
        }
        //获取所有pod
        List<PodInfoRecord> records = podInfo.getByAppId(appId);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        //获取所有pod的jvmMarketId  map: recordId -> jvmMarketId 正常 默认:0 异常:-1
        List<Integer> jvmMarketIdList = new ArrayList<>();
        HashMap<Integer, Integer> idJvmIdMap = new HashMap<>();
        records.forEach(p -> {
            Long jvmId = UserUtil.getJvmMarketId(p.getPodJvm(), defaultJvm);
            idJvmIdMap.put(p.getId(), jvmId.intValue());
            if (jvmId > 0) {
                jvmMarketIdList.add(jvmId.intValue());
            }
        });

        //jvmId -> jvmDetail
        HashMap<Integer, List<String>> jvmMap = new HashMap<>();
        List<JvmMarketInfoRecord> jvmList = jvmMarketInfo.getJvmInfo(jvmMarketIdList);
        jvmList.forEach(j -> jvmMap.put(j.getId(), StringUtils.isEmpty(j.getJvmConfig()) ? null : TuneParamUtil.convert2TuneParamItem(
                j.getJvmConfig()).stream()
                .map(TuneParamItem::getOriginTuneParam)
                .collect(Collectors.toList())));

        //jvmId -> version
        List<BaseLineRecord> lineList = baseLineInfo.getByJvmMarketId(jvmMarketIdList);
        HashMap<Integer, String> versionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(lineList)) {
            lineList.forEach(v -> versionMap.put(v.getJvmMarketId(), convertVersion(v.getVersion())));
        }

        AppTag finalAppTag = appTag;
        return records.stream().map(
                r -> {
                    try {
                        return new PodLineVO(appId, r.getId(), r.getPodName(), r.getIp(),
                                null == finalAppTag && finalAppTag.getJavaVersion() !=null ? null : finalAppTag.getJavaVersion(),
                                getSpec(r.getCpuCoreLimit(), r.getMemLimit()),
                                getJvmDetail(idJvmIdMap, jvmMap, r.getId(), defaultJvmDetail, r.getPodJvm(), defaultJvm),
                                getVersion(idJvmIdMap, versionMap, r.getId(), defaultVersion, r.getPodJvm(), defaultJvm),
                                (int) UserUtil.getJvmMarketId(r.getPodJvm(), defaultJvm),
                                (int) UserUtil.getJvmMarketId(r.getPodJvm(), defaultJvm) >= 1);
                    } catch (Exception e) {
                        log.info("single pod occurs an error appId: {}, podId: {}", appId, r.getId(), e);
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private List<String> getJvmDetail(HashMap<Integer, Integer> idJvmIdMap, HashMap<Integer, List<String>> jvmMap, Integer id,
                                      List<String> defaultJvmDetail, String jvm, String defaultJvm) {
        try {
            Long jvmId = UserUtil.getJvmMarketId(jvm, defaultJvm);
            if (jvmId == 0) {
                return defaultJvmDetail;
            }
            if (jvmId == -1) {
                return TuneParamUtil.convert2TuneParamItem(jvm).stream().map(TuneParamItem::getOriginTuneParam).collect(
                        Collectors.toList());
            }
            return jvmMap.get(idJvmIdMap.get(id));
        } catch (Exception e) {
            log.error("getJvmDetail occurs an error, podId is: {},  error: {}", id, e);
            return null;
        }

    }

    private String getVersion(HashMap<Integer, Integer> idJvmIdMap, HashMap<Integer, String> versionMap, Integer id, String defaultVersion,
                              String jvm, String defaultJvm) {
        try {
            Long jvmId = UserUtil.getJvmMarketId(jvm, defaultJvm);
            if (jvmId == 0) {
                return defaultVersion;
            }
            if (jvmId == -1) {
                return "V-1";
            }
            return versionMap.get(idJvmIdMap.get(id));
        } catch (Exception e) {
            log.error("getVersion occurs an error, podId is: {}, error: {}", id, e);
            return null;
        }

    }

    private List<TuneParamItem> getJvmConfig(Integer jvmMarketId) {
        if (null == jvmMarketId) {
            return new ArrayList<>();
        }
        JvmMarketInfoRecord jvmMarketInfoRecord = jvmMarketInfo.getJvmInfo(jvmMarketId);
        if (null != jvmMarketInfoRecord) {
            return TuneParamUtil.convert2TuneParamItem(jvmMarketInfoRecord.getJvmConfig());
        }
        return new ArrayList<>();
    }

    private String getSpec(Integer cpuLimit, Integer memLimit) {
        double mem = 0;
        if (memLimit != null) {
            mem = ((int) (memLimit * 100.0 / 1024)) / 100.0;
        }
        return String.format("%sC%sG", cpuLimit == null ? 0 : cpuLimit, mem);
    }

    private AppInfoRecord getAppInfoRecord(Integer appId) {
        AppInfoRecord appInfoRecord = appInfoRepository.getById(appId);
        if (appInfoRecord == null) {
            throw new ServerException(ResultCode.NO_DATA_IN_DB);
        }
        return appInfoRecord;
    }

    private Optional<TunePipeline> getTunePipeline(Integer appId) {
        List<TunePipeline> tunePipelines = tunePipelineRepository.findByAppIdAndStatus(appId, null);
        return tunePipelines.stream().filter(p -> !p.getStatus().equals(Status.CLOSED)).filter(p1 -> !p1.getStatus().equals(Status.CANCEL))
                .findFirst();
    }

    // 版本号4->V4
    private String convertVersion(Integer version) {
        return String.format("V%s", version);
    }

    private Integer convertToVersion(String version) {
        return Integer.valueOf(version.substring(1));
    }

    private long getJvmMarketId(String jvm) {
        if (StringUtils.isEmpty(jvm)) {
            return -1;
        }
        if (jvm.contains(UserUtil.TUNE_JVM_APPEND)) {
            int startIndex = jvm.indexOf(UserUtil.TUNE_JVM_APPEND);
            return Long.parseLong(jvm.substring(startIndex).split(" ", 2)[0].split("=")[1]);
        }
        return 0;
    }

}