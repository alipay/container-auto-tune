package com.alipay.autotuneservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.controller.model.AppVO;
import com.alipay.autotuneservice.controller.model.HealthCheckVO;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.common.AppTag.Lang;
import com.alipay.autotuneservice.service.riskcheck.RiskCheckService;
import com.alipay.autotuneservice.service.riskcheck.entity.CheckResponse;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version AppHealthCheckController.java, v 0.1 2022年04月19日 7:01 下午 huoyuqi
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/healthCheck")
public class AppHealthCheckController {

    @Autowired
    private AppInfoRepository appInfoRepository;

    @Autowired
    private RiskCheckService riskCheckService;

    @Autowired
    private PodInfo podInfo;

    /**
     * 跳转到healthCheck 返回的集群列表
     *
     * @return
     */
    @GetMapping("app")
    public ServiceBaseResult<List<AppVO>> getAppNameList() {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                            log.info("getAppNameList");
                            String accessToken = UserUtil.getAccessToken();
                            log.info("accesstoken is:{}", accessToken);
                            List<AppInfoRecord> records = appInfoRepository.getAppListByTokenAndStatus(accessToken, AppStatus.ALIVE);
                            if (records == null) {
                                return new ArrayList<>();
                            }
                            Map<Integer, List<PodInfoRecord>> appIdMapPods = podInfo.batchGetPodInstallTuneAgentNumsByAppId(
                                            records.stream().map(AppInfoRecord::getId).collect(Collectors.toList()))
                                    .stream()
                                    .filter(p -> p.getAgentInstall() >= 1)
                                    .collect(Collectors.groupingBy(PodInfoRecord::getAppId));
                            return records
                                    .stream()
                                    .filter(p -> p.getAppTag() != null)
                                    .filter(this::checkJavaApp)
                                    .filter(app -> appIdMapPods.containsKey(app.getId()))
                                    .map(record -> {
                                        AppVO appVO = new AppVO();
                                        String appAsName = String.format("%s (%s)", record.getAppName(), record.getNamespace());
                                        appVO.setAppName(appAsName);
                                        appVO.setId(record.getId());
                                        return appVO;
                                    }).collect(Collectors.toList());
                        }
                );
    }

    private boolean checkJavaApp(AppInfoRecord record) {
        try {
            AppTag appTag = JSONObject.parseObject(record.getAppTag(), new TypeReference<AppTag>() {});
            return appTag.getLang() == Lang.JAVA;
        } catch (Exception e) {
            log.error("checkJavaApp appId={} occurs an error ", record.getId(), e);
            return false;
        }
    }

    /**
     * 跳转到healthCheck 返回的集群列表
     *
     * @return
     */
    @GetMapping("app/appid")
    public ServiceBaseResult<String> getAppName(@RequestParam(value = "appid") Integer appid) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                            AppInfoRecord record = appInfoRepository.getById(appid);
                            if (record == null) {
                                return "";
                            }
                            return String.format("%s (%s)", record.getAppName(), record.getNamespace());
                        }
                );
    }

    /**
     * 返回应用安装agent的数量
     *
     * @return
     */
    @GetMapping("/getAppAgentNum")
    public ServiceBaseResult<AppVO> getAppAgentNum(@RequestParam(value = "appId") Integer appId) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                            log.info("getAppAgentNum enter. appId={}", appId);
                            AppVO appVO = new AppVO();
                            List<PodInfoRecord> records = podInfo.getPodInstallTuneAgentNumsByAppId(appId);
                            if (CollectionUtils.isEmpty(records)) {
                                appVO.setAgentNum(0);
                            }
                            appVO.setId(appId);
                            appVO.setAgentNum(records.size());
                            return appVO;
                        }
                );
    }

    /**
     * 一键体检 点击开始检查
     *
     * @param appId 应用id
     * @return
     */
    @PostMapping("/submitCheck/{appId}")
    public ServiceBaseResult<Integer> submitCheck(@PathVariable(value = "appId") Integer appId) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> Preconditions.checkArgument(appId > 0, "appid can not be less than 0"))
                .makeResult(() -> -1);
    }

    /**
     * 点击刷新查看相应的进度并最终返回调优结果
     *
     * @param healthCheckId 健康检查id
     * @return
     */
    @GetMapping("/{healthCheckId}")
    public ServiceBaseResult<HealthCheckVO> refreshCheck(@PathVariable(value = "healthCheckId") Integer healthCheckId,
                                                         @RequestParam(value = "count", required = false) Integer count) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> Preconditions.checkArgument(healthCheckId > 0, "healthId can not be less than 0"))
                .makeResult(() -> new HealthCheckVO());
    }

    /**
     * 点击刷新查看最近的调优结果
     *
     * @return
     */
    @GetMapping("/getLastDate/{appid}")
    public ServiceBaseResult<HealthCheckVO> getLastDate(@PathVariable(value = "appid") Integer appId) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> Preconditions.checkArgument(appId > 0, "appId can not be less than 0"))
                .makeResult(() -> new HealthCheckVO());
    }

    /**
     * 点击调参优化中心 显示的评估效果图
     */
    @GetMapping("/evaluate/{id}")
    public ServiceBaseResult<HealthCheckVO> evaluate(@PathVariable(value = "id") Integer healthCheckId) {
        try {
            log.info("refreshCheck healthCheckId = {}", healthCheckId);
            Preconditions.checkArgument(healthCheckId != null, "healthCheckId 不能为空.");
            return ServiceBaseResult.successResult(new HealthCheckVO());
        } catch (Exception e) {
            log.error("evaluate occurs an error.", e);
            return ServiceBaseResult.failureResult(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    String.format("healthCheckId=[%s]--->refreshCheck执行异常, errMsg=%s", healthCheckId, e.getMessage()));
        }
    }

    @GetMapping("/riskcheck")
    public ServiceBaseResult<CheckResponse> obtainRiskResult(@RequestParam(value = "trace") String trace) {
        try {
            log.info("refreshCheck healthCheckId = {}", trace);
            Preconditions.checkArgument(trace != null, "trace 不能为空.");
            return ServiceBaseResult.successResult(riskCheckService.getRiskCheckResult(trace));
        } catch (Exception e) {
            log.error("evaluate occurs an error.", e);
            return ServiceBaseResult.failureResult(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

