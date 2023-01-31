/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Indexes;
import com.alipay.autotuneservice.dao.jooq.Keys;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.ThreadpoolMonitorMetricDataRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row17;
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
public class ThreadpoolMonitorMetricData extends TableImpl<ThreadpoolMonitorMetricDataRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA</code>
     */
    public static final ThreadpoolMonitorMetricData THREADPOOL_MONITOR_METRIC_DATA = new ThreadpoolMonitorMetricData();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ThreadpoolMonitorMetricDataRecord> getRecordType() {
        return ThreadpoolMonitorMetricDataRecord.class;
    }

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.ID</code>. 唯一ID;唯一ID
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "唯一ID;唯一ID");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.HOST_NAME</code>. host name
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, String> HOST_NAME = createField(DSL.name("HOST_NAME"), SQLDataType.VARCHAR(128).nullable(false), this, "host name");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.PERIOD</code>. 日期
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> PERIOD = createField(DSL.name("PERIOD"), SQLDataType.INTEGER.nullable(false), this, "日期");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.ACTIVE_COUNT</code>. activeCount
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> ACTIVE_COUNT = createField(DSL.name("ACTIVE_COUNT"), SQLDataType.INTEGER, this, "activeCount");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.APP_NAME</code>. appName
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, String> APP_NAME = createField(DSL.name("APP_NAME"), SQLDataType.VARCHAR(128), this, "appName");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.BLOCK_QUEUE</code>. blockQueue
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> BLOCK_QUEUE = createField(DSL.name("BLOCK_QUEUE"), SQLDataType.INTEGER, this, "blockQueue");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.COMPLETED_TASK_COUNT</code>. completedTaskCount
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> COMPLETED_TASK_COUNT = createField(DSL.name("COMPLETED_TASK_COUNT"), SQLDataType.INTEGER, this, "completedTaskCount");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.CORE_POOL_SIZE</code>. corePoolSize
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> CORE_POOL_SIZE = createField(DSL.name("CORE_POOL_SIZE"), SQLDataType.INTEGER, this, "corePoolSize");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.DT</code>. dt
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> DT = createField(DSL.name("DT"), SQLDataType.INTEGER, this, "dt");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.IDLE_POOL_SIZE</code>. idlePoolSize
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> IDLE_POOL_SIZE = createField(DSL.name("IDLE_POOL_SIZE"), SQLDataType.INTEGER, this, "idlePoolSize");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.KEEP_ALIVE_TIME</code>. keepAliveTime
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> KEEP_ALIVE_TIME = createField(DSL.name("KEEP_ALIVE_TIME"), SQLDataType.INTEGER, this, "keepAliveTime");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.LARGEST_POOL_SIZE</code>. largestPoolSize
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> LARGEST_POOL_SIZE = createField(DSL.name("LARGEST_POOL_SIZE"), SQLDataType.INTEGER, this, "largestPoolSize");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.MAXI_MUM_POOL_SIZE</code>. maximumPoolSize
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> MAXI_MUM_POOL_SIZE = createField(DSL.name("MAXI_MUM_POOL_SIZE"), SQLDataType.INTEGER, this, "maximumPoolSize");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.POOL_SIZE</code>. poolSize
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> POOL_SIZE = createField(DSL.name("POOL_SIZE"), SQLDataType.INTEGER, this, "poolSize");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.REJECT_COUNT</code>. rejectCount
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> REJECT_COUNT = createField(DSL.name("REJECT_COUNT"), SQLDataType.INTEGER, this, "rejectCount");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.TASK_COUNT</code>. taskCount
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, Integer> TASK_COUNT = createField(DSL.name("TASK_COUNT"), SQLDataType.INTEGER, this, "taskCount");

    /**
     * The column <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA.THREAD_POOL_NAME</code>.
     */
    public final TableField<ThreadpoolMonitorMetricDataRecord, String> THREAD_POOL_NAME = createField(DSL.name("THREAD_POOL_NAME"), SQLDataType.VARCHAR(256), this, "");

    private ThreadpoolMonitorMetricData(Name alias, Table<ThreadpoolMonitorMetricDataRecord> aliased) {
        this(alias, aliased, null);
    }

    private ThreadpoolMonitorMetricData(Name alias, Table<ThreadpoolMonitorMetricDataRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA</code> table reference
     */
    public ThreadpoolMonitorMetricData(String alias) {
        this(DSL.name(alias), THREADPOOL_MONITOR_METRIC_DATA);
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA</code> table reference
     */
    public ThreadpoolMonitorMetricData(Name alias) {
        this(alias, THREADPOOL_MONITOR_METRIC_DATA);
    }

    /**
     * Create a <code>TMAESTRO-LITE.THREADPOOL_MONITOR_METRIC_DATA</code> table reference
     */
    public ThreadpoolMonitorMetricData() {
        this(DSL.name("THREADPOOL_MONITOR_METRIC_DATA"), null);
    }

    public <O extends Record> ThreadpoolMonitorMetricData(Table<O> child, ForeignKey<O, ThreadpoolMonitorMetricDataRecord> key) {
        super(child, key, THREADPOOL_MONITOR_METRIC_DATA);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.THREADPOOL_MONITOR_METRIC_DATA_HOST_NAME_THREAD_POOL_NAME_PERIOD_INDEX);
    }

    @Override
    public Identity<ThreadpoolMonitorMetricDataRecord, Integer> getIdentity() {
        return (Identity<ThreadpoolMonitorMetricDataRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<ThreadpoolMonitorMetricDataRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_D;
    }

    @Override
    public List<UniqueKey<ThreadpoolMonitorMetricDataRecord>> getKeys() {
        return Arrays.<UniqueKey<ThreadpoolMonitorMetricDataRecord>>asList(Keys.CONSTRAINT_D);
    }

    @Override
    public ThreadpoolMonitorMetricData as(String alias) {
        return new ThreadpoolMonitorMetricData(DSL.name(alias), this);
    }

    @Override
    public ThreadpoolMonitorMetricData as(Name alias) {
        return new ThreadpoolMonitorMetricData(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ThreadpoolMonitorMetricData rename(String name) {
        return new ThreadpoolMonitorMetricData(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ThreadpoolMonitorMetricData rename(Name name) {
        return new ThreadpoolMonitorMetricData(name, null);
    }

    // -------------------------------------------------------------------------
    // Row17 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row17<Integer, String, Integer, Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, String> fieldsRow() {
        return (Row17) super.fieldsRow();
    }
}
