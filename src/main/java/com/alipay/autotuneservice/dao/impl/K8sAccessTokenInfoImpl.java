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

import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.ConcurrentHashSet;
import org.jooq.InsertQuery;
import org.jooq.UpdateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : K8sAccessTokenInfoImpl.java, v 0.1 2022年03月21日 16:46 chenqu Exp $
 */
@Slf4j
@Service
public class K8sAccessTokenInfoImpl extends BaseDao implements K8sAccessTokenInfo {
    private final Set<String> ACCESS_TOKEN_SET = new ConcurrentHashSet<>(16);

    @Autowired
    private AppInfoRepository appInfoRepository;

    @Override
    public boolean checkToken(String accessToken) {
        K8sAccessTokenInfoRecord tokenInfoRecord = mDSLContext.select()
                .from(Tables.K8S_ACCESS_TOKEN_INFO)
                .where(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN.eq(accessToken))
                .limit(1)
                .fetchOneInto(K8sAccessTokenInfoRecord.class);
        return tokenInfoRecord != null;
    }

    @Override
    public void validAndCacheAccessToken(String accessToken) {
        Preconditions.checkArgument(StringUtils.isNotBlank(accessToken), "accessToken不能为空.");
        if (ACCESS_TOKEN_SET.contains(accessToken)) {
            return;
        }
        if (!checkToken(accessToken)) {
            String errMsg = "validAndCacheAccessToken - The accessToken is invalid, please check.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        ACCESS_TOKEN_SET.add(accessToken);
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
                .where(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN.eq(accessToken))
                .fetch()//数据类型格式转化
                .into(K8sAccessTokenInfoRecord.class);
        return records;
    }

    @Override
    public K8sAccessTokenInfoRecord getK8sRecord(String clusterName, String namespace, String appName) {
        AppInfoRecord appInfoRecord = appInfoRepository.getByAppAndATAndNamespace(appName, namespace);
        if (appInfoRecord == null) {
            String errMsg = String.format("Can not get app record by appName=%s", appName);
            log.error(errMsg);
            throw new RuntimeException(errMsg);
        }
        return selectByTokenAndCusterName(appInfoRecord.getAccessToken(), clusterName);
    }

    @Override
    public K8sAccessTokenInfoRecord selectByTokenAndCusterName(String accessToken, String clusterName) {
        return mDSLContext.select()
                .from(Tables.K8S_ACCESS_TOKEN_INFO)
                .where(Tables.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN.eq(accessToken).
                        and(Tables.K8S_ACCESS_TOKEN_INFO.CLUSTER_NAME.eq(clusterName)))
                .limit(1)
                .fetchOneInto(K8sAccessTokenInfoRecord.class);
    }

    @Override
    public K8sAccessTokenInfoRecord selectTokenByClusterAndRegion(String clusterName, String region) {
        return mDSLContext.select()
                .from(Tables.K8S_ACCESS_TOKEN_INFO)
                .where(Tables.K8S_ACCESS_TOKEN_INFO.CLUSTER_NAME.eq(clusterName).
                        and(Tables.K8S_ACCESS_TOKEN_INFO.REGION.eq(region)))
                .limit(1)
                .fetchOneInto(K8sAccessTokenInfoRecord.class);
    }

    @Override
    public int insert(K8sAccessTokenInfoRecord record) {
        InsertQuery<K8sAccessTokenInfoRecord> k8sAccessTokenInfoRecordInsertQuery = mDSLContext.insertQuery(Tables.K8S_ACCESS_TOKEN_INFO);
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
                    .set(Tables.K8S_ACCESS_TOKEN_INFO.REGION, record.getRegion())
                    .set(Tables.K8S_ACCESS_TOKEN_INFO.CREATE_TIME, record.getCreateTime())
                    .set(Tables.K8S_ACCESS_TOKEN_INFO.UPDATED_TIME, record.getUpdatedTime())
                    .set(Tables.K8S_ACCESS_TOKEN_INFO.S3_KEY, record.getS3Key())
                    .onDuplicateKeyUpdate()
                    .set(Tables.K8S_ACCESS_TOKEN_INFO.UPDATED_TIME, record.getUpdatedTime())
                    .set(Tables.K8S_ACCESS_TOKEN_INFO.S3_KEY, record.getS3Key())
                    .returning()
                    .fetch();
            return true;
        } catch (Exception e) {
            String errMsg = "K8sAccessTokenInfo insertOrUpdate occurs an error.";
            log.error(errMsg, e);
            throw new RuntimeException(errMsg);
        }
    }

    @Override
    public void update(@NonNull K8sAccessTokenInfoRecord record) {
        try {
            UpdateQuery<K8sAccessTokenInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.K8S_ACCESS_TOKEN_INFO);
            updateQuery.addValue(Tables.K8S_ACCESS_TOKEN_INFO.S3_KEY, record.getS3Key());
            updateQuery.addValue(Tables.K8S_ACCESS_TOKEN_INFO.UPDATED_TIME, LocalDateTime.now());
            if (StringUtils.isNotBlank(record.getClusterStatus())) {
                updateQuery.addValue(Tables.K8S_ACCESS_TOKEN_INFO.CLUSTER_STATUS, record.getClusterStatus());
            }
            updateQuery.addConditions(Tables.K8S_ACCESS_TOKEN_INFO.ID.eq(record.getId()));
            updateQuery.execute();
        } catch (Exception e) {
            String errMsg = "K8sAccessTokenInfo update occurs an error.";
            log.error(errMsg, e);
            throw new RuntimeException(errMsg);
        }
    }

    @Override
    public List<K8sAccessTokenInfoRecord> getK8sAccessTokenInfoRecord() {
        return mDSLContext.select()
                .from(Tables.K8S_ACCESS_TOKEN_INFO)
                .fetch()
                .into(K8sAccessTokenInfoRecord.class);
    }
}