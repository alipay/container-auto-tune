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
package com.alipay.autotuneservice.agent.twatch.hearbeat;

import com.alipay.autotuneservice.agent.twatch.model.AgentActionRequest;
import lombok.Data;

import java.util.List;

@Data
public class HeartBeatResponse {
    /**
     * agent与TMaestro交流的session id
     */
    @Deprecated
    private String                   sessionId;
    /**
     * 是否接收到心跳
     */
    private Boolean                  success = false;
    /**
     * 目标类型: POD, CONTAINER
     */
    private String                   targetType;
    /**
     * 目标ID. 如果是POD则为POD_ID, 如果为容器则为CONTAINER_ID
     */
    @Deprecated
    private String                   targetId;
    /**
     * 下发给agent的actions, 可以是一个,也可以是多个
     */
    private List<AgentActionRequest> actionList;
    /**
     * action是否完成
     */
    private Boolean                  actionFinished;
    /**
     * 处理action的agent
     */
    private String                   agentName;
}
