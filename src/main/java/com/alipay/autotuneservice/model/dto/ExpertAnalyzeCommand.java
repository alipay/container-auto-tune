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
package com.alipay.autotuneservice.model.dto;

import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.model.exception.ResourceNotFoundException;
import com.alipay.autotuneservice.model.expert.GarbageCollector;
import com.alipay.autotuneservice.model.expert.ProblemType;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

/**
 * @author dutianze
 * @version ExpertAnalyzeCommand.java, v 0.1 2022年04月28日 14:01 dutianze
 */
@Data
public class ExpertAnalyzeCommand {

    private Integer           appId;

    private List<ProblemType> problemTypeList;

    public GarbageCollector findGarbageCollector(AppInfo appInfo) {
        AppTag appTag = appInfo.getAppTag();
        if (appTag == null) {
            throw new ResourceNotFoundException(ResultCode.APP_TAG_NOT_FOUND);
        }
        return ObjectUtils.defaultIfNull(appTag.getCollector(), GarbageCollector.UNKNOWN);
    }
}