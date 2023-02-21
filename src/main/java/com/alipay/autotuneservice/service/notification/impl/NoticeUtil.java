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
package com.alipay.autotuneservice.service.notification.impl;

import com.alipay.autotuneservice.dao.NoticeRepository;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.NoticeRecord;
import com.alipay.autotuneservice.model.notice.NoticeRequest;
import com.alipay.autotuneservice.model.notice.NoticeType;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.util.HttpUtil;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huoyuqi
 * @version NoticeUtilImpl.java, v 0.1 2022年10月20日 11:22 上午 huoyuqi
 */
@Service
@Slf4j
public class NoticeUtil {

    static final String PIPELINE_ID  = "pipelineid";
    static final String APP_ID       = "appid";
    static final String PLAN_NAME    = "planname";
    static final String PREFIX_URL   = "/console/healthplan/details";
    static final String ALARM_PREFIX = "/console/appcenter";

    @Autowired
    private TunePipelineRepository tunePipelineRepository;

    @Autowired
    private TunePlanRepository tunePlanRepository;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private NoticeRepository repository;

    public NoticeRequest buildNoticeRequest(NoticeRequest noticeRequest) {
        TunePipeline tunePipeline = tunePipelineRepository.findByPipelineId(noticeRequest.getPipeLineId());
        if (null == tunePipeline) {
            log.warn("sendMessage find tunePipeline is null, pipelineId: {}", noticeRequest.getPipeLineId());
            return null;
        }
        TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePipeline.getTunePlanId());
        if (null == tunePlan) {
            log.warn("sendMessage find tunePlan is null, planId: {}", tunePipeline.getTunePlanId());
            return null;
        }
        AppInfoRecord appInfo = appInfoService.selectById(noticeRequest.getAppId());
        if (null == appInfo) {
            log.warn("sendMessage find appInfo is null, appId: {}", tunePipeline.getTunePlanId());
            return null;
        }
        noticeRequest.setAppName(appInfo.getAppName());
        noticeRequest.setPlanName(tunePlan.getPlanName());
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        noticeRequest.setDate(sdf.format(date));
        noticeRequest.setPlanWebUrl(buildUrl(noticeRequest));
        return noticeRequest;
    }

    public NoticeRequest buildAlarmNoticeRequest(NoticeRequest noticeRequest) {
        AppInfoRecord appInfo = appInfoService.selectById(noticeRequest.getAppId());
        if (null == appInfo) {
            log.warn("buildAlarmNoticeRequest find appInfo is null, appId: {}", noticeRequest.getAppId());
            return null;
        }
        noticeRequest.setAppName(appInfo.getAppName());
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        noticeRequest.setDate(sdf.format(date));
        noticeRequest.setPlanWebUrl(buildAlarmUrl(noticeRequest));
        return noticeRequest;
    }

    public NoticeRecord noticeStatusIsON(NoticeType noticeType) {
        return repository.selectByTypeAndToken(noticeType, UserUtil.getAccessToken());
    }

    /**
     * 构建url
     *
     * @param noticeRequest
     * @return
     */
    private String buildUrl(NoticeRequest noticeRequest) {
        Map<String, String> map = new HashMap<>();
        map.put(PIPELINE_ID, String.valueOf(noticeRequest.getPipeLineId()));
        map.put(APP_ID, String.valueOf(noticeRequest.getAppId()));
        map.put(PLAN_NAME, noticeRequest.getPlanName());
        return HttpUtil.buildUrl(PREFIX_URL, map);
    }

    private String buildAlarmUrl(NoticeRequest noticeRequest) {
        Map<String, String> map = new HashMap<>();
        map.put(APP_ID, String.valueOf(noticeRequest.getAppId()));
        return HttpUtil.buildUrl(ALARM_PREFIX, map);
    }

}