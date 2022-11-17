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

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.controller.model.K8sAccessTokenModel;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.CloudType;
import com.alipay.autotuneservice.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangkaifei
 * @version : K8sAccessTokenController.java, v 0.1 2022年03月21日 下午5:38 huangkaifei Exp $
 */
@Slf4j
@RestController
@RequestMapping("/api/accessToken")
@NoLogin
public class K8sAccessController {

    @Autowired
    private AppInfoRepository appInfoRepository;
    @Autowired
    private Environment       environment;

    @RequestMapping(value = "/saveAccessToken", method = RequestMethod.POST)
    @ResponseBody
    public ServiceBaseResult<Boolean> saveK8sAccessToken(@RequestBody K8sAccessTokenModel request) {
        if (request == null) {
            String errorMsg = "saveAccessTokenInfo - request is null, please check it.";
            log.error(errorMsg);
            return ServiceBaseResult.failureResult(errorMsg);
        }
        log.info(
                "saveAccessTokenInfo - request clusterId={}, clusterName={}, region={}, endpoint={}",
                request.getClusterId(), request.getClusterName(), request.getRegion(),
                request.getEndpoint());
        return ServiceBaseResult.successResult(Boolean.TRUE);
    }

    /**
     * wrap保存到DB的K8sAccessTokenModel
     * <p>
     * 这里要处理clusterName出于aws和aliyun创建k8s client使用的条件不一样， 同时是以最小改动满足现有的逻辑
     * <p>
     * aws使用clusterName
     * aliyun使用clusterId
     *
     * @param k8sModel
     * @return
     */
    public K8sAccessTokenModel wrap(K8sAccessTokenModel k8sModel) {
        CloudType cloudType = SystemUtil.getCloudTypeFromEnv(environment);
        String clusterIdentity = "";
        switch (SystemUtil.getCloudTypeFromEnv(environment)) {
            case AWS:
                clusterIdentity = k8sModel.getClusterName();
                break;
            case ALIYUN:
                clusterIdentity = k8sModel.getClusterId();
                break;
            default:
                throw new UnsupportedOperationException(String.format(
                        "CloudType=%s is not supported.", JSON.toJSONString(cloudType)));
        }
        k8sModel.setClusterName(clusterIdentity);
        return k8sModel;
    }

    @RequestMapping(value = "/getAccessTokenInfo", method = RequestMethod.GET)
    @ResponseBody
    public ServiceBaseResult<K8sAccessTokenModel> getAccessTokenInfo(
            @RequestParam(value = "accessToken", required = true) String accessToken,
            @RequestParam(value = "clusterName", required = true) String clusterName) {
        log.info("getAccessTokenInfo enter.");
        if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(clusterName)) {
            log.warn("input accessToken or clusterName empty.");
            return ServiceBaseResult.successResult(null);
        }
        return ServiceBaseResult.successResult(new K8sAccessTokenModel());
    }

    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    @ResponseBody
    public ServiceBaseResult<Boolean> validateClusterAccessToken(@RequestParam(value = "accessToken", required = true) String accessToken,
                                                                 @RequestParam(value = "clusterName", required = true) String clusterName) {
        log.info("validateClusterAccessToken enter. clusterName={}", clusterName);
        if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(clusterName)) {
            log.warn("input accessToken or clusterName empty.");
            return ServiceBaseResult.successResult(Boolean.FALSE);
        }
        boolean res = CollectionUtils.isNotEmpty(appInfoRepository.getAppByTokenAndCluster(
                accessToken, clusterName));
        log.info("validateClusterAccessToken for custerName={} res={}", clusterName, res);
        return ServiceBaseResult.successResult(res);
    }
}