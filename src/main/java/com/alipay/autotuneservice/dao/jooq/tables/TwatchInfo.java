/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Indexes;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.TwatchInfoRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TwatchInfo extends TableImpl<TwatchInfoRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TMAESTRO-LITE.TWATCH_INFO</code>
     */
    public static final TwatchInfo TWATCH_INFO = new TwatchInfo();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TwatchInfoRecord> getRecordType() {
        return TwatchInfoRecord.class;
    }

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.CONTAINER_ID</code>. cantainer Id
     */
    public final TableField<TwatchInfoRecord, String> CONTAINER_ID = createField(DSL.name("CONTAINER_ID"), SQLDataType.VARCHAR(128).nullable(false), this, "cantainer Id");

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.CONTAINER_NAME</code>. 容器名称
     */
    public final TableField<TwatchInfoRecord, String> CONTAINER_NAME = createField(DSL.name("CONTAINER_NAME"), SQLDataType.VARCHAR(64).nullable(false), this, "容器名称");

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.NAMESPACE</code>. 容器所在namespace
     */
    public final TableField<TwatchInfoRecord, String> NAMESPACE = createField(DSL.name("NAMESPACE"), SQLDataType.VARCHAR(64), this, "容器所在namespace");

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.POD_NAME</code>. pod name
     */
    public final TableField<TwatchInfoRecord, String> POD_NAME = createField(DSL.name("POD_NAME"), SQLDataType.VARCHAR(64).nullable(false), this, "pod name");

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.AGENT_NAME</code>. twatch demonset pod name
     */
    public final TableField<TwatchInfoRecord, String> AGENT_NAME = createField(DSL.name("AGENT_NAME"), SQLDataType.VARCHAR(64).nullable(false), this, "twatch demonset pod name");

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.GMT_MODIFIED</code>. 修改时间
     */
    public final TableField<TwatchInfoRecord, Long> GMT_MODIFIED = createField(DSL.name("GMT_MODIFIED"), SQLDataType.BIGINT.nullable(false), this, "修改时间");

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.DT_PERIOD</code>. 按天分区
     */
    public final TableField<TwatchInfoRecord, Long> DT_PERIOD = createField(DSL.name("DT_PERIOD"), SQLDataType.BIGINT.nullable(false), this, "按天分区");

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.NODE_NAME</code>. node name
     */
    public final TableField<TwatchInfoRecord, String> NODE_NAME = createField(DSL.name("NODE_NAME"), SQLDataType.VARCHAR(64), this, "node name");

    /**
     * The column <code>TMAESTRO-LITE.TWATCH_INFO.NODE_IP</code>. node ip
     */
    public final TableField<TwatchInfoRecord, String> NODE_IP = createField(DSL.name("NODE_IP"), SQLDataType.VARCHAR(64), this, "node ip");

    private TwatchInfo(Name alias, Table<TwatchInfoRecord> aliased) {
        this(alias, aliased, null);
    }

    private TwatchInfo(Name alias, Table<TwatchInfoRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.TWATCH_INFO</code> table reference
     */
    public TwatchInfo(String alias) {
        this(DSL.name(alias), TWATCH_INFO);
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.TWATCH_INFO</code> table reference
     */
    public TwatchInfo(Name alias) {
        this(alias, TWATCH_INFO);
    }

    /**
     * Create a <code>TMAESTRO-LITE.TWATCH_INFO</code> table reference
     */
    public TwatchInfo() {
        this(DSL.name("TWATCH_INFO"), null);
    }

    public <O extends Record> TwatchInfo(Table<O> child, ForeignKey<O, TwatchInfoRecord> key) {
        super(child, key, TWATCH_INFO);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.TWATCH_INFO_AGENT_NAME_INDEX, Indexes.TWATCH_INFO_CONTAINERID_INDEX, Indexes.TWATCH_INFO_CONTAINERNAME_INDEX);
    }

    @Override
    public TwatchInfo as(String alias) {
        return new TwatchInfo(DSL.name(alias), this);
    }

    @Override
    public TwatchInfo as(Name alias) {
        return new TwatchInfo(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TwatchInfo rename(String name) {
        return new TwatchInfo(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TwatchInfo rename(Name name) {
        return new TwatchInfo(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<String, String, String, String, String, Long, Long, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}