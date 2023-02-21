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

import com.alipay.autotuneservice.dao.BaseDao;
import com.alipay.autotuneservice.dao.NodeInfo;
import com.alipay.autotuneservice.dao.jooq.Tables;
import com.alipay.autotuneservice.dao.jooq.tables.records.NodeInfoRecord;
import com.alipay.autotuneservice.model.common.NodeModel;
import com.alipay.autotuneservice.model.common.NodeStatus;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.alipay.autotuneservice.util.DateUtils;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.jooq.InsertQuery;
import org.jooq.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author t-rex
 * @version NodeInfoImpl.java, v 0.1 2022年02月23日 3:37 下午 t-rex
 */
@Service
@Slf4j
public class NodeInfoImpl extends BaseDao implements NodeInfo {

    @Override
    public NodeInfoRecord getByNodeName(String nodeName) {
        NodeInfoRecord collect = mDSLContext.select().from(Tables.NODE_INFO).fetch()
            .sortDesc(Tables.NODE_INFO.CREATED_TIME).into(NodeInfoRecord.class).get(0);
        return collect;
    }

    @Override
    public NodeInfoRecord getById(Integer id) {
        return mDSLContext.select().from(Tables.NODE_INFO).where(Tables.NODE_INFO.ID.eq(id))
            .fetchOneInto(NodeInfoRecord.class);
    }

    @Override
    public List<NodeInfoRecord> findByStatus(NodeStatus status) {
        return mDSLContext.select().from(Tables.NODE_INFO)
            .where(Tables.NODE_INFO.STATUS.eq(status.name())).fetchInto(NodeInfoRecord.class);
    }

    @Override
    public List<NodeInfoRecord> getByIds(Collection<Integer> ids) {
        return mDSLContext.select().from(Tables.NODE_INFO).where(Tables.NODE_INFO.ID.in(ids))
            .fetchInto(NodeInfoRecord.class);
    }

    @Override
    public NodeInfoRecord queryAliveK8sNodeByParam(String accessToken, String namespace,
                                                   String podName) {
        return mDSLContext.select().from(Tables.NODE_INFO)
            .where(Tables.NODE_INFO.ACCESS_TOKEN.eq(accessToken))
            .and(Tables.NODE_INFO.STATUS.eq(NodeStatus.ALIVE.name()))
            .fetchOneInto(NodeInfoRecord.class);
    }

    @Override
    public void insert(NodeModel nodeModel) {
        NodeInfoRecord record = ConvertUtils.convert2NodeInfoRecord(nodeModel);
        InsertQuery<NodeInfoRecord> insertQuery = mDSLContext.insertQuery(Tables.NODE_INFO);
        insertQuery.addRecord(record);
        insertQuery.execute();
    }

    @Override
    public int update(NodeInfoRecord record) {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(record.getId());

        UpdateQuery<NodeInfoRecord> updateQuery = mDSLContext.updateQuery(Tables.NODE_INFO);
        if (record.getStatus() != null) {
            updateQuery.addValue(Tables.NODE_INFO.STATUS, record.getStatus());
        }
        if (record.getNodeTags() != null) {
            updateQuery.addValue(Tables.NODE_INFO.NODE_TAGS, record.getNodeTags());
        }
        updateQuery.addValue(Tables.NODE_INFO.UPDATED_TIME, DateUtils.now());
        updateQuery.addConditions(Tables.NODE_INFO.ID.eq(record.getId()));
        return updateQuery.execute();
    }

    @Override
    public int insertOrUpdateNode(NodeInfoRecord record) {
        return mDSLContext.insertInto(Tables.NODE_INFO)
            .set(Tables.NODE_INFO.NODE_NAME, record.getNodeName())
            .set(Tables.NODE_INFO.IP, record.getIp())
            .set(Tables.NODE_INFO.CREATED_TIME, record.getCreatedTime())
            .set(Tables.NODE_INFO.NODE_TAGS, record.getNodeTags())
            .set(Tables.NODE_INFO.STATUS, record.getStatus())
            .set(Tables.NODE_INFO.ACCESS_TOKEN, record.getAccessToken()).onDuplicateKeyUpdate()
            .set(Tables.NODE_INFO.STATUS, record.getStatus()).returning().fetchOne().getId();
    }

    @Override
    public NodeInfoRecord getByNodeAndAT(String nodeName, String accessToken) {
        return mDSLContext.select().from(Tables.NODE_INFO)
            .where(Tables.NODE_INFO.NODE_NAME.eq(nodeName))
            .and(Tables.NODE_INFO.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(NodeInfoRecord.class);
    }
}