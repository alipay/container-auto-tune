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

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.CommandInfoRepository;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.CommandInfoRecord;
import com.alipay.autotuneservice.model.common.CommandInfo;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
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
    public void sendCommand(String unionCode, RuleAction ruleAction, String sessionId, Map<String, Object> context) {
        log.info("fixThreadPool, sendCommand unionCode={}, sessionId={}", unionCode, sessionId);
        CommandInfoRecord record = new CommandInfoRecord();
        record.setUnionCode(unionCode);
        record.setSessionid(sessionId);
        record.setStatus(CommandStatus.INIT.name());
        record.setRunleAction(ruleAction.name());
        record.setResultType(ruleAction.getResultType().name());
        record.setCreatedTime(LocalDateTime.now());
        record.setContext(JSONObject.toJSONString(context));
        InsertQuery<CommandInfoRecord> insertQuery = mDSLContext.insertQuery(Tables.COMMAND_INFO);
        insertQuery.addRecord(record);
        insertQuery.execute();
    }

    @Override
    public void sendCommand(String taskName, String podName, String unionCode, RuleAction ruleAction, String sessionId,
                            Map<String, Object> context, String appName) {
        CommandInfoRecord record = new CommandInfoRecord();
        record.setUnionCode(unionCode);
        record.setSessionid(sessionId);
        record.setStatus(CommandStatus.INIT.name());
        record.setAccessToken(UserUtil.getAccessToken());
        record.setRunleAction(ruleAction.name());
        record.setPodName(podName);
        record.setResultType(ruleAction.getResultType().name());
        record.setCreatedTime(LocalDateTime.now());
        record.setContext(JSONObject.toJSONString(context));
        record.setTaskName(taskName);
        record.setAppName(appName);
        InsertQuery<CommandInfoRecord> insertQuery = mDSLContext.insertQuery(Tables.COMMAND_INFO);
        insertQuery.addRecord(record);
        insertQuery.execute();
    }

    @Override
    public boolean uStatus(long id, CommandStatus commandStatus) {
        try {
            UpdateQuery<CommandInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.COMMAND_INFO);
            updateQuery.addValue(Tables.COMMAND_INFO.STATUS, commandStatus.name());
            updateQuery.addConditions(Tables.COMMAND_INFO.ID.eq(id));
            updateQuery.execute();
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public boolean uStatusAndResult(long id, CommandStatus commandStatus, Map<String, String> resultObj) {
        try {
            UpdateQuery<CommandInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.COMMAND_INFO);
            if (commandStatus != null) {
                updateQuery.addValue(Tables.COMMAND_INFO.STATUS, commandStatus.name());
            }
            updateQuery.addValue(Tables.COMMAND_INFO.RESULT, JSONObject.toJSONString(resultObj));
            updateQuery.addConditions(Tables.COMMAND_INFO.ID.eq(id));
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

    @Override
    public List<CommandInfoRecord> getByTokenAndName(String token, RuleAction ruleAction, CommandStatus commandStatus, String podName,
                                                     Long startTime, Long endTime, String appName) {
        Condition condition = Tables.COMMAND_INFO.ACCESS_TOKEN.eq(UserUtil.getAccessToken());
        condition.and(Tables.COMMAND_INFO.APP_NAME.eq(appName));
        if (ruleAction != null) {
            condition = condition.and(Tables.COMMAND_INFO.RUNLE_ACTION.eq(ruleAction.name()));
        }

        if (ruleAction == null) {
            condition = condition.and(Tables.COMMAND_INFO.RUNLE_ACTION
                    .in(RuleAction.GC_DUMP.name(), RuleAction.HEAP_DUMP.name(), RuleAction.THREAD_DUMP.name(),
                            RuleAction.JVM_PROFILER.name()));
        }
        if (commandStatus != null) {
            condition = condition.and(Tables.COMMAND_INFO.STATUS.eq(commandStatus.name()));
        }
        if (StringUtils.isNotEmpty(podName)) {
            condition = condition.and(Tables.COMMAND_INFO.POD_NAME.eq(podName));
        }
        if (startTime != null && endTime != null) {
            condition = condition.and(
                    Tables.COMMAND_INFO.CREATED_TIME.between(DateUtils.asLocalData(startTime), DateUtils.asLocalData(endTime)));
        }
        return mDSLContext.select()
                .from(Tables.COMMAND_INFO)
                .where(condition)
                .orderBy(Tables.COMMAND_INFO.CREATED_TIME.desc())
                .fetch()
                .into(CommandInfoRecord.class);
    }

    @Override
    public Long save(String taskName, RuleAction ruleAction, String appName) {

        return mDSLContext.insertInto(Tables.COMMAND_INFO)
                .set(Tables.COMMAND_INFO.ACCESS_TOKEN, UserUtil.getAccessToken())
                .set(Tables.COMMAND_INFO.UNION_CODE, "xxx")
                .set(Tables.COMMAND_INFO.SESSIONID, "xxx")
                .set(Tables.COMMAND_INFO.STATUS, CommandStatus.PENDING.name())
                .set(Tables.COMMAND_INFO.RUNLE_ACTION, ruleAction.name())
                .set(Tables.COMMAND_INFO.TASK_NAME, taskName)
                .set(Tables.COMMAND_INFO.RESULT_TYPE, ruleAction.getReportType().name())
                .set(Tables.COMMAND_INFO.CREATED_TIME, LocalDateTime.now())
                .set(Tables.COMMAND_INFO.APP_NAME, appName)
                .returning(Tables.COMMAND_INFO.ID)
                .fetchOne().getId();
    }

    @Override
    public CommandInfoRecord findById(Long id) {
        return mDSLContext.select()
                .from(Tables.COMMAND_INFO)
                .where(Tables.COMMAND_INFO.ID.eq(id))
                .and(Tables.COMMAND_INFO.ACCESS_TOKEN.eq(UserUtil.getAccessToken()))
                .limit(1)
                .fetchOneInto(CommandInfoRecord.class);
    }

    @Override
    public Boolean deleteById(Long id) {
        Condition condition = Tables.COMMAND_INFO.ID.eq(id)
                .and(Tables.COMMAND_INFO.ACCESS_TOKEN.eq(UserUtil.getAccessToken()));
        try {
            mDSLContext.deleteFrom(Tables.COMMAND_INFO).where(condition).execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}