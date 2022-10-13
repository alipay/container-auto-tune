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
package com.alipay.autotuneservice.model.common;

/**
 * @author dutianze
 * @version PodAttachStatus.java, v 0.1 2022年06月15日 20:28 dutianze
 */
public enum PodAttachStatus {

    /**
     * 未安装
     */
    NOT_INSTALLED,

    /**
     * 正在安装
     */
    INSTALLING,

    /**
     * 已安装
     */
    INSTALLED,

    /**
     * 安装失败
     */
    INSTALL_FAIL,
}