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

import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.service.StorageInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

/**
 * @author huoyuqi
 * @version FileUtils.java, v 0.1 2022年12月01日 7:33 下午 huoyuqi
 */
@Service
@Slf4j
public class FileUtils {

    @Autowired
    private StorageInfoService storageInfoService;

    private final static String FILE_SUFFIX = "gz";

    public File constructFile(String fileName, String key) {
        InputStream inputStream = storageInfoService.downloadFileFromAliS3(key);
        if (null == inputStream) {
            throw new ServerException(ResultCode.NOT_FOUND_IN_DB);
        }

        if (fileName.contains(FILE_SUFFIX)) {
            fileName = fileName.substring(0, fileName.lastIndexOf(".gz"));
            try {
                inputStream = new GZIPInputStream(inputStream);
                File convFile = new File(fileName);
                try {
                    convFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(convFile);
                    byte[] b = new byte[1024];

                    int bytesRead;
                    while ((bytesRead = inputStream.read(b)) != -1) {
                        fos.write(b, 0, bytesRead);
                    }
                    inputStream.close();
                    fos.flush();
                    fos.close();
                    return convFile;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (Exception e) {
                log.error("constructFile parse gz.file occurs an error", e);
            }
        }
        return new File(fileName);
    }

    public File constructFile(MultipartFile file) {
        if (null == file) {
            return null;
        }
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convFile;
    }

}