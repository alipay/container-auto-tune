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
package com.alipay.autotuneservice.model.report;

import com.alipay.autotuneservice.service.ReportActionService;
import com.alipay.autotuneservice.util.SpringFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author dutianze
 * @version ReportType.java, v 0.1 2022年02月17日 14:49 dutianze
 */
@Slf4j
public enum ReportActionType {

    javaConfigInit("doJavaConfigInit");

    public        String              method;
    private final ReportActionService reportActionService;

    ReportActionType(String method) {
        this.method = method;
        this.reportActionService = (ReportActionService) SpringFactoryUtils.getBean("reportActionServiceImpl");
    }

    public void doFunc(String params) {
        try {
            Method runc = reportActionService.getClass().getMethod(this.method, String.class);
            runc.invoke(reportActionService, params);
        } catch (Exception e) {
            log.error("ReportActionType doFunc [{}] is error:{}", this.method, e.getMessage(), e);
        }
    }
}