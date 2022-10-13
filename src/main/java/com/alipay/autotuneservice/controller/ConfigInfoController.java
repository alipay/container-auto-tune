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
package com.alipay.autotuneservice.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.controller.model.configVO.ConfigInfoVO;
import com.alipay.autotuneservice.dao.ConfigInfoRepository;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.ConfigInfoService;
import com.alipay.autotuneservice.service.riskcheck.RiskCheckHandler;
import com.alipay.autotuneservice.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/jvm/config")
public class ConfigInfoController {

    @Autowired
    private ConfigInfoRepository configInfoRepository;

    @Autowired
    private ConfigInfoService    configInfoService;

    @Autowired
    private RiskCheckHandler     riskCheckHandler;

    /**
     * 存在 app配置就更新，否则新增
     */
    @RequestMapping(value = "/operateConfig", method = RequestMethod.POST)
    @ResponseBody
    public ServiceBaseResult<Integer> operateConfig(@RequestBody ConfigInfoVO configInfoVO) {
        log.info("operateConfig {}", JSON.toJSONString(configInfoVO));
        try {
            configInfoVO.setOperateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date()));
            Integer id = configInfoRepository.update(configInfoVO);
            return ServiceBaseResult.successResult(id);
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    /**
     * 根据appid查询 应用配置信息
     */
    @RequestMapping(value = "/findConfig", method = RequestMethod.GET)
    @ResponseBody
    public ServiceBaseResult<ConfigInfoVO> findConfig(@RequestParam("appid") Integer appid) {
        log.info("findConfig {}", appid);
        try {
            ConfigInfoVO configInfoVO = configInfoService.findAPPConfigByAPPID(appid);
            return ServiceBaseResult.successResult(configInfoVO);
        } catch (Exception e) {
            return ServiceBaseResult.failureResult(e.getMessage());
        }
    }

    @NoLogin
    @GetMapping("/receive")
    public ServiceBaseResult<Boolean> receive() {
        LogUtil.logRegister("ffffffffffffffffffffffffff");
        riskCheckHandler.executeRiskCheck(null, null);
        return ServiceBaseResult.successResult(true);
    }
}
