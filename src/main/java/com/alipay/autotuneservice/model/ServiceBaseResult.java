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
package com.alipay.autotuneservice.model;

import com.alipay.autotuneservice.util.TraceIdGenerator;
import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author dutianze
 * @version ServiceBaseResult.java, v 0.1 2022年01月10日 15:19 dutianze
 */
@Data
public class ServiceBaseResult<T> implements Serializable {

    private static final String HOST_NAME;

    static {
        String hostName;
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            hostName = localhost.getHostName();
        } catch (UnknownHostException var4) {
            hostName = "localhost";
        }

        HOST_NAME = hostName;
    }

    private Boolean             success;
    private String              status;
    private Integer             resultCode;
    private String              resultMessage;
    private T                   data;
    private String              server;
    private String              traceId;

    public ServiceBaseResult() {
        this.server = HOST_NAME;
    }

    private ServiceBaseResult(Boolean success, String status, Integer resultCode,
                              String resultMessage, T data) {
        this.success = success;
        this.status = status;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.data = data;
        this.server = HOST_NAME;
        this.traceId = MDC.get(TraceIdGenerator.TRACE_ID);
    }

    public static <T> ServiceBaseResult<T> build(Boolean success, String status,
                                                 Integer resultCode, String resultMessage, T data) {
        return new ServiceBaseResult<>(success, status, resultCode, resultMessage, data);
    }

    public static <T> ServiceBaseResult<T> successResult() {
        return build(true, "OK", 200, "", null);
    }

    public static <T> ServiceBaseResult<T> failureResult(String msg) {
        return build(false, "FAILED", 400, msg, null);
    }

    public static <T> ServiceBaseResult<T> failureResult(int resultCode, String msg) {
        return build(false, "FAILED", resultCode, msg, null);
    }

    public static <T> ServiceBaseResult<T> failureResult(int resultCode, String msg, T data) {
        return build(false, "FAILED", resultCode, msg, data);
    }

    public static <T> ServiceBaseResult<T> successResult(T data) {
        return build(true, "OK", 200, "", data);
    }

    public static <T> ServiceBaseResult<T> successResult(T data, String msg) {
        return build(true, "OK", 200, msg, data);
    }

    public static <T> ServiceBaseResult<T> failureResult(ResultCode resultCode) {
        return build(false, "FAILED", resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static ResultInvoker invoker() {
        return new ResultInvoker();
    }
}