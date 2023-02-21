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

    void sendCommand(String unionCode, RuleAction ruleAction, String sessionId, Map<String, Object> context);

    void sendCommand(String taskName, String podName, String unionCode, RuleAction ruleAction, String sessionId,
                     Map<String, Object> context, String appName);

    boolean uStatus(long id, CommandStatus commandStatus);

    boolean uStatusAndResult(long id, CommandStatus commandStatus, Map<String, String> resultObj);

    boolean uResult(String sessionId, Map<String, String> resultObj, CommandStatus commandStatus);

    boolean uStatusBySessionId(String sessionId, CommandStatus commandStatus, Map<String, String> tagsMap);

    /**
     * 通过token查询相关内容
     *
     * @param token
     * @param ruleAction
     * @param commandStatus
     * @param podName
     * @param startTime
     * @param endTime
     * @return
     */
    List<CommandInfoRecord> getByTokenAndName(String token, RuleAction ruleAction, CommandStatus commandStatus, String podName,
                                              Long startTime, Long endTime, String appName);

    /**
     * 上传文件进行保存
     *
     * @param taskName
     * @param ruleAction
     * @return
     */
    Long save(String taskName, RuleAction ruleAction, String appName);

    CommandInfoRecord findById(Long id);

    /**
     * 通过id删除记录
     *
     * @param id
     * @return
     */
    Boolean deleteById(Long id);

}