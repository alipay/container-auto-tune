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

/**
 * @author huangkaifei
 * @version : StatsConfig.java, v 0.1 2022年04月20日 8:54 AM huangkaifei Exp $
 */
@Data
public class StatsConfig {
    private Long activeAnon;
    private Long activeFile;
    private Long cache;
    private Long dirty;
    private Long hierarchicalMemoryLimit;
    private Long hierarchicalMemswLimit;
    private Long inactiveAnon;
    private Long inactiveFile;
    private Long mappedFile;
    private Long pgfault;
    private Long pgmajfault;
    private Long pgpgin;
    private Long pgpgout;
    private Long rss;
    private Long rssHuge;
    private Long swap;
    private Long totalActiveAnon;
    private Long totalActiveFile;
    private Long totalCache;
    private Long totalDirty;
    private Long totalInactiveAnon;
    private Long totalInactiveFile;
    private Long totalMappedFile;
    private Long totalPgfault;
    private Long totalPgmajfault;
    private Long totalPgpgin;
    private Long totalPgpgout;
    private Long totalRss;
    private Long totalRssHuge;
    private Long totalSwap;
    private Long totalUnevictable;
    private Long totalWriteback;
    private Long unevictable;
    private Long writeback;
}