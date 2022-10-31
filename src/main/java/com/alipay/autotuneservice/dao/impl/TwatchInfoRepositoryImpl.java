/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.TwatchInfoRepository;
import com.alipay.autotuneservice.dao.converter.TwatchInfoConverter;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.TwatchInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : TwatchInfoRepositoryImpl.java, v 0.1 2022年10月31日 5:20 PM huangkaifei Exp $
 */
@Service
public class TwatchInfoRepositoryImpl extends BaseDao implements TwatchInfoRepository {

    private final TwatchInfoConverter converter = new TwatchInfoConverter();

    @Override
    public void insert(TwatchInfoDo twatchInfoDo) {
        mDSLContext.insertInto(Tables.TWATCH_INFO)
                .set(Tables.TWATCH_INFO.CONTAINER_ID, twatchInfoDo.getContainerId())
                .set(Tables.TWATCH_INFO.CONTAINER_NAME, twatchInfoDo.getContainerName())
                .set(Tables.TWATCH_INFO.POD_NAME, twatchInfoDo.getPodName())
                .set(Tables.TWATCH_INFO.AGENT_NAME, twatchInfoDo.getAgentName())
                .set(Tables.TWATCH_INFO.NODE_NAME, twatchInfoDo.getNodeName())
                .set(Tables.TWATCH_INFO.NODE_IP, twatchInfoDo.getNodeIp())
                .set(Tables.TWATCH_INFO.NAMESPACE, twatchInfoDo.getNameSpace())
                .set(Tables.TWATCH_INFO.GMT_MODIFIED, twatchInfoDo.getGmtModified())
                .set(Tables.TWATCH_INFO.DT_PERIOD, twatchInfoDo.getDtPeriod())
                .execute();
    }

    @Override
    public List<TwatchInfoDo> findByContainerId(String containerId) {
        List<TwatchInfoRecord> result = mDSLContext.select()
                .from(Tables.TWATCH_INFO)
                .where(Tables.TWATCH_INFO.CONTAINER_ID.eq(containerId))
                .fetchInto(TwatchInfoRecord.class);
        return Optional.of(result).orElse(Lists.newArrayList()).stream()
                .map(converter::serialize).collect(Collectors.toList());
    }

    @Override
    public List<TwatchInfoDo> findInfoByPod(String podName) {
        List<TwatchInfoRecord> result = mDSLContext.select()
                .from(Tables.TWATCH_INFO)
                .where(Tables.TWATCH_INFO.POD_NAME.eq(podName))
                .fetchInto(TwatchInfoRecord.class);
        return Optional.of(result).orElse(Lists.newArrayList()).stream()
                .map(converter::serialize).collect(Collectors.toList());
    }

    @Override
    public List<TwatchInfoDo> findInfoByAgent(String agentName) {
        List<TwatchInfoRecord> result = mDSLContext.select()
                .from(Tables.TWATCH_INFO)
                .where(Tables.TWATCH_INFO.AGENT_NAME.eq(agentName))
                .fetchInto(TwatchInfoRecord.class);
        return Optional.of(result).orElse(Lists.newArrayList()).stream()
                .map(converter::serialize).collect(Collectors.toList());
    }
}