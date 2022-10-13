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

import com.alipay.autotuneservice.dao.PodAttachRepository;
import com.alipay.autotuneservice.dao.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.common.PodAttach;
import com.alipay.autotuneservice.model.common.PodAttachStatus;
import com.alipay.autotuneservice.model.exception.ResourceNotFoundException;
import com.alipay.autotuneservice.model.exception.ServerException;
import com.alipay.autotuneservice.service.AgentInvokeService;
import com.alipay.autotuneservice.service.PodAttachService;
import com.alipay.autotuneservice.service.impl.AgentInvokeServiceImpl.InvokeType;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * @author dutianze
 * @version PodAttachServiceImpl.java, v 0.1 2022年06月17日 14:15 dutianze
 */
@Slf4j
@Service
public class PodAttachServiceImpl implements PodAttachService {

    @Autowired
    private PodAttachRepository podAttachRepository;
    @Autowired
    private PodInfo             podInfo;
    @Autowired
    private AgentInvokeService  agentInvokeService;
    @Autowired
    private Executor            eventExecutor;

    @Override
    public void attachAgent(Integer podId, Integer processId) {
        log.info("attachAgent, podId:{}, processId:{}", podId, processId);
        // check
        PodInfoRecord podInfoRecord = podInfo.findById(podId);
        Preconditions.checkNotNull(podInfoRecord, new ResourceNotFoundException(ResultCode.POD_NOT_FOUND));
        PodAttach podAttachCheck = podAttachRepository.findByPodId(podId);
        if (podAttachCheck != null && podAttachCheck.cantInstall()) {
            throw new ServerException(ResultCode.DUPLICATE_ATTACH);
        }
        // save
        PodAttach podAttach = PodAttach.builder()
                .withAccessToken(podInfoRecord.getAccessToken())
                .withPodId(podInfoRecord.getId())
                .withStatus(PodAttachStatus.INSTALLING)
                .build();
        Optional.ofNullable(podAttachCheck).ifPresent(e -> podAttach.setId(e.getId()));
        PodAttach savedPodAttach = podAttachRepository.save(podAttach);
        eventExecutor.execute(() -> {
            String cmd = savedPodAttach.attachDownloadCmd();
            log.info("start to download installTuneAgent.sh, cmd={}", cmd);
            agentInvokeService.execCmd(InvokeType.SYNC, podInfoRecord.getPodName(), cmd);
            log.info("start to exec installTuneAgent.sh");
            agentInvokeService.execCmd(InvokeType.SYNC, podInfoRecord.getPodName(), savedPodAttach.attachInstallCmd(processId));
        });
    }

    @Override
    public void updateStatus(Integer id, PodAttachStatus status) {
        PodAttach podAttach = podAttachRepository.findById(id);
        Preconditions.checkNotNull(podAttach, new ResourceNotFoundException(
            ResultCode.POD_ATTACH_NOT_FOUND));
        podAttach.setStatus(status);
        podAttachRepository.save(podAttach);
    }

    @Override
    public PodAttach findByPodId(Integer podId) {
        return podAttachRepository.findByPodId(podId);
    }

    @Override
    public List<PodAttach> findByPodIds(List<Integer> podIds) {
        return podAttachRepository.findByPodIds(podIds);
    }
}