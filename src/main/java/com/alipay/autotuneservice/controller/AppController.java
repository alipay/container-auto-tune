/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.controller;

import com.alipay.autotuneservice.configuration.Cached;
import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.controller.model.AppVO;
import com.alipay.autotuneservice.controller.model.ClusterVO;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.util.UserUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author huoyuqi
 * @version AppController.java, v 0.1 2022年04月19日 7:01 下午 huoyuqi
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/app")
public class AppController {

    @Autowired
    private AppInfoService appInfoService;

    /**
     * 根据token 和应用相似名称查询
     *
     * @param appName
     * @return
     */
    @GetMapping
    public ServiceBaseResult<List<AppVO>> appList(@RequestParam(value = "appName", required = false) String appName) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                    log.info("appList enter");
                    String accessToken = UserUtil.getAccessToken();
                    log.info("accesstokrn is:{}", accessToken);
                    return appInfoService.appList(accessToken, appName);
                });
    }

    /**
     * 根据集群名称+region查询
     */
    @GetMapping("appListByCondition")
    @Cached(time = 30)
    public ServiceBaseResult<List<AppVO>> appListByCondition(@RequestParam(value = "clusterAndRegion") String clusterName) {
        return ServiceBaseResult.invoker()
                .paramCheck(() -> Preconditions.checkArgument(StringUtils.isNotEmpty(clusterName), "clusterName is empty"))
                .makeResult(() -> {
                    log.info("appListByCondition enter");
                    String accessToken = UserUtil.getAccessToken();
                    String cluster = clusterName.substring(0, clusterName.indexOf(" "));
                    return appInfoService.appListByClusterAndRegion(cluster, accessToken);
                });
    }

    /**
     * 根据集群名称+region查询
     */
    @GetMapping("/v2/appListByCondition")
    public ServiceBaseResult<Map<String, List<AppVO>>> appListByConditionV2(
            @RequestParam(value = "appName", required = false) String appName,
            @RequestParam(value = "clusterAndRegion", required = false) String clusterName) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                    String accessToken = UserUtil.getAccessToken();
                    String cluster = null;
                    if (StringUtils.isNotEmpty(clusterName)) {
                        cluster = clusterName.substring(0, clusterName.indexOf(" "));
                    }
                    return appInfoService.appListByClusterAndRegionAndApp(cluster, accessToken, appName);
                });
    }

    @GetMapping("getClusterList")
    public ServiceBaseResult<List<ClusterVO>> getClusterList() {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                    log.info("getClusterList enter");
                    String accessToken = UserUtil.getAccessToken();
                    return appInfoService.clusterList(accessToken);
                });
    }
}