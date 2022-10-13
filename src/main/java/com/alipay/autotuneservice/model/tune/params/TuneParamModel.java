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
package com.alipay.autotuneservice.model.tune.params;

import lombok.Data;

/**
 * @author huangkaifei
 * @version : TuneParamModel.java, v 0.1 2022年05月17日 10:41 PM huangkaifei Exp $
 */
@Data
public class TuneParamModel {
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数描述
     */
    private String desc;
    /**
     * 参数值
     */
    private String paramVal;
    /**
     * 参数名称和参数值之间的连接符
     */
    private String operator;
}