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
package com.alipay.autotuneservice.controller.model.configVO;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum WeekEnum {

    MON("Mon", "星期一"), TUE("Tue", "星期二"), WED("Wed", "星期三"), THU("Thu", "星期四"), FRI("Fri", "星期五"), SAT(
                                                                                                       "Sat",
                                                                                                       "星期六"), SUN(
                                                                                                                   "Sun",
                                                                                                                   "星期日");

    private String code;

    @Getter
    private String desc;

    WeekEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WeekEnum valueOfCode(String code) throws Exception{
        return Arrays.stream(values()).filter(weekEnum -> StringUtils.equals(weekEnum.code, code)).findFirst().orElseThrow(Exception::new);
    }
}
