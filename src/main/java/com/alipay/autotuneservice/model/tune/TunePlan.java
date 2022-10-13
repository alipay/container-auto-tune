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
package com.alipay.autotuneservice.model.tune;

import com.alipay.autotuneservice.controller.model.TuneEffectVO;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dutianze
 * @version TunePlan.java, v 0.1 2022年04月28日 17:11 dutianze
 */
@Data
@Builder(setterPrefix = "with")
public class TunePlan {
    private Integer          id;
    private Integer          healthCheckId;
    private String           accessToken;
    private Integer          appId;
    private String           planName;
    private TunePlanStatus   tunePlanStatus;
    private TuneActionStatus actionStatus;
    private TuneParam        tuneParam;
    private LocalDateTime    createdTime;
    private LocalDateTime    updateTime;
    private TuneEffectVO     TuneEffectVO;
    private TuneEffectVO     predictEffectVO;
    private TunePlanStatus   tuneStatus;

    public Boolean isGrayCancel() {
        TunePlanStatus tunePlanStatus = this.getTuneStatus();
        return TunePlanStatus.ROLLBACK.equals(tunePlanStatus)
               || !TunePlanStatus.CANCEL.equals(tunePlanStatus)
               && System.currentTimeMillis() > DateUtils.asTimestamp(this.getUpdateTime().plusDays(
                   +7));
    }
}