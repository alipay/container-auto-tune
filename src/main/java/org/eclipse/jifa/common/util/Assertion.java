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
package org.eclipse.jifa.common.util;

import org.eclipse.jifa.common.ErrorCode;
import org.eclipse.jifa.common.JifaException;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Assertion {

    public static final Assertion ASSERT = new Assertion() {
    };

    protected Assertion() {
    }

    public Assertion isTrue(boolean expression, ErrorCode errorCode, Supplier<String> message) {
        if (!expression) {
            throwEx(errorCode, message.get());
        }
        return self();
    }

    public Assertion isTrue(boolean expression, ErrorCode errorCode, String message) {
        return isTrue(expression, errorCode, () -> message);
    }

    public Assertion isTrue(boolean expression, Supplier<String> message) {
        return isTrue(expression, ErrorCode.SANITY_CHECK, message);
    }

    public Assertion isTrue(boolean expression, String message) {
        return isTrue(expression, ErrorCode.SANITY_CHECK, message);
    }

    public Assertion isTrue(boolean expression, ErrorCode errorCode) {
        return isTrue(expression, errorCode, errorCode.name());
    }

    public Assertion isTrue(boolean expression) {
        return isTrue(expression, ErrorCode.SANITY_CHECK);
    }

    public Assertion equals(Object expected, Object actual, ErrorCode errorCode, String message) {
        return isTrue(Objects.equals(expected, actual), errorCode, message);
    }

    public Assertion equals(Object expected, Object actual, ErrorCode errorCode) {
        return equals(expected, actual, errorCode, errorCode.name());
    }

    public Assertion equals(Object expected, Object actual, String message) {
        return equals(expected, actual, ErrorCode.SANITY_CHECK, message);
    }

    public Assertion equals(Object expected, Object actual) {
        return equals(expected, actual, ErrorCode.SANITY_CHECK);
    }

    public Assertion notNull(Object object, ErrorCode errorCode, Supplier<String> message) {
        return isTrue(object != null, errorCode, message);
    }

    public Assertion notNull(Object object, Supplier<String> message) {
        return notNull(object, ErrorCode.SANITY_CHECK, message);
    }

    public Assertion notNull(Object object, String message) {
        return notNull(object, ErrorCode.SANITY_CHECK, () -> message);
    }

    public Assertion notNull(Object object) {
        return notNull(object, ErrorCode.SANITY_CHECK, ErrorCode.SANITY_CHECK::name);
    }

    private Assertion self() {
        return this;
    }

    protected void throwEx(ErrorCode errorCode, String message) {
        throw new JifaException(errorCode, message);
    }
}
