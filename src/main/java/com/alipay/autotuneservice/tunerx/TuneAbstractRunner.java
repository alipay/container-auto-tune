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
package com.alipay.autotuneservice.tunerx;

import com.alipay.autotuneservice.model.tunepool.TuneConsistencyRq;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import com.alipay.autotuneservice.tunepool.TunePool;
import com.alipay.autotuneservice.tunepool.TuneProcessor;
import com.alipay.autotuneservice.tunepool.TuneSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chenqu
 * @version : TuneAbstractRunner.java, v 0.1 2022年05月11日 16:42 chenqu Exp $
 */
public abstract class TuneAbstractRunner {

    @Autowired
    private TuneProcessor tuneProcessor;

    void changePoolToT(TuneConsistencyRq.ChangeRq changeRq) {
        //获取资源句柄
        TuneSource tuneSource = tuneProcessor.getTuneSource(TuneEntity.builder()
            .accessToken(changeRq.getAccessToken()).appId(changeRq.getAppId())
            .pipelineId(changeRq.getPipelineId()).build());
        TunePool tunePool = null;
        switch (changeRq.getPoolType()) {
            case EXPERIMENT:
                tunePool = tuneSource.experimentTunePool();
                break;
            case BATCH:
                tunePool = tuneSource.batchTunePool();
                break;
            default:
                break;
        }
        if (tunePool == null) {
            return;
        }
        //更新为完成
        tunePool.registerTuneMeta(tunePool.getTuneMeta()).moveStatus(TunePoolStatus.TERMINATED)
            .refresh();
    }

}