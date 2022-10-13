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

import com.alipay.autotuneservice.dao.jooq.tables.records.NodeInfoRecord;
import com.alipay.autotuneservice.model.common.NodeModel;
import com.alipay.autotuneservice.model.common.NodeStatus;

import java.util.Collection;
import java.util.List;

/**
 * @author t-rex
 * @version NodeModel.java, v 0.1 2022年02月23日 3:34 下午 t-rex
 */
public interface NodeInfo {

    NodeInfoRecord getByNodeName(String nodeName);

    NodeInfoRecord getById(Integer id);

    List<NodeInfoRecord> findByStatus(NodeStatus status);

    List<NodeInfoRecord> getByIds(Collection<Integer> ids);

    NodeInfoRecord queryAliveK8sNodeByParam(String accessToken, String namespace, String podName);

    void insert(NodeModel nodeModel);

    int update(NodeInfoRecord record);

    int insertOrUpdateNode(NodeInfoRecord record);

    NodeInfoRecord getByNodeAndAT(String nodeName, String accessToken);
}