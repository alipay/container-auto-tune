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
package com.alipay.autotuneservice.model.statistics;

import lombok.Data;

import java.util.List;

/**
 * @author huangkaifei
 * @version : BlkioStatsConfig.java, v 0.1 2022年04月20日 9:00 AM huangkaifei Exp $
 */
@Data
public class BlkioStatsConfig {
    private List<BlkioStatEntry> ioServiceBytesRecursive;
    private List<BlkioStatEntry> ioServicedRecursive;
    private List<BlkioStatEntry> ioQueueRecursive;
    private List<BlkioStatEntry> ioServiceTimeRecursive;
    private List<BlkioStatEntry> ioWaitTimeRecursive;
    private List<BlkioStatEntry> ioMergedRecursive;
    private List<BlkioStatEntry> ioTimeRecursive;
    private List<BlkioStatEntry> sectorsRecursive;
}