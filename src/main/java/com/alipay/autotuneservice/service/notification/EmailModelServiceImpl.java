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
package com.alipay.autotuneservice.service.notification;

import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huoyuqi
 * @version EmailModelServiceImpl.java, v 0.1 2022年07月01日 2:18 下午 huoyuqi
 */
@Slf4j
@Service
public class EmailModelServiceImpl implements EmailModelService {

    private final static String    TUNE_PATH = "/healthplan/details";
    private final static String    SUBJECT   = "Tmeastro";

    @Autowired
    private EmailService           emailService;
    @Autowired
    private AppInfoService         appInfoService;
    @Autowired
    private TunePlanRepository     tunePlanRepository;
    @Autowired
    private TunePipelineRepository tunePipelineRepository;

    @Override
    public void tuneSubmitEmail(Integer pipelineId, Integer appId) {
        try {
            String url = buildUrl(pipelineId, appId);

            //todo 获取用户邮箱 发送邮箱通知
            String content = String
                .format(
                    "hello,  %s starts a new tuning process at %s,  <a href=\"%s\">please click for details</a>",
                    getAppName(appId), getCurrentDate(), url);
            emailService.sendMail(Arrays.asList("xx"), content, SUBJECT);
        } catch (Exception e) {
            log.error("EmailModelServiceImpl#tuneSubmitEmail 执行过程抛出异常 e:{}" + e.getMessage());
        }
    }

    @Override
    public void tunePredictEmail(Integer pipelineId, Integer appId) {
        try {
            String url = buildUrl(pipelineId, appId);
            //todo 获取用户邮箱 发送邮箱通知
            String content = String
                .format(
                    "hello,  %s starts expected evaluation at %s,  <a href=\"%s\">please click for details</a>",
                    getAppName(appId), getCurrentDate(), url);
            emailService.sendMail(Arrays.asList("xx"), content, SUBJECT);
        } catch (Exception e) {
            log.error("EmailModelServiceImpl#tuneSubmitEmail 执行过程抛出异常 e:{}" + e.getMessage());
        }
    }

    @Override
    public void tuneProcessEmail(Integer pipelineId, Integer appId) {
        try {
            String url = buildUrl(pipelineId, appId);
            //todo 获取用户邮箱 发送邮箱通知
            String content = String
                .format(
                    "hello,  %s starts batch tuning at %s,  <a href=\"%s\">please click for details</a>",
                    getAppName(appId), getCurrentDate(), url);
            emailService.sendMail(Arrays.asList("xx"), content, SUBJECT);
        } catch (Exception e) {
            log.error("EmailModelServiceImpl#tuneSubmitEmail 执行过程抛出异常 e:{}" + e.getMessage());
        }
    }

    @Override
    public void tuneFinishEmail(Integer pipelineId, Integer appId) {
        try {
            String url = buildUrl(pipelineId, appId);
            //todo 获取用户邮箱 发送邮箱通知
            String content = String
                .format(
                    "hello,  %s has finished tuning at %s,  <a href=\"%s\">please click for details</a>",
                    getAppName(appId), getCurrentDate(), url);
            emailService.sendMail(Arrays.asList("xx"), content, SUBJECT);
        } catch (Exception e) {
            log.error("EmailModelServiceImpl#tuneSubmitEmail 执行过程抛出异常 e:{}" + e.getMessage());
        }
    }

    @Override
    public void tuneCancelEmail(Integer pipelineId, Integer appId) {
        try {
            String url = buildUrl(pipelineId, appId);
            //todo 获取用户邮箱 发送邮箱通知
            String content = String
                .format(
                    "hello,  %s has canceled tuning at %s,  <a href=\"%s\">please click for details</a>",
                    getAppName(appId), getCurrentDate(), url);
            emailService.sendMail(Arrays.asList("xx"), content, SUBJECT);
        } catch (Exception e) {
            log.error("EmailModelServiceImpl#tuneSubmitEmail 执行过程抛出异常 e:{}" + e.getMessage());
        }
    }

    @Override
    public void sendNotifyMsg(List<String> emailList, Integer appId, Integer pipelineId,
                              String subject, String notifyMessage) {
        try {
            String url = buildUrl(pipelineId, appId);
            String content = String.format(
                "hello, %s,  <a href=\"%s\">please click for details</a>", notifyMessage, url);
            emailService.sendMail(emailList, content, subject);
        } catch (Exception e) {
            log.error("EmailModelServiceImpl#tsendNotifyMsg occurs an error.", e);
        }
    }

    private String buildUrl(Integer pipelineId, Integer appId) {
        Map<String, String> map = new HashMap<>();
        map.put("pipelineid", String.valueOf(pipelineId));
        map.put("appid", String.valueOf(appId));
        map.put("planname", getPlanName(pipelineId));
        return HttpUtil.buildUrl(TUNE_PATH, map);
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private String getAppName(Integer appId) {
        AppInfoRecord record = appInfoService.selectById(appId);
        if (record == null) {
            throw new RuntimeException(String.format("getAppName occurs an error, appID=[%s]",
                appId));
        }
        return record.getAppName();
    }

    private String getPlanName(Integer pipelineId) {
        TunePipeline tunePipeline = tunePipelineRepository.findByPipelineId(pipelineId);
        if (null == tunePipeline) {
            throw new RuntimeException(String.format(
                "getPlanName get tunePipeline occurs an error, pipelineId=[%s]", pipelineId));
        }

        TunePlan tunePlan = tunePlanRepository.findTunePlanById(tunePipeline.getTunePlanId());
        if (null == tunePlan) {
            throw new RuntimeException(String.format(
                "getPlanName get tunePlan occurs an error, pipelineId=[%s]", pipelineId));
        }
        return tunePlan.getPlanName();
    }
}