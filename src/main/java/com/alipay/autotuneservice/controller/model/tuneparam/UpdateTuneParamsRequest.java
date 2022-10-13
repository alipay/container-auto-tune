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

import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author huangkaifei
 * @version : SubmitTuneParamsRequest.java, v 0.1 2022年05月19日 12:32 PM huangkaifei Exp $
 */
@Data
public class UpdateTuneParamsRequest {

    /***
     * app id
     */
    private Integer             appId;

    /**
     * pipeline Id
     */
    private Integer             pipelineId;

    /**
     * 提交的调优参数
     */
    private List<TuneParamItem> updatedTuneParamItems;

    /**
     * 提交人
     */
    @Nullable
    private String              operator;
}