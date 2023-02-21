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

/**
 * @author dutianze
 * @version RuleType.java, v 0.1 2022年02月17日 14:34 dutianze
 */
public enum RuleType {

    /**
     * manual trigger by user click
     */
    MANUAL_TRIGGER,

    /**
     * analyze log file, which upload by user
     */
    MANUAL_UPLOAD_LOG,

    /**
     * auto trigger by timing
     * 自动 - 定时触发
     */
    AUTO_TIMING,

    /**
     * auto trigger by threshold
     */
    AUTO_THRESHOLD,
    ;
}