/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables.records;


import com.alipay.autotuneservice.dao.jooq.tables.TuningParamTaskData;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record21;
import org.jooq.Row21;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TuningParamTaskDataRecord extends UpdatableRecordImpl<TuningParamTaskDataRecord> implements Record21<Integer, Integer, String, String, String, String, String, String, Integer, String, Integer, Integer, Integer, LocalDateTime, LocalDateTime, LocalDateTime, LocalDateTime, String, String, LocalDateTime, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.PIPELINE_ID</code>.
     */
    public TuningParamTaskDataRecord setPipelineId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.PIPELINE_ID</code>.
     */
    public Integer getPipelineId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.APP_ID</code>.
     */
    public TuningParamTaskDataRecord setAppId(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.APP_ID</code>.
     */
    public Integer getAppId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.APP_NAME</code>.
     */
    public TuningParamTaskDataRecord setAppName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.APP_NAME</code>.
     */
    public String getAppName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.PODS</code>.
     */
    public TuningParamTaskDataRecord setPods(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.PODS</code>.
     */
    public String getPods() {
        return (String) get(3);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.OPTIMIZATION_TYPE</code>.
     */
    public TuningParamTaskDataRecord setOptimizationType(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.OPTIMIZATION_TYPE</code>.
     */
    public String getOptimizationType() {
        return (String) get(4);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.PROBLEM_DESCRIBE</code>.
     */
    public TuningParamTaskDataRecord setProblemDescribe(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.PROBLEM_DESCRIBE</code>.
     */
    public String getProblemDescribe() {
        return (String) get(5);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.PROBLEM_TYPE</code>.
     */
    public TuningParamTaskDataRecord setProblemType(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.PROBLEM_TYPE</code>.
     */
    public String getProblemType() {
        return (String) get(6);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.DIRECTION</code>.
     */
    public TuningParamTaskDataRecord setDirection(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.DIRECTION</code>.
     */
    public String getDirection() {
        return (String) get(7);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_NUMS</code>.
     */
    public TuningParamTaskDataRecord setTrialNums(Integer value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_NUMS</code>.
     */
    public Integer getTrialNums() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_PARAMS</code>.
     */
    public TuningParamTaskDataRecord setTrialParams(String value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_PARAMS</code>.
     */
    public String getTrialParams() {
        return (String) get(9);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.MAX_ITER</code>.
     */
    public TuningParamTaskDataRecord setMaxIter(Integer value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.MAX_ITER</code>.
     */
    public Integer getMaxIter() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_TIME_MIN</code>.
     */
    public TuningParamTaskDataRecord setTrialTimeMin(Integer value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_TIME_MIN</code>.
     */
    public Integer getTrialTimeMin() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_TIME_MAX</code>.
     */
    public TuningParamTaskDataRecord setTrialTimeMax(Integer value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_TIME_MAX</code>.
     */
    public Integer getTrialTimeMax() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_START_TIME</code>.
     */
    public TuningParamTaskDataRecord setTrialStartTime(LocalDateTime value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_START_TIME</code>.
     */
    public LocalDateTime getTrialStartTime() {
        return (LocalDateTime) get(13);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_STOP_TIME</code>.
     */
    public TuningParamTaskDataRecord setTrialStopTime(LocalDateTime value) {
        set(14, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TRIAL_STOP_TIME</code>.
     */
    public LocalDateTime getTrialStopTime() {
        return (LocalDateTime) get(14);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.START_TIME</code>.
     */
    public TuningParamTaskDataRecord setStartTime(LocalDateTime value) {
        set(15, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.START_TIME</code>.
     */
    public LocalDateTime getStartTime() {
        return (LocalDateTime) get(15);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.END_TIME</code>.
     */
    public TuningParamTaskDataRecord setEndTime(LocalDateTime value) {
        set(16, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.END_TIME</code>.
     */
    public LocalDateTime getEndTime() {
        return (LocalDateTime) get(16);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TASK_STATUS</code>.
     */
    public TuningParamTaskDataRecord setTaskStatus(String value) {
        set(17, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.TASK_STATUS</code>.
     */
    public String getTaskStatus() {
        return (String) get(17);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.BEFORE_PARAMS</code>.
     */
    public TuningParamTaskDataRecord setBeforeParams(String value) {
        set(18, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.BEFORE_PARAMS</code>.
     */
    public String getBeforeParams() {
        return (String) get(18);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.MODIFY_TIME</code>.
     */
    public TuningParamTaskDataRecord setModifyTime(LocalDateTime value) {
        set(19, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.MODIFY_TIME</code>.
     */
    public LocalDateTime getModifyTime() {
        return (LocalDateTime) get(19);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.COMPARE_PODS</code>.
     */
    public TuningParamTaskDataRecord setComparePods(String value) {
        set(20, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.TUNING_PARAM_TASK_DATA.COMPARE_PODS</code>.
     */
    public String getComparePods() {
        return (String) get(20);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record21 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row21<Integer, Integer, String, String, String, String, String, String, Integer, String, Integer, Integer, Integer, LocalDateTime, LocalDateTime, LocalDateTime, LocalDateTime, String, String, LocalDateTime, String> fieldsRow() {
        return (Row21) super.fieldsRow();
    }

    @Override
    public Row21<Integer, Integer, String, String, String, String, String, String, Integer, String, Integer, Integer, Integer, LocalDateTime, LocalDateTime, LocalDateTime, LocalDateTime, String, String, LocalDateTime, String> valuesRow() {
        return (Row21) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.PIPELINE_ID;
    }

    @Override
    public Field<Integer> field2() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.APP_ID;
    }

    @Override
    public Field<String> field3() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.APP_NAME;
    }

    @Override
    public Field<String> field4() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.PODS;
    }

    @Override
    public Field<String> field5() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.OPTIMIZATION_TYPE;
    }

    @Override
    public Field<String> field6() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.PROBLEM_DESCRIBE;
    }

    @Override
    public Field<String> field7() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.PROBLEM_TYPE;
    }

    @Override
    public Field<String> field8() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.DIRECTION;
    }

    @Override
    public Field<Integer> field9() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.TRIAL_NUMS;
    }

    @Override
    public Field<String> field10() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.TRIAL_PARAMS;
    }

    @Override
    public Field<Integer> field11() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.MAX_ITER;
    }

    @Override
    public Field<Integer> field12() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.TRIAL_TIME_MIN;
    }

    @Override
    public Field<Integer> field13() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.TRIAL_TIME_MAX;
    }

    @Override
    public Field<LocalDateTime> field14() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.TRIAL_START_TIME;
    }

    @Override
    public Field<LocalDateTime> field15() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.TRIAL_STOP_TIME;
    }

    @Override
    public Field<LocalDateTime> field16() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.START_TIME;
    }

    @Override
    public Field<LocalDateTime> field17() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.END_TIME;
    }

    @Override
    public Field<String> field18() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.TASK_STATUS;
    }

    @Override
    public Field<String> field19() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.BEFORE_PARAMS;
    }

    @Override
    public Field<LocalDateTime> field20() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.MODIFY_TIME;
    }

    @Override
    public Field<String> field21() {
        return TuningParamTaskData.TUNING_PARAM_TASK_DATA.COMPARE_PODS;
    }

    @Override
    public Integer component1() {
        return getPipelineId();
    }

    @Override
    public Integer component2() {
        return getAppId();
    }

    @Override
    public String component3() {
        return getAppName();
    }

    @Override
    public String component4() {
        return getPods();
    }

    @Override
    public String component5() {
        return getOptimizationType();
    }

    @Override
    public String component6() {
        return getProblemDescribe();
    }

    @Override
    public String component7() {
        return getProblemType();
    }

    @Override
    public String component8() {
        return getDirection();
    }

    @Override
    public Integer component9() {
        return getTrialNums();
    }

    @Override
    public String component10() {
        return getTrialParams();
    }

    @Override
    public Integer component11() {
        return getMaxIter();
    }

    @Override
    public Integer component12() {
        return getTrialTimeMin();
    }

    @Override
    public Integer component13() {
        return getTrialTimeMax();
    }

    @Override
    public LocalDateTime component14() {
        return getTrialStartTime();
    }

    @Override
    public LocalDateTime component15() {
        return getTrialStopTime();
    }

    @Override
    public LocalDateTime component16() {
        return getStartTime();
    }

    @Override
    public LocalDateTime component17() {
        return getEndTime();
    }

    @Override
    public String component18() {
        return getTaskStatus();
    }

    @Override
    public String component19() {
        return getBeforeParams();
    }

    @Override
    public LocalDateTime component20() {
        return getModifyTime();
    }

    @Override
    public String component21() {
        return getComparePods();
    }

    @Override
    public Integer value1() {
        return getPipelineId();
    }

    @Override
    public Integer value2() {
        return getAppId();
    }

    @Override
    public String value3() {
        return getAppName();
    }

    @Override
    public String value4() {
        return getPods();
    }

    @Override
    public String value5() {
        return getOptimizationType();
    }

    @Override
    public String value6() {
        return getProblemDescribe();
    }

    @Override
    public String value7() {
        return getProblemType();
    }

    @Override
    public String value8() {
        return getDirection();
    }

    @Override
    public Integer value9() {
        return getTrialNums();
    }

    @Override
    public String value10() {
        return getTrialParams();
    }

    @Override
    public Integer value11() {
        return getMaxIter();
    }

    @Override
    public Integer value12() {
        return getTrialTimeMin();
    }

    @Override
    public Integer value13() {
        return getTrialTimeMax();
    }

    @Override
    public LocalDateTime value14() {
        return getTrialStartTime();
    }

    @Override
    public LocalDateTime value15() {
        return getTrialStopTime();
    }

    @Override
    public LocalDateTime value16() {
        return getStartTime();
    }

    @Override
    public LocalDateTime value17() {
        return getEndTime();
    }

    @Override
    public String value18() {
        return getTaskStatus();
    }

    @Override
    public String value19() {
        return getBeforeParams();
    }

    @Override
    public LocalDateTime value20() {
        return getModifyTime();
    }

    @Override
    public String value21() {
        return getComparePods();
    }

    @Override
    public TuningParamTaskDataRecord value1(Integer value) {
        setPipelineId(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value2(Integer value) {
        setAppId(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value3(String value) {
        setAppName(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value4(String value) {
        setPods(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value5(String value) {
        setOptimizationType(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value6(String value) {
        setProblemDescribe(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value7(String value) {
        setProblemType(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value8(String value) {
        setDirection(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value9(Integer value) {
        setTrialNums(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value10(String value) {
        setTrialParams(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value11(Integer value) {
        setMaxIter(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value12(Integer value) {
        setTrialTimeMin(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value13(Integer value) {
        setTrialTimeMax(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value14(LocalDateTime value) {
        setTrialStartTime(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value15(LocalDateTime value) {
        setTrialStopTime(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value16(LocalDateTime value) {
        setStartTime(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value17(LocalDateTime value) {
        setEndTime(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value18(String value) {
        setTaskStatus(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value19(String value) {
        setBeforeParams(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value20(LocalDateTime value) {
        setModifyTime(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord value21(String value) {
        setComparePods(value);
        return this;
    }

    @Override
    public TuningParamTaskDataRecord values(Integer value1, Integer value2, String value3, String value4, String value5, String value6, String value7, String value8, Integer value9, String value10, Integer value11, Integer value12, Integer value13, LocalDateTime value14, LocalDateTime value15, LocalDateTime value16, LocalDateTime value17, String value18, String value19, LocalDateTime value20, String value21) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        value20(value20);
        value21(value21);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TuningParamTaskDataRecord
     */
    public TuningParamTaskDataRecord() {
        super(TuningParamTaskData.TUNING_PARAM_TASK_DATA);
    }

    /**
     * Create a detached, initialised TuningParamTaskDataRecord
     */
    public TuningParamTaskDataRecord(Integer pipelineId, Integer appId, String appName, String pods, String optimizationType, String problemDescribe, String problemType, String direction, Integer trialNums, String trialParams, Integer maxIter, Integer trialTimeMin, Integer trialTimeMax, LocalDateTime trialStartTime, LocalDateTime trialStopTime, LocalDateTime startTime, LocalDateTime endTime, String taskStatus, String beforeParams, LocalDateTime modifyTime, String comparePods) {
        super(TuningParamTaskData.TUNING_PARAM_TASK_DATA);

        setPipelineId(pipelineId);
        setAppId(appId);
        setAppName(appName);
        setPods(pods);
        setOptimizationType(optimizationType);
        setProblemDescribe(problemDescribe);
        setProblemType(problemType);
        setDirection(direction);
        setTrialNums(trialNums);
        setTrialParams(trialParams);
        setMaxIter(maxIter);
        setTrialTimeMin(trialTimeMin);
        setTrialTimeMax(trialTimeMax);
        setTrialStartTime(trialStartTime);
        setTrialStopTime(trialStopTime);
        setStartTime(startTime);
        setEndTime(endTime);
        setTaskStatus(taskStatus);
        setBeforeParams(beforeParams);
        setModifyTime(modifyTime);
        setComparePods(comparePods);
    }
}
