/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.controller.model.AppInfoVO;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.converter.AppInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.AppStatus;
import com.alipay.autotuneservice.model.common.AppTag;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.GsonUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<AppInfoRecord> getByAccessToken(String accessToken, int pageNum, int pageSize) {
        return mDSLContext.select()
                .from(Tables.APP_INFO)
                .offset((pageNum - 1) * pageSize)
                .limit(pageSize)
                .fetch()
                .sortDesc(Tables.APP_INFO.CREATED_TIME)
                .into(AppInfoRecord.class);
    }

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
    public List<AppInfo> findByAccessTokenAndStatus(String accessToken, AppStatus status) {
        List<AppInfoRecord> records = mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken).and(Tables.APP_INFO.STATUS.eq(status.name())))
                .orderBy(Tables.APP_INFO.APP_TAG.desc())
                .fetchInto(AppInfoRecord.class);
        return records.stream().map(converter::deserialize).collect(Collectors.toList());
    }

    @Override
    public AppInfoVO getByClusterId(int clusterId) {
        return ConvertUtils.convert2MeterMetaRecord(mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ID.eq(clusterId))
                .fetch()
                .sortDesc(Tables.APP_INFO.CREATED_TIME)
                .into(AppInfoRecord.class).get(0));
    }

    @Override
    public List<AppInfoRecord> getByClusterName(String clusterName) {
        List<AppInfoRecord> into = mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.APP_NAME.eq(clusterName))
                .fetch()
                .sortDesc(Tables.APP_INFO.CREATED_TIME)
                .into(AppInfoRecord.class);
        return into;

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
        return mDSLContext.select()
                .fetchOneInto(AppInfoRecord.class);
    }

    @Override
    public int findAppInstallAgentNums(String appId) {
        return 0;
    }

    @Override
    public int insetAppInfo(AppInfoRecord record) {
        AppInfoRecord insertRecord = mDSLContext.newRecord(Tables.APP_INFO);
        insertRecord.setAppName(record.getAppName());
        insertRecord.setStatus(AppStatus.ALIVE.name());
        insertRecord.setCreatedTime(DateUtils.now());
        insertRecord.store();
        return insertRecord.getId();
    }

    @Override
    public void insertAppInfoRecord(AppInfoRecord record) {
        log.info("insertAppInfoRecord, record:{}", record);
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
                .execute();
    }

    @Override
    public AppInfoRecord getByAppAndAT(String appName, String accessToken) {
        List<AppInfoRecord> appInfoRecords = mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.APP_NAME.eq(appName)
                        .and(Tables.APP_INFO.STATUS.eq(AppStatus.ALIVE.name()))
                        .and(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken))
                )
                .fetchInto(AppInfoRecord.class);
        if (CollectionUtils.isEmpty(appInfoRecords)) {
            return null;
        }
        return appInfoRecords.get(0);
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
    public List<AppInfo> findAppByAccessTokenAndStatusAndTag(String accessToken, AppStatus appStatus, AppTag appTag) {
        List<AppInfoRecord> apps = mDSLContext.select()
                .from(Tables.APP_INFO)
                .where(Tables.APP_INFO.ACCESS_TOKEN.eq(accessToken)
                        .and(Tables.APP_INFO.STATUS.eq(appStatus.name()))
                )
                .fetchInto(AppInfoRecord.class);
        return apps.stream()
                .map(converter::deserialize)
                .filter(app -> app.getAppTag().matchAppTag(appTag))
                .collect(Collectors.toList());
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

    @Override
    public int updateAppTag(AppInfoRecord record) {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(record.getId());
        Preconditions.checkNotNull(record.getAppTag());

        UpdateQuery<AppInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.APP_INFO);
        updateQuery.addValue(Tables.APP_INFO.APP_TAG, record.getAppTag());
        updateQuery.addValue(Tables.APP_INFO.UPDATED_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.APP_INFO.ID.eq(record.getId()));
        return updateQuery.execute();
    }
}