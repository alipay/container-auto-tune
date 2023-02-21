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
package com.alipay.autotuneservice.heap.util.exception;

public class JifaException extends RuntimeException {

    private ErrorCode code = ErrorCode.UNKNOWN_ERROR;

    public JifaException() {
        this(ErrorCode.UNKNOWN_ERROR);
    }

    public JifaException(String detail) {
        this(ErrorCode.UNKNOWN_ERROR, detail);
    }

    public JifaException(ErrorCode code) {
        this(code, code.name());
    }

    public JifaException(ErrorCode code, String detail) {
        super(detail);
        this.code = code;
    }

    public JifaException(Throwable cause) {
        super(cause);
    }

    public JifaException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.code = errorCode;
    }

    public ErrorCode getCode() {
        return code;
    }
}
