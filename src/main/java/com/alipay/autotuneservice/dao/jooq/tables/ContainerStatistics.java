/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Indexes;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.ContainerStatisticsRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
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
public class ContainerStatistics extends TableImpl<ContainerStatisticsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TMAESTRO-LITE.CONTAINER_STATISTICS</code>
     */
    public static final ContainerStatistics CONTAINER_STATISTICS = new ContainerStatistics();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContainerStatisticsRecord> getRecordType() {
        return ContainerStatisticsRecord.class;
    }

    /**
     * The column <code>TMAESTRO-LITE.CONTAINER_STATISTICS.POD_NAME</code>. pod name
     */
    public final TableField<ContainerStatisticsRecord, String> POD_NAME = createField(DSL.name("POD_NAME"), SQLDataType.VARCHAR(64).nullable(false), this, "pod name");

    /**
     * The column <code>TMAESTRO-LITE.CONTAINER_STATISTICS.GMT_MODIFIED</code>. 修改时间
     */
    public final TableField<ContainerStatisticsRecord, Long> GMT_MODIFIED = createField(DSL.name("GMT_MODIFIED"), SQLDataType.BIGINT.nullable(false), this, "修改时间");

    /**
     * The column <code>TMAESTRO-LITE.CONTAINER_STATISTICS.APP_ID</code>. app id
     */
    public final TableField<ContainerStatisticsRecord, Long> APP_ID = createField(DSL.name("APP_ID"), SQLDataType.BIGINT.nullable(false), this, "app id");

    /**
     * The column <code>TMAESTRO-LITE.CONTAINER_STATISTICS.CONTAINER_ID</code>. container Id
     */
    public final TableField<ContainerStatisticsRecord, String> CONTAINER_ID = createField(DSL.name("CONTAINER_ID"), SQLDataType.VARCHAR(64).nullable(false), this, "container Id");

    /**
     * The column <code>TMAESTRO-LITE.CONTAINER_STATISTICS.DATA</code>.
     */
    public final TableField<ContainerStatisticsRecord, String> DATA = createField(DSL.name("DATA"), SQLDataType.VARCHAR(128), this, "");

    private ContainerStatistics(Name alias, Table<ContainerStatisticsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ContainerStatistics(Name alias, Table<ContainerStatisticsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.CONTAINER_STATISTICS</code> table reference
     */
    public ContainerStatistics(String alias) {
        this(DSL.name(alias), CONTAINER_STATISTICS);
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.CONTAINER_STATISTICS</code> table reference
     */
    public ContainerStatistics(Name alias) {
        this(alias, CONTAINER_STATISTICS);
    }

    /**
     * Create a <code>TMAESTRO-LITE.CONTAINER_STATISTICS</code> table reference
     */
    public ContainerStatistics() {
        this(DSL.name("CONTAINER_STATISTICS"), null);
    }

    public <O extends Record> ContainerStatistics(Table<O> child, ForeignKey<O, ContainerStatisticsRecord> key) {
        super(child, key, CONTAINER_STATISTICS);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CONTAINER_STATISTICS_APPID_INDEX, Indexes.CONTAINER_STATISTICS_CONTAINERID_INDEX, Indexes.CONTAINER_STATISTICS_INFO_GMTMODIFIED_INDEX, Indexes.CONTAINER_STATISTICS_INFO_PODNAME_INDEX);
    }

    @Override
    public ContainerStatistics as(String alias) {
        return new ContainerStatistics(DSL.name(alias), this);
    }

    @Override
    public ContainerStatistics as(Name alias) {
        return new ContainerStatistics(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ContainerStatistics rename(String name) {
        return new ContainerStatistics(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContainerStatistics rename(Name name) {
        return new ContainerStatistics(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, Long, Long, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
