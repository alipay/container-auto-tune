/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Keys;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row15;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 调参记录表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TuneLogInfo extends TableImpl<TuneLogInfoRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>tmaestro-lite.tune_log_info</code>
     */
    public static final TuneLogInfo TUNE_LOG_INFO = new TuneLogInfo();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TuneLogInfoRecord> getRecordType() {
        return TuneLogInfoRecord.class;
    }

    /**
     * The column <code>tmaestro-lite.tune_log_info.ID</code>. 主键
     */
    public final TableField<TuneLogInfoRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "主键");

    /**
     * The column <code>tmaestro-lite.tune_log_info.PIPELINE_ID</code>. 流程id
     */
    public final TableField<TuneLogInfoRecord, Integer> PIPELINE_ID = createField(DSL.name("PIPELINE_ID"), SQLDataType.INTEGER, this, "流程id");

    /**
     * The column <code>tmaestro-lite.tune_log_info.APP_ID</code>. 应用id
     */
    public final TableField<TuneLogInfoRecord, Integer> APP_ID = createField(DSL.name("APP_ID"), SQLDataType.INTEGER, this, "应用id");

    /**
     * The column <code>tmaestro-lite.tune_log_info.JVM_MARKET_ID</code>. 参数id
     */
    public final TableField<TuneLogInfoRecord, Integer> JVM_MARKET_ID = createField(DSL.name("JVM_MARKET_ID"), SQLDataType.INTEGER, this, "参数id");

    /**
     * The column <code>tmaestro-lite.tune_log_info.ACTION</code>. 执行动作
     */
    public final TableField<TuneLogInfoRecord, String> ACTION = createField(DSL.name("ACTION"), SQLDataType.VARCHAR(255), this, "执行动作");

    /**
     * The column <code>tmaestro-lite.tune_log_info.CREATED_TIME</code>. 创建时间
     */
    public final TableField<TuneLogInfoRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("CREATED_TIME"), SQLDataType.LOCALDATETIME(0), this, "创建时间");

    /**
     * The column <code>tmaestro-lite.tune_log_info.CHANGET_TIME</code>. 更新时间
     */
    public final TableField<TuneLogInfoRecord, LocalDateTime> CHANGET_TIME = createField(DSL.name("CHANGET_TIME"), SQLDataType.LOCALDATETIME(0), this, "更新时间");

    /**
     * The column <code>tmaestro-lite.tune_log_info.CHANGE_POD_NAME</code>. 改变的单机
     */
    public final TableField<TuneLogInfoRecord, String> CHANGE_POD_NAME = createField(DSL.name("CHANGE_POD_NAME"), SQLDataType.VARCHAR(255), this, "改变的单机");

    /**
     * The column <code>tmaestro-lite.tune_log_info.ACTION_DESC</code>. 描述
     */
    public final TableField<TuneLogInfoRecord, String> ACTION_DESC = createField(DSL.name("ACTION_DESC"), SQLDataType.VARCHAR(255), this, "描述");

    /**
     * The column <code>tmaestro-lite.tune_log_info.ERROR_MSG</code>. 异常描述
     */
    public final TableField<TuneLogInfoRecord, String> ERROR_MSG = createField(DSL.name("ERROR_MSG"), SQLDataType.VARCHAR(255), this, "异常描述");

    /**
     * The column <code>tmaestro-lite.tune_log_info.BATCH_TOTAL_NUM</code>. 分批总机器数
     */
    public final TableField<TuneLogInfoRecord, Integer> BATCH_TOTAL_NUM = createField(DSL.name("BATCH_TOTAL_NUM"), SQLDataType.INTEGER, this, "分批总机器数");

    /**
     * The column <code>tmaestro-lite.tune_log_info.BATCH_PODS</code>. 分批机器变更详情
     */
    public final TableField<TuneLogInfoRecord, String> BATCH_PODS = createField(DSL.name("BATCH_PODS"), SQLDataType.CLOB, this, "分批机器变更详情");

    /**
     * The column <code>tmaestro-lite.tune_log_info.BATCH_RATIO</code>. 分批比例
     */
    public final TableField<TuneLogInfoRecord, Integer> BATCH_RATIO = createField(DSL.name("BATCH_RATIO"), SQLDataType.INTEGER, this, "分批比例");

    /**
     * The column <code>tmaestro-lite.tune_log_info.BATCH_NO</code>. 当前分批批次
     */
    public final TableField<TuneLogInfoRecord, Integer> BATCH_NO = createField(DSL.name("BATCH_NO"), SQLDataType.INTEGER, this, "当前分批批次");

    /**
     * The column <code>tmaestro-lite.tune_log_info.RISK_TRACE_ID</code>. 风险识别ID
     */
    public final TableField<TuneLogInfoRecord, String> RISK_TRACE_ID = createField(DSL.name("RISK_TRACE_ID"), SQLDataType.VARCHAR(255), this, "风险识别ID");

    private TuneLogInfo(Name alias, Table<TuneLogInfoRecord> aliased) {
        this(alias, aliased, null);
    }

    private TuneLogInfo(Name alias, Table<TuneLogInfoRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("调参记录表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>tmaestro-lite.tune_log_info</code> table reference
     */
    public TuneLogInfo(String alias) {
        this(DSL.name(alias), TUNE_LOG_INFO);
    }

    /**
     * Create an aliased <code>tmaestro-lite.tune_log_info</code> table reference
     */
    public TuneLogInfo(Name alias) {
        this(alias, TUNE_LOG_INFO);
    }

    /**
     * Create a <code>tmaestro-lite.tune_log_info</code> table reference
     */
    public TuneLogInfo() {
        this(DSL.name("tune_log_info"), null);
    }

    public <O extends Record> TuneLogInfo(Table<O> child, ForeignKey<O, TuneLogInfoRecord> key) {
        super(child, key, TUNE_LOG_INFO);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public Identity<TuneLogInfoRecord, Integer> getIdentity() {
        return (Identity<TuneLogInfoRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<TuneLogInfoRecord> getPrimaryKey() {
        return Keys.KEY_TUNE_LOG_INFO_PRIMARY;
    }

    @Override
    public List<UniqueKey<TuneLogInfoRecord>> getKeys() {
        return Arrays.<UniqueKey<TuneLogInfoRecord>>asList(Keys.KEY_TUNE_LOG_INFO_PRIMARY, Keys.KEY_TUNE_LOG_INFO_TUNE_LOG_INFO_ID_APP_ID_JVM_MARKET_ID_BATCH_NO_UINDEX, Keys.KEY_TUNE_LOG_INFO_TUNE_LOG_INFO_PIPELINE_ID_APP_ID_JVM_MARKET_ID_BATCH_NO_UINDEX);
    }

    @Override
    public TuneLogInfo as(String alias) {
        return new TuneLogInfo(DSL.name(alias), this);
    }

    @Override
    public TuneLogInfo as(Name alias) {
        return new TuneLogInfo(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TuneLogInfo rename(String name) {
        return new TuneLogInfo(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TuneLogInfo rename(Name name) {
        return new TuneLogInfo(name, null);
    }

    // -------------------------------------------------------------------------
    // Row15 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row15<Integer, Integer, Integer, Integer, String, LocalDateTime, LocalDateTime, String, String, String, Integer, String, Integer, Integer, String> fieldsRow() {
        return (Row15) super.fieldsRow();
    }
}