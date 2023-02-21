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

import com.alipay.autotuneservice.dao.jooq.tables.records.AlarmRecord;

import java.util.List;

/**
 * @author huoyuqi
 * @version AlarmRepository.java, v 0.1 2022年12月29日 10:37 上午 huoyuqi
 */
public interface AlarmRepository {

    /**
     * 插入报警通知
     *
     * @param record
     */
    void insertAlarm(AlarmRecord record);

    /**
     * 更新报警通知
     *
     * @param record
     */
    void updateAlarm(AlarmRecord record);

    /**
     * 根据appId 返回报警规则
     * @param id
     * @return
     */
    List<AlarmRecord> getByAppId(Integer id);

    /**
     * 根据alarmId 删除记录
     * @param alarmId
     * @return
     */
    Boolean deleteByAlarmId(Integer alarmId);

    /**
     * 根据id查询相应结果
     * @param id
     * @return
     */
    AlarmRecord getByAlarmId(Integer id);
}