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

import com.alipay.autotuneservice.dao.jooq.tables.records.TuneParamInfoRecord;
import com.alipay.autotuneservice.model.tune.params.TuneParamUpdateStatus;

import java.util.List;

/**
 * @author huangkaifei
 * @version : TuneParamInfoRepository.java, v 0.1 2022年05月18日 2:04 PM huangkaifei Exp $
 */
public interface TuneParamInfoRepository {

    TuneParamInfoRecord getById(Integer id);

    List<TuneParamInfoRecord> getByAppId(Integer appId);

    TuneParamInfoRecord findByAppId(Integer appId);

    TuneParamInfoRecord getByAppIdAndPipelineId(Integer appId, Integer pipelineId);

    TuneParamInfoRecord getByAppIdAndPipelineIdAndStatus(Integer appId, Integer pipelineId,
                                                         TuneParamUpdateStatus updateStatus);

    int insert(TuneParamInfoRecord tuneParamInfoRecord);

    TuneParamInfoRecord findByDecisionId(String decisionId);

    TuneParamInfoRecord getUserModifiedJvmOption(Integer appId, Integer jvmMarketId);

    int update(TuneParamInfoRecord record);

    TuneParamInfoRecord findTunableTuneParamRecord(Integer appId, Integer pipelineId);

    TuneParamInfoRecord findTuneParamRecord(Integer appId, Integer pipelineId,
                                            TuneParamUpdateStatus status);

    Integer updateJvmMarketId(Integer appId, Integer pipelineId, Integer jvmMarketId);

}