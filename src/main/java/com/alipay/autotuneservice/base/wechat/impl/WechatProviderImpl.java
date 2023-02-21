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
package com.alipay.autotuneservice.base.wechat.impl;

import com.alipay.autotuneservice.base.wechat.NotifyClientProvider;
import com.alipay.saascloud.driver.notify.qywechat.api.DefaultWorkWechatClient;
import com.alipay.saascloud.driver.notify.qywechat.api.WorkWechatClient;
import com.alipay.saascloud.driver.notify.qywechat.api.request.OapiRobotSendRequest;
import com.alipay.saascloud.driver.notify.qywechat.api.response.WorkWechatResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiqi
 * @version QyWechatNotifyProviderImpl.java, v 0.1 2022年09月16日 12:33 yiqi
 */
@Service
public class WechatProviderImpl implements NotifyClientProvider {

    @Override
    public List<Boolean> sendMessageWebHooks(List<String> webHookUrls, String messageType, String title, String content) {
        List<Boolean> res = new ArrayList<>();
        for (String webHookUrl : webHookUrls) {
            res.add(
                this.sendMessageWebhook(webHookUrl, messageType, title, content)
            );
        }
        return res;
    }

    @Override
    public boolean sendMessageWebhook(String webHookUrl, String messageType, String title, String content) {

        WorkWechatClient client = new DefaultWorkWechatClient(webHookUrl);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(messageType);
        // 判断发送webhook 消息的类型，目前暂时仅支持text/markdown类型的消息
        switch (messageType) {
            case "text":
                OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
                text.setContent(content);
                request.setText(text);
                break;
            case "markdown":
                OapiRobotSendRequest.MarkDown markdown = new OapiRobotSendRequest.MarkDown();
                markdown.setTitle(title);
                markdown.setContent(content);
                request.setMarkdown(markdown);
                break;
            default:
                break;
        }

        try {
            WorkWechatResponse response = client.execute(request);
            return response.getErrcode() == 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}