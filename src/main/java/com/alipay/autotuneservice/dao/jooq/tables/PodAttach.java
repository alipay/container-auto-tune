/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables;


import com.alipay.autotuneservice.dao.jooq.Keys;
import com.alipay.autotuneservice.dao.jooq.TmaestroLite;
import com.alipay.autotuneservice.dao.jooq.tables.records.PodAttachRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
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
public class PodAttach extends TableImpl<PodAttachRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TMAESTRO-LITE.POD_ATTACH</code>
     */
    public static final PodAttach POD_ATTACH = new PodAttach();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PodAttachRecord> getRecordType() {
        return PodAttachRecord.class;
    }

    /**
     * The column <code>TMAESTRO-LITE.POD_ATTACH.ID</code>. primary key
     */
    public final TableField<PodAttachRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "primary key");

    /**
     * The column <code>TMAESTRO-LITE.POD_ATTACH.ACCESS_TOKEN</code>. 关联的token
     */
    public final TableField<PodAttachRecord, String> ACCESS_TOKEN = createField(DSL.name("ACCESS_TOKEN"), SQLDataType.VARCHAR(256).nullable(false), this, "关联的token");

    /**
     * The column <code>TMAESTRO-LITE.POD_ATTACH.POD_ID</code>. pod id
     */
    public final TableField<PodAttachRecord, Integer> POD_ID = createField(DSL.name("POD_ID"), SQLDataType.INTEGER.nullable(false), this, "pod id");

    /**
     * The column <code>TMAESTRO-LITE.POD_ATTACH.ATTACH_STATUS</code>. attach status
     */
    public final TableField<PodAttachRecord, String> ATTACH_STATUS = createField(DSL.name("ATTACH_STATUS"), SQLDataType.VARCHAR(128).nullable(false), this, "attach status");

    /**
     * The column <code>TMAESTRO-LITE.POD_ATTACH.CREATED_TIME</code>. create time
     */
    public final TableField<PodAttachRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("CREATED_TIME"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "create time");

    /**
     * The column <code>TMAESTRO-LITE.POD_ATTACH.UPDATED_TIME</code>. update time
     */
    public final TableField<PodAttachRecord, LocalDateTime> UPDATED_TIME = createField(DSL.name("UPDATED_TIME"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "update time");

    private PodAttach(Name alias, Table<PodAttachRecord> aliased) {
        this(alias, aliased, null);
    }

    private PodAttach(Name alias, Table<PodAttachRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.POD_ATTACH</code> table reference
     */
    public PodAttach(String alias) {
        this(DSL.name(alias), POD_ATTACH);
    }

    /**
     * Create an aliased <code>TMAESTRO-LITE.POD_ATTACH</code> table reference
     */
    public PodAttach(Name alias) {
        this(alias, POD_ATTACH);
    }

    /**
     * Create a <code>TMAESTRO-LITE.POD_ATTACH</code> table reference
     */
    public PodAttach() {
        this(DSL.name("POD_ATTACH"), null);
    }

    public <O extends Record> PodAttach(Table<O> child, ForeignKey<O, PodAttachRecord> key) {
        super(child, key, POD_ATTACH);
    }

    @Override
    public Schema getSchema() {
        return TmaestroLite.TMAESTRO_LITE;
    }

    @Override
    public Identity<PodAttachRecord, Integer> getIdentity() {
        return (Identity<PodAttachRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<PodAttachRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_C5;
    }

    @Override
    public List<UniqueKey<PodAttachRecord>> getKeys() {
        return Arrays.<UniqueKey<PodAttachRecord>>asList(Keys.CONSTRAINT_C5);
    }

    @Override
    public PodAttach as(String alias) {
        return new PodAttach(DSL.name(alias), this);
    }

    @Override
    public PodAttach as(Name alias) {
        return new PodAttach(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PodAttach rename(String name) {
        return new PodAttach(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PodAttach rename(Name name) {
        return new PodAttach(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Integer, String, Integer, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
