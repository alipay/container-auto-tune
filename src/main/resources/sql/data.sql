create schema if not exists `tmaestro-lite`;

create table if not exists `tmaestro-lite`.alarm
(
    ID               int auto_increment
    primary key,
    ALARM_NAME       varchar(256)  null,
    APP_NAME         varchar(256)  null,
    APP_ID           int           null,
    STATUS           varchar(128)  null,
    ALARM_RULE       varchar(512)  null,
    RULE_ACTION      varchar(1024) null,
    ALARM_NOTICE     varchar(1024) null,
    CREATE_BY        varchar(128)  null,
    context          varchar(2048) null,
    CREATED_TIME     datetime      null,
    UPDATED_TIME     datetime      null,
    COMBINATION_TYPE varchar(128)  null,
    ACCESS_TOKEN     varchar(125)  null
    );
-- comment '报警管理列表';

create table if not exists `tmaestro-lite`.app_info
(
    ID               int auto_increment comment '唯一ID;唯一ID'
    primary key,
    USER_ID          int           null comment '关联的用户id',
    ACCESS_TOKEN     varchar(255)  not null comment '关联的token',
    NODE_IDS         varchar(255)  null comment '关联的集群组',
    APP_NAME         varchar(255)  null comment '集群真实名称;集群真实名称',
    APP_AS_NAME      varchar(255)  null comment '应用别名',
    APP_DESC         varchar(255)  null comment '集群说明;集群说明',
    CREATED_TIME     datetime      not null comment '创建时间;创建时间',
    UPDATED_TIME     datetime      null comment '更新时间;更新时间',
    STATUS           varchar(255)  not null comment '状态;状态',
    APP_DEFAULT_JVM  varchar(1024) null comment '集群jvm生效配置;集群jvm生效配置',
    CLUSTER_NAME     varchar(255)  null comment 'k8s集群名',
    app_tag          varchar(255)  null comment '标注应用类型',
    namespace        varchar(100)  null,
    CLUSTER_ID       varchar(255)  null comment '集群ID',
    SERVER_TYPE     varchar(255)  null comment '资源类型',
    APP_JVM_TEMPLATE varchar(2048) null comment '该应用对应的APP模版',
    constraint app_info_APP_NAME_ACCESS_TOKEN_namespace_uindex
    unique (APP_NAME, ACCESS_TOKEN, namespace)
    );
-- comment '应用管理表';

create table if not exists `tmaestro-lite`.app_log
(
    ID           bigint auto_increment comment '主键'
    primary key,
    APP_ID       int           not null comment '应用id',
    LOG_TYPE     varchar(128)  not null comment '日志类型',
    S3_KEY       varchar(256)  not null comment 's3文件key',
    HOST_NAME    varchar(256)  not null comment '主机名',
    CREATED_TIME datetime      not null comment '创建时间',
    UPDATED_TIME datetime      not null comment '更新时间',
    FILE_NAME    varchar(256)  not null comment '文件名',
    context      varchar(2048) null comment '上下文',
    status       varchar(128)  null
    );
-- comment '应用日志';

create index if not exists app_info_ACCESS_TOKEN_CLUSTER_NAME_STATUS_index
    on tmaster-lite.app_info (ACCESS_TOKEN, CLUSTER_NAME, STATUS);

create index if not exists app_info_APP_NAME_ACCESS_TOKEN_STATUS_index
    on tmaster-lite.app_info (APP_NAME, ACCESS_TOKEN, STATUS);

create index if not exists app_info_ID_STATUS_index
    on tmaster-lite.app_info (ID, STATUS);



create table if not exists `tmaestro-lite`.base_line
(
    ID            int auto_increment comment '主键ID'
    primary key,
    APP_ID        int      null comment '关联的应用id',
    PIPELINE_ID   int      null comment '关联的集群id',
    JVM_MARKET_ID int      null,
    CREATED_TIME  datetime null,
    VERSION       int      null
);
-- comment '基线管理表';

create table if not exists `tmaestro-lite`.command_info
(
    ID           bigint auto_increment comment '主键'
    primary key,
    SESSIONID    varchar(128) not null comment '会话ID',
    RUNLE_ACTION varchar(256) not null comment '执行类型',
    RESULT_TYPE  varchar(128) not null comment '结果类型',
    RESULT       mediumtext   null comment '结果',
    ACCESS_TOKEN varchar(255) null comment '关联的token',
    APP_NAME     varchar(32)  null comment '应用名',
    UNION_CODE   varchar(32)  not null comment '每个VM启动后,自动生成唯一标识,用来进行唯一关联',
    CONTEXT      mediumtext   null comment '执行上下文',
    STATUS       varchar(128) null comment '执行状态',
    CREATED_TIME datetime     not null comment '创建时间',
    UPDATED_TIME datetime     null comment '更新时间',
    TASK_NAME    varchar(256) null,
    POD_NAME     varchar(256) null
    );
-- comment '命令记录表';

