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

import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;

import java.util.List;

/**
 * @author huoyuqi
 * @version PodInfo.java, v 0.1 2022年04月18日 7:13 下午 huoyuqi
 */
public interface PodInfo {

    void insertPodInfo(PodInfoRecord podInfoRecord);

    List<PodInfoRecord> getAllPods();

    List<PodInfoRecord> getAllVMPods();

    List<PodInfoRecord> getByAppId(Integer appId);

    List<PodInfoRecord> getAllAlivePods();

    List<PodInfoRecord> getDHostNameAlivePods(String dHostName);

    List<PodInfoRecord> getByAllPodByAppId(Integer appId);

    List<PodInfoRecord> getAllAlivePodsByToken(String accessToken, String clusterName);

    List<PodInfoRecord> findByAppIds(List<Integer> appIds);

    int update(PodInfoRecord record);

    PodInfoRecord getById(Integer id);

    int updatePodInfoResourceFields(PodInfoRecord record);

    PodInfoRecord getByPodAndAT(String podName, String accessToken);

    PodInfoRecord getByPodAndAN(String podName, String namespace);

    PodInfoRecord getByPodAndAID(String podName, Integer appId);

    String findOneRunningPodNameByAppId(Integer appId);

    PodInfoRecord findOneRunningPodByAppId(Integer appId);

    List<PodInfoRecord> findByAccessToken(String accessToken);

    int updatePodInstallTuneAgent(PodInfoRecord record);

    List<PodInfoRecord> getPodInstallTuneAgentNumsByAppId(Integer appId);

    List<PodInfoRecord> batchGetPodInstallTuneAgentNumsByAppId(List<Integer> appId);

    List<PodInfoRecord> getAllAlivePodsByApp(Integer appId);

    void deletePod(Integer appId);

    PodInfoRecord findById(Integer id);

    void updateServerTypeUnicode(PodInfoRecord record);

    PodInfoRecord findByUnicode(String unicode);
}