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
package com.alipay.autotuneservice.model.agent;

import lombok.Data;

import java.io.Serializable;

/**
 * @author quchen
 * @version : ThreadPoolRequest.java, v 0.1 2022年12月06日 19:10 quchen Exp $
 */
@Data
public class ThreadPoolRequest implements Serializable {

    private String hostName;
    private String appName;
    private String threadPoolName;
    /**
     * 核心线程数
     */
    private int    corePoolSize;
    /**
     * 存活时间,单位:TimeUnit.MILLISECONDS
     */
    private long   keepAliveTime;
    /**
     * 最大线程数
     */
    private int    maximumPoolSize;
    /**
     * 队列排空
     */
    private String clearQ;
}