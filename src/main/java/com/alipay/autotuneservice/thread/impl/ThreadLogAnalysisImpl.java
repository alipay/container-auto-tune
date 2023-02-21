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
package com.alipay.autotuneservice.thread.impl;

import com.alipay.autotuneservice.thread.Analyzer;
import com.alipay.autotuneservice.thread.ThreadLogAnalysisService;
import com.alipay.autotuneservice.thread.model.ThreadVO;
import com.alipay.autotuneservice.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author huoyuqi
 * @version ThreadLogAnalysisImpl.java, v 0.1 2022年12月01日 6:36 下午 huoyuqi
 */
@Service
@Slf4j
public class ThreadLogAnalysisImpl implements ThreadLogAnalysisService {

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private Analyzer analyzer;

    @Override
    public ThreadVO threadAnalysis(MultipartFile file) {
        File convFile = fileUtils.constructFile(file);
        try {
            return analyzer.readJStacks(convFile);
        } catch (Exception e) {
            //do thing
            return null;
        }
    }

    @Override
    public ThreadVO threadFileAnalysis(String fileName, String s3Key) {
        File convFile = fileUtils.constructFile(fileName, s3Key);
        try {
            return analyzer.readJStacks(convFile);
        } catch (Exception e) {
            throw new RuntimeException("parse failure ", e);
        }
    }
}