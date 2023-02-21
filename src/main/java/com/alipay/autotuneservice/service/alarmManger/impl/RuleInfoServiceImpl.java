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
package com.alipay.autotuneservice.service.alarmManger.impl;

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.RuleInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.RuleInfoRecord;
import com.alipay.autotuneservice.service.alarmManger.RuleInfoService;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmType;
import com.alipay.autotuneservice.service.alarmManger.model.RuleModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version RuleInfoServiceImpl.java, v 0.1 2023年01月05日 7:58 下午 huoyuqi
 */
@Service
public class RuleInfoServiceImpl extends BaseDao implements RuleInfoService {

    @Autowired
    private RuleInfoRepository ruleInfoRepository;

    @Override
    public List<RuleModel> selectByAlarmType(AlarmType alarmType) {
        List<RuleInfoRecord> ruleModels = ruleInfoRepository.selectByAlarmType(alarmType);
        if (CollectionUtils.isEmpty(ruleModels)) {
            return null;
        }
        return ruleModels.stream().map(this::construct2RuleModel).collect(Collectors.toList());
    }

    private RuleModel construct2RuleModel(RuleInfoRecord record) {
        if (null == record) {
            return null;
        }
        return new RuleModel(record.getId(), record.getRuleName(), record.getRuleFunction(), record.getRuleSymbol(), record.getRuleData(),
                record.getTimeInterval());
    }

}