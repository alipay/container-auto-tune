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
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMarketInfoRecord;

import java.util.List;

/**
 * @author chenqu
 * @version : JvmMarketInfo.java, v 0.1 2022年04月18日 15:54 chenqu Exp $
 */
public interface JvmMarketInfo {

    public JvmMarketInfoRecord getJvmInfo(Integer marketId);

    public Integer insert(String jvmConfig);

    public Integer getOrInsertJvmByCMD(String jvmCmd, Integer appId, Integer pipelineId);

    public void updateJvmConfig(Integer marketId, String jvmCmd);

    public List<JvmMarketInfoRecord> getJvmInfo(List<Integer> marketIds);
}