create table if not exists `tmaestro-lite`.config_info
(
    ID                int auto_increment comment '唯一ID;唯一ID'
    primary key,
    APP_ID            int          null comment '关联的集群id',
    AUTO_TUNE         varchar(5)   not null comment '自动调优开关 。true：开启；false关闭',
    TUNE_PRIMARY_TIME varchar(500) not null comment '调节约束时间',
    TUNE_GROUP_CONFIG varchar(500) null comment '调节分组配置',
    RISK_SWITCH       varchar(5)   null comment '智能防控开关。true:打开；false:关闭',
    AUTO_DISPATCH     varchar(255) not null comment '托管标识。true：开启；false关闭。',
    ADVANCED_SETUP    varchar(500) not null comment '高级设置：每批次条调参完成，会对这些配置进行检查',
    TIME_ZONE         varchar(50)  not null comment '时区',
    TUNE_TIME_TAG     varchar(5)   not null comment '调节时间标识。true代表可调有时间。false代表不可调有时间。与TUNE_PRIMARY_TIME结合使用',
    OPERATE_TIME      varchar(25)  not null comment '操作时间',
    constraint config_info_app_id_index
    unique (APP_ID)
    );
-- comment '应用管理表';

create table if not exists `tmaestro-lite`.expert_knowledge
(
    ID                int auto_increment
    primary key,
    GARBAGE_COLLECTOR varchar(128)  null comment '垃圾回收器种类',
    JDK_VERSION       varchar(128)  null comment 'jdk版本',
    `DESC`            varchar(2048) null comment '问题描述',
    PROBLEM_TYPE_SET  varchar(512)  null comment '问题类型集合',
    EXPERT_JVM_PLANS  varchar(2048) null comment '调整方案',
    CREATED_TIME      datetime      null comment '创建时间',
    CREATED_BY        varchar(128)  null comment '创建人',
    UPDATED_TIME      datetime      null comment '更新时间'
    );
-- comment '专家经验';

create table if not exists `tmaestro-lite`.health_check_info
(
    ID             int auto_increment comment '主键ID'
    primary key,
    ACCESS_TOKEN   varchar(255)  not null comment '关联的token',
    APP_ID         int           null comment '关联的集群id',
    CREATED_BY     varchar(255)  null comment '创建人',
    CREATED_TIME   datetime      null comment '创建时间',
    STATUS         varchar(255)  not null comment '状态:检查成功、检查失败',
    PROBLEAM_POINT varchar(4000) not null comment '检查问题点',
    GRADE          varchar(255)  not null comment '分数',
    ENCHANGE_POINT varchar(255)  null comment '改变的点',
    ALGO_PROBLEAM  varchar(4000) null
    );
-- comment '健康检查';

create table if not exists `tmaestro-lite`.health_check_result
(
    ID            int auto_increment comment '主键ID'
    primary key,
    APP_ID        int          not null comment '关联的集群id',
    ACCESS_TOKEN  varchar(255) null comment '关联的token',
    CREATED_BY    varchar(255) null comment '创建人',
    CREATED_TIME  datetime     null comment '创建时间',
    UPDATE_TIME   datetime     null comment '检查完成后更新时间',
    CREATE_MODE   varchar(30)  null comment 'online 在线 offline 离线任务',
    STATUS        varchar(30)  null comment '检测状态',
    PROBLEAM      mediumtext   null comment '检查简要结果',
    REPORT        mediumtext   null comment '结果报告',
    REPORT_DETAIL mediumtext   null comment '结果报告详情'
    );
-- comment '健康检查结果标';

create index if not exists health_check_result_ID_index
    on tmaster-lite.health_check_result (ID);

create table if not exists `tmaestro-lite`.help_info
(
    ID                int auto_increment comment '主键ID'
    primary key,
    STEP              int          not null comment '当前步骤',
    STEP_TITLE        varchar(255) not null comment '步骤标题',
    STEP_SHOW_MESSAGE varchar(900) not null comment '展示内容',
    STEP_TAG          varchar(32)  null comment '步骤标签',
    CREATED_BY        varchar(255) not null comment '创建人',
    CREATED_TIME      datetime     null comment '创建时间',
    UPDATED_BY        varchar(255) null comment '更新人',
    UPDATED_TIME      datetime     null comment '更新时间'
    );
-- comment 'agent安装帮助';

create table if not exists `tmaestro-lite`.java_info
(
    ID              int auto_increment comment '唯一ID;唯一ID'
    primary key,
    PID             int           null comment '应用id',
    VERSION         varchar(255)  null comment '版本',
    JVM_HOME        varchar(1000) null comment 'javaHome',
    CLASS_PATH      varchar(1000) null comment 'classPath',
    LIBRARY_PATH    varchar(255)  null comment '文件路径',
    USER_NAME       varchar(255)  null comment 'userName',
    USER_DIR        varchar(255)  null comment 'userDir',
    EXEC_TIME       varchar(255)  null comment '存活时间',
    JAVA_LIBS       text          null comment '依赖包',
    INPUT_ARGUMENTS text          null comment '启动参数',
    HOST_NAME       varchar(255)  null comment 'hostname',
    OS_VERSION      varchar(255)  null comment 'osVersion',
    OS_ARCH         varchar(255)  null comment 'osArch',
    APP_NAME        varchar(255)  null comment '集群真实名称;集群真实名称',
    NAMESPACE       varchar(100)  null,
    CREATED_TIME    datetime      not null comment '创建时间;创建时间',
    UPDATED_TIME    datetime      null comment '更新时间;更新时间'
    );
