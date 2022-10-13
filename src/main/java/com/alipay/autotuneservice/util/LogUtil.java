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
package com.alipay.autotuneservice.util;

public class LogUtil {

    private static final ThreadLocal<String> RESOURCES        = new ThreadLocal<>();

    private static final String              SCURE_LOG_FORMAT = "{%s}%s";

    public static String scureLogFormat(String format, Object... args) {
        return scureLogFormatById(RESOURCES.get(), format, args);
    }

    public static String scureLogFormatById(String decisionId, String format, Object... args) {
        return String.format(SCURE_LOG_FORMAT, decisionId, String.format(format, args));
    }

    public static String getDecisionId() {
        return RESOURCES.get();
    }

    public static void logRegister(String decisionId) {
        RESOURCES.set(decisionId);
    }
}