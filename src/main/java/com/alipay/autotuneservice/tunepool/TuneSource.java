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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.JvmMarketInfo;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.TunePoolInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePoolInfoRecord;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.model.tunepool.MetaDataType;
import com.alipay.autotuneservice.model.tunepool.PoolType;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.alipay.autotuneservice.model.tunepool.MetaData.Type.NUMBER;

/**
 * 单例;调节资源句柄,可以获取应用对应的实验池、调参池
 *
 * @author chenqu
 * @version : TuneSource.java, v 0.1 2022年03月29日 14:20 chenqu Exp $
 */
@Slf4j
public class TuneSource {

    private static final Map<String, TuneSource> CACHE_SOURCE  = Maps.newConcurrentMap();
    private final        Map<PoolType, TunePool> TUNE_POOL_MAP = Maps.newConcurrentMap();
    private              ApplicationContext      applicationContext;
    private              TuneEntity              tuneEntity;
    private              TunePoolInfo            tunePoolInfo;
    private              AppInfoRepository       appInfo;
    private              JvmMarketInfo           jvmMarketInfo;
    private              PodInfo                 podInfo;

    private TuneSource() {
    }

    private TuneSource(TuneSource tuneSource) {
        this.applicationContext = tuneSource.applicationContext;
        this.tuneEntity = tuneSource.tuneEntity;
        this.tunePoolInfo = (TunePoolInfo) applicationContext.getBean("tunePoolInfoImpl");
        this.appInfo = (AppInfoRepository) applicationContext.getBean("appInfoRepositoryImpl");
        this.jvmMarketInfo = (JvmMarketInfo) applicationContext.getBean("jvmMarketInfoImpl");
        this.podInfo = (PodInfo) applicationContext.getBean("podInfoImpl");
        if (TUNE_POOL_MAP.isEmpty()) {
            initSource();
        }
    }

    /**
     * 返回实验池
     *
     * @return
     */
    public TunePool experimentTunePool() {
        return TUNE_POOL_MAP.get(PoolType.EXPERIMENT);
    }

    /**
     * 返回批量池
     *
     * @return
     */
    public TunePool batchTunePool() {
        return TUNE_POOL_MAP.get(PoolType.BATCH);
    }

    public void resetSource() {
        initSource();
    }

    //init初始化
    private void initSource() {
        TunePoolInfoRecord tunePoolInfoRecord = tunePoolInfo.getTunePool(tuneEntity.getAccessToken(), tuneEntity.getPipelineId(),
                tuneEntity.getAppId());
        if (tunePoolInfoRecord == null) {
            //find
            String appName = appInfo.getAppName(tuneEntity.getAppId());
            if (StringUtils.isEmpty(appName)) {
                log.warn(String.format("not found appName by %s", tuneEntity.getAppId()));
                return;
            }
            //create
            tunePoolInfoRecord = new TunePoolInfoRecord();
            tunePoolInfoRecord.setPipelineId(tuneEntity.getPipelineId());
            tunePoolInfoRecord.setAccessToken(tuneEntity.getAccessToken());
            tunePoolInfoRecord.setAppId(tuneEntity.getAppId());
            tunePoolInfoRecord.setAppName(appName);
            tunePoolInfo.createTunePool(tunePoolInfoRecord);
        }
        //转换池
        buildTunePool(tunePoolInfoRecord);
    }

    private void buildTunePool(TunePoolInfoRecord tunePoolInfoRecord) {
        Arrays.stream(PoolType.values()).forEach(poolType -> {
            String config;
            String status;
            switch (poolType) {
                case EXPERIMENT:
                    config = tunePoolInfoRecord.getExperimentPoolConfig();
                    status = tunePoolInfoRecord.getExperimentPoolStatus();
                    break;
                case BATCH:
                    config = tunePoolInfoRecord.getBatchPoolConfig();
                    status = tunePoolInfoRecord.getBatchPoolStatus();
                    break;
                default:
                    return;
            }
            Map<MetaDataType, MetaData> tuneConfig = StringUtils.isEmpty(config) ? initMap() : JSON.parseObject(
                    config,
                    new TypeReference<Map<MetaDataType, MetaData>>() {
                    });
            if (MapUtils.isEmpty(tuneConfig)) {
                tuneConfig = initMap();
            }
            TUNE_POOL_MAP.put(poolType,
                    new TunePool(TunePoolStatus.valueOf(status),
                            this.tuneEntity,
                            tuneConfig,
                            (pool) -> {
                                //存储
                                TunePoolInfoRecord record = new TunePoolInfoRecord();
                                record.setAppId(pool.getTuneEntity().getAppId());
                                record.setAccessToken(pool.getTuneEntity().getAccessToken());
                                record.setPipelineId(pool.getTuneEntity().getPipelineId());
                                switch (poolType) {
                                    case BATCH:
                                        record.setBatchPoolConfig(JSONObject.toJSONString(pool.getPoolMetaConfig()));
                                        record.setBatchPoolStatus(pool.getPoolStatus().name());
                                        break;
                                    case EXPERIMENT:
                                        record.setExperimentPoolConfig(JSONObject.toJSONString(pool.getPoolMetaConfig()));
                                        record.setExperimentPoolStatus(pool.getPoolStatus().name());
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected poolType: " + poolType);
                                }
                                record.setUpdatedTime(LocalDateTime.now());
                                //更新
                                int result = tunePoolInfo.updateTunePool(record);
                                if (result <= 0) {
                                    throw new RuntimeException("update error");
                                }
                            },
                            (appId) -> {
                                List<PodInfoRecord> pods = podInfo.getByAppId(appId);
                                return pods.size();
                            }
                    ));
        });
    }

    //build
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TuneSource tuneSource;

        private Builder() {
            this.tuneSource = new TuneSource();
        }

        public Builder context(ApplicationContext applicationContext) {
            tuneSource.applicationContext = applicationContext;
            return this;
        }

        public Builder tuneEntity(TuneEntity tuneEntity) {
            tuneSource.tuneEntity = tuneEntity;
            return this;
        }

        private boolean checkEmpty() {
            return tuneSource.tuneEntity == null
                    || tuneSource.tuneEntity.checkEmpty()
                    || tuneSource.applicationContext == null;
        }

        public TuneSource build() {
            if (checkEmpty()) {
                throw new RuntimeException("tuneEntity is required,please check!");
            }
            return new TuneSource(tuneSource);
        }
    }

    private Map<MetaDataType, MetaData> initMap() {
        MetaData defaultMetaData = new MetaData();
        AppInfoRecord appInfoRecord = appInfo.getById(this.tuneEntity.getAppId());
        //JVM市场ID
        Integer jvmId = jvmMarketInfo.getOrInsertJvmByCMD(appInfoRecord.getAppDefaultJvm(), tuneEntity.getAppId(), tuneEntity.getPipelineId());
        defaultMetaData.setJvmMarketId(jvmId);
        defaultMetaData.setJvmCmd(appInfoRecord.getAppDefaultJvm());
        List<PodInfoRecord> pods = podInfo.getByAppId(this.tuneEntity.getAppId());
        defaultMetaData.setReplicas(pods.size());
        defaultMetaData.setType(NUMBER);
        return ImmutableMap.of(MetaDataType.TUNE, new MetaData(),
                MetaDataType.DEFAULT, defaultMetaData);
    }

}