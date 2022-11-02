/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Indexes;
import com.alipay.autotuneservice.dao.jooq.Keys;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PodInfo extends TableImpl<PodInfoRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TMAESTRO-LITE.POD_INFO</code>
     */
    public static final PodInfo POD_INFO = new PodInfo();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PodInfoRecord> getRecordType() {
        return PodInfoRecord.class;
    }

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.ID</code>. 主键ID
     */
    public final TableField<PodInfoRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "主键ID");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.APP_ID</code>. 关联的应用id
     */
    public final TableField<PodInfoRecord, Integer> APP_ID = createField(DSL.name("APP_ID"), SQLDataType.INTEGER, this, "关联的应用id");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.NODE_ID</code>. 关联的集群id
     */
    public final TableField<PodInfoRecord, Integer> NODE_ID = createField(DSL.name("NODE_ID"), SQLDataType.INTEGER, this, "关联的集群id");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.POD_NAME</code>. 所属节点名称
     */
    public final TableField<PodInfoRecord, String> POD_NAME = createField(DSL.name("POD_NAME"), SQLDataType.VARCHAR(255).nullable(false), this, "所属节点名称");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.IP</code>. IP地址
     */
    public final TableField<PodInfoRecord, String> IP = createField(DSL.name("IP"), SQLDataType.VARCHAR(255), this, "IP地址");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.STATUS</code>. 状态;状态;包含：存活、失效
     */
    public final TableField<PodInfoRecord, String> STATUS = createField(DSL.name("STATUS"), SQLDataType.VARCHAR(255), this, "状态;状态;包含：存活、失效");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.CREATED_TIME</code>. 创建时间
     */
    public final TableField<PodInfoRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("CREATED_TIME"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "创建时间");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.POD_JVM</code>. 生效jvm配置
     */
    public final TableField<PodInfoRecord, String> POD_JVM = createField(DSL.name("POD_JVM"), SQLDataType.VARCHAR(4000), this, "生效jvm配置");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.ENV</code>. 环境变量
     */
    public final TableField<PodInfoRecord, String> ENV = createField(DSL.name("ENV"), SQLDataType.VARCHAR(4000), this, "环境变量");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.POD_DEPLOY_TYPE</code>. pod部署类型
     */
    public final TableField<PodInfoRecord, String> POD_DEPLOY_TYPE = createField(DSL.name("POD_DEPLOY_TYPE"), SQLDataType.VARCHAR(255), this, "pod部署类型");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.POD_TEMPLATE</code>. pod基础模板信息;pod部署类型：4C8G
     */
    public final TableField<PodInfoRecord, String> POD_TEMPLATE = createField(DSL.name("POD_TEMPLATE"), SQLDataType.VARCHAR(255), this, "pod基础模板信息;pod部署类型：4C8G");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.POD_TAGS</code>. 标签
     */
    public final TableField<PodInfoRecord, String> POD_TAGS = createField(DSL.name("POD_TAGS"), SQLDataType.VARCHAR(4000), this, "标签");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.ACCESS_TOKEN</code>. 关联的token
     */
    public final TableField<PodInfoRecord, String> ACCESS_TOKEN = createField(DSL.name("ACCESS_TOKEN"), SQLDataType.VARCHAR(255).nullable(false), this, "关联的token");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.CLUSTER_NAME</code>. k8s集群名
     */
    public final TableField<PodInfoRecord, String> CLUSTER_NAME = createField(DSL.name("CLUSTER_NAME"), SQLDataType.VARCHAR(128), this, "k8s集群名");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.K8S_NAMESPACE</code>. k8s命名空间
     */
    public final TableField<PodInfoRecord, String> K8S_NAMESPACE = createField(DSL.name("K8S_NAMESPACE"), SQLDataType.VARCHAR(512), this, "k8s命名空间");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.POD_STATUS</code>. 状态;状态;包含：存活、失效
     */
    public final TableField<PodInfoRecord, String> POD_STATUS = createField(DSL.name("POD_STATUS"), SQLDataType.VARCHAR(255), this, "状态;状态;包含：存活、失效");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.UPDATED_TIME</code>.
     */
    public final TableField<PodInfoRecord, LocalDateTime> UPDATED_TIME = createField(DSL.name("UPDATED_TIME"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.CPU_CORE_LIMIT</code>. cpu core limit
     */
    public final TableField<PodInfoRecord, Integer> CPU_CORE_LIMIT = createField(DSL.name("CPU_CORE_LIMIT"), SQLDataType.INTEGER, this, "cpu core limit");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.MEM_LIMIT</code>. memory limit
     */
    public final TableField<PodInfoRecord, Integer> MEM_LIMIT = createField(DSL.name("MEM_LIMIT"), SQLDataType.INTEGER, this, "memory limit");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.CPU_LIMIT</code>. cpu limit
     */
    public final TableField<PodInfoRecord, String> CPU_LIMIT = createField(DSL.name("CPU_LIMIT"), SQLDataType.CLOB, this, "cpu limit");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.AGENT_INSTALL</code>. 是否安装autotuneagent.
1 - 安装
0 - 未安装
     */
    public final TableField<PodInfoRecord, Integer> AGENT_INSTALL = createField(DSL.name("AGENT_INSTALL"), SQLDataType.INTEGER.defaultValue(DSL.field("0", SQLDataType.INTEGER)), this, "是否安装autotuneagent.\n1 - 安装\n0 - 未安装");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.D_HOSTNAME</code>.
     */
    public final TableField<PodInfoRecord, String> D_HOSTNAME = createField(DSL.name("D_HOSTNAME"), SQLDataType.VARCHAR(200), this, "");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.NODE_IP</code>.
     */
    public final TableField<PodInfoRecord, String> NODE_IP = createField(DSL.name("NODE_IP"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>TMAESTRO-LITE.POD_INFO.NODE_NAME</code>.
     */
    public final TableField<PodInfoRecord, String> NODE_NAME = createField(DSL.name("NODE_NAME"), SQLDataType.VARCHAR(255), this, "");

    private PodInfo(Name alias, Table<PodInfoRecord> aliased) {
        this(alias, aliased, null);
    }

    private PodInfo(Name alias, Table<PodInfoRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.POD_INFO</code> table reference
     */
    public PodInfo(String alias) {
        this(DSL.name(alias), POD_INFO);
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.POD_INFO</code> table reference
     */
    public PodInfo(Name alias) {
        this(alias, POD_INFO);
    }

    /**
     * Create a <code>TMAESTRO-LITE.POD_INFO</code> table reference
     */
    public PodInfo() {
        this(DSL.name("POD_INFO"), null);
    }

    public <O extends Record> PodInfo(Table<O> child, ForeignKey<O, PodInfoRecord> key) {
        super(child, key, POD_INFO);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.POD_INFO_APP_ID_AGENT_INSTALL_INDEX, Indexes.POD_INFO_APP_ID_POD_STATUS_INDEX);
    }

    @Override
    public Identity<PodInfoRecord, Integer> getIdentity() {
        return (Identity<PodInfoRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<PodInfoRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_4;
    }

    @Override
    public List<UniqueKey<PodInfoRecord>> getKeys() {
        return Arrays.<UniqueKey<PodInfoRecord>>asList(Keys.CONSTRAINT_4, Keys.POD_INFO_POD_NAME_ACCESS_TOKEN_UINDEX);
    }

    @Override
    public PodInfo as(String alias) {
        return new PodInfo(DSL.name(alias), this);
    }

    @Override
    public PodInfo as(Name alias) {
        return new PodInfo(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PodInfo rename(String name) {
        return new PodInfo(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PodInfo rename(Name name) {
        return new PodInfo(name, null);
    }
}
