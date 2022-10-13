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
package com.alipay.autotuneservice.controller.model.tuneplan;

import com.alipay.autotuneservice.model.tune.TunePlanStatus;

/**
 * @author huangkaifei
 * @version : TunePlanActionEnum.java, v 0.1 2022年05月07日 10:26 AM huangkaifei Exp $
 */
public enum TunePlanActionEnum {
    /**
     * 确认执行指令
     */
    CONFIRM_EXEC(10001, TunePlanStatus.CONFIRM),
    // TODO 需要确认TunePlan 和 TunePipeline的stop的状态
    /**
     * 暂停指令
     */
    STOP_EXEC(10002, TunePlanStatus.PAUSE),
    /**
     * 终止指令
     */
    END_EXEC(10003, TunePlanStatus.END), ;

    private final Integer        code;
    private final TunePlanStatus status;

    TunePlanActionEnum(Integer code, TunePlanStatus status) {
        this.code = code;
        this.status = status;
    }

    public static TunePlanActionEnum getByCode(Integer code) {
        for (TunePlanActionEnum actionEnum : values()) {
            if (actionEnum.code.equals(code)) {
                return actionEnum;
            }
        }
        throw new RuntimeException("can not found by " + code);
    }

    public static Boolean checkValid(Integer code) {
        try {
            for (TunePlanActionEnum actionEnum : values()) {
                if (actionEnum.code.equals(code)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        } catch (Exception e) {
            // do nothing
            return Boolean.FALSE;
        }
    }

    public Integer getCode() {
        return code;
    }

    public TunePlanStatus getStatus() {
        return status;
    }
}