-- comment 'java基础信息表';

create table if not exists `tmaestro-lite`.jvm_market_info
(
    ID           int auto_increment comment '主键ID'
    primary key,
    CREATED_TIME datetime      not null comment '创建时间',
    JVM_CONFIG   varchar(2000) not null comment 'jvm配置',
    RECOMMEND    varchar(255)  null comment '简介',
    CREATED_BY   varchar(255)  null comment '创建人'
    );
-- comment '调参管理表';

create table if not exists `tmaestro-lite`.jvm_opts_config
(
    id          bigint auto_increment
    primary key,
    jvm_opt     varchar(512) null,
    create_time datetime     not null,
    update_time datetime     not null
    );

create table if not exists `tmaestro-lite`.jvm_tuning_risk_center
(
    id        bigint auto_increment
    primary key,
    appId     int          null,
    app       varchar(255) null,
    metric    varchar(255) null,
    model     varchar(255) null,
    dt        varchar(255) null,
    sucess    varchar(255) null,
    info      varchar(255) null,
    lowline   double       null,
    upline    double       null,
    timestamp datetime     null,
    constraint jvm_tuning_risk_center_appId_metric_dt_uindex
    unique (appId, metric, dt)
    );

create table if not exists `tmaestro-lite`.k8s_access_token_info
(
    ID                int auto_increment comment '主键ID'
    primary key,
    CREATE_TIME       datetime     not null comment '创建时间',
    UPDATED_TIME      datetime     not null comment '修改时间',
    ACCESS_TOKEN      varchar(200) not null comment 'access;token',
    CLUSTER_NAME      varchar(128) not null comment '集群名称',
    REGION            varchar(100) not null comment 'aws;region',
    S3_KEY            varchar(200) not null,
    ACCESS_KEY_ID     varchar(128) null comment 'access;key id',
    SECRET_ACCESS_KEY varchar(100) null comment 'Secret;Access Key',
    CER               varchar(100) null comment '证书',
    ENDPOINT          varchar(100) null comment 'aws;endpoint url',
    CLUSTER_ID        varchar(128) null comment '集群ID',
    CLUSTER_STATUS    varchar(48)  null comment '集群状态：INSTALLING 安装中; INSTALLED 已安装； INSTALL_FAILED 安装失败',
    constraint k8s_access_token_info_ACCESS_TOKEN_CLUSTER_NAME_uindex
    unique (ACCESS_TOKEN, CLUSTER_NAME)
    );
-- comment 'k8s;access token信息表';

create table if not exists `tmaestro-lite`.`lock`
(
    ID           int auto_increment
    primary key,
    LOCK_NAME    varchar(128) null,
    CREATED_TIME datetime     null,
    UPDATED_TIME datetime     null,
    constraint lock_LOCK_NAME_uindex
    unique (LOCK_NAME)
    );
-- comment '锁表';

create table if not exists `tmaestro-lite`.meter_meta_info
(
    ID            int auto_increment comment '唯一ID;唯一ID'
    primary key,
    CREATED_TIME  datetime      null,
    MODIFIED_TIME datetime      null,
    METER_NAME    varchar(100)  not null comment '注册的监控名称',
    METER_DOMAIN  varchar(200)  null comment '注册监控的domain',
    METER_METRICS varchar(2048) null comment '监控指标信息',
    METER_ENABLE  varchar(20)   null,
    APP_ID        int           not null
    );
-- comment '存放注册监控的元数据信息';

create table if not exists `tmaestro-lite`.node_info
(
    ID           int auto_increment comment '主键ID'
    primary key,
    NODE_NAME    varchar(255)  not null comment '节点名称',
    IP           varchar(255)  not null comment 'IP地址',
    STATUS       varchar(255)  null comment '状态;状态;包含：存活、失效',
    CREATED_TIME datetime      not null comment '创建时间',
    NODE_TAGS    varchar(8192) null comment '标签',
    ACCESS_TOKEN varchar(255)  not null comment '关联的token',
    UPDATED_TIME datetime      null,
    constraint node_info_NODE_NAME_ACCESS_TOKEN_uindex
    unique (NODE_NAME, ACCESS_TOKEN)
    );
-- comment '节点管理表';

create table if not exists `tmaestro-lite`.notice
(
    ID            int auto_increment
    primary key,
    NOTICE_TYPE   varchar(128)  null,
    CREATED_TIME  datetime      null,
    UPDATED_TIME  datetime      null,
    ACCEPT        varchar(2048) null,
    ACCESS_TOKEN  varchar(256)  null,
    NOTICE_STATUS varchar(128)  null,
    constraint notice_NOTICE_TYPE_ACCESS_TOKEN_uindex
    unique (NOTICE_TYPE, ACCESS_TOKEN)
    );
-- comment '消息通知表';

create table if not exists `tmaestro-lite`.notify
(
    ID           int auto_increment
    primary key,
    GROUP_NAME   varchar(256)  null,
    STATUS       varchar(128)  null,
    CREATE_BY    varchar(128)  null,
    CREATED_TIME datetime      null,
    UPDATED_TIME datetime      null,
    context      varchar(2048) null,
    ACCESS_TOKEN varchar(256)  null
    );
-- comment '消息组通知表';

