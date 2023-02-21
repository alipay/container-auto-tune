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
 * @version EmailService.java, v 0.1 2022年06月28日 10:42 上午 huoyuqi
 */
public interface EmailService {

    /**
     * 发送邮件
     * @param acceptEmail 接收方mail地址
     * @param content mail 内容
     * @param subject mail 主题
     * @throws Exception
     */
    void sendMail(List<String> acceptEmail, String content, String subject) throws Exception;


    /**
     * 发送带有附件的邮件
     * @param acceptEmail 接收方mail地址
     * @param content mail 内容
     * @param subject mail 主题
     * @param filePath 附件文件路径
     * @param fileName 附件文件主题
     * @throws Exception
     */
    void sendMail(List<String> acceptEmail, String content, String subject, String filePath, String fileName) throws Exception;
}