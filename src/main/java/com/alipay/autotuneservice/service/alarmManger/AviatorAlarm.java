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
package com.alipay.autotuneservice.service.alarmManger;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.AlarmRepository;
import com.alipay.autotuneservice.dao.JvmMonitorMetricRepository;
import com.alipay.autotuneservice.dao.NotifyRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.RuleInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AlarmRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.RuleInfoRecord;
import com.alipay.autotuneservice.gc.service.GcLogAnalysisService;
import com.alipay.autotuneservice.service.alarmManger.actionRepository.GcDumpFunction;
import com.alipay.autotuneservice.service.alarmManger.actionRepository.HeapDumpFunction;
import com.alipay.autotuneservice.service.alarmManger.actionRepository.JvmProfileFunction;
import com.alipay.autotuneservice.service.alarmManger.actionRepository.NoticeFunction;
import com.alipay.autotuneservice.service.alarmManger.actionRepository.ThreadDumpFunction;
import com.alipay.autotuneservice.service.alarmManger.model.ActionEnum;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmContext;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmStatus;
import com.alipay.autotuneservice.service.alarmManger.model.CombinationType;
import com.alipay.autotuneservice.service.alarmManger.model.JudgeActionExecuteModel;
import com.alipay.autotuneservice.service.alarmManger.model.ResultModel;
import com.alipay.autotuneservice.service.alarmManger.model.RuleModel;
import com.alipay.autotuneservice.service.alarmManger.ruleRepository.CodeCacheFunction;
import com.alipay.autotuneservice.service.alarmManger.ruleRepository.FgcCountFunction;
import com.alipay.autotuneservice.service.alarmManger.ruleRepository.MemFunction;
import com.alipay.autotuneservice.service.alarmManger.ruleRepository.YgcCountFunction;
import com.alipay.autotuneservice.service.chronicmap.ChronicleMapService;
import com.alipay.autotuneservice.service.notification.NoticeDefAction;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huoyuqi
 * @version AviatorAlarm.java, v 0.1 2022年12月23日 1:55 下午 huoyuqi
 */
@Service
@Slf4j
public class AviatorAlarm {

    @Autowired
    private JvmMonitorMetricRepository jvmMetricRepository;

    @Autowired
    private PodInfo podInfo;

    @Autowired
    private ChronicleMapService redisClient;

    @Autowired
    private GcLogAnalysisService gcLogAnalysisService;

    @Autowired
    private NoticeDefAction noticeDefAction;

    @Autowired
    private NotifyRepository notifyRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private RuleInfoRepository ruleInfoRepository;

    private final static Long ALARM_TIME = 10 * 60L;

    static {
        //注册函数 前置规则判断
        AviatorEvaluator.addFunction(new YgcCountFunction());
        AviatorEvaluator.addFunction(new FgcCountFunction());
        AviatorEvaluator.addFunction(new MemFunction());
        AviatorEvaluator.addFunction(new CodeCacheFunction());

        //注册函数 后置动作 gc heap thread jvmProfile notice
        AviatorEvaluator.addFunction(new GcDumpFunction());
        AviatorEvaluator.addFunction(new HeapDumpFunction());
        AviatorEvaluator.addFunction(new ThreadDumpFunction());
        AviatorEvaluator.addFunction(new JvmProfileFunction());
        AviatorEvaluator.addFunction(new NoticeFunction());
    }

