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
package com.alipay.autotuneservice.base.mail.impl;

import com.alipay.autotuneservice.base.mail.MailProvider;
import com.alipay.autotuneservice.base.mail.model.Mail;
import com.alipay.autotuneservice.configuration.ConstantsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author huoyuqi
 */
@Service
public class MailProviderImpl implements MailProvider {

    @Autowired
    private ConstantsProperties constantsProperties;

    @Override
    public void send(Mail mail) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.qq.com");
        //设置端口:
//        props.put("mail.smtp.port", "80");//或"25", 如果使用ssl，则去掉使用80或25端口的配置，进行如下配置：
        //加密方式:
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.port", "465");
        //mailfrom 参数
        String fromEmail = constantsProperties.getFromEmail();
        props.put("mail.smtp.from", fromEmail);
        // 发件人的账号(在控制台创建的发信地址)
        props.put("mail.user", fromEmail);
        // 发信地址的smtp密码(在控制台选择发信地址进行设置)
        props.put("mail.password", constantsProperties.getEmailSecret());
        MimeMessage mimeMessage = this.convertMessage(mail, fromEmail, props);
        Transport.send(mimeMessage);
    }
}