create table if not exists `tmaestro-lite`.pod_attach
(
    ID            int auto_increment comment primary key
    primary key,
    ACCESS_TOKEN  varchar(256) not null comment '关联的token',
    POD_ID        int          not null comment 'pod id',
    ATTACH_STATUS varchar(128) not null comment 'attach status',
    CREATED_TIME  datetime     not null comment 'create time',
    UPDATED_TIME  datetime     not null comment 'update time'
    );
-- comment 'pod attach info'

create table if not exists `tmaestro-lite`.pod_info
(
    ID              int auto_increment comment '主键ID'
    primary key,
    APP_ID          int           null comment '关联的应用id',
    NODE_ID         int           null comment '关联的集群id',
    POD_NAME        varchar(255)  not null comment '所属节点名称',
    IP              varchar(255)  null comment 'IP地址',
    STATUS          varchar(255)  null comment '状态;状态;包含：存活、失效',
    CREATED_TIME    datetime      not null comment '创建时间',
    POD_JVM         varchar(4000) null comment '生效jvm配置',
    ENV             varchar(4000) null comment '环境变量',
    POD_DEPLOY_TYPE varchar(255)  null comment 'pod部署类型',
    POD_TEMPLATE    varchar(255)  null comment 'pod基础模板信息;pod部署类型：4C8G',
    POD_TAGS        varchar(4000) null comment '标签',
    ACCESS_TOKEN    varchar(255)  not null comment '关联的token',
    CLUSTER_NAME    varchar(128)  null comment 'k8s集群名',
    K8S_NAMESPACE   varchar(512)  null comment 'k8s命名空间',
    POD_STATUS      varchar(255)  null comment '状态;状态;包含：存活、失效',
    UPDATED_TIME    datetime      null,
    CPU_CORE_LIMIT  int           null comment 'cpu core limit',
    MEM_LIMIT       int           null comment 'memory limit',
    CPU_LIMIT       mediumtext    null comment 'cpu limit',
    AGENT_INSTALL   int default 0 null comment '是否安装autotuneagent.
1 - 安装
0 - 未安装',
    D_HOSTNAME      varchar(200)  null,
    NODE_IP         varchar(100)  null,
    NODE_NAME       varchar(255)  null,
    SERVER_TYPE     varchar(127)  null,
    UNICODE         varchar(127)  null,
    constraint pod_info_POD_NAME_ACCESS_TOKEN_uindex
    unique (POD_NAME, ACCESS_TOKEN)
    );
-- comment '单机管理表';

create index if not exists pod_info_APP_ID_AGENT_INSTALL_index
    on tmaster-lite.pod_info (APP_ID, AGENT_INSTALL);

create index if not exists pod_info_APP_ID_POD_STATUS_index
    on tmaster-lite.pod_info (APP_ID, POD_STATUS);

create index if not exists pod_info_D_HOSTNAME_index
    on tmaster-lite.pod_info (D_HOSTNAME);

create table if not exists `tmaestro-lite`.risk_check_control
(
    ID            int auto_increment comment '主键ID'
    primary key,
    APP_ID        int           null comment '应用id',
    APP_NAME      varchar(50)   null comment '应用名称',
    CHECK_TIME    int           not null comment '风险检查次数',
    TASK_IDS      varchar(255)  null comment '任务ID列表',
    STATUS        varchar(10)   null comment '任务执行状态(EXECETING,END)',
    CHECK_RESULT  varchar(10)   not null comment '风险检查结果',
    TRACE_ID      varchar(100)  null comment '任务唯一标识',
    RISK_MSG      varchar(5000) null comment '风险详情',
    CREATE_TIME   datetime      null comment '创建时间',
    riskBeginTime datetime      null,
    riskEndTime   datetime      null
    );
-- comment '风险识别控制表';

create index if not exists risk_check_controller_TRACE_ID_IDX
    on tmaster-lite.risk_check_control (TRACE_ID);

create table if not exists `tmaestro-lite`.risk_check_task
(
    ID            int auto_increment comment '主键ID'
    primary key,
    JOB_ID        int           null comment '对应risk_check_controller的主键',
    EXECUTE_TIME  datetime      not null comment '任务执行时间',
    EXECUTE_PARAM varchar(5000) not null comment '任务ID列表',
    TASK_STATUS   varchar(10)   null comment '任务执行状态(READY,INTERUPTE,END)',
    TASK_RESULT   varchar(10)   null comment '任务检查结果',
    TASK_TRACE_ID varchar(100)  null comment '任务唯一标识',
    TASK_RISK_MSG varchar(5000) null comment '风险详情',
    CREATE_TIME   datetime      null comment '创建时间'
    );
-- comment '风险识别任务表';

create index if not exists risk_check_task_JOB_ID_IDX
    on tmaster-lite.risk_check_task (JOB_ID);

create index if not exists risk_check_task_TASK_TRACE_ID_IDX
    on tmaster-lite.risk_check_task (TASK_TRACE_ID);

create table if not exists `tmaestro-lite`.rule_info
(
    ID            int auto_increment
    primary key,
    RULE_TYPE     varchar(128) null,
    RULE_NAME     varchar(128) null,
    RULE_FUNCTION varchar(128) null,
    RULE_SYMBOL   varchar(128) null,
    RULE_DATA     int          null,
    TIME_INTERVAL int          null,
    CREATED_TIME  datetime     null,
    UPDATED_TIME  datetime     null,
    CREATE_BY     varchar(128) null
    );
