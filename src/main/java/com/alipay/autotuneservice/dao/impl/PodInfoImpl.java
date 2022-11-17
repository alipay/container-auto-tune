/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.common.PodStatus;
import com.alipay.autotuneservice.util.DateUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author huoyuqi
 * @version PodInfoImpl.java, v 0.1 2022年04月18日 7:14 下午 huoyuqi
 */
@Service
@Slf4j
public class PodInfoImpl extends BaseDao implements PodInfo {

    @Override
    public void insertPodInfo(PodInfoRecord record) {
        mDSLContext.insertInto(Tables.POD_INFO)
                .set(Tables.POD_INFO.APP_ID, record.getAppId())
                .set(Tables.POD_INFO.NODE_ID, record.getNodeId())
                .set(Tables.POD_INFO.POD_NAME, record.getPodName())
                .set(Tables.POD_INFO.IP, record.getIp())
                .set(Tables.POD_INFO.STATUS, record.getStatus())
                .set(Tables.POD_INFO.POD_JVM, record.getPodJvm())
                .set(Tables.POD_INFO.ENV, record.getEnv())
                .set(Tables.POD_INFO.POD_DEPLOY_TYPE, record.getPodDeployType())
                .set(Tables.POD_INFO.POD_TEMPLATE, record.getPodTemplate())
                .set(Tables.POD_INFO.POD_TAGS, record.getPodTags())
                .set(Tables.POD_INFO.ACCESS_TOKEN, record.getAccessToken())
                .set(Tables.POD_INFO.CLUSTER_NAME, record.getClusterName())
                .set(Tables.POD_INFO.K8S_NAMESPACE, record.getK8sNamespace())
                .set(Tables.POD_INFO.CREATED_TIME, record.getCreatedTime())
                .set(Tables.POD_INFO.POD_STATUS, record.getPodStatus())
                .set(Tables.POD_INFO.D_HOSTNAME, record.getDHostname())
                .set(Tables.POD_INFO.NODE_IP, record.getNodeIp())
                .set(Tables.POD_INFO.NODE_NAME, record.getNodeName())
                .set(Tables.POD_INFO.SERVER_TYPE, record.getServerType())
                .set(Tables.POD_INFO.AGENT_INSTALL, record.getAgentInstall())
                .onDuplicateKeyUpdate()
                .set(Tables.POD_INFO.NODE_ID, record.getNodeId())
                .set(Tables.POD_INFO.POD_STATUS, record.getPodStatus())
                .set(Tables.POD_INFO.STATUS, record.getStatus())
                .set(Tables.POD_INFO.POD_JVM, record.getPodJvm())
                .set(Tables.POD_INFO.ENV, record.getEnv())
                .set(Tables.POD_INFO.D_HOSTNAME, record.getDHostname())
                .set(Tables.POD_INFO.NODE_IP, record.getNodeIp())
                .set(Tables.POD_INFO.NODE_NAME, record.getNodeName())
                .returning()
                .fetchOne();
    }

