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
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jooq.InsertQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : K8sAccessTokenInfoImpl.java, v 0.1 2022年03月21日 16:46 chenqu Exp $
 */
@Slf4j
@Service
public class K8sAccessTokenInfoImpl extends BaseDao implements K8sAccessTokenInfo {

    @Override
    public boolean checkToken(String accessToken) {
        K8sAccessTokenInfoRecord tokenInfoRecord = mDSLContext.select()
            .from(Tables.K8S_ACCESS_TOKEN_INFO)
            .where(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN.eq(accessToken)).limit(1)
            .fetchOneInto(K8sAccessTokenInfoRecord.class);
        return tokenInfoRecord != null;
    }

    @Override
    public List<String> getClustersByToken(String accessToken) {
        List<K8sAccessTokenInfoRecord> tokenInfoRecords = mDSLContext.select()
                .from(Tables.K8S_ACCESS_TOKEN_INFO)
                .where(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN.eq(accessToken))
                .fetch()//数据类型格式转化
                .into(K8sAccessTokenInfoRecord.class);
        if (CollectionUtils.isEmpty(tokenInfoRecords)) {
            return Lists.newArrayList();
        }
        return tokenInfoRecords.stream().map(K8sAccessTokenInfoRecord::getClusterName).collect(Collectors.toList());
    }

    @Override
    public List<K8sAccessTokenInfoRecord> selectByToken(String accessToken) {
        List<K8sAccessTokenInfoRecord> records = mDSLContext.select()
            .from(Tables.K8S_ACCESS_TOKEN_INFO)
            .where(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN.eq(accessToken)).fetch()//数据类型格式转化
            .into(K8sAccessTokenInfoRecord.class);
        return records;
    }

    @Override
    public K8sAccessTokenInfoRecord selectByTokenAndCusterName(String accessToken,
                                                               String clusterName) {
        return mDSLContext
            .select()
            .from(Tables.K8S_ACCESS_TOKEN_INFO)
            .where(
                Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN.eq(accessToken).and(
                    Tables.K8S_ACCESS_TOKEN_INFO.CLUSTER_NAME.eq(clusterName))).limit(1)
            .fetchOneInto(K8sAccessTokenInfoRecord.class);
    }

    @Override
    public K8sAccessTokenInfoRecord selectTokenByClusterAndRegion(String clusterName, String region) {
        return mDSLContext
            .select()
            .from(Tables.K8S_ACCESS_TOKEN_INFO)
            .where(
                Tables.K8S_ACCESS_TOKEN_INFO.CLUSTER_NAME.eq(clusterName).and(
                    Tables.K8S_ACCESS_TOKEN_INFO.REGION.eq(region))).limit(1)
            .fetchOneInto(K8sAccessTokenInfoRecord.class);
    }

    @Override
    public int insert(K8sAccessTokenInfoRecord record) {
        InsertQuery<K8sAccessTokenInfoRecord> k8sAccessTokenInfoRecordInsertQuery = mDSLContext
            .insertQuery(Tables.K8S_ACCESS_TOKEN_INFO);
        k8sAccessTokenInfoRecordInsertQuery.addRecord(record);
        return k8sAccessTokenInfoRecordInsertQuery.execute();
    }

    @Override
    public boolean insertOrUpdate(K8sAccessTokenInfoRecord record) {
        try {
            if (record == null) {
                return false;
            }
            mDSLContext.insertInto(Tables.K8S_ACCESS_TOKEN_INFO)
                .set(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN, record.getAccessToken())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.CLUSTER_NAME, record.getClusterName())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_KEY_ID, record.getAccessKeyId())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.SECRET_ACCESS_KEY, record.getSecretAccessKey())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.CER, record.getCer())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.REGION, record.getRegion())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.ENDPOINT, record.getEndpoint())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.CREATE_TIME, record.getCreateTime())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.UPDATED_TIME, record.getUpdatedTime())
                .onDuplicateKeyUpdate()
                .set(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_KEY_ID, record.getAccessKeyId())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.SECRET_ACCESS_KEY, record.getSecretAccessKey())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.CER, record.getCer())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.REGION, record.getRegion())
                .set(Tables.K8S_ACCESS_TOKEN_INFO.ENDPOINT, record.getEndpoint()).execute();
            return true;
        } catch (Exception e) {
            log.error("insertOrUpdate occurs an error.", e);
            return false;
        }
    }

    @Override
    public List<K8sAccessTokenInfoRecord> getK8sAccessTokenInfoRecord() {
        return mDSLContext.select().from(Tables.K8S_ACCESS_TOKEN_INFO).fetch()
            .into(K8sAccessTokenInfoRecord.class);
    }
}