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

import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.notice.NoticeButtonType;
import com.alipay.autotuneservice.model.notice.NoticeContentEnum;
import com.alipay.autotuneservice.model.notice.NoticeRequest;
import com.alipay.autotuneservice.service.notification.NoticeDefAction;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huoyuqi
 * @version NoticeControl.java, v 0.1 2022年09月27日 10:48 上午 huoyuqi
 */
@Slf4j
@RestController
@RequestMapping("/api/notice")
public class NoticeControl {

    @Autowired
    private NoticeDefAction noticeDefAction;

    @Autowired
    private AppInfoRepository appInfoRepository;

    @GetMapping
    public ServiceBaseResult<String> notice(@RequestParam(value = "appId", required = false) Integer appId,
                                            @RequestParam(value = "pipelineId", required = false) Integer pipelineId,
                                            @RequestParam(value = "NoticeContentEnum", required = false) String noticeContentEnum,
                                            @RequestParam(value = "NoticeButtonType", required = false) String noticeButtonType) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                    log.info("notice enter");
                    AppInfoRecord record = appInfoRepository.findByIdAndToken(UserUtil.getAccessToken(), appId);
                    if (record == null) {
                        return null;
                    }
                    NoticeRequest noticeRequest = new NoticeRequest(pipelineId, appId, NoticeContentEnum.valueOf(noticeContentEnum),
                            NoticeButtonType.valueOf(noticeButtonType), "alarm");
                    return noticeDefAction.sendMessage(noticeRequest) ? "SUCCESS" : "FALSE";
                });
    }
}