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
package com.alipay.autotuneservice.agent.twatch.model;

import com.alipay.autotuneservice.util.TraceIdGenerator;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.Serializable;
import java.util.Objects;

@Data
public class AgentActionRequest implements Serializable {
    private String              agentName;
    private String              sessionId;
    private ActionMethodRequest actionMethodRequest;
    private String              traceId;

    public AgentActionRequest() {
        this.traceId = StringUtils.defaultIfEmpty(MDC.get(TraceIdGenerator.TRACE_ID), "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AgentActionRequest that = (AgentActionRequest) o;
        return Objects.equals(agentName, that.agentName)
               && Objects.equals(sessionId, that.sessionId)
               && Objects.equals(actionMethodRequest, that.actionMethodRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentName, sessionId, actionMethodRequest);
    }

    public boolean checkEmpty() {
        if (StringUtils.isEmpty(sessionId)) {
            return Boolean.TRUE;
        }
        try {
            actionMethodRequest.checkArgument();
        } catch (Exception e) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public String toString() {
        return "AgentActionRequest(agentName=" + this.getAgentName() + ", sessionId="
               + this.getSessionId() + ", actionMethodRequest=" + this.getActionMethodRequest()
               + ", traceId=" + this.getTraceId() + ")";
    }
}
