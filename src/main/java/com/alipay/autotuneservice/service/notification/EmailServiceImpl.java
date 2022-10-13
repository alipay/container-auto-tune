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

import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.MultiCloudSdkFactory;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.MailProvider;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.aliyun.model.AttachBean;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.aliyun.model.BodyTypeEnum;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.aliyun.model.Mail;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huoyuqi
 * @version EmailServiceImpl.java, v 0.1 2022年06月28日 10:43 上午 huoyuqi
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    public MailProvider getEmailProvider() {
        return MultiCloudSdkFactory.getInstance().mailProvider();
    }

    @Override
    public void sendMail(List<String> acceptEmail, String content, String subject) throws Exception {
        Mail mail = new Mail();
        mail.setToAddress(acceptEmail);
        mail.setContent(content);
        mail.setSubject(subject);
        mail.setBodyTypeEnum(BodyTypeEnum.HTML);
        getEmailProvider().send(mail);
    }

    @Override
    public void sendMail(List<String> acceptEmail, String content, String subject, String filePath,
                         String fileName) throws Exception {
        Mail mail = new Mail();
        mail.setToAddress(acceptEmail);
        mail.setContent(content);
        mail.setSubject(subject);
        mail.setBodyTypeEnum(BodyTypeEnum.HTML);
        AttachBean attachBean = new AttachBean();
        attachBean.setCid("123");
        attachBean.setFile(filePath);
        attachBean.setFileName(fileName);
        mail.setAttachList(Lists.newArrayList(attachBean));
        getEmailProvider().send(mail);
    }
}