-- comment '规则元素表';

create table if not exists `tmaestro-lite`.storage_info
(
    ID           bigint auto_increment
    primary key,
    TYPE         varchar(128) null,
    FILE_NAME    varchar(512) null,
    S3_KEY       varchar(512) not null,
    RESULT_KEY   varchar(128) null,
    ACCESS_TOKEN varchar(128) null,
    OPERATOR     varchar(128) null,
    UPDATED_TIME datetime     null,
    CREATED_TIME datetime     null
    );
-- comment 's3永久文件存储卷';

create table if not exists `tmaestro-lite`.task_pipeline_info
(
    ID                  int auto_increment comment '主键ID'
    primary key,
    ACCESS_TOKEN        varchar(255) not null comment '关联的token',
    EXECUTE_CLUSTER_ID  varchar(255) null comment '执行集群ID',
    EXECUTE_NODE_ID     varchar(255) null comment '执行单机ID',
    REPORT_TYPE         varchar(255) null comment '报告类型;报告类型：线程、内存、GC、ALL、TUNE',
    EXECUTE_TYPE        varchar(255) null comment '执行类型;执行类型：人工、定时、阈值、upload',
    EXECUTE_PARAM       varchar(255) null comment '执行参数',
    EXECUTE_FUTURE_TIME datetime     null comment '期望执行时间',
    EXECUTE_TIME        datetime     null comment '实际执行时间',
    RULE_IDS            int          null comment '关联的规则id;[]',
    REPORT_ID           int          null comment '关联的报告id',
    CREATED_BY          varchar(255) null comment '创建人',
    CREATED_TIME        datetime     null comment '创建时间',
    STATUS              varchar(255) null comment '状态;准备-->运行(采集日志、分析日志)--->结束'
    );
-- comment '任务管理表';

create table if not exists `tmaestro-lite`.tune_log_info
(
    ID              int auto_increment comment '主键'
    primary key,
    PIPELINE_ID     int          null comment '流程id',
    APP_ID          int          null comment '应用id',
    JVM_MARKET_ID   int          null comment '参数id',
    ACTION          varchar(255) null comment '执行动作',
    CREATED_TIME    datetime     null comment '创建时间',
    CHANGET_TIME    datetime     null comment '更新时间',
    CHANGE_POD_NAME varchar(255) null comment '改变的单机',
    ACTION_DESC     varchar(255) null comment '描述',
    ERROR_MSG       varchar(255) null comment '异常描述',
    BATCH_TOTAL_NUM int          null comment '分批总机器数',
    BATCH_PODS      mediumtext   null comment '分批机器变更详情',
    BATCH_RATIO     int          null comment '分批比例',
    BATCH_NO        int          null comment '当前分批批次',
    RISK_TRACE_ID   varchar(255) null comment '风险识别ID',
    constraint tune_log_info_ID_APP_ID_JVM_MARKET_ID_BATCH_NO_uindex
    unique (ID, APP_ID, JVM_MARKET_ID, BATCH_NO),
    constraint tune_log_info_PIPELINE_ID_APP_ID_JVM_MARKET_ID_BATCH_NO_uindex
    unique (PIPELINE_ID, APP_ID, JVM_MARKET_ID, BATCH_NO)
    );
-- comment '调参记录表';

create table if not exists `tmaestro-lite`.tune_param_info
(
    ID                 int auto_increment
    primary key,
    CREATE_TIME        datetime      null comment '创建时间',
    UPDATED_TIME       datetime      null comment '更新时间',
    APP_ID             int           null comment 'app id',
    PIPELINE_ID        int           null comment 'pipeline id',
    JVM_MARKET_ID      int           null comment 'jvm market id',
    DECISION_ID        varchar(128)  null comment 'decision Id',
    UPDATE_STATUS      varchar(128)  null comment '更新参数的状态',
    ACCESS_TOKEN       varchar(256)  null comment 'acess token',
    UPDATE_PARAMS      varchar(4096) null comment '更新的JVM参数，以JSON形式存储',
    OPERATOR           varchar(128)  null comment '操作员',
    CHANGED_TUNE_GROUP varchar(255)  null comment '修改的调参分组信息，json形式',
    DEFAULT_PARAM      varchar(4096) null comment 'app默认启动参数',
    constraint tune_param_info_ID_uindex
    unique (ID)
    );
-- comment '负责用户修改的调参参数的管理';

create table if not exists `tmaestro-lite`.tune_pipeline
(
    ID               int auto_increment comment '主键'
    primary key,
    PIPELINE_ID      int          null comment '流程id',
    ACCESS_TOKEN     varchar(255) not null comment '关联的token',
    APP_ID           int          not null comment '应用ID',
    CREATED_TIME     datetime     not null comment '创建时间',
    UPDATED_TIME     datetime     not null comment '更新时间',
    STATUS           varchar(128) not null comment '当前状态',
    STAGE            varchar(128) not null comment '当前阶段',
    PRE_PHASE_ID     int          null comment '前一阶段id',
    CURRENT_PHASE_ID int          null comment '当前阶段id',
    MACHINE_ID       varchar(128) not null comment '状态机id',
    TUNE_PLAN_ID     int          null comment '对应的计划id',
    TYPE             varchar(50)  null
    );
