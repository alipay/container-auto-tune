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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.NodeInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.NodeInfoRecord;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient.Builder;
import com.alipay.autotuneservice.model.common.AppModel;
import com.alipay.autotuneservice.model.common.NodeModel;
import com.alipay.autotuneservice.model.common.NodeStatus;
import com.alipay.autotuneservice.service.AppInfoService;
import com.alipay.autotuneservice.service.MetaAnalyzeService;
import com.alipay.autotuneservice.service.UserInfoService;
import com.alipay.autotuneservice.util.DateUtils;
import com.auto.tune.client.SystemCommonGrpc;
import com.google.common.cache.Cache;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenqu
 * @version : MetaAnalyzeServiceImpl.java, v 0.1 2022年03月21日 16:32 chenqu Exp $
 */
@Service
@Slf4j
public class MetaAnalyzeServiceImpl implements MetaAnalyzeService {

    @Autowired
    private UserInfoService       userInfoService;
    @Autowired
    private K8sAccessTokenInfo    k8sAccessTokenInfo;
    @Autowired
    private NodeInfo              nodeInfo;
    @Autowired
    private AppInfoService        appInfoService;
    @Autowired
    private Cache<String, Object> lCache;

    @Override
    public void analyze(SystemCommonGrpc systemCommonGrpc) {
        String accessToken = systemCommonGrpc.getAccessToken();
        //判断accessToken是否存在
        if (!userInfoService.checkTokenValidity(accessToken)) {
            log.info(String.format("this access token=[%s] not found!", accessToken));
            return;
        }
        //获取k8s token
        if (!k8sAccessTokenInfo.checkToken(accessToken)) {
            log.info(String.format("can not found access = [%s] token to k8s table,please check!", accessToken));
            return;
        }
        String nameSpace = systemCommonGrpc.getNamespace();
        String podName = systemCommonGrpc.getHostname();
        //判断 namespace + accessToken 是否存在
        NodeInfoRecord nodeInfoRecord = nodeInfo.queryAliveK8sNodeByParam(accessToken, nameSpace, podName);
        if (nodeInfoRecord != null) {
            return;
        }
        //获取clusterName集群
        List<String> clusters = k8sAccessTokenInfo.getClustersByToken(accessToken);
        if (CollectionUtils.isEmpty(clusters)) {
            return;
        }
        for (String cluster : clusters) {//根据token、clusterName获取namespace集合
            // 和目标匹配namespace
            K8sClient eksClient = null;
            try {
                eksClient = this.createEksClient(accessToken, cluster);
                NamespaceList namespaceList = eksClient.listNameSpace();
                Set<String> namespaceSet = namespaceList.getItems().stream()
                        .map(Namespace::getMetadata)
                        .map(ObjectMeta::getName)
                        .collect(Collectors.toSet());
                boolean contains = namespaceSet.contains(nameSpace);
                if (contains) {
                    // insetOrUpdate
                    int id = this.createOrGetAppInfoId(accessToken, nameSpace, podName);
                    // 记录到node里
                    NodeModel nodeModel = new NodeModel();
                    nodeModel.setAppId(id);
                    nodeModel.setPodName(podName);
                    nodeModel.setIp(systemCommonGrpc.getPodIp());
                    nodeModel.setNodeStatus(NodeStatus.ALIVE);
                    nodeModel.setCreatedTime(DateUtils.now());
                    nodeModel.setAccessToken(accessToken);
                    nodeModel.setK8sNamespace(nameSpace);
                    nodeModel.setClusterName(cluster);
                    nodeInfo.insert(nodeModel);
                    return;
                }
            } catch (Exception e) {
                log.error("analyze createEksClient occur an error e", e);
            } finally {
                if (eksClient != null) {
                    eksClient.close();
                }
            }

        }
    }

    private int createOrGetAppInfoId(String accessToken, String nameSpace, String hostname) {
        // hostname -> appName | test-api-9fbd5bd7c-7lqmc -> tets-api
        String appName = hostname.substring(0, StringUtils.lastOrdinalIndexOf(hostname, "-", 2));
        AppModel appModel = appInfoService.findAppModel(accessToken, nameSpace, appName);
        if (appModel != null) {
            return appModel.getId();
        }
        return appInfoService.insertCoreParam(accessToken, nameSpace, appName);
    }

    @Override
    public K8sClient createEksClient(String accessToken, String clusterName) {
        try {
            K8sAccessTokenInfoRecord k8AccessTokenRecord = k8sAccessTokenInfo
                .selectByTokenAndCusterName(accessToken, clusterName);
            try {
                //判断是阿里云还是本地集群
                if (k8AccessTokenRecord != null
                    && !StringUtils
                        .equals(k8AccessTokenRecord.getAccessToken(), "IOeiob2AI9n_test")) {
                    log.info(
                        "AccessKeyId is: {}, SecretAccessKey isEmpty: {}, Cer isEmpty: {}, EndPoint is: {}, ClusterName is: {}",
                        k8AccessTokenRecord.getAccessKeyId(),
                        StringUtils.isEmpty(k8AccessTokenRecord.getSecretAccessKey()),
                        StringUtils.isEmpty(k8AccessTokenRecord.getCer()),
                        k8AccessTokenRecord.getEndpoint(), k8AccessTokenRecord.getClusterName());
                    return K8sClient.Builder.builder()
                        .withAccessKey(k8AccessTokenRecord.getAccessKeyId())
                        .withSecretKey(k8AccessTokenRecord.getSecretAccessKey())
                        .withCaStr(k8AccessTokenRecord.getCer())
                        .withEndpoint(k8AccessTokenRecord.getEndpoint())
                        .withClusterName(k8AccessTokenRecord.getClusterName()).build();
                }
                return Builder.builder().withKubeConfigPath("/.kube/config").build();
            } catch (Exception e) {
                log.error("createEksClient is error", e);
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("createEksClient is error", e);
        }
    }

    @Override
    public void refreshCache(String accessToken, String clusterName) {
        try {
            String cacheKey = String.format("%s_%s", accessToken, clusterName);
            lCache.invalidate(cacheKey);
        } catch (Exception e) {
            log.error("refreshCache is error", e);
        }
    }
}