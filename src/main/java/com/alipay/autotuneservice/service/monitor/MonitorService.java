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
package com.alipay.autotuneservice.service.monitor;

import com.alipay.autotuneservice.controller.model.monitor.AppBasicInfoVO;
import com.alipay.autotuneservice.controller.model.monitor.AppIndicatorVO;
import com.alipay.autotuneservice.controller.model.monitor.PodIndicatorVO;

/**
 * @author huoyuqi
 * @version MonitorService.java, v 0.1 2022年10月17日 5:42 下午 huoyuqi
 */
public interface MonitorService {

    /**
     * 获取Pod监控指标
     *
     * @param podId
     * @param podName
     * @param type
     * @param start
     * @param end
     * @return
     */
    PodIndicatorVO getPodIndicators(Integer podId, String podName, String type, Long start, Long end);

    /**
     * 获取应用下监控指标
     *
     * @param appId
     * @param type
     * @param start
     * @param end
     * @return
     */
    AppIndicatorVO getAppIndicators(Integer appId, String type, Long start, Long end);

    /**
     * 获取应用的基本信息
     *
     * @param appId
     * @return
     */
    AppBasicInfoVO getAppBasicInfo(Integer appId);

}