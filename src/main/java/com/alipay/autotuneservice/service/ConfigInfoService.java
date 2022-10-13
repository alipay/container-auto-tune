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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.controller.model.configVO.ConfigInfoVO;
import com.alipay.autotuneservice.controller.model.configVO.TuneConfig;
import com.alipay.autotuneservice.dao.jooq.tables.records.ConfigInfoRecord;

import java.util.List;

public interface ConfigInfoService {

    /**
     * 根据APPID 查询 应用所在AWS机器的当前时间 是否可以调参
     * @param appID
     * @return
     */
    Boolean checkTuneIsEnableByAppID(Integer appID);

    /**
     * 根据APPID 查询 应用调参配置
     * 托管配置(人工执行/自动执行) || 风险开关配置 等等
     * @return
     */
    ConfigInfoVO findAPPConfigByAPPID(Integer appID);

    /**
     * 查询应用调参分组
     *
     * @param appID
     * @return
     */
    List<TuneConfig> findTuneGroupsByAppId(Integer appID);

    /**
     * 根据APPID 查询应用所在AWS机器 所属时区
     * @param appID
     * @return
     */
    String findTimeZone(Integer appID);

    /**
     * 根据appIds查询
     * @param appIds
     * @return
     */
    List<ConfigInfoRecord> batchAppConfigByAppIds(List<Integer> appIds);
}
