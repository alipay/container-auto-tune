/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.controller.model.tuneparam;

import com.alipay.autotuneservice.util.TuneParamUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author huangkaifei
 * @version : TuneParamItem.java, v 0.1 2022年05月23日 2:29 PM huangkaifei Exp $
 */
@Data
public class TuneParamItem {

    /**
     * 参数名称
     * e.g : -Xmn
     *
     * 前端不使用
     */
    private String paramName;

    /**
     * app原始的调优参数
     * e.g : -Xmn300m
     */
    private String originTuneParam;

    /**
     * app当前的调优参数
     * e.g : -Xmn400m
     */
    private String currentTuneParam;

    /**
     * 参数顺序
     * 未知设置为10000以便于参数排序
     */
    private int paramOrder = 10000;

    /**
     * app调优参数的属性
     */
    private TuneParamAttributeEnum attributeEnum;

    public String getTuneParamName() {
        if (StringUtils.isNotBlank(paramName)) {
            return paramName;
        }
        if (StringUtils.isNotBlank(originTuneParam)) {
            this.paramName = TuneParamUtil.extractTuneParamName(this);
            return this.paramName;
        }
        if (StringUtils.isNotBlank(currentTuneParam)) {
            this.paramName = TuneParamUtil.extractTuneParamName(this);
            return this.paramName;
        }
        return null;
    }
}