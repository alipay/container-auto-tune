/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.CommandInfoRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.CommandInfoRecord;
import com.alipay.autotuneservice.model.common.CommandInfo;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.InsertQuery;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author t-rex
 * @version ClusterInfoImpl.java, v 0.1 2022年02月17日 8:24 下午 t-rex
 */
@Service
@Slf4j
public class CommandInfoRepositoryImpl extends BaseDao implements CommandInfoRepository {

    @Override
    public List<CommandInfo> getByUnionCode(String unionCode, CommandStatus commandStatus) {
        List<CommandInfoRecord> records = mDSLContext.select()
                .from(Tables.COMMAND_INFO)
                .where(Tables.COMMAND_INFO.UNION_CODE.eq(unionCode))
                .and(Tables.COMMAND_INFO.STATUS.eq(commandStatus.name()))
                .fetch()
                .into(CommandInfoRecord.class);
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return records.stream().map(record -> {
            CommandInfo commandInfo = new CommandInfo();
            commandInfo.setUnionCode(record.getUnionCode());
            commandInfo.setSessionId(record.getSessionid());
            commandInfo.setRuleAction(record.getRunleAction());
            commandInfo.setContext(record.getContext());
            commandInfo.setId(record.getId());
            return commandInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public CommandInfoRecord getBySessionId(String sessionId) {
        return mDSLContext.select()
                .from(Tables.COMMAND_INFO)
                .where(Tables.COMMAND_INFO.SESSIONID.eq(sessionId))
                .limit(1)
                .fetchOneInto(CommandInfoRecord.class);
    }

    @Override
    public void sendCommand(String unionCode, RuleAction ruleAction, String sessionId) {
        CommandInfoRecord record = new CommandInfoRecord();
        record.setUnionCode(unionCode);
        record.setSessionid(sessionId);
        record.setStatus(CommandStatus.INIT.name());
        record.setRunleAction(ruleAction.name());
        record.setResultType(ruleAction.getResultType().name());
        record.setCreatedTime(LocalDateTime.now());
        InsertQuery<CommandInfoRecord> insertQuery = mDSLContext.insertQuery(Tables.COMMAND_INFO);
        insertQuery.addRecord(record);
        insertQuery.execute();
    }

    @Override
    public boolean uStatus(long id, CommandStatus commandStatus) {
        try {
            UpdateQuery<CommandInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.COMMAND_INFO);
            updateQuery.addValue(Tables.COMMAND_INFO.STATUS, commandStatus.name());
            updateQuery.addConditions(Tables.COMMAND_INFO.ID.eq((int) id));
            updateQuery.execute();
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public boolean uResult(String sessionId, Map<String, String> resultObj, CommandStatus commandStatus) {
        try {
            UpdateQuery<CommandInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.COMMAND_INFO);
            if (commandStatus != null) {
                updateQuery.addValue(Tables.COMMAND_INFO.STATUS, commandStatus.name());
            }
            updateQuery.addValue(Tables.COMMAND_INFO.RESULT, JSONObject.toJSONString(resultObj));
            updateQuery.addConditions(Tables.COMMAND_INFO.SESSIONID.eq(sessionId));
            updateQuery.execute();
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public boolean uStatusBySessionId(String sessionId, CommandStatus commandStatus, Map<String, String> tagsMap) {
        try {
            UpdateQuery<CommandInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.COMMAND_INFO);
            updateQuery.addValue(Tables.COMMAND_INFO.STATUS, commandStatus.name());
            updateQuery.addValue(Tables.COMMAND_INFO.CONTEXT, JSONObject.toJSONString(tagsMap));
            updateQuery.addConditions(Tables.COMMAND_INFO.SESSIONID.eq(sessionId));
            updateQuery.execute();
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }
}