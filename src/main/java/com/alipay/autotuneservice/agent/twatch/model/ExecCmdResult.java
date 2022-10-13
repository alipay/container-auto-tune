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
package com.alipay.autotuneservice.agent.twatch.model;

import lombok.Data;

/**
 * @author huangkaifei
 * @version : TwatchCmdExecResult.java, v 0.1 2022年05月10日 11:29 AM huangkaifei Exp $
 */
@Data
public class ExecCmdResult<T> {

    private boolean success = false;
    private T       data;

    public ExecCmdResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public ExecCmdResult<T> setData(T data) {
        this.data = data;
        return this;
    }
}