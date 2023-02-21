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
package com.alipay.autotuneservice.service.alarmManger.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author huoyuqi
 * @version ActionEnum.java, v 0.1 2022年12月23日 2:07 下午 huoyuqi
 */
public enum ActionEnum {

    GC_DUMP("自动上传GC日志"),

    HEAP_DUMP("自动上传内存日志"),

    THREAD_DUMP("自动dump线程top"),

    JVM_PROFILE("自动保存火焰图"),

    NOTICE("通知订阅");

    @Getter
    private String desc;

    ActionEnum(String desc){
        this.desc = desc;
    }

    public static ActionEnum valueOfCode(String desc) throws Exception{
        return Arrays.stream(values()).filter(ActionEnum -> StringUtils.equals(ActionEnum.desc, desc)).findFirst().orElseThrow(Exception::new);
    }

}