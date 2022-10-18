/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Keys;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmTuningRiskCenterRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row11;
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
public class JvmTuningRiskCenter extends TableImpl<JvmTuningRiskCenterRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>tmaestro-lite.jvm_tuning_risk_center</code>
     */
    public static final JvmTuningRiskCenter JVM_TUNING_RISK_CENTER = new JvmTuningRiskCenter();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<JvmTuningRiskCenterRecord> getRecordType() {
        return JvmTuningRiskCenterRecord.class;
    }

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.id</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.appId</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, Integer> APPID = createField(DSL.name("appId"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.app</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, String> APP = createField(DSL.name("app"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.metric</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, String> METRIC = createField(DSL.name("metric"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.model</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, String> MODEL = createField(DSL.name("model"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.dt</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, String> DT = createField(DSL.name("dt"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.sucess</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, String> SUCESS = createField(DSL.name("sucess"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.info</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, String> INFO = createField(DSL.name("info"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.lowline</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, Double> LOWLINE = createField(DSL.name("lowline"), SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.upline</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, Double> UPLINE = createField(DSL.name("upline"), SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>tmaestro-lite.jvm_tuning_risk_center.timestamp</code>.
     */
    public final TableField<JvmTuningRiskCenterRecord, LocalDateTime> TIMESTAMP = createField(DSL.name("timestamp"), SQLDataType.LOCALDATETIME(0), this, "");

    private JvmTuningRiskCenter(Name alias, Table<JvmTuningRiskCenterRecord> aliased) {
        this(alias, aliased, null);
    }

    private JvmTuningRiskCenter(Name alias, Table<JvmTuningRiskCenterRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>tmaestro-lite.jvm_tuning_risk_center</code> table reference
     */
    public JvmTuningRiskCenter(String alias) {
        this(DSL.name(alias), JVM_TUNING_RISK_CENTER);
    }

    /**
     * Create an aliased <code>tmaestro-lite.jvm_tuning_risk_center</code> table reference
     */
    public JvmTuningRiskCenter(Name alias) {
        this(alias, JVM_TUNING_RISK_CENTER);
    }

    /**
     * Create a <code>tmaestro-lite.jvm_tuning_risk_center</code> table reference
     */
    public JvmTuningRiskCenter() {
        this(DSL.name("jvm_tuning_risk_center"), null);
    }

    public <O extends Record> JvmTuningRiskCenter(Table<O> child, ForeignKey<O, JvmTuningRiskCenterRecord> key) {
        super(child, key, JVM_TUNING_RISK_CENTER);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public Identity<JvmTuningRiskCenterRecord, Long> getIdentity() {
        return (Identity<JvmTuningRiskCenterRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<JvmTuningRiskCenterRecord> getPrimaryKey() {
        return Keys.KEY_JVM_TUNING_RISK_CENTER_PRIMARY;
    }

    @Override
    public List<UniqueKey<JvmTuningRiskCenterRecord>> getKeys() {
        return Arrays.<UniqueKey<JvmTuningRiskCenterRecord>>asList(Keys.KEY_JVM_TUNING_RISK_CENTER_PRIMARY, Keys.KEY_JVM_TUNING_RISK_CENTER_JVM_TUNING_RISK_CENTER_APPID_METRIC_DT_UINDEX);
    }

    @Override
    public JvmTuningRiskCenter as(String alias) {
        return new JvmTuningRiskCenter(DSL.name(alias), this);
    }

    @Override
    public JvmTuningRiskCenter as(Name alias) {
        return new JvmTuningRiskCenter(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public JvmTuningRiskCenter rename(String name) {
        return new JvmTuningRiskCenter(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public JvmTuningRiskCenter rename(Name name) {
        return new JvmTuningRiskCenter(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<Long, Integer, String, String, String, String, String, String, Double, Double, LocalDateTime> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}