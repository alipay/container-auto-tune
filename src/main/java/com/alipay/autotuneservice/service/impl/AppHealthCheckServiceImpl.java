/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.configuration.ConstantsProperties;
import com.alipay.autotuneservice.controller.model.HealthCheckVO;
import com.alipay.autotuneservice.controller.model.HealthCheckVO.HealthCheckStatusModel;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.HealthCheckInfo;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.HealthCheckData;
import com.alipay.autotuneservice.dynamodb.repository.HealthCheckDataRepository;
import com.alipay.autotuneservice.model.ProblemContent;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.common.HealthCheckEnum;
import com.alipay.autotuneservice.model.common.HealthCheckItemStatus;
import com.alipay.autotuneservice.model.common.HealthCheckStatus;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.multiCloudAdapter.NosqlService;
import com.alipay.autotuneservice.schedule.riskstatistic.RiskStatisticPreTask;
import com.alipay.autotuneservice.service.AppHealthCheckService;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.TuneEvaluateService;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.GsonUtil;
import com.alipay.autotuneservice.util.HttpUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chenqu
 * @version : AppHealthCheckService.java, v 0.1 2022年04月26日 10:50 chenqu Exp $
 */
@Slf4j
@Service
public class AppHealthCheckServiceImpl implements AppHealthCheckService {
    private final       String PROBLEM_TYPE      = "problem_type";
    public static final int HEALTH_CHECK_COUNT = 10;

    @Autowired
    private HealthCheckInfo     healthCheckInfo;
    @Autowired
    private AppInfoRepository   appInfoRepository;
    @Autowired
    private HealthCheckDataRepository healthCheckDataRepository;
    @Autowired
    private TuneEvaluateService tuneEvaluateService;
    @Autowired
    private AppInfoService      appInfoService;
    @Autowired
    private AsyncTaskExecutor   eventExecutor;
    @Autowired
    private PodInfo             podInfo;
    @Autowired
    private NosqlService        nosqlService;
    @Autowired
    private ConstantsProperties constantsProperties;

    @Autowired
    private RiskStatisticPreTask riskStatisticPreTask;

    /**
     * 提交之后得到相应的结果
     *
     * @param appId
     * @return
     */
    @Override
    public Integer submitHealthCheck(Integer appId) {
        long startTime = System.currentTimeMillis();
        preCheck(appId);
        //提交任务
        AppInfoRecord appInfoRecord = appInfoRepository.getById(appId);
        if (appInfoRecord == null) {
            throw new RuntimeException(String.format("未找到指定应用=[%s]", appId));
        }
        List<PodInfoRecord> pods = podInfo.getPodInstallTuneAgentNumsByAppId(appId);
        if (CollectionUtils.isEmpty(pods)) {
            throw new RuntimeException(String.format("此应用没有安装agent，appId=[%s]", appId));
        }

        // 实时计算结果 TODO 测试整个流程运行的时间
        long tuneTime = 0;
        if(StringUtils.isNotEmpty(appInfoRecord.getAppTag())){
            AppTag appTag = GsonUtil.fromJson(appInfoRecord.getAppTag(), AppTag.class);
            if(appTag==null || appTag.getCollector()==null
                    || (appTag.getCollector()!=GarbageCollector.CMS_GARBAGE_COLLECTOR
                    && appTag.getCollector()!=GarbageCollector.G1_GARBAGE_COLLECTOR)){
                throw new RuntimeException(String.format("unsupported GarbageCollector，appId=[%s]", appId));
            }
            if(appTag.getLastModifyTime()!=null){
                tuneTime = appTag.getLastModifyTime();
                if(System.currentTimeMillis()-tuneTime < 60 * 60){
                    throw new RuntimeException(String.format("最近一次检查在1h内，请稍后检测，appId=[%s]", appId));
                }
            }
        }
        this.riskStatisticPreTask.caluPreDataOnline(appInfoRecord, tuneTime);
        this.riskStatisticPreTask.saveDailyStatDataOnline(appInfoRecord, tuneTime);

        //获取结果
        HealthCheckData checkData = getHealthCheckResult(appId);
        List<String> problemList = new ArrayList<>();

        // if java algorithm fail, try python
        String result = checkData == null ? getProblemType(appInfoRecord.getAppName(), appId) : checkData.getJvm_problem();
        //反解析一下，然后取problem_type数组，调用打分，执行update--->值&&ENDING
        Map<String, String> problemDetailMap = new HashMap<>();
        Map<String, List<Long>> problemTimeMap = new HashMap<>();
        long parseStartTime = System.currentTimeMillis();
        if (StringUtils.isNotEmpty(result)) {
            Map<String, ProblemContent> problemContentMap = JSONObject.parseObject(result,
                    new TypeReference<Map<String, ProblemContent>>() {});
            problemContentMap.forEach((key, problemContent) -> {
                if (problemContent.isValid()) {
                    String reason = appInfoRecord.getAppName();
                    List<Long> timeLong = new ArrayList<>();
                    if (MapUtils.isNotEmpty(problemContent.getProblem_pod())) {
                        problemContent.getProblem_pod().forEach((podName, time) -> {
                            //获取时间戳
                            String tempTime = time.substring(time.lastIndexOf(">") + 1);
                            timeLong.add(Long.parseLong(tempTime.substring(0, tempTime.indexOf("."))));
                        });
                    }
                    // 原因 appName + problemText+:
                    reason = String.format("%s %s %s", reason, problemContent.getProblem_text(), ":");
                    problemList.add(problemContent.getProblem_type());
                    problemDetailMap.put(problemContent.getProblem_type(), reason);
                    problemTimeMap.put(problemContent.getProblem_type(), timeLong);
                }
            });
        }
        //初始化check任务
        Integer checkerId = initCheckTask(appId, appInfoRecord.getAccessToken(), String.valueOf(appInfoRecord.getUserId()), result);
        long executeTime = System.currentTimeMillis() - startTime;
        //异步触发诊断
        //        eventExecutor.execute(() -> delayExecute(problemList, checkerId, problemDetailMap, problemTimeMap));
        delayExecute(problemList, checkerId, problemDetailMap, problemTimeMap);
        log.info("submitHealthCheck execute time is:{}", System.currentTimeMillis() - startTime);
        return checkerId;
    }

