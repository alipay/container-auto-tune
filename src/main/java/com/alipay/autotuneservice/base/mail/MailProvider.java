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
package com.alipay.autotuneservice.base.mail;


import com.alipay.autotuneservice.base.mail.model.AttachBean;
import com.alipay.autotuneservice.base.mail.model.Mail;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.util.List;
import java.util.Properties;

/**
 * 统一邮件服务
 * @author yiqi
 * @date 2022/05/31
 */
public interface MailProvider {
    /**
     * 发送邮件
     * @param mail Mail
     * @throws Exception ex
     */
    void send(final Mail mail) throws Exception;

    /**
     * 转化数据
     * @param mail 邮件内容对象
     * @param fromEmail 固定发送方
     * @param properties 属性设置
     * @return MimeMessage java mail对象
     * @throws MessagingException
     */
    default MimeMessage convertMessage(Mail mail, String fromEmail, Properties properties) throws Exception {
        MimeMessage msg;
        if (properties != null) {
            // aliyun 发送的话，需要设置用户名以及smtp的密码
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = properties.getProperty("mail.user");
                    String password = properties.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            msg = new MimeMessage(Session.getInstance(properties, authenticator));
        } else {
            // aws 不需要properties设置，所以是空的
            msg = new MimeMessage(Session.getDefaultInstance(new Properties()));
        }

        // 创建邮件对象
        // 设置发件人
        msg.setFrom(new InternetAddress(fromEmail));
        // 设置收件人
        msg.addRecipients(MimeMessage.RecipientType.TO, StringUtils.join(mail.getToAddress(), ","));

        // 设置抄送
        List<String> cc = mail.getCcAddress();
        if (!cc.isEmpty()) {
            msg.addRecipients(MimeMessage.RecipientType.CC, StringUtils.join(cc, ","));
        }

        // 设置暗送
        List<String> bcc = mail.getBccAddress();
        if (!bcc.isEmpty()) {
            msg.addRecipients(MimeMessage.RecipientType.BCC, StringUtils.join(bcc, ","));
        }

        // 设置主题
        msg.setSubject(MimeUtility.encodeText(mail.getSubject(),MimeUtility.mimeCharset("gb2312"), null));

        // 创建部件集对象
        MimeMultipart parts = new MimeMultipart();

        // 创建一个部件
        MimeBodyPart part = new MimeBodyPart();
        // 设置邮件文本内容
        part.setContent(mail.getContent(), mail.getBodyTypeEnum().getCode());
        // 把部件添加到部件集中
        parts.addBodyPart(part);


        // 添加附件
        // 获取所有附件
        List<AttachBean> attachBeanList = mail.getAttachList();
        attachBeanList.stream().forEach(attach -> {
            try {
                MimeBodyPart attachPart = new MimeBodyPart();//
                java.io.File theFile = new java.io.File(attach.getFile());
                attachPart.attachFile(theFile);// 设置附件文件
                attachPart.setFileName(MimeUtility.encodeText(attach
                        .getFileName()));// 设置附件文件名
                String cid = attach.getCid();
                if (cid != null) {
                    attachPart.setContentID(cid);
                }
                parts.addBodyPart(attachPart);
            }catch (Exception e){
                throw new RuntimeException("convert mimeMessageFail, please check param. " + e.getMessage());
            }
        });

        // 给邮件设置内容
        msg.setContent(parts);
        return msg;
    }

}
