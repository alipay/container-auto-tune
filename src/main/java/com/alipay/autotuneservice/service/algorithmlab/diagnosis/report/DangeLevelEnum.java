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
package com.alipay.autotuneservice.service.algorithmlab.diagnosis.report;

import lombok.Getter;

/**
 * 通过风险等级，对当前系统进行打分
 * @author hognshu
 * @version AnasisMetricEnum.java, v 0.1 2022年10月26日 11:17 下午 hognshu
 */
@Getter
public enum DangeLevelEnum {
    DISASTER(1,50),
    EXCEPTION(2,10),
    WARN(3,5),
    SUGGESTION(4,1),


    ;
    private final int level;;
    private final int multiple;

    DangeLevelEnum(int level, int multiple) {
        this.level = level;
        this.multiple = multiple;
    }
}