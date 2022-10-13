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

import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;

import java.util.List;

/**
 * @author chenqu
 * @version : K8sAccessTokenInfo.java, v 0.1 2022年03月21日 16:45 chenqu Exp $
 */
public interface K8sAccessTokenInfo {

    boolean checkToken(String accessToken);

    List<String> getClustersByToken(String accessToken);

    K8sAccessTokenInfoRecord selectByTokenAndCusterName(String accessToken, String clusterName);

    K8sAccessTokenInfoRecord selectTokenByClusterAndRegion(String clusterName, String region);

    int insert(K8sAccessTokenInfoRecord record);

    boolean insertOrUpdate(K8sAccessTokenInfoRecord record);

    List<K8sAccessTokenInfoRecord> getK8sAccessTokenInfoRecord();

    List<K8sAccessTokenInfoRecord> selectByToken(String accessToken);
}