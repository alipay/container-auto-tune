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
package com.alipay.autotuneservice.model.agent;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author huangkaifei
 * @version : RedisSaveRequest.java, v 0.1 2022年04月11日 9:57 PM huangkaifei Exp $
 */
@Data
public class CallBackRequest {
    private String  sessionId;
    private String  podName;
    private String  containerId;
    private boolean actionDone = false;
    private Object  data;
    private String  traceId;

    public void checkEmpty() {
        if (StringUtils.isBlank(this.sessionId) || null == this.data) {
            throw new RuntimeException("sessionId or value is empty. so skip.");
        }
    }
}