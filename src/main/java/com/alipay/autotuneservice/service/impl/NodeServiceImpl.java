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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.dao.NodeInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.NodeInfoRecord;
import com.alipay.autotuneservice.model.common.NodeModel;
import com.alipay.autotuneservice.model.common.NodeStatus;
import com.alipay.autotuneservice.service.NodeService;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author dutianze
 * @version NodeServiceImpl.java, v 0.1 2022年03月10日 16:51 dutianze
 */
@Slf4j
@Service
public class NodeServiceImpl implements NodeService {

    @Autowired
    private NodeInfo nodeInfo;

    @Override
    public NodeModel getById(Integer id) {
        NodeInfoRecord nodeInfoRecord = nodeInfo.getById(id);
        return ConvertUtils.convert2NodeModel(nodeInfoRecord);
    }

    @Override
    public List<NodeModel> getByIds(Collection<Integer> ids) {
        List<NodeInfoRecord> nodeInfoRecords = nodeInfo.getByIds(ids);
        return ConvertUtils.convert2NodeModels(nodeInfoRecords);
    }

    @Override
    public List<NodeModel> getAllAliveNodes() {
        log.info("getAllAliveNodes");
        List<NodeInfoRecord> nodeInfoRecords = nodeInfo.findByStatus(NodeStatus.ALIVE);
        return ConvertUtils.convert2NodeModels(nodeInfoRecords);
    }

    @Override
    public void updateNodeStatue(Integer id, NodeStatus status) {
        log.info("updateNodeStatue, id:{}, status:{}", id, status);
        NodeModel nodeModel = new NodeModel();
        nodeModel.setId(id);
        nodeModel.setNodeStatus(status);
        NodeInfoRecord record = ConvertUtils.convert2NodeInfoRecord(nodeModel);
        nodeInfo.update(record);
    }

    @Override
    public int insertOrUpdateNode(String nodeName, String ip, NodeStatus status, String nodeTag,
                                  String accessToken) {
        NodeInfoRecord record = new NodeInfoRecord();
        record.setNodeName(nodeName);
        record.setIp(ip);
        record.setStatus(String.valueOf(status));
        record.setNodeTags(nodeTag);
        record.setCreatedTime(DateUtils.now());
        record.setAccessToken(accessToken);
        return nodeInfo.insertOrUpdateNode(record);
    }

    @Override
    public int getByNodeAndAT(String nodeName, String accessToken) {
        NodeInfoRecord nodeInfoRecord = nodeInfo.getByNodeAndAT(nodeName, accessToken);
        if (nodeInfoRecord == null) {
            return -1;
        }
        return nodeInfoRecord.getId();
    }
}