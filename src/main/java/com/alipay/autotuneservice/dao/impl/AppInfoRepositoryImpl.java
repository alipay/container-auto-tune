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
import com.alipay.autotuneservice.dao.converter.AppInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.GsonUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static org.jooq.impl.DSL.concat;

/**
 * @author t-rex
 * @version ClusterInfoImpl.java, v 0.1 2022年02月17日 8:24 下午 t-rex
 */
@Service
@Slf4j
public class AppInfoRepositoryImpl extends BaseDao implements AppInfoRepository {

    private final AppInfoConverter converter = new AppInfoConverter();

    @Override
    public List<AppInfoRecord> getAppListByTokenAndStatus(String accessToken, AppStatus status) {
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken))
                .and(Tables.APP_INFO.STATUS.eq(status.name()))
                .orderBy(Tables.APP_INFO.APP_TAG.desc())
                .fetchInto(AppInfoRecord.class);
    }

    @Override
    public String getAppName(Integer id) {
        AppInfoRecord record = getById(id);
        if (record == null) {
            return "";
        }
        return record.getAppName();
    }

    @Override
    public AppInfoRecord getById(Integer id) {
        List<AppInfoRecord> records = mDSLContext.select().from(Tables.APP_INFO).where(Tables.APP_INFO.ID.eq(id))
                .fetch().into(AppInfoRecord.class);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.get(0);
    }

    @Override
    public List<AppInfoRecord> getByIds(Collection<Integer> ids) {
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ID.in(ids))
                .fetchInto(AppInfoRecord.class);
    }

    @Override
    public List<AppInfoRecord> getByNodeId(Integer id) {
        String nodeId = String.valueOf(id);
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.NODE_IDS.contains(nodeId))
                .fetchInto(AppInfoRecord.class);
    }

    @Override
    public AppInfoRecord findAppModel(String accessToken, String k8sNamespace, String appName) {
        return mDSLContext.select().from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken))
                .and(Tables.APP_INFO.NAMESPACE.eq(k8sNamespace))
                .and(Tables.APP_INFO.APP_NAME.eq(appName))
                .fetchOneInto(AppInfoRecord.class);
    }

    @Override
    public AppInfoRecord findAliveAppModel(String accessToken, String k8sNamespace, String appName) {
        return mDSLContext.select().from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken))
                .and(Tables.APP_INFO.NAMESPACE.eq(k8sNamespace))
                .and(Tables.APP_INFO.APP_NAME.eq(appName))
                .and(Tables.APP_INFO.STATUS.eq(AppStatus.ALIVE.name()))
                .fetchOneInto(AppInfoRecord.class);
    }

    @Override
    public int insetAppInfo(AppInfoRecord record) {
        AppInfoRecord insertRecord = mDSLContext.newRecord(Tables.APP_INFO);
        insertRecord.setAppName(record.getAppName());
        insertRecord.setStatus(AppStatus.ALIVE.name());
        insertRecord.setCreatedTime(DateUtils.now());
        insertRecord.setClusterName(record.getClusterName());
        insertRecord.setAccessToken(record.getAccessToken());
        insertRecord.setNamespace(record.getNamespace());
        insertRecord.setAppDefaultJvm(record.getAppDefaultJvm());
        if (StringUtils.isNotEmpty(record.getAppTag())) {
            insertRecord.setAppTag(record.getAppTag());
        }
        insertRecord.store();
        return insertRecord.getId();
    }

    @Override
    public void insertAppInfoRecord(AppInfoRecord record) {
        log.info("insertAppInfoRecord, record appName:{}", record.getAppName());
        mDSLContext.insertInto(Tables.APP_INFO)
                .set(Tables.APP_INFO.USER_ID, record.getUserId())
                .set(Tables.APP_INFO.ACCESS_TOKEN, record.getAccessToken())
                .set(Tables.APP_INFO.NODE_IDS, record.getNodeIds())
                .set(Tables.APP_INFO.APP_NAME, record.getAppName())
                .set(Tables.APP_INFO.APP_AS_NAME, record.getAppAsName())
                .set(Tables.APP_INFO.APP_DESC, record.getAppDesc())
                .set(Tables.APP_INFO.CREATED_TIME, record.getCreatedTime())
                .set(Tables.APP_INFO.STATUS, record.getStatus())
                .set(Tables.APP_INFO.APP_DEFAULT_JVM, record.getAppDefaultJvm())
                .set(Tables.APP_INFO.CLUSTER_NAME, record.getClusterName())
                .set(Tables.APP_INFO.NAMESPACE, record.getNamespace())
                .onDuplicateKeyUpdate()
                .set(Tables.APP_INFO.STATUS, record.getStatus())
                .set(Tables.APP_INFO.NAMESPACE, record.getNamespace())
                .set(Tables.APP_INFO.ACCESS_TOKEN, record.getAccessToken())
                .returning()
                .fetch();
    }

    @Override
    public AppInfoRecord getByAppAndATAndNamespace(String appName, String accessToken, String namesapce) {
        List<AppInfoRecord> appInfoRecords = mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.APP_NAME.eq(appName)
                        .and(Tables.APP_INFO.STATUS.eq(AppStatus.ALIVE.name()))
                        .and(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken))
                        .and(Tables.APP_INFO.NAMESPACE.eq(namesapce)))
                .fetchInto(AppInfoRecord.class);
        if (CollectionUtils.isEmpty(appInfoRecords)) {
            return null;
        }
        return appInfoRecords.get(0);
    }

    @Override
    public AppInfoRecord getByAppAndATAndNamespace(String appName, String namesapce) {
        List<AppInfoRecord> appInfoRecords = mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.APP_NAME.eq(appName)
                        .and(Tables.APP_INFO.NAMESPACE.eq(namesapce)))
                .fetchInto(AppInfoRecord.class);
        if (CollectionUtils.isEmpty(appInfoRecords)) {
            return null;
        }
        return appInfoRecords.get(0);
    }

    @Override
    public AppInfo findByAppAndATAndNamespace(String appName, String accessToken, String namespace) {
        return converter.deserialize(mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.APP_NAME.eq(appName)
                        .and(Tables.APP_INFO.STATUS.eq(AppStatus.ALIVE.name()))
                        .and(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken))
                        .and(Tables.APP_INFO.NAMESPACE.eq(namespace)))
                .limit(1)
                .fetchOneInto(AppInfoRecord.class));
    }

    @Override
    public int updateNodeIds(AppInfoRecord record) {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(record.getId());

        UpdateQuery<AppInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.APP_INFO);
        if (record.getNodeIds() != null) {
            updateQuery.addValue(Tables.APP_INFO.NODE_IDS, record.getNodeIds());
        }
        if (record.getStatus() != null) {
            updateQuery.addValue(Tables.APP_INFO.STATUS, record.getStatus());
        }
        updateQuery.addValue(Tables.APP_INFO.UPDATED_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.APP_INFO.ID.eq(record.getId()));
        return updateQuery.execute();
    }

    @Override
    public List<AppInfoRecord> appList(String accessToken, String appNameLike) {
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.APP_NAME.like(concat("%", appNameLike, "%")))
                .and(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken))
                .and(Tables.APP_INFO.STATUS.eq(AppStatus.ALIVE.name()))
                .orderBy(Tables.APP_INFO.APP_TAG.desc())
                .fetchInto(Tables.APP_INFO);
    }

    @Override
    public AppInfoRecord findByIdAndToken(String accessToken, Integer id) {
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ID.eq(id))
                .and(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken))
                .and(Tables.APP_INFO.STATUS.eq(AppStatus.ALIVE.name()))
                .orderBy(Tables.APP_INFO.APP_TAG.desc())
                .fetchOneInto(Tables.APP_INFO);
    }

    @Override
    public List<AppInfoRecord> getAppByTokenAndCluster(String accessToken, String cluster) {
        Condition condition = Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken).and(Tables.APP_INFO.STATUS.eq(AppStatus.ALIVE.name()));
        if (cluster != null) {
            condition = condition.and(Tables.APP_INFO.CLUSTER_NAME.eq(cluster));
        }
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(condition)
                .orderBy(Tables.APP_INFO.APP_TAG.desc())
                .fetchInto(AppInfoRecord.class);
    }

    @Override
    public AppInfo findById(Integer id) {
        return converter.deserialize(mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ID.eq(id))
                .limit(1)
                .fetchOneInto(AppInfoRecord.class));
    }

    @Override
    public void save(Integer id, AppTag appTag) {
        mDSLContext.update(Tables.APP_INFO)
                .set(Tables.APP_INFO.APP_TAG, GsonUtil.toJson(appTag))
                .where(Tables.APP_INFO.ID.eq(id))
                .execute();
    }

    @Override
    public void deleteAppById(Integer id) {
        mDSLContext.deleteFrom(Tables.APP_INFO)
                .where(Tables.APP_INFO.ID.eq(id))
                .execute();
    }

    @Override
    public List<AppInfoRecord> getAppListByStatus(AppStatus appStatus) {
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.STATUS.eq(appStatus.name()))
                .fetchInto(AppInfoRecord.class);
    }

    @Override
    public List<AppInfoRecord> getReportedApp() {
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.STATUS.eq(AppStatus.ALIVE.name())
                        .and(Tables.APP_INFO.APP_TAG.isNotNull()))
                .fetchInto(AppInfoRecord.class);
    }


    @Override
    public int updateAppDefaultJvm(AppInfoRecord record) {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(record.getId());
        Preconditions.checkNotNull(record.getAppDefaultJvm());

        UpdateQuery<AppInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.APP_INFO);
        updateQuery.addValue(Tables.APP_INFO.APP_DEFAULT_JVM, record.getAppDefaultJvm());
        updateQuery.addConditions(Tables.APP_INFO.ID.eq(record.getId()));
        return updateQuery.execute();
    }
}