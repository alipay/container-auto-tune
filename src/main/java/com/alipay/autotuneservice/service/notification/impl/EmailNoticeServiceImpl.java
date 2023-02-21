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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.base.wechat.NotifyClientProvider;
import com.alipay.autotuneservice.dao.jooq.tables.records.NoticeRecord;
import com.alipay.autotuneservice.model.common.FileContent;
import com.alipay.autotuneservice.model.notice.NoticeRequest;
import com.alipay.autotuneservice.model.notice.NoticeStatus;
import com.alipay.autotuneservice.model.notice.NoticeType;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.service.notification.EmailService;
import com.alipay.autotuneservice.service.notification.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author huoyuqi
 * @version EmailServiceImpl.java, v 0.1 2022年10月19日 7:36 下午 huoyuqi
 */
@Slf4j
@Component
public class EmailNoticeServiceImpl implements NoticeService {

    private StorageInfoService storageInfoService;
    private EmailService       emailService;
    private NoticeUtil         noticeUtil;

    @Override
    public void init(NoticeUtil noticeUtil, NotifyClientProvider clientProvider, StorageInfoService storageInfoService,
                     EmailService emailService) {
        this.noticeUtil = noticeUtil;
        this.storageInfoService = storageInfoService;
        this.emailService = emailService;
    }

    @Override
    public boolean sendMessage(NoticeRequest noticeRequest) {
        //查看邮箱列表是否开启
        NoticeRecord record = noticeUtil.noticeStatusIsON(NoticeType.EMAIL);
        if (null == record || record.getNoticeStatus().equals(NoticeStatus.OFF.name())) {
            log.info("NoticeService off, appId: {}, pipelineId: {}", noticeRequest.getAppId(), noticeRequest.getPipeLineId());
            return true;
        }
        pipelineNotice(noticeRequest, record);
        return true;
    }

    @Override
    public boolean sendAlarmMessage(NoticeRequest noticeRequest) {
        //查看邮箱列表是否开启
        Map<NoticeType, List<String>> noticeMap = noticeRequest.getNoticeMap();
        if (MapUtils.isEmpty(noticeMap) || !noticeMap.containsKey(NoticeType.EMAIL)) {
            return true;
        }
        alarmNotice(noticeRequest);
        return true;
    }

    public boolean pipelineNotice(NoticeRequest noticeRequest, NoticeRecord record) {
        NoticeRequest request = noticeUtil.buildNoticeRequest(noticeRequest);
        noticeRequest.setNoticeType(NoticeType.EMAIL);
        FileContent fileContent = storageInfoService.generateTuneNotice(request);
        try {
            emailService.sendMail(JSON.parseObject(record.getAccept(), new TypeReference<List<String>>() {}),
                    fileContent.getContent(), noticeRequest.getNoticeContentEnum().getTitle());
            return true;
        } catch (Exception e) {
            log.error("sendMessage occurs an error, appId: {}", noticeRequest.getAppId());
            return false;
        }
    }

    public boolean alarmNotice(NoticeRequest noticeRequest) {
        NoticeRequest request = noticeUtil.buildAlarmNoticeRequest(noticeRequest);
        noticeRequest.setNoticeType(NoticeType.EMAIL);
        FileContent fileContent = storageInfoService.generateAlarmNotice(request);
        try {
            emailService.sendMail(noticeRequest.getNoticeMap().get(NoticeType.EMAIL), fileContent.getContent(), "AlarmNotice");
            return true;
        } catch (Exception e) {
            log.error("sendMessage occurs an error, appId: {}", noticeRequest.getAppId());
            return false;
        }
    }

}