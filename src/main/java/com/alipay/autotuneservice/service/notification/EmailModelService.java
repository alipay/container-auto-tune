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

import java.util.List;

/**
 * @author huoyuqi
 * @version EmailModelService.java, v 0.1 2022年07月01日 2:17 下午 huoyuqi
 */
public interface EmailModelService {

    /**
     * 调优计划开启 发送邮件消息通知
     *
     * @param pipelineId 主流程id
     * @param appId      应用id
     */
    void tuneSubmitEmail(Integer pipelineId, Integer appId);

    /**
     * 调优预期评估 发送邮件消息通知
     *
     * @param pipelineId 主流程id
     * @param appId      应用id
     */
    void tunePredictEmail(Integer pipelineId, Integer appId);

    /**
     * 分批调优 发送邮件消息通知
     *
     * @param pipelineId 主流程id
     * @param appId      应用id
     */
    void tuneProcessEmail(Integer pipelineId, Integer appId);

    /**
     * 调优流程结束 发送邮件消息通知
     *
     * @param pipelineId 主流程id
     * @param appId      应用id
     */
    void tuneFinishEmail(Integer pipelineId, Integer appId);

    /**
     * 调优流程取消 发送邮件消息通知
     *
     * @param pipelineId 主流程id
     * @param appId      应用id
     */
    void tuneCancelEmail(Integer pipelineId, Integer appId);

    /**
     * 发送邮件通知
     *
     * @param emailList
     * @param appId
     * @param pipelineId
     * @param notifyMessage
     */
    void sendNotifyMsg(List<String> emailList, Integer appId, Integer pipelineId, String subject, String notifyMessage);
}