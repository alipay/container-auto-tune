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
package com.alipay.autotuneservice.model.common;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dutianze
 * @version AppLog.java, v 0.1 2022年05月07日 14:19 dutianze
 */
@Data
@Builder(setterPrefix = "with")
public class AppLog {

    private Long          id;
    private Integer       appId;
    private AppLogType    appLogType;
    private String        s3Key;
    private String        fileName;
    private String        hostName;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}