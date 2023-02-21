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
package com.alipay.autotuneservice.controller;

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.alarmManger.AlarmService;
import com.alipay.autotuneservice.service.alarmManger.AviatorAlarm;
import com.alipay.autotuneservice.service.alarmManger.RuleInfoService;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmStatus;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmType;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmVO;
import com.alipay.autotuneservice.service.alarmManger.model.CombinationType;
import com.alipay.autotuneservice.service.alarmManger.model.RuleModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huoyuqi
 * @version AlarmManagerController.java, v 0.1 2022年12月26日 3:26 下午 huoyuqi
 */

@Slf4j
@RestController
@RequestMapping("/api/alarm")
@NoLogin
public class AlarmManagerController {

    @Autowired
    private AviatorAlarm aviatorAlarm;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private RuleInfoService ruleInfoService;

    @GetMapping
    public ServiceBaseResult<String> appList() {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                    aviatorAlarm.invoke(20578);
                    return "触发";
                });
    }

    @GetMapping("/insert")
    public ServiceBaseResult<Boolean> insert(@RequestParam(value = "appId") Integer appId,
                                             @RequestParam(value = "alarmName") String alarmName,
                                             @RequestParam(value = "status") AlarmStatus status,
                                             @RequestParam(value = "ruleModels") String ruleModels,
                                             @RequestParam(value = "combinationType") CombinationType combinationType,
                                             @RequestParam(value = "actions") String actions,
                                             @RequestParam(value = "notices") String notices) {
        try {
            alarmService.insert(appId, alarmName, status, ruleModels, combinationType, actions, notices);
            return ServiceBaseResult
                    .invoker()
                    .makeResult(() -> Boolean.TRUE);
        } catch (Exception e) {
            log.error("insert occurs an error", e);
            return ServiceBaseResult
                    .invoker()
                    .makeResult(() -> Boolean.FALSE);
        }
    }

    @GetMapping("/update")
    public ServiceBaseResult<Boolean> update(@RequestParam(value = "alarmId") Integer alarmId,
                                             @RequestParam(value = "alarmName") String alarmName,
                                             @RequestParam(value = "status") AlarmStatus status,
                                             @RequestParam(value = "ruleModels") String ruleModels,
                                             @RequestParam(value = "combinationType") CombinationType combinationType,
                                             @RequestParam(value = "actions") String actions,
                                             @RequestParam(value = "notices") String notices) {
        try {
            alarmService.update(alarmId, alarmName, status, ruleModels, combinationType, actions, notices);
            return ServiceBaseResult
                    .invoker()
                    .makeResult(() -> Boolean.TRUE);
        } catch (Exception e) {
            log.error("update occurs an error", e);
            return ServiceBaseResult
                    .invoker()
                    .makeResult(() -> Boolean.FALSE);
        }
    }

    @GetMapping("/search")
    public ServiceBaseResult<List<AlarmVO>> search(@RequestParam(value = "appId") Integer appId) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> alarmService.selectByAppId(appId));
    }

    @GetMapping("deleteByAlarmId")
    public ServiceBaseResult<Boolean> delete(@RequestParam(value = "alarmId") Integer alarmId) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> alarmService.deleteByAlarmId(alarmId));
    }

    @GetMapping("/getRule")
    public ServiceBaseResult<List<RuleModel>> getRule(@RequestParam(value = "alarType") AlarmType alarmType) {
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> ruleInfoService.selectByAlarmType(alarmType));
    }

}