    @Override
    public List<PodInfoRecord> getByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.APP_ID.eq(appId))
                .and(Tables.POD_INFO.POD_STATUS.eq("ALIVE"))
                .orderBy(Tables.POD_INFO.CREATED_TIME.desc())
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public List<PodInfoRecord> getByAllPodByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.APP_ID.eq(appId))
                .orderBy(Tables.POD_INFO.CREATED_TIME.asc())
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public List<PodInfoRecord> getAllAlivePodsByType(String serverType) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.SERVER_TYPE.eq(serverType))
                .and(Tables.POD_INFO.POD_STATUS.eq("ALIVE"))
                .orderBy(Tables.POD_INFO.CREATED_TIME.asc())
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public List<PodInfoRecord> getAllAlivePodsByToken(String accessToken, String clusterName) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.POD_STATUS.eq(PodStatus.ALIVE.name()))
                .and(Tables.POD_INFO.ACCESS_TOKEN.eq(accessToken))
                .and(Tables.POD_INFO.CLUSTER_NAME.eq(clusterName))
                .orderBy(Tables.POD_INFO.AGENT_INSTALL.desc(), Tables.POD_INFO.APP_ID)
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public List<PodInfoRecord> findByAppIds(List<Integer> appIds) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.APP_ID.in(appIds).and(Tables.POD_INFO.POD_STATUS.eq(PodStatus.ALIVE.name())))
                .orderBy(Tables.POD_INFO.AGENT_INSTALL.desc(), Tables.POD_INFO.APP_ID)
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public PodInfoRecord getById(Integer id) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.ID.eq(id))
                .fetchOneInto(PodInfoRecord.class);
    }

    @Override
    public List<PodInfoRecord> getAllAlivePods() {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.POD_STATUS.eq(PodStatus.ALIVE.name()))
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public List<PodInfoRecord> getAllAlivePodsByApp(Integer appId) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.APP_ID.eq(appId))
                .and(Tables.POD_INFO.POD_STATUS.eq(PodStatus.ALIVE.name()))
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public void deletePod(Integer appId) {
        mDSLContext.deleteFrom(Tables.POD_INFO)
                .where(Tables.POD_INFO.APP_ID.eq(appId))
                .execute();
    }

    @Override
    public PodInfoRecord findById(Integer id) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.ID.eq(id))
                .fetchOneInto(PodInfoRecord.class);
    }

    @Override
    public String findOneRunningPodNameByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.APP_ID.eq(appId).and(Tables.POD_INFO.POD_STATUS.eq("ALIVE")))
                .fetchAny(Tables.POD_INFO.POD_NAME);
    }

    @Override
    public List<PodInfoRecord> findByAccessToken(String accessToken) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.ACCESS_TOKEN.eq(accessToken)
                        .and(Tables.POD_INFO.POD_STATUS.eq("ALIVE")))
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public int updatePodInstallTuneAgent(PodInfoRecord record) {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(record.getId());
        Preconditions.checkArgument(record.getAgentInstall() > 0, "AGENT_INSTALL filed must be more than 0");
        UpdateQuery<PodInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.POD_INFO);
        updateQuery.addValue(Tables.POD_INFO.AGENT_INSTALL, record.getAgentInstall());
        updateQuery.addValue(Tables.POD_INFO.UPDATED_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.POD_INFO.ID.eq(record.getId()));
        return updateQuery.execute();
    }

    @Override
    public List<PodInfoRecord> getPodInstallTuneAgentNumsByAppId(Integer appId) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.APP_ID.eq(appId))
                .and(Tables.POD_INFO.AGENT_INSTALL.ge(1))
                .and(Tables.POD_INFO.POD_STATUS.eq(PodStatus.ALIVE.name()))
                .orderBy(Tables.POD_INFO.CREATED_TIME.desc())
                .fetchInto(PodInfoRecord.class);
    }

    @Override
    public List<PodInfoRecord> batchGetPodInstallTuneAgentNumsByAppId(List<Integer> appIds) {
        Condition condition = Tables.POD_INFO.APP_ID.in(appIds)
                .and(Tables.POD_INFO.AGENT_INSTALL.ge(1))
                .and(Tables.POD_INFO.POD_STATUS.eq(PodStatus.ALIVE.name()));
        List<PodInfoRecord> records = mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(condition)
                .orderBy(Tables.POD_INFO.CREATED_TIME.desc())
                .fetchInto(PodInfoRecord.class);
        return records;

    }

    @Override
    public int update(PodInfoRecord record) {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(record.getId());
        UpdateQuery<PodInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.POD_INFO);
        String status = record.getPodStatus();
        String jvm = record.getPodJvm();
        Integer agentInstall = record.getAgentInstall();
        if (status != null) {
            updateQuery.addValue(Tables.POD_INFO.POD_STATUS, status);
            updateQuery.addValue(Tables.POD_INFO.STATUS, status);
        }
        if (StringUtils.isNotEmpty(jvm)) {
            updateQuery.addValue(Tables.POD_INFO.POD_JVM, jvm);
        }
        if (agentInstall != null && agentInstall > 0) {
            updateQuery.addValue(Tables.POD_INFO.AGENT_INSTALL, agentInstall);
        }
        updateQuery.addValue(Tables.POD_INFO.UPDATED_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.POD_INFO.ID.eq(record.getId()));
        return updateQuery.execute();
    }

    @Override
    public int updatePodInfoResourceFields(PodInfoRecord record) {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(record.getId());

        UpdateQuery<PodInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.POD_INFO);
        updateQuery.addValue(Tables.POD_INFO.UPDATED_TIME, DateUtils.now());
        if (record.getCpuCoreLimit() > 0) {
            updateQuery.addValue(Tables.POD_INFO.CPU_CORE_LIMIT, record.getCpuCoreLimit());
        }
        if (record.getMemLimit() > 0) {
            updateQuery.addValue(Tables.POD_INFO.MEM_LIMIT, record.getMemLimit());
        }
        updateQuery.addConditions(Tables.POD_INFO.ID.eq(record.getId()));
        return updateQuery.execute();
    }

    @Override
    public List<PodInfoRecord> getAllPods() {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.POD_STATUS.eq(PodStatus.ALIVE.name()))
                .fetch()
                .into(PodInfoRecord.class);
    }

    @Override
    public PodInfoRecord getByPodAndAT(String podName, String accessToken) {
        return mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.POD_NAME.eq(podName))
                .and(Tables.POD_INFO.ACCESS_TOKEN.eq(accessToken))
                .fetchOneInto(PodInfoRecord.class);
    }

    @Override
    public List<PodInfoRecord> getDHostNameAlivePods(String dHostName) {
        List<PodInfoRecord> records = mDSLContext.select()
                .from(Tables.POD_INFO)
                .where(Tables.POD_INFO.D_HOSTNAME.eq(dHostName))
                .and(Tables.POD_INFO.POD_STATUS.eq("ALIVE"))
                .fetchInto(PodInfoRecord.class);
        return CollectionUtils.isEmpty(records) ? Lists.newArrayList() : records;
    }
}