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

import com.alipay.autotuneservice.dao.jooq.tables.records.NotifyRecord;
import com.alipay.autotuneservice.service.notifyGroup.model.NotifyStatus;

import java.util.List;

/**
 * @author huoyuqi
 * @version NotifyRepository.java, v 0.1 2022年12月28日 2:28 下午 huoyuqi
 */
public interface NotifyRepository {

    /**
     * 插入
     * @param groupName
     * @param status
     * @param detail
     */
    void insertNotify(String groupName, NotifyStatus status, String context);

    /**
     * 更新
     * @param id
     * @param groupName
     * @param status
     * @param context
     */
    void updateNotify(Integer id, String groupName, NotifyStatus status, String context);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    NotifyRecord getById(Integer id);

    /**
     * 根据ids查询
     * @param ids
     * @return
     */
    List<NotifyRecord> getByIds(List<Integer> ids);

    /**
     * 根据token查询
     * @param token
     * @return
     */
    List<NotifyRecord> getByAccessToken(String  token);

    /**
     * 根据id 进行删除
     * @param id
     * @return
     */
    Boolean deleteById(Integer id);
}