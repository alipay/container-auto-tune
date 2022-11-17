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
package com.alipay.autotuneservice.model.pipeline;

import org.apache.commons.lang3.StringUtils;

/**
 * @author dutianze
 * @version Status.java, v 0.1 2022年03月30日 16:49 dutianze
 */
public enum Status {

    // 初始
    INIT,

    // 等待
    WAIT,

    // 运行中
    RUNNING,

    // 关闭
    CLOSED,

    // 取消
    CANCEL,

    // 异常
    EXCEPTION, ;

    public static Status findByName(String statusStr) {
        for (Status status : values()) {
            if (StringUtils.equals(status.name(), statusStr)) {
                return status;
            }
        }
        throw new UnsupportedOperationException(String.format("%s is not supported.", statusStr));
    }

}