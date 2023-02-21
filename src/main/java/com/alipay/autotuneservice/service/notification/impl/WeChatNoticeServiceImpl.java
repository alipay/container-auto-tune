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
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author huoyuqi
 * @version NoticeServiceImpl.java, v 0.1 2022年09月26日 7:44 下午 huoyuqi
 */
@Slf4j
@Component
public class WeChatNoticeServiceImpl implements NoticeService {

    private NotifyClientProvider clientProvider;
    private StorageInfoService   storageInfoService;
    private NoticeUtil           noticeUtil;

    @Override
    public void init(NoticeUtil noticeUtil, NotifyClientProvider clientProvider, StorageInfoService storageInfoService,
                     EmailService emailService) {
        this.noticeUtil = noticeUtil;
        this.clientProvider = clientProvider;
        this.storageInfoService = storageInfoService;
    }

    @Override
    public boolean sendMessage(NoticeRequest noticeRequest) {
        try {
            NoticeRecord record = noticeUtil.noticeStatusIsON(NoticeType.WECHAT);
            if (null == record || record.getNoticeStatus().equals(NoticeStatus.OFF.name())) {
                log.info("NoticeService off, appId: {}, pipelineId: {}", noticeRequest.getAppId(), noticeRequest.getPipeLineId());
                return true;
            }
            NoticeRequest request = noticeUtil.buildNoticeRequest(noticeRequest);
            request.setNoticeType(NoticeType.WECHAT);
            FileContent fileContent = storageInfoService.generateTuneNotice(request);
            List<String> accept = JSON.parseObject(record.getAccept(), new TypeReference<List<String>>() {});
            clientProvider.sendMessageWebHooks(accept, "markdown", noticeRequest.getNoticeContentEnum().getTitle(),
                    fileContent.getContent());
            return true;
        } catch (Exception e) {
            log.error("WeChatNoticeServiceImpl occurs an error", e);
            return false;
        }
    }

    @Override
    public boolean sendAlarmMessage(NoticeRequest noticeRequest) {
        return false;
    }
}