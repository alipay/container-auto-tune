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

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.controller.model.PodProcessInfo;
import com.alipay.autotuneservice.controller.model.PodVO;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.NodeInfo;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.NodeInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.PageResult;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.common.PodAttach;
import com.alipay.autotuneservice.model.common.PodAttachStatus;
import com.alipay.autotuneservice.service.PodAttachService;
import com.alipay.autotuneservice.service.PodService;
import com.alipay.autotuneservice.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version PodController.java, v 0.1 2022年04月21日 17:57 dutianze
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/pod")
public class PodController {

    @Autowired
    private PodInfo           podInfo;
    @Autowired
    private AppInfoRepository appInfoRepository;
    @Autowired
    private NodeInfo          nodeInfo;
    @Autowired
    private PodAttachService  podAttachService;
    @Autowired
    private PodService        podService;

    @PostMapping("/agent/attach")
    public ServiceBaseResult<Boolean> attachJavaAgent(@RequestParam(value = "podId") Integer podId,
                                                      @RequestParam("processId") Integer processId) {
        log.info("attachJavaAgent, podId:{}, processId:{}", podId, processId);
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> {
                    podAttachService.attachAgent(podId, processId);
                    return true;
                });
    }

    @NoLogin
    @GetMapping("/agent/attach-hook")
    public ServiceBaseResult<Boolean> attachJavaAgentHook(@RequestParam(value = "id") Integer id) {
        log.info("attachJavaAgentHook, id:{}", id);
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> {
                    podAttachService.updateStatus(id, PodAttachStatus.INSTALLED);
                    return true;
                });
    }

    @GetMapping("/podList")
    public ServiceBaseResult<PageResult<PodVO>> getPodList(@RequestParam(value = "appId") Integer appId,
                                                           @RequestParam(value = "podName", required = false) String podName,
                                                           @RequestParam(value = "install", defaultValue = "false") Boolean isInstall) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                            PageResult<PodVO> defaultResult = PageResult.of(0, 0, 0, 0, 0,
                                    0, 0, new ArrayList<>());
                            //过滤出Java应用
                            AppInfo appInfo = appInfoRepository.findById(appId);
                            if (appInfo == null || appInfo.getAppTag() == null || !appInfo.isJava()) {
                                return defaultResult;
                            }
                            List<Integer> appIds = new ArrayList<>();
                            appIds.add(appId);
                            //获取存活的pod & filter
                            List<PodInfoRecord> records = podInfo.findByAppIds(appIds).stream()
                                    .filter(p -> {
                                        if (StringUtils.isNotBlank(podName)) {
                                            return p.getPodName().contains(podName);
                                        }
                                        return true;
                                    }).collect(Collectors.toList());
                            if (CollectionUtils.isEmpty(records)) {
                                return defaultResult;
                            }
                            // 过滤出pod对应的nodeId 并构建nodeMap<nodeId,NodeName>
                            List<Integer> nodeIds = records.stream().map(PodInfoRecord::getNodeId).collect(Collectors.toList());
                            Map<Integer, String> nodeMap = nodeInfo.getByIds(nodeIds).stream()
                                    .collect(Collectors.toMap(NodeInfoRecord::getId, NodeInfoRecord::getNodeName));
                            // map of ( podId -> PodAttach )
                            List<Integer> podIds = records.stream().map(PodInfoRecord::getId).collect(Collectors.toList());
                            List<PodAttach> podAttaches = podAttachService.findByPodIds(podIds);
                            Map<Integer, PodAttach> podIdMapAttach = podAttaches.stream()
                                    .collect(Collectors.toMap(PodAttach::getPodId, Function.identity(), (e, n) -> e));
                            //构建List<PodVO>
                            List<PodVO> vos = records.stream()
                                    .map(p -> new PodVO(p, nodeMap.get(p.getNodeId()), podIdMapAttach.get(p.getId())))
                                    .collect(Collectors.toList());
                            if (isInstall) {
                                vos = vos.stream().filter(PodVO::hasAgent).collect(Collectors.toList());
                            }
                            long count = vos.size();
                            long agentTotal = vos.stream().filter(PodVO::hasAgent).count();
                            return PageResult.of(0, 0, 0, count, agentTotal, 0, 0, vos);
                        }
                );
    }

    @GetMapping("/{podId}/process")
    public ServiceBaseResult<List<PodProcessInfo>> getPodProcesses(@PathVariable(value = "podId") Integer podId) {
        log.info("getPodProcesses enter, podId:{}", podId);
        return ServiceBaseResult
                .invoker()
                .paramCheck(() -> {
                    ObjectUtil.checkIntegerPositive(podId);
                })
                .makeResult(() -> podService.getPodProcessInfos(podId));
    }
}