-- comment '调参pipeline';

create table if not exists `tmaestro-lite`.tune_pipeline_phase
(
    ID                 int auto_increment comment '主键'
    primary key,
    STAGE              varchar(128)   not null comment '阶段',
    PIPELINE_ID        int            null comment 'pipeline id',
    PIPELINE_BRANCH_ID int            null comment 'tune_pipeline主键id',
    UPDATED_TIME       datetime       not null comment '更新时间',
    CREATED_TIME       datetime       not null comment '创建时间',
    CONTEXT            varchar(16000) null comment '上下文'
    );
-- comment '调参pipeline阶段';

create table if not exists `tmaestro-lite`.tune_plan
(
    ID              int auto_increment comment '主键ID'
    primary key,
    HEALTH_CHECK_ID int           null comment '关联的健康检查id',
    ACCESS_TOKEN    varchar(255)  not null comment '关联的token',
    APP_ID          int           null comment '关联的集群id',
    PLAN_NAME       varchar(255)  null comment '任务名称',
    PLAN_STATUS     varchar(255)  not null comment '任务状态:RUNNING,END,INIT',
    ACTION_STATUS   varchar(255)  not null comment '任务状态:AUTO、MANUAL',
    PLAN_PARAM      varchar(255)  null comment '任务参数',
    CREATED_TIME    datetime      not null comment '创建时间',
    UPDATE_TIME     datetime      null comment '修改时间',
    TUNE_EFFECT     varchar(2000) null,
    PREDICT_EFFECT  varchar(2000) null comment '预期评估',
    TUNE_STATUS     varchar(255)  null
    );
-- comment '调参计划';

create index if not exists tune_plan_APP_ID_index
    on tmaster-lite.tune_plan (APP_ID);

create table if not exists `tmaestro-lite`.tune_pool_info
(
    ID                     int auto_increment comment '主键ID'
    primary key,
    ACCESS_TOKEN           varchar(255)  not null comment '关联的token',
    APP_ID                 int           not null comment '应用ID',
    PIPELINE_ID            int           not null comment '关联的流程ID',
    APP_NAME               varchar(32)   not null comment '应用名',
    CREATED_TIME           datetime      null comment '创建时间',
    UPDATED_TIME           datetime      null comment '更新时间',
    UPDATED_BY             varchar(255)  null comment '更新人',
    EXPERIMENT_POOL_STATUS varchar(255)  not null comment '实验池状态',
    BATCH_POOL_STATUS      varchar(255)  not null comment '调参池状态',
    EXPERIMENT_POOL_CONFIG varchar(2000) not null comment '实验池配置',
    BATCH_POOL_CONFIG      varchar(2000) not null comment '调参池配置'
    );
-- comment '调参池管理表';

create table if not exists `tmaestro-lite`.tuning_param_task_data
(
    pipeline_id       int           not null
    primary key,
    app_id            int           null,
    app_name          varchar(255)  null,
    pods              varchar(255)  null,
    optimization_type varchar(255)  null,
    problem_describe  varchar(255)  null,
    problem_type      varchar(255)  null,
    direction         varchar(255)  null,
    trial_nums        int           null,
    trial_params      varchar(1000) null,
    max_iter          int           null,
    trial_time_min    int           null,
    trial_time_max    int           null,
    trial_start_time  datetime      null,
    trial_stop_time   datetime      null,
    start_time        datetime      null,
    end_time          datetime      null,
    task_status       varchar(255)  null,
    before_params     varchar(1000) null,
    modify_time       datetime      null,
    compare_pods      varchar(1000) null
    );

create table if not exists `tmaestro-lite`.tuning_param_task_data_dev
(
    pipeline_id       int           not null
    primary key,
    app_id            int           null,
    app_name          varchar(255)  null,
    pods              varchar(255)  null,
    optimization_type varchar(255)  null,
    problem_describe  varchar(255)  null,
    problem_type      varchar(255)  null,
    direction         varchar(255)  null,
    trial_nums        int           null,
    trial_params      varchar(1000) null,
    max_iter          int           null,
    trial_time_min    int           null,
    trial_time_max    int           null,
    trial_start_time  datetime      null,
    trial_stop_time   datetime      null,
    start_time        datetime      null,
    end_time          datetime      null,
    task_status       varchar(255)  null,
    before_params     varchar(1000) null,
    modify_time       datetime      null,
    compare_pods      varchar(1000) null
    );

create table if not exists `tmaestro-lite`.tuning_param_task_info
(
    info_id            bigint auto_increment
    primary key,
    app_id             int           null,
    app                varchar(255)  null,
    optimization_type  varchar(255)  null,
    problem_type       varchar(510)  null,
    param_distribution varchar(2000) null,
    problem_id         varchar(255)  null,
    dt                 varchar(255)  null,
    write_time         datetime      null
    );

