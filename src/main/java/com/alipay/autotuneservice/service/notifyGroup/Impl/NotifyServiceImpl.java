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
package com.alipay.autotuneservice.service.notifyGroup.Impl;

import com.alipay.autotuneservice.dao.NotifyRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.NotifyRecord;
import com.alipay.autotuneservice.service.notifyGroup.NotifyService;
import com.alipay.autotuneservice.service.notifyGroup.model.NotifyStatus;
import com.alipay.autotuneservice.service.notifyGroup.model.NotifyVO;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.UserUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version NotifyServiceImpl.java, v 0.1 2022年12月28日 2:41 下午 huoyuqi
 */
@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private NotifyRepository repository;

    @Override
    public void insertNotify(String groupName, NotifyStatus status, String context) {
        repository.insertNotify(groupName, status, context);
    }

    @Override
    public void updateNotify(Integer id, String groupName, NotifyStatus status, String context) {
        repository.updateNotify(id, groupName, status, context);
    }

    @Override
    public NotifyRecord getById(Integer id) {
        NotifyRecord notifyRecord = repository.getById(id);
        if (null == notifyRecord) {
            return null;
        }
        convert2NotifyVO(notifyRecord);
        return repository.getById(id);
    }

    private NotifyVO convert2NotifyVO(NotifyRecord notifyRecord) {
        NotifyVO notifyVO = new NotifyVO();
        notifyVO.setNotifyId(notifyRecord.getId());
        notifyVO.setGroupName(notifyRecord.getGroupName());
        notifyVO.setStatus(NotifyStatus.valueOf(notifyRecord.getStatus()));
        notifyVO.setCreateBy(notifyRecord.getCreateBy());
        notifyVO.setCreateTime(DateUtils.asTimestamp(notifyRecord.getCreatedTime()));
        notifyVO.setContext(notifyRecord.getContext());
        return notifyVO;
    }

    @Override
    public List<NotifyVO> getByAccessToken() {
        List<NotifyRecord> records = repository.getByAccessToken(UserUtil.getAccessToken());
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.stream().map(this::convert2NotifyVO).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteById(Integer id) {
        return repository.deleteById(id);
    }
}