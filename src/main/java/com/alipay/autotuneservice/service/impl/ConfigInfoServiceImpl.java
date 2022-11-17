/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.configuration.Cached;
import com.alipay.autotuneservice.controller.model.configVO.ConfigInfoVO;
import com.alipay.autotuneservice.controller.model.configVO.TimeHHmm;
import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import com.alipay.autotuneservice.controller.model.configVO.WeekEnum;
import com.alipay.autotuneservice.dao.ConfigInfoRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.ConfigInfoRecord;
import com.alipay.autotuneservice.service.ConfigInfoService;
import com.alipay.autotuneservice.service.PodService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ConfigInfoServiceImpl implements ConfigInfoService {

    @Autowired
    private ConfigInfoRepository configInfoRepository;

    @Autowired
    private PodService podService;

    @Autowired
    private PodInfo podInfo;

    //如果owner没配置调参时间，默认时间除了饭点都可调
    private static final List<TimeHHmm> DEFAULT_TIME_CHECKER = new ArrayList<TimeHHmm>() {
        {
            add(new TimeHHmm("12:00",
                    "14:00"));
            add(new TimeHHmm("17:00",
                    "19:00"));
        }
    };

    @Override
    public Boolean checkTuneIsEnableByAppID(Integer appID) {
        try {
            ConfigInfoVO configInfoVO = findAPPConfigByAPPID(appID);
            if ((configInfoVO == null) || !configInfoVO.getAutoTune()) {
                return true;
            }
            String dateTime = getPodTimeOfApp(appID);
            WeekEnum week = WeekEnum.valueOfCode(dateTime.substring(0, dateTime.indexOf(",")));
            String machineTime = dateTime.split(" ")[4].substring(0, 5);
            Map<WeekEnum, List<TimeHHmm>> tunePrimaryTime = configInfoVO.getTunePrimaryTime();
            if (CollectionUtils.isEmpty(tunePrimaryTime) || CollectionUtils.isEmpty(tunePrimaryTime.get(week))) {
                return DEFAULT_TIME_CHECKER.stream().allMatch(timeHHmm -> {
                    Boolean start = machineTime.compareTo(timeHHmm.getStart()) < 0;
                    Boolean end = machineTime.compareTo(timeHHmm.getEnd()) > 0;
                    return start || end;
                });
            }
            List<TimeHHmm> baseline = tunePrimaryTime.get(week);
            if (configInfoVO.getTuneTimeTag()) {
                return baseline.stream().anyMatch(timeHHmm -> {
                    Boolean start = machineTime.compareTo(timeHHmm.getStart()) > 0;
                    Boolean end = machineTime.compareTo(timeHHmm.getEnd()) < 0;
                    return start && end;
                });
            }
            return baseline.stream().allMatch(timeHHmm -> {
                Boolean start = machineTime.compareTo(timeHHmm.getStart()) < 0;
                Boolean end = machineTime.compareTo(timeHHmm.getEnd()) > 0;
                return start || end;
            });
        } catch (Exception e) {
            //configInfoVO=null || dateTime=null || WeekEnum异常 || podName=null
            log.error("checkTuneIsEnableByAppID error", e);
            return false;
        }
    }

    @Override
    @Cached
    public ConfigInfoVO findAPPConfigByAPPID(Integer appID) {
        ConfigInfoVO configInfoVO = configInfoRepository.findConfigByAPPID(appID);
        if (configInfoVO == null || configInfoVO.getAppId() <= 0) {
            configInfoVO = new ConfigInfoVO();
            configInfoVO.setAppId(appID);
            configInfoVO.setAutoTune(false);
            configInfoVO.setTuneTimeTag(false);
            configInfoVO.setTunePrimaryTime(Maps.newHashMap());
            configInfoVO.setAutoDispatch(false);
            configInfoVO.setTuneGroupConfig(ConfigInfoVO.defaultTuneConfig());
            configInfoVO.setRiskSwitch(false);
            configInfoVO.setAdvancedSetup(ConfigInfoVO.defaultAdvancedSetup());
            configInfoVO.setTimeZone(findTimeZone(appID));
            configInfoVO.setOperateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date()));
            configInfoRepository.save(configInfoVO);
        }
        return configInfoVO;
    }

    @Override
    public List<TuneConfig> findTuneGroupsByAppId(Integer appID) {
        ConfigInfoVO configInfoVO = configInfoRepository.findConfigByAPPID(appID);
        if (configInfoVO == null || configInfoVO.getAppId() <= 0) {
            return ConfigInfoVO.defaultTuneConfig();
        }
        return configInfoVO.getTuneGroupConfig();
    }

    @Override
    public String findTimeZone(Integer appID) {
        try {
            //"Tue, 19 Apr 2022 12:27:55 +0000"
            String dateTime = getPodTimeOfApp(appID);
            return "UTC" + dateTime.substring(dateTime.indexOf("+"), dateTime.indexOf("+") + 5);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public List<ConfigInfoRecord> batchAppConfigByAppIds(List<Integer> appIds) {
        return configInfoRepository.batchFindConfigByAppIds(appIds);
    }

    private String getPodTimeOfApp(Integer appID) {
        String podName = podInfo.findOneRunningPodNameByAppId(appID);
        return podService.getPodDate(podName);
    }
}
