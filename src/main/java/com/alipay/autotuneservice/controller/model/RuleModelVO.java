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
package com.alipay.autotuneservice.controller.model;

import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.model.rule.RuleParam;
import com.alipay.autotuneservice.model.rule.RuleStatus;
import com.alipay.autotuneservice.model.rule.RuleType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dutianze
 * @version RuleModelVO.java, v 0.1 2022年03月07日 10:46 dutianze
 */
@Data
public class RuleModelVO {

    private Integer           id;
    private String            createdBy;
    private LocalDateTime     createdTime;
    private String            updatedBy;
    private LocalDateTime     updatedTime;
    private RuleStatus        ruleStatus;
    private String            ruleName;
    private RuleType          ruleType;
    private RuleParam         ruleParam;
    private RuleAction        ruleAction;
    private List<Integer>     targetClusterIds;
    private List<Integer>     targetNodeIds;
    private List<ClusterNode> clusterNodes;

    @Data
    public static class ClusterNode {
        private Integer           id;
        private String            name;
        private List<ClusterNode> nodes = new ArrayList<>();

        public void addNode(ClusterNode node) {
            nodes.add(node);
        }
    }
}