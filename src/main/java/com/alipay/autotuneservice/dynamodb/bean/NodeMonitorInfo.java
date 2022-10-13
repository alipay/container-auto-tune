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
package com.alipay.autotuneservice.dynamodb.bean;

import lombok.Data;

/**
 * @author huoyuqi
 * @version NodeMonitorInfo.java, v 0.1 2022年04月19日 12:58 下午 huoyuqi
 */
@Data
public class NodeMonitorInfo {
    private String nodeName;
    private long   gmtCreated;
    private String Host;

    public String getNodeName() {
        return this.nodeName;
    }

    public long getGmtCreated() {
        return this.gmtCreated;
    }
}