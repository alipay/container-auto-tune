/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.util;

/**
 * @author chenqu
 * @version : AgentConstant.java, v 0.1 2022年04月13日 20:46 chenqu Exp $
 */
public class AgentConstant {

    public static final String CALL_BACK_KEY = "doCallBack";
    public static final String TWATCH_TABLE  = "TwatchInfo";
    public static final String STATUS        = "STATUS";
    public static final String RESULT        = "RESULT";

    public static final String AGENT_HEART_KEY = "agentHeartKey_%s";

    public static String generateCallBackKey(String sessionId) {
        return String.format("%s_%s", CALL_BACK_KEY, sessionId);
    }

    public static String generateQueueKey(String agentName) {
        return String.format("queue_%s", agentName);
    }
}