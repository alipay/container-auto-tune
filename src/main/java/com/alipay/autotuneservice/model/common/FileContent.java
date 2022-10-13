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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author dutianze
 * @version FileContent.java, v 0.1 2022年04月19日 15:44 dutianze
 */
@Slf4j
@Data
public class FileContent {

    private String fileName;
    private String content;

    public FileContent(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public int getLength() {
        return content.getBytes(StandardCharsets.UTF_8).length;
    }

    public InputStream getAsInputStream() {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    public String curl(String url, ShellEnvEnum envEnum) {
        return "curl -o " + fileName + " '" + url + "'"
               + String.format(" && %s %s", envEnum.getShell(), fileName);
    }

    public String wget(String url, ShellEnvEnum envEnum) {
        return "wget -O " + fileName + " '" + url + "'"
               + String.format(" && %s %s", envEnum.getShell(), fileName);
    }
}