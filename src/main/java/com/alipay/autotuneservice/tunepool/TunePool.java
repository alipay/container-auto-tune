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
package com.alipay.autotuneservice.tunepool;

import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.model.tunepool.MetaDataType;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 调参的池子抽象,每个池子由2个元数据组成:tunepool、default
 *
 * @author chenqu
 * @version : TunePool.java, v 0.1 2022年03月29日 14:22 chenqu Exp $
 */
@Slf4j
public final class TunePool {

    @Getter
    private TunePoolStatus              poolStatus;
    @Getter
    private TuneEntity                  tuneEntity;
    private boolean                     changeLabel    = false;
    private TunePoolStatus              tmpPoolStatus;
    private Consumer<TunePool>          callbackRefresh;
    private Function<Integer, Integer>  podTotalNum;

    /**
     * 池子元数据集
     */
    private Map<MetaDataType, MetaData> tuneMetaConfig = new ConcurrentHashMap<>();
    private Map<MetaDataType, MetaData> tmpMetaConfig  = new ConcurrentHashMap<>();

    public TunePool(TunePoolStatus poolStatus, TuneEntity tuneEntity,
                    Map<MetaDataType, MetaData> tuneMetaSource, Consumer<TunePool> callbackRefresh,
                    Function<Integer, Integer> podTotalNum) {
        this.poolStatus = poolStatus;
        this.tuneEntity = tuneEntity;
        this.tuneMetaConfig.putAll(tuneMetaSource);
        this.callbackRefresh = callbackRefresh;
        this.tmpPoolStatus = poolStatus;
        this.podTotalNum = podTotalNum;
        this.copyTmp();
    }

    private void copyTmp() {
        tuneMetaConfig.forEach((k, v) -> tmpMetaConfig.put(k, v.copy()));
    }

    public MetaData getTuneMeta() {
        return getMetaData(MetaDataType.TUNE);
    }

    public MetaData getDefaultMeta() {
        return getMetaData(MetaDataType.DEFAULT);
    }

    Map<MetaDataType, MetaData> getPoolMetaConfig() {
        //返回副本,防止变更冲突
        return tuneMetaConfig;
    }

    private MetaData getMetaData(MetaDataType metaDataType) {
        if (tuneMetaConfig.containsKey(metaDataType)) {
            return tuneMetaConfig.get(metaDataType);
        }
        return new MetaData();
    }

    public TunePool registerTuneMeta(MetaData metaData) {
        this.changeLabel = true;
        tmpMetaConfig.put(MetaDataType.TUNE, metaData);
        return this;
    }

    public TunePool moveStatus(TunePoolStatus status) {
        this.changeLabel = true;
        this.tmpPoolStatus = status;
        return this;
    }

    public TunePool updateTuneMeta(MetaData metaData) {
        this.changeLabel = true;
        MetaData sourceMetaData = tuneMetaConfig.get(MetaDataType.TUNE).copy();
        BeanUtils.copyProperties(metaData, sourceMetaData);
        tmpMetaConfig.put(MetaDataType.TUNE, sourceMetaData);
        return this;
    }

    public TunePool removeTuneMeta() {
        return moveStatus(TunePoolStatus.DELETE);
    }

    public void refresh() {
        if (!this.changeLabel) {
            log.warn("this pool not change");
            return;
        }
        synchronized (this) {
            MetaData tuneMetaData = tmpMetaConfig.get(MetaDataType.TUNE);
            Integer totalPodNum = podTotalNum.apply(tuneEntity.getAppId());
            tmpMetaConfig.forEach((type, metaData) -> {
                if (metaData == null) {
                    return;
                }
                if (type == MetaDataType.DEFAULT) {
                    metaData.setType(tuneMetaData.getType());
                    switch (tuneMetaData.getType()) {
                        case RATIO:
                            metaData.setReplicas((100 - tuneMetaData.getReplicas()));
                            break;
                        case NUMBER:
                            //TODO 总量-调试量
                            metaData.setReplicas(totalPodNum - tuneMetaData.getReplicas());
                            break;
                    }
                }
                tuneMetaConfig.put(type, metaData);
            });
            this.poolStatus = tmpPoolStatus;
            callbackRefresh.accept(this);
        }
    }
}