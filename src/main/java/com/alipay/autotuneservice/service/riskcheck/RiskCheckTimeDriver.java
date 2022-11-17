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
package com.alipay.autotuneservice.service.riskcheck;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.base.cache.LocalCache;
import com.alipay.autotuneservice.configuration.EnvHandler;
import com.alipay.autotuneservice.dao.JvmTuningRiskCenterRepository;
import com.alipay.autotuneservice.dao.RiskCheckControlRepository;
import com.alipay.autotuneservice.dao.RiskCheckTaskRepository;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.RiskCheckControl;
import com.alipay.autotuneservice.dao.jooq.tables.pojos.RiskCheckTask;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskCheckEnum;
import com.alipay.autotuneservice.service.riskcheck.entity.RiskControlStatus;
import com.alipay.autotuneservice.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RiskCheckTimeDriver {

    private static final String           LOCK_TASK_LEY   = "RiskCheckTimeDriver_do_task";
    private static final String           LOCK_DELETE_LEY = "RiskCheckTimeDriver_delete_validate";
    private static final Integer          STORAGE_TIME    = 60;
    @Autowired
    private              RiskCheckHandler riskCheckHandler;

    @Autowired
    private RiskCheckTaskRepository riskCheckTaskRepository;

    @Autowired
    private RiskCheckControlRepository riskCheckControlRepository;

    @Autowired
    private JvmTuningRiskCenterRepository jvmTuningRiskCenterRepository;

    @Autowired
    private LocalCache<Object, Object> localCache;

    @Autowired
    private EnvHandler envHandler;

    @Scheduled(fixedRate = 60 * 1000)
    public void doTask() {
        if (envHandler.isDev()) {
            return;
        }
        executeTask();

        handleJobStatus();
    }

    @Scheduled(cron = "0 0 22 * * ?")
    public void deleteValidateData() {
        if (envHandler.isDev()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        riskCheckControlRepository.delete(now.minusDays(STORAGE_TIME));
        riskCheckTaskRepository.delete(now.minusDays(STORAGE_TIME));
        jvmTuningRiskCenterRepository.delete(now.minusDays(STORAGE_TIME));
    }

    /**
     * 查出48h内的 并且还在ready状态下的任务列表
     * 执行风险监测任务
     */
    public void executeTask() {
        LocalDateTime now = LocalDateTime.now();
        List<RiskCheckTask> riskCheckTaskList = riskCheckTaskRepository.find(now.minusHours(48), now);

        riskCheckTaskList.parallelStream().forEach(riskCheckTask -> {
            LogUtil.logRegister(riskCheckTask.getTaskTraceId());
            log.info(LogUtil.scureLogFormat("riskCheckTimeDriver execute"));
            riskCheckHandler.executeRiskCheck(riskCheckTask, (checkResult, riskCollector, status) -> {
                log.info(LogUtil.scureLogFormat("更新执行状态 %s,%s,%s", checkResult, JSON.toJSONString(riskCollector), status));
                riskCheckTaskRepository.updateByTaskID(riskCheckTask.getId(), status, checkResult, JSON.toJSONString(riskCollector));
                if (RiskCheckEnum.HIGH_RISK == checkResult) {
                    riskCheckTaskRepository.updateByJobId(riskCheckTask.getJobId());
                    riskCheckControlRepository.update(riskCheckTask.getJobId(), checkResult, RiskControlStatus.END,
                            JSON.toJSONString(riskCollector), LocalDateTime.now());
                }
            });
        });
    }

    /**
     * 查出48h内的 并且还在执行状态下的任务
     * 检查总任务下的子任务是否执行完成，然后更新总任务状态以及结果
     */
    public void handleJobStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<RiskCheckControl> riskCheckTaskList = riskCheckControlRepository.find(now.minusHours(48), now);
        riskCheckTaskList.parallelStream().forEach(riskCheckControl -> {
            try {
                List<RiskCheckTask> taskList = riskCheckTaskRepository.findByJobID(riskCheckControl.getId());
                List<Integer> taskIDs = JSON.parseObject(riskCheckControl.getTaskIds(), new TypeReference<List<Integer>>() {
                });
                if (CollectionUtils.isEmpty(taskList)) {
                    return;
                }

                List<RiskCheckEnum> lowRisk = taskList.stream().map(task ->
                        RiskCheckEnum.valueOf(task.getTaskResult())
                ).filter(RiskCheckEnum.LOW_RISK::equals).collect(Collectors.toList());
                if (lowRisk.size() >= Math.ceil(taskIDs.size() * 0.6)) {
                    riskCheckTaskRepository.updateByJobId(riskCheckControl.getId());
                    riskCheckControlRepository.update(riskCheckControl.getId(), RiskCheckEnum.LOW_RISK, RiskControlStatus.END,
                            taskList.get(0).getTaskRiskMsg(), LocalDateTime.now());
                    return;
                }

                List<RiskCheckEnum> unknow = taskList.stream().map(task ->
                        RiskCheckEnum.valueOf(task.getTaskResult())
                ).filter(RiskCheckEnum.UNKNOW::equals).collect(Collectors.toList());
                if (unknow.size() >= Math.ceil(taskIDs.size() * 0.5)) {
                    riskCheckTaskRepository.updateByJobId(riskCheckControl.getId());
                    riskCheckControlRepository.update(riskCheckControl.getId(), RiskCheckEnum.UNKNOW, RiskControlStatus.END,
                            taskList.get(0).getTaskRiskMsg(), LocalDateTime.now());
                    return;
                }

                if (taskList.size() == taskIDs.size()) {
                    riskCheckControlRepository.update(riskCheckControl.getId(), RiskCheckEnum.NORMAL, RiskControlStatus.END, null,
                            LocalDateTime.now());
                }
            } catch (Exception e) {
                log.error("handleJobStatus error", e);
            }
        });
    }
}