    public void invoke(Integer appId) {
        log.info("start invoke rule, appId: {}", appId);
        List<AlarmRecord> records = alarmRepository.getByAppId(appId);
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(r -> {
                Map<String, Object> env = convertEnv(r);
                alarm(env);
            });
        }
    }

    Map<String, Object> convertEnv(AlarmRecord alarmRecord) {
        AlarmContext alarmContext = JSON.parseObject(alarmRecord.getContext(), AlarmContext.class);
        alarmContext.setAlarmId((long) alarmRecord.getId());
        Map<String, Object> env = new HashMap<>();
        alarmContext.setCurrentTime(System.currentTimeMillis());
        env.put("alarmContext", alarmContext);
        return env;
    }

    private void alarm(Map<String, Object> env) {

        //1解析env
        log.info("alarm enter, env: {}", JSON.toJSONString(env));
        AlarmContext alarmContext = (AlarmContext) env.get("alarmContext");
        alarmContext.setJvmMonitorMetricRepository(jvmMetricRepository);
        alarmContext.setGcLogAnalysisService(gcLogAnalysisService);
        alarmContext.setNoticeDefAction(noticeDefAction);
        alarmContext.setNotifyRepository(notifyRepository);
        List<PodInfoRecord> records = podInfo.getByAppId(alarmContext.getAppId());
        if (CollectionUtils.isEmpty(records)) {
            log.info("podInfo.getByAppId get result is null");
            return;
        }

        // 2.1前置条件是否生效
        if (AlarmStatus.OFF.equals(alarmContext.getAlarmStatus())) {
            return;
        }

        //2.2前置条件 是否满足设置规则判断
        List<RuleModel> ruleModels = alarmContext.getRuleModels();

        //基于pod 判断相应规则以及执行相应动作
        if (CollectionUtils.isNotEmpty(ruleModels)) {
            records.forEach(p -> {
                try {
                    log.info("进入pod开始判断规则");
                    List<ResultModel> resultModels = new ArrayList<>();
                    //做幂等判断 10分钟内触发动作引擎  多个pod只触发一个pod
                    if (redisClient.exists(alarmContext.getAlarmId().toString())) {

                        return;
                    }

                    ruleModels.forEach(item -> {
                        alarmContext.setRuleModel(constructRuleModel(item));
                        alarmContext.setPodInfoRecord(p);
                        env.put("alarmContext", alarmContext);
                        String expression = String.format("%s()", item.getRuleFunction());
                        Expression compiledExp = AviatorEvaluator.compile(expression, true);
                        ResultModel resultModel = JSON.parseObject(compiledExp.execute(env).toString(), ResultModel.class);
                        resultModels.add(resultModel);
                    });

                    //3.1根据or and 进行多条规则融合判断
                    JudgeActionExecuteModel judgeActionExecuteModel = judge(alarmContext.getCombinationType(), resultModels);
                    //3.2 判断结果塞入进来
                    alarmContext.setJudgeActionExecuteModel(judgeActionExecuteModel);
                    env.put("alarmContext", alarmContext);

                    //4.是否执行后置动作 不满足规则返回
                    if (!judgeActionExecuteModel.getNoticeAction()) {
                        log.info("不满足规则");
                        return;
                    }

                    //5.后置动作
                    List<ActionEnum> actionEnums = alarmContext.getActionEnums();
                    if (CollectionUtils.isNotEmpty(actionEnums)) {
                        actionEnums.forEach(a -> {
                            String expression = String.format("%s()", a.name());
                            Expression compiledExp = AviatorEvaluator.compile(expression, true);
                            compiledExp.execute(env);
                        });
                    }

                    //报警做幂等
                    redisClient.set(Long.toString(alarmContext.getAlarmId()), alarmContext.getAppId(), ALARM_TIME);

                } catch (Exception e) {
                    log.error("occurs an error", e);
                }
            });
        }
    }

    /**
     * 融合规则判断
     *
     * @param combinationType
     * @param resultModels
     * @return
     */
    private JudgeActionExecuteModel judge(CombinationType combinationType, List<ResultModel> resultModels) {
        JudgeActionExecuteModel judgeAction = new JudgeActionExecuteModel();

        if (null == combinationType || CombinationType.OR.equals(combinationType)) {
            judgeAction.setNoticeAction(false);
            resultModels.forEach(r -> {
                if (r.getStatus()) {
                    judgeAction.setNoticeAction(true);
                }
                String resultMessage = judgeAction.getResultMessage();
                judgeAction.setResultMessage(String.format("%s %s", resultMessage == null ? "" : resultMessage, r.getResultMessage()));
            });
        }
        if (CombinationType.AND.equals(combinationType)) {
            judgeAction.setNoticeAction(true);
            resultModels.forEach(r -> {
                if (!r.getStatus()) {
                    judgeAction.setNoticeAction(false);
                }
                String resultMessage = judgeAction.getResultMessage();
                judgeAction.setResultMessage(String.format("%s %s", resultMessage == null ? "" : resultMessage, r.getResultMessage()));
            });
        }

        return judgeAction;
    }

    /**
     * 当传入的只是名字 从数据库中拉取默认配置 构建完整规则
     *
     * @param ruleModel
     * @return
     */
    private RuleModel constructRuleModel(RuleModel ruleModel) {
        if (ruleModel.getOperatorSymbol() == null || ruleModel.getData() == null) {
            RuleInfoRecord ruleInfoRecord = ruleInfoRepository.selectById(ruleModel.getRuleId());
            if (null != ruleInfoRecord) {
                ruleModel.setOperatorSymbol(ruleInfoRecord.getRuleSymbol());
                ruleModel.setData(ruleInfoRecord.getRuleData());
                ruleModel.setTime(ruleInfoRecord.getTimeInterval());
            }
        }
        return ruleModel;
    }

}





