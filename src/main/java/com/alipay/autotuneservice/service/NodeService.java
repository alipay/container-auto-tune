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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.model.common.NodeModel;
import com.alipay.autotuneservice.model.common.NodeStatus;

import java.util.Collection;
import java.util.List;

/**
 * @author dutianze
 * @version NodeService.java, v 0.1 2022年03月10日 16:50 dutianze
 */
public interface NodeService {

    NodeModel getById(Integer id);

    List<NodeModel> getByIds(Collection<Integer> ids);

    List<NodeModel> getAllAliveNodes();

    void updateNodeStatue(Integer id, NodeStatus status);

    int insertOrUpdateNode(String nodeName, String ip, NodeStatus status, String nodeTag,
                           String accessToken);

    int getByNodeAndAT(String nodeName, String accessToken);
}