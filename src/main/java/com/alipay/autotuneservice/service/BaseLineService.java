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

import com.alipay.autotuneservice.controller.model.baseLine.BaseLineVO;
import com.alipay.autotuneservice.controller.model.baseLine.HistoryBaseLineVO;
import com.alipay.autotuneservice.controller.model.baseLine.JvmDateVO;
import com.alipay.autotuneservice.controller.model.baseLine.PodLineVO;
import com.alipay.autotuneservice.controller.model.tuneparam.AppTuneParamsVO;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamItem;

import java.util.List;

/**
 * @author huoyuqi
 * @version BaseLineService.java, v 0.1 2022年08月09日 10:56 上午 huoyuqi
 */
public interface BaseLineService {

    /**
     * 获取应用上一次Jvm更改时间列表
     */
    List<JvmDateVO> getJvmDate(Integer appId);

    /**
     * 获取应用jvm
     */
    BaseLineVO getJvm(Integer appId);

    /**
     * 获取应用的历史jvm
     * @param appId
     * @return
     */
    List<HistoryBaseLineVO> getHistoryJvm(Integer appId);

    /**
     * 与历史版本对比
     * @param appId
     * @param currentMarketId
     * @param historyMarketId
     * @param version
     * @return
     */
    AppTuneParamsVO getCompare(Integer appId, Integer currentMarketId, Integer historyMarketId,
                               String version);

    /**
     * 获取podJvm
     * @param appId
     * @return
     */
    List<PodLineVO> getPodLine(Integer appId);

}