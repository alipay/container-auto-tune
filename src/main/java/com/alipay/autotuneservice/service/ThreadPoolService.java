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
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.controller.model.ThreadPoolMonitorVO;

/**
 * @author huoyuqi
 * @version ThreadPoolService.java, v 0.1 2022年12月06日 12:26 下午 huoyuqi
 */
public interface ThreadPoolService {

    /**
     * threadPool 监控信息
     *
     * @param workLoadName
     * @param threadPoolName
     * @param start
     * @param end
     * @return
     */
    ThreadPoolMonitorVO monitorThreadPool(String workLoadName, String threadPoolName, Long start, Long end);

}