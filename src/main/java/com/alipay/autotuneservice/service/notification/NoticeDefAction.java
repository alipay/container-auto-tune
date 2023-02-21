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

import com.alipay.autotuneservice.base.wechat.NotifyClientProvider;
import com.alipay.autotuneservice.model.notice.NoticeRequest;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.service.notification.impl.NoticeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huoyuqi
 * @version NoticeDefAction.java, v 0.1 2022年09月26日 7:43 下午 huoyuqi
 */
@Slf4j
@Component
public class NoticeDefAction {

    @Autowired
    private NoticeUtil           noticeUtil;
    @Autowired
    private NotifyClientProvider clientProvider;
    @Autowired
    private StorageInfoService   storageInfoService;
    @Autowired
    private EmailService         emailService;

    private final static String                                    KEY = "NOTICE";
    private              Map<String, ServiceLoader<NoticeService>> map = new ConcurrentHashMap<>();

    public Boolean sendMessage(NoticeRequest noticeRequest) {
        try {
            log.info("sendMessage 开始发送消息");
            Iterator<NoticeService> iterator = null;
            if (!map.containsKey(KEY)) {
                ServiceLoader<NoticeService> noticeServices = ServiceLoader.load(NoticeService.class);
                map.put(KEY, noticeServices);
            }
            ServiceLoader<NoticeService> noticeServices = map.get(KEY);
            iterator = noticeServices.iterator();
            while (iterator.hasNext()) {
                NoticeService notice = iterator.next();
                notice.init(noticeUtil, clientProvider, storageInfoService, emailService);
                notice.sendMessage(noticeRequest);
            }
            return true;
        } catch (Exception e) {
            log.error("sendMessage 发送消息失败");
            return false;
        }
    }

    public Boolean sendAlarmMessage(NoticeRequest noticeRequest) {
        try {
            log.info("sendAlarmMessage 开始发送消息");
            Iterator<NoticeService> iterator = null;
            if (!map.containsKey(KEY)) {
                ServiceLoader<NoticeService> noticeServices = ServiceLoader.load(NoticeService.class);
                map.put(KEY, noticeServices);
            }
            ServiceLoader<NoticeService> noticeServices = map.get(KEY);
            iterator = noticeServices.iterator();
            while (iterator.hasNext()) {
                NoticeService notice = iterator.next();
                notice.init(noticeUtil, clientProvider, storageInfoService, emailService);
                notice.sendAlarmMessage(noticeRequest);
            }
            return true;
        } catch (Exception e) {
            log.error("sendMessage 发送消息失败");
            return false;
        }
    }
}