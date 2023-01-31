/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Keys;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePipelinePhaseRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
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
public class TunePipelinePhase extends TableImpl<TunePipelinePhaseRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE</code>
     */
    public static final TunePipelinePhase TUNE_PIPELINE_PHASE = new TunePipelinePhase();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TunePipelinePhaseRecord> getRecordType() {
        return TunePipelinePhaseRecord.class;
    }

    /**
     * The column <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE.ID</code>. 主键
     */
    public final TableField<TunePipelinePhaseRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "主键");

    /**
     * The column <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE.STAGE</code>. 阶段
     */
    public final TableField<TunePipelinePhaseRecord, String> STAGE = createField(DSL.name("STAGE"), SQLDataType.VARCHAR(128).nullable(false), this, "阶段");

    /**
     * The column <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE.PIPELINE_ID</code>. pipeline id
     */
    public final TableField<TunePipelinePhaseRecord, Integer> PIPELINE_ID = createField(DSL.name("PIPELINE_ID"), SQLDataType.INTEGER, this, "pipeline id");

    /**
     * The column <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE.PIPELINE_BRANCH_ID</code>. tune_pipeline主键id
     */
    public final TableField<TunePipelinePhaseRecord, Integer> PIPELINE_BRANCH_ID = createField(DSL.name("PIPELINE_BRANCH_ID"), SQLDataType.INTEGER, this, "tune_pipeline主键id");

    /**
     * The column <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE.UPDATED_TIME</code>. 更新时间
     */
    public final TableField<TunePipelinePhaseRecord, LocalDateTime> UPDATED_TIME = createField(DSL.name("UPDATED_TIME"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "更新时间");

    /**
     * The column <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE.CREATED_TIME</code>. 创建时间
     */
    public final TableField<TunePipelinePhaseRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("CREATED_TIME"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "创建时间");

    /**
     * The column <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE.CONTEXT</code>. 上下文
     */
    public final TableField<TunePipelinePhaseRecord, String> CONTEXT = createField(DSL.name("CONTEXT"), SQLDataType.VARCHAR(1000), this, "上下文");

    private TunePipelinePhase(Name alias, Table<TunePipelinePhaseRecord> aliased) {
        this(alias, aliased, null);
    }

    private TunePipelinePhase(Name alias, Table<TunePipelinePhaseRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE</code> table reference
     */
    public TunePipelinePhase(String alias) {
        this(DSL.name(alias), TUNE_PIPELINE_PHASE);
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE</code> table reference
     */
    public TunePipelinePhase(Name alias) {
        this(alias, TUNE_PIPELINE_PHASE);
    }

    /**
     * Create a <code>TMAESTRO-LITE.TUNE_PIPELINE_PHASE</code> table reference
     */
    public TunePipelinePhase() {
        this(DSL.name("TUNE_PIPELINE_PHASE"), null);
    }

    public <O extends Record> TunePipelinePhase(Table<O> child, ForeignKey<O, TunePipelinePhaseRecord> key) {
        super(child, key, TUNE_PIPELINE_PHASE);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public Identity<TunePipelinePhaseRecord, Integer> getIdentity() {
        return (Identity<TunePipelinePhaseRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<TunePipelinePhaseRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_F7;
    }

    @Override
    public List<UniqueKey<TunePipelinePhaseRecord>> getKeys() {
        return Arrays.<UniqueKey<TunePipelinePhaseRecord>>asList(Keys.CONSTRAINT_F7);
    }

    @Override
    public TunePipelinePhase as(String alias) {
        return new TunePipelinePhase(DSL.name(alias), this);
    }

    @Override
    public TunePipelinePhase as(Name alias) {
        return new TunePipelinePhase(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TunePipelinePhase rename(String name) {
        return new TunePipelinePhase(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TunePipelinePhase rename(Name name) {
        return new TunePipelinePhase(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<Integer, String, Integer, Integer, LocalDateTime, LocalDateTime, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
