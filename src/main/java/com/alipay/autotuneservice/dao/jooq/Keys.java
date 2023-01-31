/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq;


import com.alipay.autotuneservice.dao.jooq.tables.Alarm;
import com.alipay.autotuneservice.dao.jooq.tables.AppInfo;
import com.alipay.autotuneservice.dao.jooq.tables.AppLog;
import com.alipay.autotuneservice.dao.jooq.tables.BaseLine;
import com.alipay.autotuneservice.dao.jooq.tables.CommandInfo;
import com.alipay.autotuneservice.dao.jooq.tables.ConfigInfo;
import com.alipay.autotuneservice.dao.jooq.tables.ExpertKnowledge;
import com.alipay.autotuneservice.dao.jooq.tables.HealthCheckInfo;
import com.alipay.autotuneservice.dao.jooq.tables.HealthCheckResult;
import com.alipay.autotuneservice.dao.jooq.tables.HelpInfo;
import com.alipay.autotuneservice.dao.jooq.tables.JavaInfo;
import com.alipay.autotuneservice.dao.jooq.tables.JvmMarketInfo;
import com.alipay.autotuneservice.dao.jooq.tables.JvmMonitorMetric;
import com.alipay.autotuneservice.dao.jooq.tables.JvmOptsConfig;
import com.alipay.autotuneservice.dao.jooq.tables.JvmTuningRiskCenter;
import com.alipay.autotuneservice.dao.jooq.tables.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.jooq.tables.Lock;
import com.alipay.autotuneservice.dao.jooq.tables.MeterMetaInfo;
import com.alipay.autotuneservice.dao.jooq.tables.NodeInfo;
import com.alipay.autotuneservice.dao.jooq.tables.Notice;
import com.alipay.autotuneservice.dao.jooq.tables.Notify;
import com.alipay.autotuneservice.dao.jooq.tables.PodAttach;
import com.alipay.autotuneservice.dao.jooq.tables.PodInfo;
import com.alipay.autotuneservice.dao.jooq.tables.RiskCheckControl;
import com.alipay.autotuneservice.dao.jooq.tables.RiskCheckTask;
import com.alipay.autotuneservice.dao.jooq.tables.RuleInfo;
import com.alipay.autotuneservice.dao.jooq.tables.StorageInfo;
import com.alipay.autotuneservice.dao.jooq.tables.TaskPipelineInfo;
import com.alipay.autotuneservice.dao.jooq.tables.TuneLogInfo;
import com.alipay.autotuneservice.dao.jooq.tables.TuneParamInfo;
import com.alipay.autotuneservice.dao.jooq.tables.TunePipeline;
import com.alipay.autotuneservice.dao.jooq.tables.TunePipelinePhase;
import com.alipay.autotuneservice.dao.jooq.tables.TunePlan;
import com.alipay.autotuneservice.dao.jooq.tables.TunePoolInfo;
import com.alipay.autotuneservice.dao.jooq.tables.TuningParamTaskData;
import com.alipay.autotuneservice.dao.jooq.tables.TuningParamTaskDataDev;
import com.alipay.autotuneservice.dao.jooq.tables.TuningParamTaskInfo;
import com.alipay.autotuneservice.dao.jooq.tables.TuningParamTrialData;
import com.alipay.autotuneservice.dao.jooq.tables.UserInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.AlarmRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.AppLogRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.BaseLineRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.CommandInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.ConfigInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.ExpertKnowledgeRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.HealthCheckResultRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.HelpInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JavaInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMarketInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMonitorMetricRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmOptsConfigRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmTuningRiskCenterRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.LockRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.MeterMetaInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.NodeInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.NoticeRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.NotifyRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodAttachRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.RiskCheckControlRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.RiskCheckTaskRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.RuleInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.StorageInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TaskPipelineInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneParamInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelinePhaseRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelineRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePlanRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePoolInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTaskDataDevRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTaskDataRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTaskInfoRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTrialDataRecord;
import com.alipay.autotuneservice.dao.jooq.tables.records.UserInfoRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in 
 * TMAESTRO-LITE.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AlarmRecord> CONSTRAINT_3 = Internal.createUniqueKey(Alarm.ALARM, DSL.name("CONSTRAINT_3"), new TableField[] { Alarm.ALARM.ID }, true);
    public static final UniqueKey<AppInfoRecord> APP_INFO_APP_NAME_ACCESS_TOKEN_NAMESPACE_UINDEX = Internal.createUniqueKey(AppInfo.APP_INFO, DSL.name("APP_INFO_APP_NAME_ACCESS_TOKEN_NAMESPACE_UINDEX"), new TableField[] { AppInfo.APP_INFO.APP_NAME, AppInfo.APP_INFO.ACCESS_TOKEN, AppInfo.APP_INFO.NAMESPACE }, true);
    public static final UniqueKey<AppInfoRecord> CONSTRAINT_7 = Internal.createUniqueKey(AppInfo.APP_INFO, DSL.name("CONSTRAINT_7"), new TableField[] { AppInfo.APP_INFO.ID }, true);
    public static final UniqueKey<AppLogRecord> CONSTRAINT_F = Internal.createUniqueKey(AppLog.APP_LOG, DSL.name("CONSTRAINT_F"), new TableField[] { AppLog.APP_LOG.ID }, true);
    public static final UniqueKey<BaseLineRecord> CONSTRAINT_3B = Internal.createUniqueKey(BaseLine.BASE_LINE, DSL.name("CONSTRAINT_3B"), new TableField[] { BaseLine.BASE_LINE.ID }, true);
    public static final UniqueKey<CommandInfoRecord> CONSTRAINT_76 = Internal.createUniqueKey(CommandInfo.COMMAND_INFO, DSL.name("CONSTRAINT_76"), new TableField[] { CommandInfo.COMMAND_INFO.ID }, true);
    public static final UniqueKey<ConfigInfoRecord> CONFIG_INFO_APP_ID_INDEX = Internal.createUniqueKey(ConfigInfo.CONFIG_INFO, DSL.name("CONFIG_INFO_APP_ID_INDEX"), new TableField[] { ConfigInfo.CONFIG_INFO.APP_ID }, true);
    public static final UniqueKey<ConfigInfoRecord> CONSTRAINT_8 = Internal.createUniqueKey(ConfigInfo.CONFIG_INFO, DSL.name("CONSTRAINT_8"), new TableField[] { ConfigInfo.CONFIG_INFO.ID }, true);
    public static final UniqueKey<ExpertKnowledgeRecord> CONSTRAINT_8A = Internal.createUniqueKey(ExpertKnowledge.EXPERT_KNOWLEDGE, DSL.name("CONSTRAINT_8A"), new TableField[] { ExpertKnowledge.EXPERT_KNOWLEDGE.ID }, true);
    public static final UniqueKey<HealthCheckInfoRecord> CONSTRAINT_83 = Internal.createUniqueKey(HealthCheckInfo.HEALTH_CHECK_INFO, DSL.name("CONSTRAINT_83"), new TableField[] { HealthCheckInfo.HEALTH_CHECK_INFO.ID }, true);
    public static final UniqueKey<HealthCheckResultRecord> CONSTRAINT_F4 = Internal.createUniqueKey(HealthCheckResult.HEALTH_CHECK_RESULT, DSL.name("CONSTRAINT_F4"), new TableField[] { HealthCheckResult.HEALTH_CHECK_RESULT.ID }, true);
    public static final UniqueKey<HelpInfoRecord> CONSTRAINT_F5 = Internal.createUniqueKey(HelpInfo.HELP_INFO, DSL.name("CONSTRAINT_F5"), new TableField[] { HelpInfo.HELP_INFO.ID }, true);
    public static final UniqueKey<JavaInfoRecord> CONSTRAINT_75 = Internal.createUniqueKey(JavaInfo.JAVA_INFO, DSL.name("CONSTRAINT_75"), new TableField[] { JavaInfo.JAVA_INFO.ID }, true);
    public static final UniqueKey<JvmMarketInfoRecord> CONSTRAINT_5 = Internal.createUniqueKey(JvmMarketInfo.JVM_MARKET_INFO, DSL.name("CONSTRAINT_5"), new TableField[] { JvmMarketInfo.JVM_MARKET_INFO.ID }, true);
    public static final UniqueKey<JvmMonitorMetricRecord> CONSTRAINT_CC = Internal.createUniqueKey(JvmMonitorMetric.JVM_MONITOR_METRIC, DSL.name("CONSTRAINT_CC"), new TableField[] { JvmMonitorMetric.JVM_MONITOR_METRIC.ID }, true);
    public static final UniqueKey<JvmOptsConfigRecord> CONSTRAINT_2 = Internal.createUniqueKey(JvmOptsConfig.JVM_OPTS_CONFIG, DSL.name("CONSTRAINT_2"), new TableField[] { JvmOptsConfig.JVM_OPTS_CONFIG.ID }, true);
    public static final UniqueKey<JvmTuningRiskCenterRecord> CONSTRAINT_84 = Internal.createUniqueKey(JvmTuningRiskCenter.JVM_TUNING_RISK_CENTER, DSL.name("CONSTRAINT_84"), new TableField[] { JvmTuningRiskCenter.JVM_TUNING_RISK_CENTER.ID }, true);
    public static final UniqueKey<JvmTuningRiskCenterRecord> JVM_TUNING_RISK_CENTER_APPID_METRIC_DT_UINDEX = Internal.createUniqueKey(JvmTuningRiskCenter.JVM_TUNING_RISK_CENTER, DSL.name("JVM_TUNING_RISK_CENTER_APPID_METRIC_DT_UINDEX"), new TableField[] { JvmTuningRiskCenter.JVM_TUNING_RISK_CENTER.APPID, JvmTuningRiskCenter.JVM_TUNING_RISK_CENTER.METRIC, JvmTuningRiskCenter.JVM_TUNING_RISK_CENTER.DT }, true);
    public static final UniqueKey<K8sAccessTokenInfoRecord> CONSTRAINT_1 = Internal.createUniqueKey(K8sAccessTokenInfo.K8S_ACCESS_TOKEN_INFO, DSL.name("CONSTRAINT_1"), new TableField[] { K8sAccessTokenInfo.K8S_ACCESS_TOKEN_INFO.ID }, true);
    public static final UniqueKey<K8sAccessTokenInfoRecord> K8S_ACCESS_TOKEN_INFO_ACCESS_TOKEN_CLUSTER_NAME_UINDEX = Internal.createUniqueKey(K8sAccessTokenInfo.K8S_ACCESS_TOKEN_INFO, DSL.name("K8S_ACCESS_TOKEN_INFO_ACCESS_TOKEN_CLUSTER_NAME_UINDEX"), new TableField[] { K8sAccessTokenInfo.K8S_ACCESS_TOKEN_INFO.ACCESS_TOKEN, K8sAccessTokenInfo.K8S_ACCESS_TOKEN_INFO.CLUSTER_NAME }, true);
    public static final UniqueKey<LockRecord> CONSTRAINT_23 = Internal.createUniqueKey(Lock.LOCK, DSL.name("CONSTRAINT_23"), new TableField[] { Lock.LOCK.ID }, true);
    public static final UniqueKey<LockRecord> LOCK_LOCK_NAME_UINDEX = Internal.createUniqueKey(Lock.LOCK, DSL.name("LOCK_LOCK_NAME_UINDEX"), new TableField[] { Lock.LOCK.LOCK_NAME }, true);
    public static final UniqueKey<MeterMetaInfoRecord> CONSTRAINT_FE = Internal.createUniqueKey(MeterMetaInfo.METER_META_INFO, DSL.name("CONSTRAINT_FE"), new TableField[] { MeterMetaInfo.METER_META_INFO.ID }, true);
    public static final UniqueKey<NodeInfoRecord> CONSTRAINT_C = Internal.createUniqueKey(NodeInfo.NODE_INFO, DSL.name("CONSTRAINT_C"), new TableField[] { NodeInfo.NODE_INFO.ID }, true);
    public static final UniqueKey<NodeInfoRecord> NODE_INFO_NODE_NAME_ACCESS_TOKEN_UINDEX = Internal.createUniqueKey(NodeInfo.NODE_INFO, DSL.name("NODE_INFO_NODE_NAME_ACCESS_TOKEN_UINDEX"), new TableField[] { NodeInfo.NODE_INFO.NODE_NAME, NodeInfo.NODE_INFO.ACCESS_TOKEN }, true);
    public static final UniqueKey<NoticeRecord> CONSTRAINT_89 = Internal.createUniqueKey(Notice.NOTICE, DSL.name("CONSTRAINT_89"), new TableField[] { Notice.NOTICE.ID }, true);
    public static final UniqueKey<NoticeRecord> NOTICE_NOTICE_TYPE_ACCESS_TOKEN_UINDEX = Internal.createUniqueKey(Notice.NOTICE, DSL.name("NOTICE_NOTICE_TYPE_ACCESS_TOKEN_UINDEX"), new TableField[] { Notice.NOTICE.NOTICE_TYPE, Notice.NOTICE.ACCESS_TOKEN }, true);
    public static final UniqueKey<NotifyRecord> CONSTRAINT_899 = Internal.createUniqueKey(Notify.NOTIFY, DSL.name("CONSTRAINT_899"), new TableField[] { Notify.NOTIFY.ID }, true);
    public static final UniqueKey<PodAttachRecord> CONSTRAINT_C5 = Internal.createUniqueKey(PodAttach.POD_ATTACH, DSL.name("CONSTRAINT_C5"), new TableField[] { PodAttach.POD_ATTACH.ID }, true);
    public static final UniqueKey<PodInfoRecord> CONSTRAINT_4 = Internal.createUniqueKey(PodInfo.POD_INFO, DSL.name("CONSTRAINT_4"), new TableField[] { PodInfo.POD_INFO.ID }, true);
    public static final UniqueKey<PodInfoRecord> POD_INFO_POD_NAME_ACCESS_TOKEN_UINDEX = Internal.createUniqueKey(PodInfo.POD_INFO, DSL.name("POD_INFO_POD_NAME_ACCESS_TOKEN_UINDEX"), new TableField[] { PodInfo.POD_INFO.POD_NAME, PodInfo.POD_INFO.ACCESS_TOKEN }, true);
    public static final UniqueKey<RiskCheckControlRecord> CONSTRAINT_45 = Internal.createUniqueKey(RiskCheckControl.RISK_CHECK_CONTROL, DSL.name("CONSTRAINT_45"), new TableField[] { RiskCheckControl.RISK_CHECK_CONTROL.ID }, true);
    public static final UniqueKey<RiskCheckTaskRecord> CONSTRAINT_6 = Internal.createUniqueKey(RiskCheckTask.RISK_CHECK_TASK, DSL.name("CONSTRAINT_6"), new TableField[] { RiskCheckTask.RISK_CHECK_TASK.ID }, true);
    public static final UniqueKey<RuleInfoRecord> CONSTRAINT_2B = Internal.createUniqueKey(RuleInfo.RULE_INFO, DSL.name("CONSTRAINT_2B"), new TableField[] { RuleInfo.RULE_INFO.ID }, true);
    public static final UniqueKey<StorageInfoRecord> CONSTRAINT_C1 = Internal.createUniqueKey(StorageInfo.STORAGE_INFO, DSL.name("CONSTRAINT_C1"), new TableField[] { StorageInfo.STORAGE_INFO.ID }, true);
    public static final UniqueKey<TaskPipelineInfoRecord> CONSTRAINT_9 = Internal.createUniqueKey(TaskPipelineInfo.TASK_PIPELINE_INFO, DSL.name("CONSTRAINT_9"), new TableField[] { TaskPipelineInfo.TASK_PIPELINE_INFO.ID }, true);
    public static final UniqueKey<TuneLogInfoRecord> CONSTRAINT_B = Internal.createUniqueKey(TuneLogInfo.TUNE_LOG_INFO, DSL.name("CONSTRAINT_B"), new TableField[] { TuneLogInfo.TUNE_LOG_INFO.ID }, true);
    public static final UniqueKey<TuneLogInfoRecord> TUNE_LOG_INFO_ID_APP_ID_JVM_MARKET_ID_BATCH_NO_UINDEX = Internal.createUniqueKey(TuneLogInfo.TUNE_LOG_INFO, DSL.name("TUNE_LOG_INFO_ID_APP_ID_JVM_MARKET_ID_BATCH_NO_UINDEX"), new TableField[] { TuneLogInfo.TUNE_LOG_INFO.ID, TuneLogInfo.TUNE_LOG_INFO.APP_ID, TuneLogInfo.TUNE_LOG_INFO.JVM_MARKET_ID, TuneLogInfo.TUNE_LOG_INFO.BATCH_NO }, true);
    public static final UniqueKey<TuneLogInfoRecord> TUNE_LOG_INFO_PIPELINE_ID_APP_ID_JVM_MARKET_ID_BATCH_NO_UINDEX = Internal.createUniqueKey(TuneLogInfo.TUNE_LOG_INFO, DSL.name("TUNE_LOG_INFO_PIPELINE_ID_APP_ID_JVM_MARKET_ID_BATCH_NO_UINDEX"), new TableField[] { TuneLogInfo.TUNE_LOG_INFO.PIPELINE_ID, TuneLogInfo.TUNE_LOG_INFO.APP_ID, TuneLogInfo.TUNE_LOG_INFO.JVM_MARKET_ID, TuneLogInfo.TUNE_LOG_INFO.BATCH_NO }, true);
    public static final UniqueKey<TuneParamInfoRecord> CONSTRAINT_B3 = Internal.createUniqueKey(TuneParamInfo.TUNE_PARAM_INFO, DSL.name("CONSTRAINT_B3"), new TableField[] { TuneParamInfo.TUNE_PARAM_INFO.ID }, true);
    public static final UniqueKey<TuneParamInfoRecord> TUNE_PARAM_INFO_ID_UINDEX = Internal.createUniqueKey(TuneParamInfo.TUNE_PARAM_INFO, DSL.name("TUNE_PARAM_INFO_ID_UINDEX"), new TableField[] { TuneParamInfo.TUNE_PARAM_INFO.ID }, true);
    public static final UniqueKey<TunePipelineRecord> CONSTRAINT_24 = Internal.createUniqueKey(TunePipeline.TUNE_PIPELINE, DSL.name("CONSTRAINT_24"), new TableField[] { TunePipeline.TUNE_PIPELINE.ID }, true);
    public static final UniqueKey<TunePipelinePhaseRecord> CONSTRAINT_F7 = Internal.createUniqueKey(TunePipelinePhase.TUNE_PIPELINE_PHASE, DSL.name("CONSTRAINT_F7"), new TableField[] { TunePipelinePhase.TUNE_PIPELINE_PHASE.ID }, true);
    public static final UniqueKey<TunePlanRecord> CONSTRAINT_BD = Internal.createUniqueKey(TunePlan.TUNE_PLAN, DSL.name("CONSTRAINT_BD"), new TableField[] { TunePlan.TUNE_PLAN.ID }, true);
    public static final UniqueKey<TunePoolInfoRecord> CONSTRAINT_A = Internal.createUniqueKey(TunePoolInfo.TUNE_POOL_INFO, DSL.name("CONSTRAINT_A"), new TableField[] { TunePoolInfo.TUNE_POOL_INFO.ID }, true);
    public static final UniqueKey<TuningParamTaskDataRecord> CONSTRAINT_6E = Internal.createUniqueKey(TuningParamTaskData.TUNING_PARAM_TASK_DATA, DSL.name("CONSTRAINT_6E"), new TableField[] { TuningParamTaskData.TUNING_PARAM_TASK_DATA.PIPELINE_ID }, true);
    public static final UniqueKey<TuningParamTaskDataDevRecord> CONSTRAINT_B0 = Internal.createUniqueKey(TuningParamTaskDataDev.TUNING_PARAM_TASK_DATA_DEV, DSL.name("CONSTRAINT_B0"), new TableField[] { TuningParamTaskDataDev.TUNING_PARAM_TASK_DATA_DEV.PIPELINE_ID }, true);
    public static final UniqueKey<TuningParamTaskInfoRecord> CONSTRAINT_6ED = Internal.createUniqueKey(TuningParamTaskInfo.TUNING_PARAM_TASK_INFO, DSL.name("CONSTRAINT_6ED"), new TableField[] { TuningParamTaskInfo.TUNING_PARAM_TASK_INFO.INFO_ID }, true);
    public static final UniqueKey<TuningParamTrialDataRecord> CONSTRAINT_67 = Internal.createUniqueKey(TuningParamTrialData.TUNING_PARAM_TRIAL_DATA, DSL.name("CONSTRAINT_67"), new TableField[] { TuningParamTrialData.TUNING_PARAM_TRIAL_DATA.ID }, true);
    public static final UniqueKey<UserInfoRecord> CONSTRAINT_BC = Internal.createUniqueKey(UserInfo.USER_INFO, DSL.name("CONSTRAINT_BC"), new TableField[] { UserInfo.USER_INFO.ID }, true);
}
