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
package com.alipay.autotuneservice.model.common;

import lombok.Data;

/**
 * @author dutianze
 * @version DumpCommand.java, v 0.1 2022年09月15日 19:01 dutianze
 */
@Data
public class DumpCommand {

    // 必传
    private Integer      appId;
    private AppLogType   type;
    private OperatorType operatorType;
    private String       hostname;

    // 非必 默认整个日志, 24表示过去一天的gc
    private Integer lookBackTimeHour;

    private long dumpId;
}