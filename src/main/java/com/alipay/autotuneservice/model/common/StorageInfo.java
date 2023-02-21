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

import com.alipay.autotuneservice.controller.model.diagnosis.FileType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dutianze
 * @version StorageInfo.java, v 0.1 2022年04月19日 13:58 dutianze
 */
@Data
public class StorageInfo {

    public Long          id;
    public FileType      type;
    public String        operator;
    @JsonIgnore
    public String        accessToken;
    @JsonIgnore
    public String        s3Key;
    public String        resultKey;
    public String        fileName;
    public LocalDateTime createdTime;
    public LocalDateTime updatedTime;

    public StorageInfo() {
    }

    public StorageInfo(String s3Key, String fileName) {
        this.s3Key = s3Key;
        this.fileName = fileName;
    }

    public StorageInfo(String s3Key, String resultKey, String fileName, FileType type) {
        this.s3Key = s3Key;
        this.resultKey = resultKey;
        this.fileName = fileName;
        this.type = type;
    }
}

