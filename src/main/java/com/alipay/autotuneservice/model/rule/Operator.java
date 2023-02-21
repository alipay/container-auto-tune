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
package com.alipay.autotuneservice.model.rule;

import java.util.NoSuchElementException;

/**
 * @author dutianze
 * @version Operator.java, v 0.1 2022年02月23日 20:35 dutianze
 */
public enum Operator {

    LESS("<") {
        @Override
        public boolean apply(int left, int right) {
            return left < right;
        }
    },

    LESS_OR_EQUAL("<=") {
        @Override
        public boolean apply(int left, int right) {
            return left <= right;
        }
    },

    EQUAL("=") {
        @Override
        public boolean apply(int left, int right) {
            return left == right;
        }
    },

    GREATER(">") {
        @Override
        public boolean apply(int left, int right) {
            return left > right;
        }
    },

    GREATER_OR_EQUAL(">=") {
        @Override
        public boolean apply(int left, int right) {
            return left >= right;
        }
    };

    private final String operator;

    Operator(String operator) {
        this.operator = operator;
    }

    public static Operator parseOperator(String operator) {
        for (Operator op : values()) {
            if (op.operator.equals(operator)) {return op;}
        }
        throw new NoSuchElementException(String.format("Unknown operator [%s]", operator));
    }

    public abstract boolean apply(int left, int right);
}