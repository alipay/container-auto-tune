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

/**
 * @author huangkaifei
 * @version : TuneParamVO.java, v 0.1 2022年05月04日 5:42 PM huangkaifei Exp $
 */
@Data
public class TuneParamVO {
    /**
     * 参数名称
     */
    private String                 paramName;
    /**
     * 参数描述
     */
    private String                 desc;
    /**
     * 参数的原始值
     */
    private String                 originVal;
    /**
     * 参数的
     */
    private String                 currentVal;
    /**
     * 参数名称与参数值的连接符, 例如=, :
     */
    private String                 operator = "";
    /**
     * 参数属性
     */
    private TuneParamAttributeEnum attributeEnum;

    public String getTuneParam() {
        return String.format("%s%s%s", this.paramName, this.operator, this.currentVal);
    }

    public String getOriginParam() {
        if (this.attributeEnum == TuneParamAttributeEnum.NEW) {
            return "";
        }
        return String.format("%s%s%s", this.paramName, this.operator, this.originVal);
    }
}