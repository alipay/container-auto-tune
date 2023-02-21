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
package com.alipay.autotuneservice.base.mail.model;


import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoxing
 * @version MailInfoMO.java, v 0.1 2022年02月22日 3:32 下午 zhaoxing
 */
public class Mail {
    /**
     * 收件人
     */
    private List<String> toAddress = new ArrayList<>();
    /**
     * 抄送
     */
    private List<String> ccAddress = new ArrayList<>();
    /**
     * 暗送
     */
    private List<String> bccAddress = new ArrayList<>();
    /**
     * 主题
     */
    private String subject;
    /**
     * 正文
     */
    private String content;

    /**
     * 正文类型
     */
    private BodyTypeEnum bodyTypeEnum;
    /**
     * 附件列表
     */
    private List<AttachBean> attachList = new ArrayList<>();

    public Mail() {}

    public Mail(List<String> to, String subject, String content) {
        this.toAddress = to;
        this.subject = subject;
        this.content = content;
    }

    public List<String> getToAddress() {
        return toAddress;
    }

    public void setToAddress(List<String> toAddress) {
        this.toAddress = toAddress;
    }

    public List<String> getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(List<String> ccAddress) {
        this.ccAddress = ccAddress;
    }

    public List<String> getBccAddress() {
        return bccAddress;
    }

    public void setBccAddress(List<String> bccAddress) {
        this.bccAddress = bccAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BodyTypeEnum getBodyTypeEnum() {
        return bodyTypeEnum;
    }

    public void setBodyTypeEnum(BodyTypeEnum bodyTypeEnum) {
        this.bodyTypeEnum = bodyTypeEnum;
    }

    public List<AttachBean> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<AttachBean> attachList) {
        this.attachList = attachList;
    }
}