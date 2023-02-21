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

/**
 * @author huoyuqiø
 * @version NoticeService.java, v 0.1 2022年09月26日 7:45 下午 huoyuqi
 */
public interface NoticeService {

    /**
     * init NoticeService
     * @param noticeUtil
     * @param clientProvider
     * @param storageInfoService
     * @param emailService
     */
    void init(NoticeUtil noticeUtil, NotifyClientProvider clientProvider, StorageInfoService storageInfoService,
              EmailService emailService);
    /**
     * 发送消息
     * @param noticeRequest
     * @return
     */
    boolean sendMessage(NoticeRequest noticeRequest);

    /**
     * 发送报警配置信息
     * @param noticeRequest
     * @return
     */
    boolean sendAlarmMessage(NoticeRequest noticeRequest);

}