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

import com.alipay.autotuneservice.controller.model.AppInfoVO;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;

import java.util.Collection;
import java.util.List;

/**
 * @author t-rex
 * @version AppInfoRepository.java, v 0.1 2022年02月17日 8:25 下午 t-rex
 */
public interface AppInfoRepository {

    List<AppInfoRecord> getByIds(Collection<Integer> ids);

    List<AppInfoRecord> getByNodeId(Integer id);

    List<AppInfoRecord> getAppListByStatus(AppStatus appStatus);

    List<AppInfoRecord> getReportedApp();

    List<AppInfoRecord> getAppListByTokenAndStatus(String accessToken, AppStatus status);

    AppInfoRecord findAppModel(String accessToken, String k8sNamespace, String appName);

    AppInfoRecord findAliveAppModel(String accessToken, String k8sNamespace, String appName);

    String getAppName(Integer id);

    AppInfoRecord getById(Integer id);

    int insetAppInfo(AppInfoRecord record);

    void insertAppInfoRecord(AppInfoRecord record);

    AppInfoRecord getByAppAndATAndNamespace(String appName, String accessToken, String namesapce);

    AppInfoRecord getByAppAndATAndNamespace(String appName, String namesapce);

    AppInfo findByAppAndATAndNamespace(String appName, String accessToken, String namespace);

    int updateNodeIds(AppInfoRecord record);

    int updateAppDefaultJvm(AppInfoRecord record);

    List<AppInfoRecord> appList(String accessToken, String appName);

    AppInfoRecord findByIdAndToken(String accessToken, Integer id);

    List<AppInfoRecord> getAppByTokenAndCluster(String accessToken, String cluster);

    AppInfo findById(Integer id);

    void save(Integer id, AppTag appTag);

    void deleteAppById(Integer id);
}