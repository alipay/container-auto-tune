/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Keys;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.NotifyRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
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
public class Notify extends TableImpl<NotifyRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TMAESTRO-LITE.NOTIFY</code>
     */
    public static final Notify NOTIFY = new Notify();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<NotifyRecord> getRecordType() {
        return NotifyRecord.class;
    }

    /**
     * The column <code>TMAESTRO-LITE.NOTIFY.ID</code>.
     */
    public final TableField<NotifyRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>TMAESTRO-LITE.NOTIFY.GROUP_NAME</code>.
     */
    public final TableField<NotifyRecord, String> GROUP_NAME = createField(DSL.name("GROUP_NAME"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>TMAESTRO-LITE.NOTIFY.STATUS</code>.
     */
    public final TableField<NotifyRecord, String> STATUS = createField(DSL.name("STATUS"), SQLDataType.VARCHAR(128), this, "");

    /**
     * The column <code>TMAESTRO-LITE.NOTIFY.CREATE_BY</code>.
     */
    public final TableField<NotifyRecord, String> CREATE_BY = createField(DSL.name("CREATE_BY"), SQLDataType.VARCHAR(128), this, "");

    /**
     * The column <code>TMAESTRO-LITE.NOTIFY.CREATED_TIME</code>.
     */
    public final TableField<NotifyRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("CREATED_TIME"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>TMAESTRO-LITE.NOTIFY.UPDATED_TIME</code>.
     */
    public final TableField<NotifyRecord, LocalDateTime> UPDATED_TIME = createField(DSL.name("UPDATED_TIME"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>TMAESTRO-LITE.NOTIFY.CONTEXT</code>.
     */
    public final TableField<NotifyRecord, String> CONTEXT = createField(DSL.name("CONTEXT"), SQLDataType.VARCHAR(2048), this, "");

    /**
     * The column <code>TMAESTRO-LITE.NOTIFY.ACCESS_TOKEN</code>.
     */
    public final TableField<NotifyRecord, String> ACCESS_TOKEN = createField(DSL.name("ACCESS_TOKEN"), SQLDataType.VARCHAR(256), this, "");

    private Notify(Name alias, Table<NotifyRecord> aliased) {
        this(alias, aliased, null);
    }

    private Notify(Name alias, Table<NotifyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.NOTIFY</code> table reference
     */
    public Notify(String alias) {
        this(DSL.name(alias), NOTIFY);
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.NOTIFY</code> table reference
     */
    public Notify(Name alias) {
        this(alias, NOTIFY);
    }

    /**
     * Create a <code>TMAESTRO-LITE.NOTIFY</code> table reference
     */
    public Notify() {
        this(DSL.name("NOTIFY"), null);
    }

    public <O extends Record> Notify(Table<O> child, ForeignKey<O, NotifyRecord> key) {
        super(child, key, NOTIFY);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public Identity<NotifyRecord, Integer> getIdentity() {
        return (Identity<NotifyRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<NotifyRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_899;
    }

    @Override
    public List<UniqueKey<NotifyRecord>> getKeys() {
        return Arrays.<UniqueKey<NotifyRecord>>asList(Keys.CONSTRAINT_899);
    }

    @Override
    public Notify as(String alias) {
        return new Notify(DSL.name(alias), this);
    }

    @Override
    public Notify as(Name alias) {
        return new Notify(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Notify rename(String name) {
        return new Notify(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Notify rename(Name name) {
        return new Notify(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Integer, String, String, String, LocalDateTime, LocalDateTime, String, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}