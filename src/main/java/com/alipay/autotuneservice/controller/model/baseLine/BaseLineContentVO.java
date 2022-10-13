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
package com.alipay.autotuneservice.controller.model.baseLine;

import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version BaseLineContentVO.java, v 0.1 2022年08月29日 9:06 下午 huoyuqi
 */
@Data
public class BaseLineContentVO {

    /**
     * 默认参数
     */
    private List<String> param;

    /**
     * 默认版本号
     */
    private String       version;

    /**
     * 默认更新时间
     */
    private Long         time;

    public BaseLineContentVO() {

    }

    public BaseLineContentVO(List<String> param, String version, Long time) {
        this.param = param;
        this.version = version;
        this.time = time;
    }

}