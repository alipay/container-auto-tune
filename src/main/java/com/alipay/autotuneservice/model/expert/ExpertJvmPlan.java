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
package com.alipay.autotuneservice.model.expert;

import com.alipay.autotuneservice.model.dto.ExpertEvalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author dutianze
 * @version ExpertJvmPlan.java, v 0.1 2022年04月28日 14:55 dutianze
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExpertJvmPlan {

    /**
     * 可优化的jvm参数
     */
    private String         jvmOpts;

    /**
     * 优化方向
     */
    private ExpertEvalType expertEvalType;

    public String extractTarget(String[] jvmOpts) {
        return Arrays.stream(ArrayUtils.nullToEmpty(jvmOpts))
                .filter(jvmOpt -> jvmOpt.contains(this.jvmOpts))
                .findFirst().orElse("");
    }

    public String extractTargetValue(String target) {
        if (StringUtils.isEmpty(target)) {
            return "";
        }
        return target.replaceAll("=", "").replaceAll(jvmOpts, "");
    }
}