create table if not exists `tmaestro-lite`.tuning_param_trial_data
(
    id                 bigint auto_increment
    primary key,
    pipeline_id        int           null,
    trial_id           int           null,
    app                varchar(255)  null,
    app_id             varchar(1000) null,
    trial_status       varchar(1000) null,
    trial_status_info  varchar(255)  null,
    task_status        varchar(255)  null,
    refer_params       varchar(1000) null,
    trial_params       varchar(1000) null,
    alg_rec_type       varchar(255)  null,
    refer_pods         varchar(255)  null,
    trial_pods         varchar(255)  null,
    refer_metric_value varchar(1000) null,
    trial_metric_value varchar(1000) null,
    start_time         datetime      null,
    stop_time          datetime      null
    );

create table if not exists `tmaestro-lite`.user_info
(
    ID                 int auto_increment comment '主键ID'
    primary key,
    ACCOUNT_ID         varchar(255) not null comment '关联的AWS用户ID',
    ACCESS_TOKEN       varchar(255) not null comment '关联的token',
    USER_COMPANY       varchar(255) null comment '所属公司',
    USER_NAME          varchar(255) null comment '名称',
    CREATED_TIME       datetime     not null comment '创建时间',
    UPDATED_TIME       datetime     not null comment '更新时间',
    TENANT_CODE        varchar(128) null comment '租户
',
    PRODUCT_ACCOUNT_ID varchar(128) null comment '产品账户',
    PLAN_CODE          varchar(128) null
    );
-- comment '用户信息表';

create table if not exists `tmaestro-lite`.jvm_monitor_metric_data
(
    POD_NAME     varchar(64) not null comment 'pod name',
    GMT_MODIFIED bigint      not null comment '修改时间',
    APP_NAME     varchar(32) not null comment 'app name',
    DATA         varchar(128)
    );
-- comment '容器java进程jvm监控信息';
create index if not exists jvm_monitor_metric_data_appName_index
    on `tmaestro-lite`.jvm_monitor_metric_data (APP_NAME);
create index if not exists jvm_monitor_metric_data_podName_index
    on `tmaestro-lite`.jvm_monitor_metric_data (POD_NAME);
create index if not exists jvm_monitor_metric_data_gmtModified_index
    on `tmaestro-lite`.jvm_monitor_metric_data (GMT_MODIFIED);


create table if not exists `tmaestro-lite`.container_statistics
(
    POD_NAME     varchar(64) not null comment 'pod name',
    GMT_MODIFIED bigint      not null comment '修改时间',
    APP_ID       bigint      not null comment 'app id',
    CONTAINER_ID varchar(64) not null comment 'container Id',
    DATA         varchar(128)
    );
-- comment '容器系统统计信息';
create index if not exists container_statistics_containerId_index
    on `tmaestro-lite`.container_statistics (CONTAINER_ID);
create index if not exists container_statistics_appId_index
    on `tmaestro-lite`.container_statistics (APP_ID);
create index if not exists container_statistics_info_podName_index
    on `tmaestro-lite`.container_statistics (POD_NAME);
create index if not exists container_statistics_info_gmtModified_index
    on `tmaestro-lite`.container_statistics (GMT_MODIFIED);


create table if not exists `tmaestro-lite`.container_process_info
(
    POD_NAME     varchar(64) not null comment 'pod name',
    GMT_MODIFIED bigint      not null comment '修改时间',
    APP_ID       bigint      not null comment 'app id',
    CONTAINER_ID varchar(64) not null comment 'container Id',
    DATA         varchar(128)
    );
-- comment 'container process info';
create index if not exists container_process_info_containerId_index
    on `tmaestro-lite`.container_process_info (CONTAINER_ID);
create index if not exists container_process_info_appId_index
    on `tmaestro-lite`.container_process_info (APP_ID);
create index if not exists container_process_info_podName_index
    on `tmaestro-lite`.container_process_info (POD_NAME);
create index if not exists container_process_info_gmtModified_index
    on `tmaestro-lite`.container_process_info (GMT_MODIFIED);

create table if not exists `tmaestro-lite`.twatch_info
(
    CONTAINER_ID   varchar(128) not null comment 'cantainer Id',
    CONTAINER_NAME varchar(64)  not null comment '容器名称',
    NAMESPACE      varchar(64)  null comment '容器所在namespace',
    POD_NAME       varchar(64)  not null comment 'pod name',
    AGENT_NAME     varchar(64)  not null comment 'twatch demonset pod name',
    GMT_MODIFIED   bigint       not null comment '修改时间',
    DT_PERIOD      bigint       not null comment '按天分区',
    NODE_NAME      varchar(64)  null comment 'node name',
    NODE_IP        varchar(64)  null comment 'node ip'
    );
-- comment 'daemonSet 采集容器元数据';
create index if not exists twatch_info_containerId_index
    on `tmaestro-lite`.twatch_info (CONTAINER_ID);

create index if not exists twatch_info_containerName_index
    on `tmaestro-lite`.twatch_info (CONTAINER_NAME);

create index if not exists twatch_info_AGENT_NAME_index
    on `tmaestro-lite`.twatch_info (AGENT_NAME);

create table if not exists `tmaestro-lite`.meter_metric_info
(
    METRIC_NAME   varchar(48)  not null comment 'pod name',
    APP_ID        bigint       not null comment 'app id',
    APP_NAME      varchar(48)  not null comment 'app name',
    GMT_CREATED   bigint       not null comment '创建时间',
    DATA          varchar(256) not null comment 'data',
    DT            bigint       not null comment '天分区',
    METRIC_VENDOR varchar(48)  not null comment 'jvm state'

    );
