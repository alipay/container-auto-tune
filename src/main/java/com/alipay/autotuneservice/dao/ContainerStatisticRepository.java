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
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;

import java.util.List;

/**
 * @author huangkaifei
 * @version : ContainerStatisticRepository.java, v 0.1 2022年10月31日 9:08 PM huangkaifei Exp $
 */
public interface ContainerStatisticRepository {

    /**
     * insert ContainerStatistics record
     *
     * @param statistics ContainerStatistics record
     */
    void insert(ContainerStatistics statistics);

    /**
     * query by containerId and range of timestamp
     *
     * @param containerId
     * @param start
     * @param end
     * @return
     */
    List<ContainerStatistics> queryRange(String containerId, long start, long end);
}