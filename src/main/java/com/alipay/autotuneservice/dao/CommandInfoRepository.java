/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dao.jooq.tables.records.CommandInfoRecord;
import com.alipay.autotuneservice.model.common.CommandInfo;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.rule.RuleAction;

import java.util.List;
import java.util.Map;

/**
 * @author quchen
 * @version : CommandInfoRepository.java, v 0.1 2022年10月27日 16:04 quchen Exp $
 */
public interface CommandInfoRepository {

    List<CommandInfo> getByUnionCode(String unionCode, CommandStatus commandStatus);

    CommandInfoRecord getBySessionId(String sessionId);

    void sendCommand(String unionCode, RuleAction ruleAction, String sessionId);

    boolean uStatus(long id, CommandStatus commandStatus);

    boolean uResult(String sessionId, Map<String, String> resultObj, CommandStatus commandStatus);

    boolean uStatusBySessionId(String sessionId, CommandStatus commandStatus, Map<String, String> tagsMap);
}