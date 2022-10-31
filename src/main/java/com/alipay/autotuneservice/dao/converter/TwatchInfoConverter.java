/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.converter;

import com.alipay.autotuneservice.dao.jooq.tables.records.TwatchInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;

/**
 * @author huangkaifei
 * @version : TwatchInfoConverter.java, v 0.1 2022年10月31日 7:48 PM huangkaifei Exp $
 */
public class TwatchInfoConverter implements EntityConverter<TwatchInfoRecord, TwatchInfoDo>{

    @Override
    public TwatchInfoDo serialize(TwatchInfoRecord entity) {
        if (entity == null){
            return null;
        }
        return TwatchInfoDo.builder()
                .containerId(entity.getContainerId())
                .containerName(entity.getContainerName())
                .podName(entity.getPodName())
                .nameSpace(entity.getNamespace())
                .nodeIp(entity.getNodeIp())
                .nodeName(entity.getNodeName())
                .gmtModified(entity.getGmtModified())
                .dtPeriod(entity.getDtPeriod())
                .agentName(entity.getAgentName())
                .build();
    }

    @Override
    public TwatchInfoRecord deserialize(TwatchInfoDo twatchInfo) {
        if (twatchInfo == null) {
            return null;
        }
        TwatchInfoRecord twatchInfoRecord = new TwatchInfoRecord();
        twatchInfoRecord.setContainerId(twatchInfo.getContainerId());
        twatchInfoRecord.setContainerName(twatchInfo.getContainerName());
        twatchInfoRecord.setPodName(twatchInfo.getPodName());
        twatchInfoRecord.setNamespace(twatchInfo.getNameSpace());
        twatchInfoRecord.setNodeIp(twatchInfo.getNodeIp());
        twatchInfoRecord.setNodeName(twatchInfo.getNodeName());
        twatchInfoRecord.setGmtModified(twatchInfo.getGmtModified());
        twatchInfoRecord.setDtPeriod(twatchInfo.getDtPeriod());
        twatchInfoRecord.setAgentName(twatchInfo.getAgentName());
        return twatchInfoRecord;
    }
}