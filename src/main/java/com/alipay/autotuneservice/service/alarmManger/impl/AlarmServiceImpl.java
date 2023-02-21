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
package com.alipay.autotuneservice.service.alarmManger.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.AlarmRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AlarmRecord;
import com.alipay.autotuneservice.service.alarmManger.AlarmService;
import com.alipay.autotuneservice.service.alarmManger.model.ActionEnum;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmContext;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmNoticeModel;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmStatus;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmVO;
import com.alipay.autotuneservice.service.alarmManger.model.CombinationType;
import com.alipay.autotuneservice.service.alarmManger.model.RuleModel;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version AlarmServiceImpl.java, v 0.1 2022年12月29日 4:04 下午 huoyuqi
 */
@Service
@Slf4j
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private AlarmRepository alarmRepository;

    @Override
    public void insert(Integer appId, String alarmName, AlarmStatus status, String ruleModels,
                       CombinationType combinationType, String actions, String notices) {
        AlarmRecord record = new AlarmRecord();
        //appId
        record.setAppId(appId);
        //设置规则名称
        record.setAlarmName(alarmName);
        //规则状态
        record.setStatus(status.name());
        //判断规则
        record.setAlarmRule(ruleModels);
        //联合类型
        record.setCombinationType(combinationType.name());
        //规则动作
        record.setRuleAction(actions);
        //通知组
        record.setAlarmNotice(notices);
        record.setAccessToken(UserUtil.getAccessToken());
        //更改context
        record.setContext(JSON.toJSONString(constructAlarmContext(record, ruleModels, actions, notices)));
        alarmRepository.insertAlarm(record);
    }

    @Override
    public void update(Integer alarmId, String alarmName, AlarmStatus status, String ruleModels, CombinationType combinationType,
                       String actions, String notices) {
        AlarmRecord record = alarmRepository.getByAlarmId(alarmId);
        if (null == record) {
            throw new RuntimeException("Not Found In DB");
        }
        record.setStatus(status.name());
        record.setAlarmName(alarmName);
        record.setAlarmRule(StringUtils.isEmpty(ruleModels) ? record.getRuleAction() : ruleModels);
        record.setCombinationType(combinationType.name());
        record.setRuleAction(StringUtils.isEmpty(actions) ? record.getRuleAction() : actions);
        record.setAlarmNotice(StringUtils.isEmpty(notices) ? record.getAlarmNotice() : notices);
        record.setContext(JSON.toJSONString(constructAlarmContext(record, ruleModels, actions, notices)));
        alarmRepository.updateAlarm(record);
    }

    @Override
    public List<AlarmVO> selectByAppId(Integer appId) {
        List<AlarmRecord> records = alarmRepository.getByAppId(appId);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }

        return records.stream().map(this::convert2AlarmVO).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteByAlarmId(Integer alarmId) {
        return alarmRepository.deleteByAlarmId(alarmId);
    }

    /**
     * 构建AlarmContext 文本
     */
    private AlarmContext constructAlarmContext(AlarmRecord record, String rules, String actions,
                                               String notices) {
        AlarmContext alarmContext = new AlarmContext();
        alarmContext.setAppId(record.getAppId());
        alarmContext.setAppName(record.getAppName());
        alarmContext.setAlarmStatus(AlarmStatus.valueOf(record.getStatus()));

        alarmContext.setRuleModels(
                StringUtils.isEmpty(rules) ? new ArrayList<>() : JSON.parseObject(rules, new TypeReference<List<RuleModel>>() {}));
        alarmContext.setCombinationType(CombinationType.valueOf(record.getCombinationType()));
        alarmContext.setActionEnums(
                StringUtils.isEmpty(actions) ? new ArrayList<>() : JSON.parseObject(actions, new TypeReference<List<ActionEnum>>() {}));
        alarmContext.setAlarmNotices(
                StringUtils.isEmpty(notices) ? new ArrayList<>()
                        : JSON.parseObject(notices, new TypeReference<List<AlarmNoticeModel>>() {}));
        return alarmContext;
    }

    /**
     * record 转换成 AlarmVO
     *
     * @param r
     * @return
     */
    private AlarmVO convert2AlarmVO(AlarmRecord r) {
        try {
            if (null == r) {
                return null;
            }
            List<RuleModel> ruleModels = new ArrayList<>();
            List<ActionEnum> actionEnums = new ArrayList<>();
            List<AlarmNoticeModel> noticeModels = new ArrayList<>();
            List<String> actions = new ArrayList<>();
            if (StringUtils.isNotEmpty(r.getAlarmRule())) {
                ruleModels = JSON.parseObject(r.getAlarmRule(), new TypeReference<List<RuleModel>>() {});
            }
            if (StringUtils.isNotEmpty(r.getRuleAction())) {
                actionEnums = JSON.parseObject(r.getRuleAction(), new TypeReference<List<ActionEnum>>() {});
            }

            if (StringUtils.isNotEmpty(r.getAlarmNotice())) {
                noticeModels = JSON.parseObject(r.getAlarmNotice(), new TypeReference<List<AlarmNoticeModel>>() {});
            }

            return new AlarmVO(r.getId(), r.getAlarmName(), AlarmStatus.valueOf(r.getStatus()), r.getCreateBy(),
                    DateUtils.asTimestamp(r.getCreatedTime()), ruleModels, CombinationType.valueOf(r.getCombinationType()), actionEnums,
                    noticeModels);
        } catch (Exception e) {
            log.error("convert2AlarmVO occurs an error, alarmId: {}", r.getId(), e);
            return null;
        }

    }

    @Override
    public void insert() {

        AlarmRecord record = new AlarmRecord();

        //构建规则引擎
        List<RuleModel> list = new ArrayList<>();
        list.add(new RuleModel(1, "fullgc次数(分钟)", "FGC_COUNT", ">=", 2, 5 * 60 * 1000));
        list.add(new RuleModel(3, "younggc次数(分钟)", "YGC_COUNT", null, null, 60 * 1000));
        list.add(new RuleModel(5, "内存利用率", "JVM_UTIL", null, null, 60 * 1000));
        list.add(new RuleModel(6, "codecache利用率", "CODECACHE_UTIL", null, null, 60 * 1000));

        //构建规则动作
        List<ActionEnum> actions = new ArrayList<>();
        actions.add(ActionEnum.NOTICE);
        actions.add(ActionEnum.GC_DUMP);
        actions.add(ActionEnum.HEAP_DUMP);
        actions.add(ActionEnum.THREAD_DUMP);
        actions.add(ActionEnum.JVM_PROFILE);

        //通知
        List<AlarmNoticeModel> notices = new ArrayList<>();
        notices.add(new AlarmNoticeModel(2, "通知组1"));

        //appId
        record.setAppId(20578);
        //appName
        record.setAppName("jvm-lab-saas-test");
        //设置规则名称
        record.setAlarmName("test1");
        //规则状态
        record.setStatus(AlarmStatus.ON.name());
        //判断规则
        record.setAlarmRule(JSON.toJSONString(list));
        //联合类型
        record.setCombinationType(CombinationType.OR.name());
        //规则动作
        record.setRuleAction(JSON.toJSONString(actions));
        //通知组
        record.setAlarmNotice(JSON.toJSONString(notices));

        record.setContext(JSON.toJSONString(
                constructAlarmContext(record, JSON.toJSONString(list), JSON.toJSONString(actions), JSON.toJSONString(notices))));
        alarmRepository.insertAlarm(record);
    }

}