-- comment '容器java进程jvm监控信息';
create index if not exists meter_metric_info_appId_metricName_dt_index
    on `tmaestro-lite`.meter_metric_info (APP_ID, METRIC_NAME, DT);


create table if not exists `tmaestro-lite`.threadpool_monitor_metric_data
(
    id    int auto_increment comment '唯一ID;唯一ID' primary key,
    HOST_NAME     varchar(128) not null comment 'host name',
    PERIOD      int      not null comment '日期',
    ACTIVE_COUNT     int null comment 'activeCount',
    APP_NAME     varchar(128) null comment 'appName',
    BLOCK_QUEUE     int null comment 'blockQueue',
    COMPLETED_TASK_COUNT     int null comment 'completedTaskCount',
    CORE_POOL_SIZE    int null comment 'corePoolSize',
    DT     int null comment 'dt',
    IDLE_POOL_SIZE     int null comment 'idlePoolSize',
    KEEP_ALIVE_TIME     int null comment 'keepAliveTime',
    LARGEST_POOL_SIZE     int null comment 'largestPoolSize',
    MAXI_MUM_POOL_SIZE     int null comment 'maximumPoolSize',
    POOL_SIZE     int null comment 'poolSize',
    REJECT_COUNT     int null comment 'rejectCount',
    TASK_COUNT     int null comment 'taskCount',
    THREAD_POOL_NAME         varchar(256)
    );
-- comment '线程监控信息';

create table if not exists `tmaestro-lite`.threadpool1_monitor_metric_data
(
    id    int auto_increment comment '唯一ID;唯一ID' primary key,
    HOST_NAME     varchar(128) not null comment 'host name',
    PERIOD      bigint      not null comment '日期',
    ACTIVE_COUNT     int null comment 'activeCount',
    APP_NAME     varchar(128) null comment 'appName',
    BLOCK_QUEUE     bigint null comment 'blockQueue',
    COMPLETED_TASK_COUNT     bigint null comment 'completedTaskCount',
    CORE_POOL_SIZE    int null comment 'corePoolSize',
    DT     bigint null comment 'dt',
    IDLE_POOL_SIZE     int null comment 'idlePoolSize',
    KEEP_ALIVE_TIME     bigint null comment 'keepAliveTime',
    LARGEST_POOL_SIZE     int null comment 'largestPoolSize',
    MAXI_MUM_POOL_SIZE     int null comment 'maximumPoolSize',
    POOL_SIZE     int null comment 'poolSize',
    REJECT_COUNT     bigint null comment 'rejectCount',
    TASK_COUNT     bigint null comment 'taskCount',
    THREAD_POOL_NAME         varchar(256)
    );
create index if not exists threadpool_monitor_metric_data_HOST_NAME_THREAD_POOL_NAME_PERIOD_index
    on `tmaestro-lite`.threadpool_monitor_metric_data (HOST_NAME, THREAD_POOL_NAME, PERIOD);


create table if not exists `tmaestro-lite`.pod_attach
(
    ID            int auto_increment comment 'primary key'
    primary key,
    ACCESS_TOKEN  varchar(256) not null comment '关联的token',
    POD_ID        int          not null comment 'pod id',
    ATTACH_STATUS varchar(128) not null comment 'attach status',
    CREATED_TIME  datetime     not null comment 'create time',
    UPDATED_TIME  datetime     not null comment 'update time'
    );

create table if not exists `tmaestro-lite`.jvm_monitor_metric (
                                                                  id    int auto_increment comment '唯一ID;唯一ID' primary key,
                                                                  cpuCount bigint,
                                                                  systemCpuLoad double,
                                                                  processCpuLoad double,
                                                                  cluster VARCHAR(255),
    period bigint,
    pod VARCHAR(255),
    dt bigint null comment '分区',
    appId INTEGER,
    app VARCHAR(255),
    eden_used double,
    eden_max double,
    eden_capacity double,
    eden_util double,
    old_used double,
    old_max double,
    old_capacity double,
    old_util double,
    meta_util double,
    meta_used double,
    meta_max double,
    meta_capacity double,
    jvm_mem_util double,
    jvm_mem_used double,
    jvm_mem_max double,
    jvm_mem_capacity double,
    system_mem_util double,
    system_mem_used double,
    system_mem_max double,
    system_mem_capacity double,
    ygc_count bigint,
    ygc_time double,
    fgc_count bigint,
    fgc_time double,
    s0c double,
    s1c double,
    s0u double,
    s1u double,
    ec double,
    eu double,
    oc double,
    ou double,
    mc double,
    mu double,
    ccsc double,
    ccsu double,
    ygc INTEGER,
    ygct double,
    fgc INTEGER,
    fgct double,
    gct double,
    ngcmn double,
    ngcmx double,
    ngc double,
    ogcmn double,
    ogcmx double,
    ogc double,
    mcmn double,
    mcmx double,
    ccsmn double,
    ccsmx double,
    codeCacheUsed bigint,
    codeCacheMax bigint,
    codeCacheUtil double
    );

