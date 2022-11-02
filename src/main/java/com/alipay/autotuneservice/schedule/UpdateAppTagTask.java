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
package com.alipay.autotuneservice.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.configuration.EnvHandler;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.common.AppInstallInfo;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.util.TraceIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author huoyuqi
 * @version UpdateAppTagTask.java, v 0.1 2022年06月13日 9:05 下午 huoyuqi
 */

@Slf4j
@Component
public class UpdateAppTagTask {

    private static final String LOCK_LEY = "UpdateAppTagTask_V1";

    @Autowired
    private EnvHandler          envHandler;

    @Autowired
    private RedisClient         redisClient;

    @Autowired
    private AppInfoRepository   appInfoRepository;

    @Autowired
    private AppInfoService      appInfoService;

    @Scheduled(fixedRate = 60 * 2000)
    public void doTask() {
        if (envHandler.isDev()) {
            return;
        }
        try {
            TraceIdGenerator.generateAndSet();
            invoke();
            //redisClient.doExec(LOCK_LEY, this::invoke);
        } finally {
            TraceIdGenerator.clear();
        }
    }

    void invoke() {
        try {
            log.info("UpdateAppTagTask enter");
            List<AppInfoRecord> records = appInfoRepository.getAppListByStatus(AppStatus.ALIVE);
            records.forEach(record -> {
                AppTag tag = record.getAppTag() != null ? JSON.parseObject(record.getAppTag(), new TypeReference<AppTag>() {}) : null;
                if (tag != null) {
                    AppInstallInfo appInstallInfo = appInfoService.findAppInstallInfo(record.getId());
                    tag.setInstallAgent(appInstallInfo.isInstallAutoAgent());
                    tag.setInstallDockFile(appInstallInfo.isIntegrateDockerFile());
                    if(tag.getLastModifyTime() == null){
                        tag.setLastModifyTime(System.currentTimeMillis());
                    }
                    record.setAppTag(JSONObject.toJSONString(tag));
                    appInfoRepository.updateAppTag(record);
                }
            });
        } catch (Exception e) {
            log.error("updateAppTag is error", e);
        }
    }
}