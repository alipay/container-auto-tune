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
/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TuningParamTaskDataDev implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       pipelineId;
    private Integer       appId;
    private String        appName;
    private String        pods;
    private String        optimizationType;
    private String        problemDescribe;
    private String        problemType;
    private String        direction;
    private Integer       trialNums;
    private String        trialParams;
    private Integer       maxIter;
    private Integer       trialTimeMin;
    private Integer       trialTimeMax;
    private LocalDateTime trialStartTime;
    private LocalDateTime trialStopTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String        taskStatus;
    private String        beforeParams;
    private LocalDateTime modifyTime;
    private String        comparePods;

    public TuningParamTaskDataDev() {}

    public TuningParamTaskDataDev(TuningParamTaskDataDev value) {
        this.pipelineId = value.pipelineId;
        this.appId = value.appId;
        this.appName = value.appName;
        this.pods = value.pods;
        this.optimizationType = value.optimizationType;
        this.problemDescribe = value.problemDescribe;
        this.problemType = value.problemType;
        this.direction = value.direction;
        this.trialNums = value.trialNums;
        this.trialParams = value.trialParams;
        this.maxIter = value.maxIter;
        this.trialTimeMin = value.trialTimeMin;
        this.trialTimeMax = value.trialTimeMax;
        this.trialStartTime = value.trialStartTime;
        this.trialStopTime = value.trialStopTime;
        this.startTime = value.startTime;
        this.endTime = value.endTime;
        this.taskStatus = value.taskStatus;
        this.beforeParams = value.beforeParams;
        this.modifyTime = value.modifyTime;
        this.comparePods = value.comparePods;
    }

    public TuningParamTaskDataDev(
        Integer       pipelineId,
        Integer       appId,
        String        appName,
        String        pods,
        String        optimizationType,
        String        problemDescribe,
        String        problemType,
        String        direction,
        Integer       trialNums,
        String        trialParams,
        Integer       maxIter,
        Integer       trialTimeMin,
        Integer       trialTimeMax,
        LocalDateTime trialStartTime,
        LocalDateTime trialStopTime,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String        taskStatus,
        String        beforeParams,
        LocalDateTime modifyTime,
        String        comparePods
    ) {
        this.pipelineId = pipelineId;
        this.appId = appId;
        this.appName = appName;
        this.pods = pods;
        this.optimizationType = optimizationType;
        this.problemDescribe = problemDescribe;
        this.problemType = problemType;
        this.direction = direction;
        this.trialNums = trialNums;
        this.trialParams = trialParams;
        this.maxIter = maxIter;
        this.trialTimeMin = trialTimeMin;
        this.trialTimeMax = trialTimeMax;
        this.trialStartTime = trialStartTime;
        this.trialStopTime = trialStopTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.taskStatus = taskStatus;
        this.beforeParams = beforeParams;
        this.modifyTime = modifyTime;
        this.comparePods = comparePods;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.PIPELINE_ID</code>.
     */
    public Integer getPipelineId() {
        return this.pipelineId;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.PIPELINE_ID</code>.
     */
    public TuningParamTaskDataDev setPipelineId(Integer pipelineId) {
        this.pipelineId = pipelineId;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.APP_ID</code>.
     */
    public Integer getAppId() {
        return this.appId;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.APP_ID</code>.
     */
    public TuningParamTaskDataDev setAppId(Integer appId) {
        this.appId = appId;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.APP_NAME</code>.
     */
    public String getAppName() {
        return this.appName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.APP_NAME</code>.
     */
    public TuningParamTaskDataDev setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.PODS</code>.
     */
    public String getPods() {
        return this.pods;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.PODS</code>.
     */
    public TuningParamTaskDataDev setPods(String pods) {
        this.pods = pods;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.OPTIMIZATION_TYPE</code>.
     */
    public String getOptimizationType() {
        return this.optimizationType;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.OPTIMIZATION_TYPE</code>.
     */
    public TuningParamTaskDataDev setOptimizationType(String optimizationType) {
        this.optimizationType = optimizationType;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.PROBLEM_DESCRIBE</code>.
     */
    public String getProblemDescribe() {
        return this.problemDescribe;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.PROBLEM_DESCRIBE</code>.
     */
    public TuningParamTaskDataDev setProblemDescribe(String problemDescribe) {
        this.problemDescribe = problemDescribe;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.PROBLEM_TYPE</code>.
     */
    public String getProblemType() {
        return this.problemType;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.PROBLEM_TYPE</code>.
     */
    public TuningParamTaskDataDev setProblemType(String problemType) {
        this.problemType = problemType;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.DIRECTION</code>.
     */
    public String getDirection() {
        return this.direction;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.DIRECTION</code>.
     */
    public TuningParamTaskDataDev setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_NUMS</code>.
     */
    public Integer getTrialNums() {
        return this.trialNums;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_NUMS</code>.
     */
    public TuningParamTaskDataDev setTrialNums(Integer trialNums) {
        this.trialNums = trialNums;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_PARAMS</code>.
     */
    public String getTrialParams() {
        return this.trialParams;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_PARAMS</code>.
     */
    public TuningParamTaskDataDev setTrialParams(String trialParams) {
        this.trialParams = trialParams;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.MAX_ITER</code>.
     */
    public Integer getMaxIter() {
        return this.maxIter;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.MAX_ITER</code>.
     */
    public TuningParamTaskDataDev setMaxIter(Integer maxIter) {
        this.maxIter = maxIter;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_TIME_MIN</code>.
     */
    public Integer getTrialTimeMin() {
        return this.trialTimeMin;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_TIME_MIN</code>.
     */
    public TuningParamTaskDataDev setTrialTimeMin(Integer trialTimeMin) {
        this.trialTimeMin = trialTimeMin;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_TIME_MAX</code>.
     */
    public Integer getTrialTimeMax() {
        return this.trialTimeMax;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_TIME_MAX</code>.
     */
    public TuningParamTaskDataDev setTrialTimeMax(Integer trialTimeMax) {
        this.trialTimeMax = trialTimeMax;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_START_TIME</code>.
     */
    public LocalDateTime getTrialStartTime() {
        return this.trialStartTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_START_TIME</code>.
     */
    public TuningParamTaskDataDev setTrialStartTime(LocalDateTime trialStartTime) {
        this.trialStartTime = trialStartTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_STOP_TIME</code>.
     */
    public LocalDateTime getTrialStopTime() {
        return this.trialStopTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TRIAL_STOP_TIME</code>.
     */
    public TuningParamTaskDataDev setTrialStopTime(LocalDateTime trialStopTime) {
        this.trialStopTime = trialStopTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.START_TIME</code>.
     */
    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.START_TIME</code>.
     */
    public TuningParamTaskDataDev setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.END_TIME</code>.
     */
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.END_TIME</code>.
     */
    public TuningParamTaskDataDev setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TASK_STATUS</code>.
     */
    public String getTaskStatus() {
        return this.taskStatus;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.TASK_STATUS</code>.
     */
    public TuningParamTaskDataDev setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.BEFORE_PARAMS</code>.
     */
    public String getBeforeParams() {
        return this.beforeParams;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.BEFORE_PARAMS</code>.
     */
    public TuningParamTaskDataDev setBeforeParams(String beforeParams) {
        this.beforeParams = beforeParams;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.MODIFY_TIME</code>.
     */
    public LocalDateTime getModifyTime() {
        return this.modifyTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.MODIFY_TIME</code>.
     */
    public TuningParamTaskDataDev setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.COMPARE_PODS</code>.
     */
    public String getComparePods() {
        return this.comparePods;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA_DEV.COMPARE_PODS</code>.
     */
    public TuningParamTaskDataDev setComparePods(String comparePods) {
        this.comparePods = comparePods;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TuningParamTaskDataDev (");

        sb.append(pipelineId);
        sb.append(", ").append(appId);
        sb.append(", ").append(appName);
        sb.append(", ").append(pods);
        sb.append(", ").append(optimizationType);
        sb.append(", ").append(problemDescribe);
        sb.append(", ").append(problemType);
        sb.append(", ").append(direction);
        sb.append(", ").append(trialNums);
        sb.append(", ").append(trialParams);
        sb.append(", ").append(maxIter);
        sb.append(", ").append(trialTimeMin);
        sb.append(", ").append(trialTimeMax);
        sb.append(", ").append(trialStartTime);
        sb.append(", ").append(trialStopTime);
        sb.append(", ").append(startTime);
        sb.append(", ").append(endTime);
        sb.append(", ").append(taskStatus);
        sb.append(", ").append(beforeParams);
        sb.append(", ").append(modifyTime);
        sb.append(", ").append(comparePods);

        sb.append(")");
        return sb.toString();
    }
}
