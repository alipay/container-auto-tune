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
package com.alipay.autotuneservice.controller.model.diagnosis;

import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.rule.RuleAction;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huoyuqi
 * @version FileVO.java, v 0.1 2022年11月15日 3:22 下午 huoyuqi
 */
@Data
@AllArgsConstructor
public class FileVO {

    private Long id;

    /**
     * 类型
     */
    private RuleAction fileType;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * pod名称
     */
    private String podName;

    /**
     * 触发时间
     */
    private Long createTime;

    /**
     * 生成状态
     */
    private CommandStatus commandStatus;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * s3key
     */
    private String s3Key;


}