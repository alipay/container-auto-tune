/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import com.alipay.autotuneservice.controller.model.tuneparam.AppTuneParamsVO;
import com.alipay.autotuneservice.controller.model.tuneparam.SubmitTuneParamsRequest;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamItem;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsRequest;
import com.alipay.autotuneservice.controller.model.tuneparam.UpdateTuneParamsVO;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.TuneParamInfoRepository;
import com.alipay.autotuneservice.dao.TunePipelinePhaseRepository;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneParamInfoRecord;
import com.alipay.autotuneservice.model.pipeline.PipelineStatus;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.model.tune.params.DecisionedTuneParam;
import com.alipay.autotuneservice.model.tune.params.TuneParamUpdateStatus;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.service.ConfigInfoService;
import com.alipay.autotuneservice.service.TuneParamService;
import com.alipay.autotuneservice.service.pipeline.TunePipelineService;
import com.alipay.autotuneservice.tunepool.TuneProcessor;
import com.alipay.autotuneservice.tunepool.TuneSource;
import com.alipay.autotuneservice.util.ObjectUtil;
import com.alipay.autotuneservice.util.TuneParamUtil;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.alipay.autotuneservice.util.ObjectUtil.checkIntegerPositive;

/**
 * @author huangkaifei
 * @version : TuneParamServiceImpl.java, v 0.1 2022年05月17日 3:20 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class TuneParamServiceImpl implements TuneParamService {

    @Autowired
    private ConfigInfoService       configInfoService;
    @Autowired
    private AppInfoRepository       appInfoRepository;
    @Autowired
    private TuneProcessor           tuneProcessor;
    @Autowired
    private TuneParamInfoRepository tuneParamInfoRepository;
    @Autowired
    private TunePipelineService     tunePipelineService;
    @Autowired
    private TunePipelineRepository  tunePipelineRepository;
    @Autowired
    TunePipelinePhaseRepository tunePipelinePhaseRepository;
    @Autowired
    private DSLContext         dslContext;
    @Autowired
    private TunePlanRepository tunePlanRepository;

    @Override
    public AppTuneParamsVO getTuneParams(Integer appId, Integer pipelineId) {
        log.info("getTuneParams enter. appId={}, pipelineId={}", appId, pipelineId);
        AppTuneParamsVO appTuneParamsVO = new AppTuneParamsVO();
        appTuneParamsVO.setAppId(checkIntegerPositive(appId));
        appTuneParamsVO.setPipelineId(checkIntegerPositive(pipelineId));

        List<TuneParamItem> result = getAppTuneParamItems(appId, pipelineId, UserUtil.getAccessToken());
        log.info("getTuneParams - getAppTuneParamItems res={}", JSON.toJSONString(result));
        appTuneParamsVO.setTuneParamItems(result);
        appTuneParamsVO.countParamNums();
        // find tune group
        List<TuneConfig> tuneConfigs = configInfoService.findTuneGroupsByAppId(appId);
        log.info("getTuneParams - get tuneGroups res={}", JSON.toJSONString(tuneConfigs));
        appTuneParamsVO.setTuneGroups(tuneConfigs);
        // check whether tuneParam finished
        TuneParamInfoRecord record = tuneParamInfoRepository.findTunableTuneParamRecord(appId, pipelineId);
        if (record != null) {
            appTuneParamsVO.setTuneParamFinished(StringUtils.equals(record.getUpdateStatus(), TuneParamUpdateStatus.END.name()));
        }
        appTuneParamsVO.setAutoTune(!isGrayPipelineId(pipelineId) && tunePipelineService.checkTunePlanIsAuto(pipelineId));

        TunePipeline tunePipeline = tunePipelineRepository.findByPipelineId(pipelineId);
        if (null != tunePipeline) {
            TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePipeline.getTunePlanId());
            if (tunePlan != null) {
                appTuneParamsVO.setGrayCancelStatus(tunePlan.getTuneStatus() != null && tunePlan.getTuneStatus().equals(TunePlanStatus.CANCEL));
            }
        }
        return appTuneParamsVO;
    }

    private Boolean isGrayPipelineId(Integer pipelineId) {
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
        return tunePipeline.getPipelineStatus() == PipelineStatus.GRAY && TuneStage.ADJUSTMENT_PARAMETER.equals(tunePipeline.getStage());
    }

    /**
     * 提交调优参数. 包含参数和分组
     *
     * @param appId
     * @param pipelineId
     * @param request
     * @return
     */
    @Override
    public Boolean submitTuneParam(Integer appId, Integer pipelineId, SubmitTuneParamsRequest request) {
        Preconditions.checkArgument(request != null, "SubmitTuneParamsRequest can not be null.");
        log.info("submitTuneParam enter. appId={}, pipelineId={}, request={}", checkIntegerPositive(appId),
                checkIntegerPositive(pipelineId), JSON.toJSONString(request));
        // DB里查找appId + pipelineId + update_status(not end)
        TuneParamInfoRecord tunableTuneParamRecord = tuneParamInfoRepository.findTunableTuneParamRecord(appId, pipelineId);
        if (tunableTuneParamRecord != null && TuneParamUpdateStatus.findByName(tunableTuneParamRecord.getUpdateStatus())
                == TuneParamUpdateStatus.END) {
            String errMsg = String.format("Can not update TuneParam Record for END status for pipelineId=%s and appId=%s", pipelineId,
                    appId);
            log.warn("submitTuneParam - {}", errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        request.setAppId(appId);
        request.setPipelineId(pipelineId);
        Boolean res = saveOrUpdate(request);
        log.info("submitTuneParam end. appId={}, pipelineId={}, res={}", appId, pipelineId, res);
        return res;
    }

    @Override
    public Boolean submitGrayAutoTuneParam(Integer appId, Integer pipelineId, String appDefaultJvm, String recommendJvm) {
        log.info("submitGrayAutoTuneParam enter. appId={}, pipelineId={},appDefaultJvm={}, recommendJvm={}", appId, pipelineId,
                appDefaultJvm, recommendJvm);
        ObjectUtil.checkIntegerPositive(appId, "appId不能为空");
        ObjectUtil.checkIntegerPositive(pipelineId, "pipelineId不能为空");
        String msg = String.format("appId=%s, pipelineId=%s, appDefaultJvm=%s, recommendJvm=%s", appId, pipelineId, appDefaultJvm,
                recommendJvm);
        List<TuneParamItem> defaultJvmItems = null;
        List<TuneParamItem> recommendJvmItems = null;
        if (StringUtils.isNotBlank(appDefaultJvm)) {
            defaultJvmItems = TuneParamUtil.convert2TuneParamItem(appDefaultJvm);
        }
        if (StringUtils.isNotBlank(recommendJvm)) {
            recommendJvmItems = TuneParamUtil.convert2TuneParamItem(recommendJvm);
        }
        SubmitTuneParamsRequest request = SubmitTuneParamsRequest.builder()
                .appId(appId)
                .pipelineId(pipelineId)
                .tuneParamItems(recommendJvmItems)
                .tuneGroups(Lists.newArrayList())
                .defaultTuneParamItems(defaultJvmItems)
                .operator("TMAESTRO")
                .flag(true)
                .build();
        return saveOrUpdate(request);
    }

    @Override
    public Boolean submitAutoTuneParam(Integer appId, Integer pipelineId, String appDefaultJvm, String recommendJvm) {
        try {
            log.info("submitAutoTuneParam enter. appId={}, pipelineId={},appDefaultJvm={}, recommendJvm={}", appId, pipelineId,
                    appDefaultJvm, recommendJvm);
            ObjectUtil.checkIntegerPositive(appId, "appId不能为空");
            ObjectUtil.checkIntegerPositive(pipelineId, "pipelineId不能为空");
            String msg = String.format("appId=%s, pipelineId=%s, appDefaultJvm=%s, recommendJvm=%s", appId, pipelineId, appDefaultJvm,
                    recommendJvm);
            List<TuneParamItem> defaultJvmItems = null;
            List<TuneParamItem> recommendJvmItems = null;
            if (StringUtils.isNotBlank(appDefaultJvm)) {
                defaultJvmItems = TuneParamUtil.convert2TuneParamItem(appDefaultJvm);
            }
            if (StringUtils.isNotBlank(recommendJvm)) {
                recommendJvmItems = TuneParamUtil.convert2TuneParamItem(recommendJvm);
            }
            SubmitTuneParamsRequest request = SubmitTuneParamsRequest.builder()
                    .appId(appId)
                    .pipelineId(pipelineId)
                    .tuneParamItems(recommendJvmItems)
                    .tuneGroups(Lists.newArrayList())
                    .defaultTuneParamItems(defaultJvmItems)
                    .flag(false)
                    .operator("TMAESTRO")
                    .build();
            return saveOrUpdate(request);
        } catch (Exception e) {
            log.info("submitAutoTuneParam occurs an error.", e);
            return false;
        }
    }

    private Boolean saveOrUpdate(SubmitTuneParamsRequest request) {
        try {
            log.info("saveOrUpdate enter ");
            TuneParamInfoRecord record = new TuneParamInfoRecord();
            if (CollectionUtils.isNotEmpty(request.getTuneParamItems())) {
                // 获取app default tune param
                List<TuneParamItem> defaultItems = request.getDefaultTuneParamItems();
                List<TuneParamItem> defaultTuneParamItems = CollectionUtils.isNotEmpty(defaultItems) ? defaultItems
                        : getAppDefaultTuneParamItems(request.getAppId(), request.getPipelineId());
                record.setDefaultParam(JSON.toJSONString(defaultTuneParamItems));
                // merge tune params
                List<TuneParamItem> mergeResult = TuneParamUtil.mergeUpdateTuneParamItem(defaultTuneParamItems,
                        TuneParamUtil.wrapUpdateTuneParamsWithParamName(request.getTuneParamItems()));
                record.setUpdateParams(JSON.toJSONString(mergeResult));
            }
            if (CollectionUtils.isNotEmpty(request.getTuneGroups())) {
                record.setChangedTuneGroup(JSON.toJSONString(request.getTuneGroups()));
            }
            record.setUpdateStatus(TuneParamUpdateStatus.END.name());
            record.setAppId(request.getAppId());
            record.setPipelineId(request.getPipelineId());
            String decisionId = generateSubmitTuneParamUUID();
            record.setDecisionId(generateSubmitTuneParamUUID());
            record.setOperator(request.getOperator());
            //判断灰度主流程是第一次提交
            if (isGray(request.getPipelineId())) {
                if (!request.getFlag()) {
                    record.setUpdateStatus(TuneParamUpdateStatus.RUNNING.name());
                }
            }
            TuneParamInfoRecord recordFromDB = tuneParamInfoRepository.findTunableTuneParamRecord(request.getAppId(),
                    request.getPipelineId());
            if (recordFromDB != null) {
                record.setId(recordFromDB.getId());
                log.info("saveOrUpdate - start to update appId={}, pipelineId={}", request.getAppId(), request.getPipelineId());
                tuneParamInfoRepository.update(record);
                return Boolean.TRUE;
            }
            log.info("saveOrUpdate - start to insert. appId={}, pipelineId={}", request.getAppId(), request.getPipelineId());
            tuneParamInfoRepository.insert(record);
            log.info("saveOrUpdate success. appId={}, pipelineId={}, decisionId={}", request.getAppId(), request.getPipelineId(),
                    decisionId);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("saveOrUpdate occurs an error", e);
            return Boolean.FALSE;
        }
    }

    @Override
    public TuneParamUpdateStatus queryTuneParamStatus(String decisionId) {
        log.info("queryTuneParamStatus enter. decisionId={}", decisionId);
        if (StringUtils.isBlank(decisionId)) {
            throw new IllegalArgumentException("decisionId can be empty.");
        }
        TuneParamInfoRecord record = tuneParamInfoRepository.findByDecisionId(decisionId);
        if (record == null) {
            throw new IllegalArgumentException(String.format("Can not find record by decisionId=%s.", decisionId));
        }
        return TuneParamUpdateStatus.findByName(record.getUpdateStatus());
    }

    @Override
    public TuneParamUpdateStatus queryTuneParamStatus(Integer appId, Integer pipelineId) {
        TuneParamInfoRecord record = tuneParamInfoRepository.getByAppIdAndPipelineId(checkIntegerPositive(appId),
                checkIntegerPositive(pipelineId));
        if (record == null) {
            log.warn("queryTuneParamStatus - can not find TuneParamInfoRecord for appId={}, pipelineId={}", appId, pipelineId);
            return null;
        }
        return TuneParamUpdateStatus.findByName(record.getUpdateStatus());
    }

    @Override
    public DecisionedTuneParam getDecisionedTuneParams(Integer appId, Integer pipelineId) {
        DecisionedTuneParam decisionedTuneParam = new DecisionedTuneParam();
        TuneParamUpdateStatus tuneParamUpdateStatus = isGray(pipelineId) ? TuneParamUpdateStatus.RUNNING : TuneParamUpdateStatus.END;
        log.info("getDecisionedTuneParams tuneParamUpdateStatus is: {}", tuneParamUpdateStatus);
        TuneParamInfoRecord record = tuneParamInfoRepository.getByAppIdAndPipelineIdAndStatus(checkIntegerPositive(appId),
                checkIntegerPositive(pipelineId), tuneParamUpdateStatus);
        if (record == null) {
            log.warn("getDecisionedTuneParams - can not find tune params record for appId={} and pipelineId={}", appId, pipelineId);
            return null;
        }
        decisionedTuneParam.setDecisionedTuneParamItems(getDecisionedParamItem(record));
        decisionedTuneParam.setDecisionedTuneGroups(getDecisionedTuneGroup(record));
        return decisionedTuneParam;
    }

    @Override
    public UpdateTuneParamsVO updateTuneParams(UpdateTuneParamsRequest request) {
        Integer appId = checkIntegerPositive(request.getAppId());
        Integer pipelineId = checkIntegerPositive(request.getPipelineId());
        log.info("updateTuneParams enter. request={}", JSON.toJSONString(request));
        UpdateTuneParamsVO updateTuneParamsVO = new UpdateTuneParamsVO();
        List<TuneParamItem> updatedTuneParamItems = request.getUpdatedTuneParamItems();
        if (CollectionUtils.isEmpty(updatedTuneParamItems)) {
            log.warn("updateTuneParams - request updated params is empty, so will not handle it.");
            throw new UnsupportedOperationException("request updated params is empty, so will not handle it.");
        }
        // 获取app default tune param
        List<TuneParamItem> defaultTuneParamItems = getAppTuneParamItems(appId, pipelineId, UserUtil.getAccessToken());
        // merge tune params
        List<TuneParamItem> mergeResult = TuneParamUtil.mergeUpdateTuneParamItem(defaultTuneParamItems,
                TuneParamUtil.wrapUpdateTuneParamsWithParamName(updatedTuneParamItems));
        updateTuneParamsVO.setTuneParamItems(mergeResult);
        log.info("updateTuneParams end. merge updated tuneParamItem result={}", JSON.toJSONString(mergeResult));
        return updateTuneParamsVO;
    }

    private List<TuneParamItem> getAppTuneParamItems(Integer appId, Integer pipelineId, String accessToken) {
        try {
            log.info("getAppTuneParamItems start. appId={}, pipelineId={}", appId, pipelineId);
            TuneParamInfoRecord tunableTuneParamRecord = tuneParamInfoRepository.findTuneParamRecord(appId, pipelineId,
                    TuneParamUpdateStatus.END);
            // 提交调优参数后直接从DB里的updateparams获取
            if (tunableTuneParamRecord != null) {
                String tuneParams = tunableTuneParamRecord.getUpdateParams();
                log.info("getAppTuneParamItems status=END from db, appId={}, pipelineId={}, res={}", appId, pipelineId, tuneParams);
                return JSON.parseObject(tuneParams, new TypeReference<List<TuneParamItem>>() {});
            }
            // get default JVM
            List<TuneParamItem> defaultTuneParam = getAppDefaultTuneParamItems(appId, pipelineId);
            // get recommend JVM
            List<TuneParamItem> appRecommendTuneParamItem = getAppRecommendTuneParamItem(appId, pipelineId, accessToken);
            // merge Result
            List<TuneParamItem> tuneParamItems = TuneParamUtil.mergeUpdateTuneParamItem(defaultTuneParam,
                    TuneParamUtil.wrapUpdateTuneParamsWithParamName(appRecommendTuneParamItem));
            log.info("getAppTuneParam for appId={}, pipelineId={}, res={}", appId, pipelineId, JSON.toJSONString(tuneParamItems));
            return tuneParamItems;
        } catch (Exception e) {
            log.error("getAppTuneParam occurs an error.", e);
            return Lists.newArrayList();
        }
    }

    private String buildAppTuneParamItemsKey(Integer appId, Integer pipelineId) {
        return String.format("appTuneParams_%s_%s", appId, pipelineId);
    }

    private String generateSubmitTuneParamUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private List<TuneParamItem> getDecisionedParamItem(TuneParamInfoRecord record) {
        if (record == null || StringUtils.isBlank(record.getUpdateParams())) {
            return Lists.newArrayList();
        }
        log.info("getDecisionedParam result={}", record.getUpdateParams());
        return JSON.parseObject(record.getUpdateParams(), new TypeReference<List<TuneParamItem>>() {});
    }

    private MetaData getMetaData(String accessToken, Integer appId, Integer pipelineId) {
        TuneEntity tuneEntity = TuneEntity.builder().accessToken(accessToken).appId(appId).pipelineId(pipelineId).build();
        TuneSource tuneSource = tuneProcessor.getTuneSource(tuneEntity);
        return tuneSource.experimentTunePool().getTuneMeta();
    }

    @Override
    public List<TuneParamItem> getAppRecommendTuneParamItem(Integer appId, Integer pipelineId, String accessToken) {
        try {
            MetaData metaData = getMetaData(accessToken, appId, pipelineId);
            String jvmCmd = metaData.getJvmCmd();
            log.info("getAppRecommendTuneParamItem - appId={},pipelineId={}, metaData={}", appId, pipelineId, JSON.toJSONString(metaData));
            return TuneParamUtil.convert2TuneParamItem(jvmCmd);
        } catch (Exception e) {
            log.info("getAppRecommendTuneParamItem occurs an error.", e);
            return Lists.newArrayList();
        }
    }

    private List<TuneParamItem> getAppDefaultTuneParamItems(Integer appId, Integer pipelineId) {
        // submit之后， appDefault从DB里获取
        TuneParamInfoRecord tunableTuneParamRecord = tuneParamInfoRepository.findTuneParamRecord(appId, pipelineId,
                TuneParamUpdateStatus.END);
        if (tunableTuneParamRecord != null) {
            String defaultParam = tunableTuneParamRecord.getDefaultParam();
            log.info("getAppDefaultTuneParamItems from db, appId={}, pipelineId={}, res={}", appId, pipelineId, defaultParam);
            return JSON.parseObject(defaultParam, new TypeReference<List<TuneParamItem>>() {});
        }
        AppInfoRecord appInfoRecord = appInfoRepository.getById(appId);
        if (appInfoRecord == null) {
            return Lists.newArrayList();
        }
        log.info("getAppDefaultTuneParamItems - appId={}, defaultJVM={}", appId, appInfoRecord.getAppDefaultJvm());
        return TuneParamUtil.convert2TuneParamItem(appInfoRecord.getAppDefaultJvm());
    }

    private List<TuneConfig> getDecisionedTuneGroup(TuneParamInfoRecord record) {
        if (record == null || StringUtils.isBlank(record.getChangedTuneGroup())) {
            return Lists.newArrayList();
        }
        log.info("queryChangeTuneGroup res={}", record.getChangedTuneGroup());
        return JSON.parseObject(record.getChangedTuneGroup(), new TypeReference<List<TuneConfig>>() {});
    }

    private Boolean isGray(Integer pipelineId) {
        TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
        return tunePipeline.getPipelineStatus().equals(PipelineStatus.GRAY) && tunePipeline.getStage().equals(
                TuneStage.ADJUSTMENT_PARAMETER);
    }
}