    private String getProblemType(String appName, Integer appId) {
        try {
            Header header = new BasicHeader("Cookie", "token");
            String url = String.format("%s/%s?app=%s&app_id=%s&timestamp=%s", constantsProperties.getAlgorithmUrl(),
                    "api/intelligent/getJvmProblemInfo", appName, appId, System.currentTimeMillis());
            URI uri = new URI(url);
            String str = HttpUtil.callGetApi(uri, 8000, header);
            if (StringUtils.isEmpty(str)) {
                throw new RuntimeException(
                        String.format("request monitorData failed from algo by appName=[%s], appId=[%s]", appName, appId));
            }
            JSONObject jsonObject = JSONObject.parseObject(str);
            if (StringUtils.equals("success", jsonObject.get("state").toString()) && jsonObject.containsKey("result")) {
                JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.get("result").toString());
                return jsonObject1.get("jvm_problem").toString();
            }
            log.info("getProblemType get algo data failed response is:{}",str);
            throw new RuntimeException(String.format("Can't find monitorData from algo by appName=[%s], appId=[%s]", appName, appId));
        } catch (Exception e) {
            log.info("getProblemType occurs an error", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer getHealthScore(Integer appId){
        AppInfoRecord appInfoRecord = appInfoRepository.getById(appId);
        HealthCheckData checkData = getHealthCheckResult(appId);
        List<String> problemList = new ArrayList<>();
        String result = checkData == null ? getProblemType(appInfoRecord.getAppName(), appId) : checkData.getJvm_problem();
        if (StringUtils.isNotEmpty(result)) {
            Map<String, ProblemContent> problemContentMap = JSONObject.parseObject(result,
                    new TypeReference<Map<String, ProblemContent>>() {});
            problemContentMap.forEach((key, problemContent) -> {
                if (problemContent.isValid()) {
                    problemList.add(problemContent.getProblem_type());
                }
            });
        }
        return tuneEvaluateService.evaluate(problemList);
    }


    private void delayExecute(List<String> problemList, Integer checkerId, Map<String, String> problemDetailMap,
                              Map<String, List<Long>> problemTimeMap) {
        //获取打分
        int grade = tuneEvaluateService.evaluate(problemList);
        //处理诊断
        List<String> checkProblemList = HealthCheckEnum.healthCheckNames();
        final List<HealthCheckStatusModel> healthCheckList = Lists.newArrayList();
        try {
            checkProblemList.forEach(item -> {
                HealthCheckStatusModel healthCheckStatusModel = new HealthCheckStatusModel();
                healthCheckStatusModel.setType(item);
                HealthCheckStatusModel model = constructHealthCheckList(item,
                        problemList.contains(item) ? HealthCheckItemStatus.ABNORMAL.name() : HealthCheckItemStatus.NORMAL.name(),
                        problemList.contains(item) ? HealthCheckEnum.valueOf(item).getAbnormal()
                                : HealthCheckEnum.valueOf(item).getNormal(),
                        problemList.contains(item) ? problemDetailMap.get(item) : null,
                        problemList.contains(item) ? problemTimeMap.get(item) : new ArrayList<>());
                healthCheckList.add(model);
                //执行更新
                //                healthCheckInfo.update(checkerId, String.valueOf(grade), HealthCheckStatus.RUNNING.name(),
                //                        JSONObject.toJSONString(healthCheckList));
                //                try {
                //                    TimeUnit.SECONDS.sleep(1);
                //                } catch (InterruptedException e) {
                //                    //do noting
                //                }
            });
            //更新状态为完成
            //执行更新
            healthCheckInfo.update(checkerId, String.valueOf(grade), HealthCheckStatus.ENDING.name(),
                    JSONObject.toJSONString(healthCheckList));
        } catch (Exception e) {
            //do noting
            log.error("delayExecute is error", e);
        }
    }

    private HealthCheckStatusModel constructHealthCheckList(String type, String status, String conclusion, String problemDetail,
                                                            List<Long> probleTime) {
        HealthCheckStatusModel healthCheckStatusModel = new HealthCheckStatusModel();
        healthCheckStatusModel.setType(type);
        healthCheckStatusModel.setStatus(status);
        healthCheckStatusModel.setConclusion(conclusion);
        healthCheckStatusModel.setProblemDetail(problemDetail);
        healthCheckStatusModel.setProblemTime(probleTime);
        return healthCheckStatusModel;
    }

    @Override
    public HealthCheckVO refreshCheck(Integer healthCheckId, int count) {
        HealthCheckInfoRecord record = healthCheckInfo.selectById(healthCheckId);
        if (record == null) {
            return null;
        }
        String problemPoint = record.getProbleamPoint();
        if (StringUtils.isEmpty(problemPoint)) {
            return null;
        }
        List<HealthCheckStatusModel> list = JSONObject.parseObject(problemPoint, new TypeReference<List<HealthCheckStatusModel>>() {});
        if(count>list.size()){
            count = list.size();;
        }
        List<HealthCheckStatusModel> modelList = list.subList(0, count);
        List<String> checkList = HealthCheckEnum.healthCheckNames();
        HealthCheckVO healthCheckVO = new HealthCheckVO();
        healthCheckVO.setId(healthCheckId);
        healthCheckVO.setAppId(record.getAppId());
        healthCheckVO.setCheckedNum(modelList.size());
        healthCheckVO.setCheckStartTime(DateUtils.asTimestamp(record.getCreatedTime()));
        healthCheckVO.setStatus(modelList.size() == HEALTH_CHECK_COUNT ? HealthCheckStatus.ENDING : HealthCheckStatus.RUNNING);
        healthCheckVO.setCheckEndTime(modelList.size() == HEALTH_CHECK_COUNT ? healthCheckVO.getCheckStartTime() + HEALTH_CHECK_COUNT * 1000L : null);
        //展示分为两种展示
        //第一种正在检测中
        if (!StringUtils.equals(record.getStatus(), HealthCheckStatus.INTERRUPT.name()) && modelList.size() < HEALTH_CHECK_COUNT) {
            for (int i = modelList.size(); i < HEALTH_CHECK_COUNT; i++) {
                HealthCheckStatusModel model = new HealthCheckStatusModel();
                model.setType(checkList.get(i));
                model.setStatus(HealthCheckItemStatus.CHECKING.name());
                model.setConclusion("正在检测中");
                modelList.add(model);
            }
            healthCheckVO.setCheckStatusList(modelList);
            return healthCheckVO;
        }
        //第二种检测完成
        if (!StringUtils.equals(record.getStatus(), HealthCheckStatus.INTERRUPT.name()) && modelList.size() >= HEALTH_CHECK_COUNT) {
            healthCheckVO.setCheckStatusList(modelList);
            healthCheckVO.setScore(Integer.parseInt(record.getGrade()));
            healthCheckVO.setCheckNum(
                    (int) modelList.stream().filter(item -> StringUtils.equals(item.getStatus(), HealthCheckItemStatus.ABNORMAL.name()))
                            .count());
            return healthCheckVO;
        }
        return healthCheckVO;
    }

    /**
     * 健康详情
     *
     * @param healthCheckId 健康检查id
     * @return
     */
    @Override
    public HealthCheckVO healthDetail(Integer healthCheckId) {
        HealthCheckInfoRecord record = healthCheckInfo.selectById(healthCheckId);
        if (record == null) {
            throw new RuntimeException(String.format("健康检测记录为空,HealthCheckID=[%s]", healthCheckId));
        }
        HealthCheckVO healthCheckVO = new HealthCheckVO();
        healthCheckVO.setId(healthCheckId);
        healthCheckVO.setAppId(record.getAppId());
        healthCheckVO.setCheckStartTime(DateUtils.asTimestamp(record.getCreatedTime()));
        //todo 表中字段加一更改时间
        healthCheckVO.setCheckEndTime(DateUtils.asTimestamp(record.getCreatedTime()));
        healthCheckVO.setCheckTime(DateUtils.asTimestamp(record.getCreatedTime()) - DateUtils.asTimestamp(record.getCreatedTime()));
        healthCheckVO.setScore(Integer.parseInt(record.getGrade()));
        // 剩余的检测项个数
        AppInfoRecord appInfoRecord = appInfoService.selectById(record.getAppId());
        if (appInfoRecord == null && StringUtils.isEmpty(appInfoRecord.getAppName())) {
            throw new RuntimeException(String.format("appInfoRecord记录为空,appId=[%s]", record.getAppId()));
        }
        String result = "";
        List<HealthCheckStatusModel> modelList = new ArrayList<>();
        //反解析一下，然后取problem_type数组，调用打分，执行update--->值&&ENDING
        if (StringUtils.isNotEmpty(result)) {
            Map<String, Map<String, String>> map = JSONObject.parseObject(result, new TypeReference<Map<String, Map<String, String>>>() {});
            map.forEach((key, value) -> {
                HealthCheckStatusModel healthCheckStatusModel = new HealthCheckStatusModel();
                String item = value.get(PROBLEM_TYPE);
                if (StringUtils.isNotEmpty(item) && EnumUtils.isValidEnum(HealthCheckEnum.class, item)) {
                    healthCheckStatusModel.setType(item);
                    healthCheckStatusModel.setStatus(HealthCheckItemStatus.ABNORMAL.name());
                    healthCheckStatusModel.setConclusion(HealthCheckEnum.valueOf(item).getAbnormal());
                    modelList.add(healthCheckStatusModel);
                }
            });
        }
        healthCheckVO.setCheckStatusList(modelList);
        healthCheckVO.setCheckNum(modelList.size());
        return healthCheckVO;
    }

    @Override
    public HealthCheckVO getLastData(Integer appId) {
        List<HealthCheckInfoRecord> recordList = healthCheckInfo.findByAppId(appId);
        if (CollectionUtils.isEmpty(recordList)) {
            return null;
        }
        HealthCheckVO healthCheckVO = refreshCheck(recordList.get(0).getId(), HEALTH_CHECK_COUNT);
        //获取需要人工确认的步骤
        //tunePlanRepository.findTunePlanByAppId(appId);
        return healthCheckVO;
    }

    /**
     * 查询健康检测任务状态的接口
     *
     * @param appId
     */
    public void preCheck(Integer appId) {
        List<HealthCheckInfoRecord> recordList = healthCheckInfo.findByAppId(appId);
        //判断是否有正在进行中的诊断
        Optional<HealthCheckInfoRecord> optional = recordList.stream()
                .filter(record -> HealthCheckStatus.RUNNING.name().equals(record.getStatus()))
                .findAny();
        if (optional.isPresent()) {
            throw new RuntimeException(String.format("存在检测中的任务,任务ID=[%s]", optional.get().getId()));
        }
    }

    private HealthCheckData getHealthCheckResult(Integer appId) {
        try {
            String dt = new SimpleDateFormat("yyyyMMdd").format(new Date());
            List<HealthCheckData> dataList = this.healthCheckDataRepository.getJvmProblemByAppIdPerDay(dt,appId);
            //判断一下，预防npe
            if (CollectionUtils.isEmpty(dataList) || dataList.get(dataList.size()-1) == null) {
                log.info("AppHealthCheckServiceImpl#getHealthCheckResult dataList or dataList.get(0) is null");
                return null;
            }
            log.info("AppHealthCheckServiceImpl#getHealthCheckResult have data");
            return dataList.get(dataList.size()-1);
        } catch (Exception e) {
            log.info("AppHealthCheckServiceImpl#getHealthCheckResult 查询dynamodb occurs an error", e);
            return null;
        }

    }

    private Integer initCheckTask(Integer appId, String accessToken, String createBy, String result) {
        return healthCheckInfo.insert(appId, accessToken, createBy, HealthCheckStatus.RUNNING.name(), "", 0, "", result);
    }
}