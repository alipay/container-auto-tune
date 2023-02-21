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

import com.alipay.autotuneservice.dao.jooq.tables.records.RuleInfoRecord;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmType;

import java.util.List;

/**
 * @author huoyuqi
 * @version RuleInfoRepository.java, v 0.1 2023年01月05日 7:48 下午 huoyuqi
 */
public interface RuleInfoRepository {

    /**
     * 根据规则类型进行查询
     * @param alarmType
     * @return
     */
    List<RuleInfoRecord> selectByAlarmType(AlarmType alarmType);

    /**
     * 根据规则名称进行查询
     * @param ruleName
     * @return
     */
    RuleInfoRecord selectByRuleName(String ruleName);

    /**
     * 根据id 查询
     * @param id
     * @return
     */
    RuleInfoRecord selectById(Integer id);


}