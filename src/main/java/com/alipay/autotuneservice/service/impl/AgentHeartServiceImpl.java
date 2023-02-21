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

import com.alipay.autotuneservice.agent.twatch.model.AgentActionRequest;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.model.agent.CallBackRequest;
import com.alipay.autotuneservice.service.AgentHeartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author quchen
 * @version AgentHeartService.java, v 0.1 2022年03月10日 17:20 dutianze
 */
@Service
public class AgentHeartServiceImpl implements AgentHeartService {

    @Override
    public Set<AgentActionRequest> askAction(String agentName) {
        return null;
    }

    @Override
    public boolean doCallBack(CallBackRequest request) {
        return false;
    }

    @Override
    public void boundUnion(List<TwatchInfoDo> infoDos) {

    }
}
