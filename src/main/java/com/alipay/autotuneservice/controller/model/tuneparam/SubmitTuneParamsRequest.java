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
package com.alipay.autotuneservice.controller.model.tuneparam;

import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author huangkaifei
 * @version : SubmitTuneParamsRequest.java, v 0.1 2022年05月19日 12:32 PM huangkaifei Exp $
 */
@Data
@Builder
public class SubmitTuneParamsRequest {

    /***
     * app id
     */
    private Integer appId;

    /**
     * pipeline Id
     */
    private Integer pipelineId;

    /**
     * 默认的调优参数，仅后端使用
     */
    private List<TuneParamItem> defaultTuneParamItems;

    /**
     * 提交的调优参数
     */
    private List<TuneParamItem> tuneParamItems;

    /**
     * 提交的调优分组
     */
    private List<TuneConfig> tuneGroups;

    /**
     * 提交人
     */
    private String operator;

    /**
     * 判断灰度是否是二次提交
     */
    private Boolean flag;
}