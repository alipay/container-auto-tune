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
package com.alipay.autotuneservice.util;

import com.alipay.autotuneservice.base.h2.H2DBManagerImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;

/**
 * @author huangkaifei
 * @version : FileUtil.java, v 0.1 2023年02月01日 4:38 PM huangkaifei Exp $
 */
@Slf4j
public class FileUtil {

    /**
     * copy file from resource path to target path
     *
     * @param resourceFilePath resource path
     * @param targetFilePath   target path
     */
    public static void copyfileFromResourcePath(String resourceFilePath, String targetFilePath) {
        try {
            log.info("*****  start to copyfileFromResourcePath.  *****");
            InputStream resourceAsStream = H2DBManagerImpl.class.getClassLoader().getResourceAsStream(resourceFilePath);
            File file = new File(targetFilePath);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(resourceAsStream, file);
            log.info("*****  end to copyFile.  *****");
        } catch (Exception e) {
            log.error("copyfileFromResourcePath occurs an error.", e);
        }
    }

    /**
     * read resource file as input stream
     *
     * @param resourceFilePath resource path
     * @return Inputstream object
     */
    public static InputStream readResourceFileAsInputStream(String resourceFilePath) {
        return FileUtil.class.getClassLoader().getResourceAsStream(resourceFilePath);
    }
}