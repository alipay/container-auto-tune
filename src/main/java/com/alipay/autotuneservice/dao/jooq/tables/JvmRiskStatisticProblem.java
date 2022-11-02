/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Indexes;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmRiskStatisticProblemRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
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
public class JvmRiskStatisticProblem extends TableImpl<JvmRiskStatisticProblemRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM</code>
     */
    public static final JvmRiskStatisticProblem JVM_RISK_STATISTIC_PROBLEM = new JvmRiskStatisticProblem();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<JvmRiskStatisticProblemRecord> getRecordType() {
        return JvmRiskStatisticProblemRecord.class;
    }

    /**
     * The column <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM.POD_NAME</code>. pod name
     */
    public final TableField<JvmRiskStatisticProblemRecord, String> POD_NAME = createField(DSL.name("POD_NAME"), SQLDataType.VARCHAR(64).nullable(false), this, "pod name");

    /**
     * The column <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM.APP_ID</code>. app id
     */
    public final TableField<JvmRiskStatisticProblemRecord, Long> APP_ID = createField(DSL.name("APP_ID"), SQLDataType.BIGINT.nullable(false), this, "app id");

    /**
     * The column <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM.APP</code>. app name
     */
    public final TableField<JvmRiskStatisticProblemRecord, String> APP = createField(DSL.name("APP"), SQLDataType.VARCHAR(96).nullable(false), this, "app name");

    /**
     * The column <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM.DT</code>. 天分区
     */
    public final TableField<JvmRiskStatisticProblemRecord, String> DT = createField(DSL.name("DT"), SQLDataType.VARCHAR(96).nullable(false), this, "天分区");

    /**
     * The column <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM.TIME_STAMP</code>. 修改时间
     */
    public final TableField<JvmRiskStatisticProblemRecord, Long> TIME_STAMP = createField(DSL.name("TIME_STAMP"), SQLDataType.BIGINT.nullable(false), this, "修改时间");

    /**
     * The column <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM.JVM_STATE</code>. jvm state
     */
    public final TableField<JvmRiskStatisticProblemRecord, String> JVM_STATE = createField(DSL.name("JVM_STATE"), SQLDataType.VARCHAR(48).nullable(false), this, "jvm state");

    /**
     * The column <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM.TUNE_MODE</code>. mode. e.g cost
     */
    public final TableField<JvmRiskStatisticProblemRecord, String> TUNE_MODE = createField(DSL.name("TUNE_MODE"), SQLDataType.VARCHAR(48).nullable(false), this, "mode. e.g cost");

    /**
     * The column <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM.CAL_TYPE</code>. calculate type. e.g 1-ONLINE 2-OFFLINE
     */
    public final TableField<JvmRiskStatisticProblemRecord, Long> CAL_TYPE = createField(DSL.name("CAL_TYPE"), SQLDataType.BIGINT.nullable(false), this, "calculate type. e.g 1-ONLINE 2-OFFLINE");

    private JvmRiskStatisticProblem(Name alias, Table<JvmRiskStatisticProblemRecord> aliased) {
        this(alias, aliased, null);
    }

    private JvmRiskStatisticProblem(Name alias, Table<JvmRiskStatisticProblemRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM</code> table reference
     */
    public JvmRiskStatisticProblem(String alias) {
        this(DSL.name(alias), JVM_RISK_STATISTIC_PROBLEM);
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM</code> table reference
     */
    public JvmRiskStatisticProblem(Name alias) {
        this(alias, JVM_RISK_STATISTIC_PROBLEM);
    }

    /**
     * Create a <code>TMAESTRO-LITE.JVM_RISK_STATISTIC_PROBLEM</code> table reference
     */
    public JvmRiskStatisticProblem() {
        this(DSL.name("JVM_RISK_STATISTIC_PROBLEM"), null);
    }

    public <O extends Record> JvmRiskStatisticProblem(Table<O> child, ForeignKey<O, JvmRiskStatisticProblemRecord> key) {
        super(child, key, JVM_RISK_STATISTIC_PROBLEM);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.JVM_RISK_STATISTIC_PROBLEM_APPID_TIMESTAMP_INDEX);
    }

    @Override
    public JvmRiskStatisticProblem as(String alias) {
        return new JvmRiskStatisticProblem(DSL.name(alias), this);
    }

    @Override
    public JvmRiskStatisticProblem as(Name alias) {
        return new JvmRiskStatisticProblem(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public JvmRiskStatisticProblem rename(String name) {
        return new JvmRiskStatisticProblem(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public JvmRiskStatisticProblem rename(Name name) {
        return new JvmRiskStatisticProblem(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<String, Long, String, String, Long, String, String, Long> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
