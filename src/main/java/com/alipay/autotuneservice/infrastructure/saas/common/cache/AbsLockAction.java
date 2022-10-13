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
package com.alipay.autotuneservice.infrastructure.saas.common.cache;

/**
 * redis分布式锁抽象事件
 * @author yiqi
 * @date 2022/07/07
 */
public interface AbsLockAction {
    /**
     * 加锁
     * @param resourceName 锁key
     */
    default void onAcquire(String resourceName) {
    }

    /**
     * 加锁完成之后干的事
     * @param resourceName
     */
    void doInLock(String resourceName);

    /**
     * 释放锁
     * @param resourceName 锁key
     */
    default void onExit(String resourceName) {
    }

    /**
     * 加锁失败之后的事
     * @param resourceName 锁key
     */
    default void tryLockFail(String resourceName) {

    }
}
