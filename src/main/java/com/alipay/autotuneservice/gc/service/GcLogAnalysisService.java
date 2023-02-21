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
package com.alipay.autotuneservice.gc.service;

import com.alipay.autotuneservice.controller.model.diagnosis.FileVO;
import com.alipay.autotuneservice.gc.model.GCObject;
import com.alipay.autotuneservice.gc.model.GcVO;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.rule.RuleAction;
import org.eclipse.jifa.gclog.model.GCTimeStamp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author huoyuqi
 */
public interface GcLogAnalysisService {

    /**
     * gc log analysis by history file
     *
     * @param startTime
     * @param endTime
     * @param fileName
     * @param s3key
     * @return
     */
    GcVO gcFileAnalysis(Long startTime, Long endTime, String fileName, String s3key);

    /**
     * 获取文件的历史记录
     *
     * @param ruleAction
     * @param commandStatus
     * @param podName
     * @param startTime
     * @param endTime
     * @return
     */
    List<FileVO> getHistory(RuleAction ruleAction, CommandStatus commandStatus, String podName, Long startTime, Long endTime, String appName);

    /**
     * 上传文件
     *
     * @param file
     * @param taskName
     * @param ruleAction
     * @param startTime
     * @param endTime
     * @return
     */
    Boolean upload(MultipartFile file, String taskName, RuleAction ruleAction, Long startTime, Long endTime, String appName);

    /**
     * 自动上传文件
     * @param unicode
     * @param podName
     * @param ruleAction
     * @param taskName
     * @param context
     * @return
     */
    Boolean autoUpload(String unicode, String podName, RuleAction ruleAction, String taskName, Map<String, Object> context, String appName);

    /**
     * 删除相应记录
     *
     * @param id
     * @return
     */
    Boolean delete(Long id);

    /**
     * 获取应用的 晋升大小  晋升速率  吞吐量
     *
     * @param fileName
     * @param key
     * @return
     */
    GCObject gcObjectSPT(String fileName, String key);

    /**
     * 获取FullGC之后 youngGen的大小
     *
     * @param fileName
     * @param key
     * @return
     */
    List<GCTimeStamp> getGenFGC(String fileName, String key);

}