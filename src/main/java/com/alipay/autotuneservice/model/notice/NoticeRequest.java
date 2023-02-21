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
package com.alipay.autotuneservice.model.notice;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author huoyuqi
 * @version NoticeRequest.java, v 0.1 2022年09月26日 7:14 下午 huoyuqi
 */
@Data
public class NoticeRequest {

    private Integer           appId;
    private Integer           pipeLineId;
    private String            token;
    private String            appName;
    private String            planName;
    private String            date;
    private NoticeContentEnum noticeContentEnum;
    private NoticeButtonType  noticeButtonType;
    private String            planWebUrl;
    private NoticeType        noticeType;
    private String            noticeSort;

    private String                        noticeMessage;
    private Map<NoticeType, List<String>> noticeMap;

    public NoticeRequest(Integer pipelineId, Integer appId, NoticeContentEnum contentEnum, NoticeButtonType buttonType) {
        this.pipeLineId = pipelineId;
        this.appId = appId;
        this.noticeContentEnum = contentEnum;
        this.noticeButtonType = buttonType;
    }

    public NoticeRequest(Integer pipelineId, Integer appId, NoticeContentEnum contentEnum, NoticeButtonType buttonType, String noticeSort) {
        this.pipeLineId = pipelineId;
        this.appId = appId;
        this.noticeContentEnum = contentEnum;
        this.noticeButtonType = buttonType;
        this.noticeSort = noticeSort;
    }

    public NoticeRequest(Integer appId, String appName, String noticeMessage, Map<NoticeType, List<String>> noticeMap,
                         NoticeButtonType buttonType) {
        this.appId = appId;
        this.noticeMessage = noticeMessage;
        this.noticeMap = noticeMap;
        this.noticeButtonType = buttonType;
    }

}