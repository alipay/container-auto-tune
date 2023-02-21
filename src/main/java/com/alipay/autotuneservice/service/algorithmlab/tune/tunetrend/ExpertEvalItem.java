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
package com.alipay.autotuneservice.service.algorithmlab.tune.tunetrend;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dutianze
 * @version ExpertEvalItem.java, v 0.1 2022年04月28日 14:08 dutianze
 */
@Data
@NoArgsConstructor
public class ExpertEvalItem {

    /**
     * 当前参数 -XX:PermSize=256
     */
    private String target;

    /**
     * 256
     */
    private String value;

    /**
     * -XX:PermSize
     */
    private String param;

    /**
     * up、down、append
     */
    private ExpertEvalType type;

    public ExpertEvalItem(String target, String value, String param, ExpertEvalType type) {
        this.target = StringUtils.trim(target);
        this.value = StringUtils.trim(value);
        this.param = StringUtils.trim(param);
        this.type = type;
    }
}