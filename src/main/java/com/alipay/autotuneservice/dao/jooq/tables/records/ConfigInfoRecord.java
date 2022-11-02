/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables.records;


import com.alipay.autotuneservice.dao.jooq.tables.ConfigInfo;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ConfigInfoRecord extends UpdatableRecordImpl<ConfigInfoRecord> implements Record11<Integer, Integer, String, String, String, String, String, String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.ID</code>. 唯一ID;唯一ID
     */
    public ConfigInfoRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.ID</code>. 唯一ID;唯一ID
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.APP_ID</code>. 关联的集群id
     */
    public ConfigInfoRecord setAppId(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.APP_ID</code>. 关联的集群id
     */
    public Integer getAppId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.AUTO_TUNE</code>. 自动调优开关 。true：开启；false关闭
     */
    public ConfigInfoRecord setAutoTune(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.AUTO_TUNE</code>. 自动调优开关 。true：开启；false关闭
     */
    public String getAutoTune() {
        return (String) get(2);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.TUNE_PRIMARY_TIME</code>. 调节约束时间
     */
    public ConfigInfoRecord setTunePrimaryTime(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.TUNE_PRIMARY_TIME</code>. 调节约束时间
     */
    public String getTunePrimaryTime() {
        return (String) get(3);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.TUNE_GROUP_CONFIG</code>. 调节分组配置
     */
    public ConfigInfoRecord setTuneGroupConfig(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.TUNE_GROUP_CONFIG</code>. 调节分组配置
     */
    public String getTuneGroupConfig() {
        return (String) get(4);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.RISK_SWITCH</code>. 智能防控开关。true:打开；false:关闭
     */
    public ConfigInfoRecord setRiskSwitch(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.RISK_SWITCH</code>. 智能防控开关。true:打开；false:关闭
     */
    public String getRiskSwitch() {
        return (String) get(5);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.AUTO_DISPATCH</code>. 托管标识。true：开启；false关闭。
     */
    public ConfigInfoRecord setAutoDispatch(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.AUTO_DISPATCH</code>. 托管标识。true：开启；false关闭。
     */
    public String getAutoDispatch() {
        return (String) get(6);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.ADVANCED_SETUP</code>. 高级设置：每批次条调参完成，会对这些配置进行检查
     */
    public ConfigInfoRecord setAdvancedSetup(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.ADVANCED_SETUP</code>. 高级设置：每批次条调参完成，会对这些配置进行检查
     */
    public String getAdvancedSetup() {
        return (String) get(7);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.TIME_ZONE</code>. 时区
     */
    public ConfigInfoRecord setTimeZone(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.TIME_ZONE</code>. 时区
     */
    public String getTimeZone() {
        return (String) get(8);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.TUNE_TIME_TAG</code>. 调节时间标识。true代表可调有时间。false代表不可调有时间。与TUNE_PRIMARY_TIME结合使用
     */
    public ConfigInfoRecord setTuneTimeTag(String value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.TUNE_TIME_TAG</code>. 调节时间标识。true代表可调有时间。false代表不可调有时间。与TUNE_PRIMARY_TIME结合使用
     */
    public String getTuneTimeTag() {
        return (String) get(9);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONFIG_INFO.OPERATE_TIME</code>. 操作时间
     */
    public ConfigInfoRecord setOperateTime(String value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONFIG_INFO.OPERATE_TIME</code>. 操作时间
     */
    public String getOperateTime() {
        return (String) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, Integer, String, String, String, String, String, String, String, String, String> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Integer, Integer, String, String, String, String, String, String, String, String, String> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return ConfigInfo.CONFIG_INFO.ID;
    }

    @Override
    public Field<Integer> field2() {
        return ConfigInfo.CONFIG_INFO.APP_ID;
    }

    @Override
    public Field<String> field3() {
        return ConfigInfo.CONFIG_INFO.AUTO_TUNE;
    }

    @Override
    public Field<String> field4() {
        return ConfigInfo.CONFIG_INFO.TUNE_PRIMARY_TIME;
    }

    @Override
    public Field<String> field5() {
        return ConfigInfo.CONFIG_INFO.TUNE_GROUP_CONFIG;
    }

    @Override
    public Field<String> field6() {
        return ConfigInfo.CONFIG_INFO.RISK_SWITCH;
    }

    @Override
    public Field<String> field7() {
        return ConfigInfo.CONFIG_INFO.AUTO_DISPATCH;
    }

    @Override
    public Field<String> field8() {
        return ConfigInfo.CONFIG_INFO.ADVANCED_SETUP;
    }

    @Override
    public Field<String> field9() {
        return ConfigInfo.CONFIG_INFO.TIME_ZONE;
    }

    @Override
    public Field<String> field10() {
        return ConfigInfo.CONFIG_INFO.TUNE_TIME_TAG;
    }

    @Override
    public Field<String> field11() {
        return ConfigInfo.CONFIG_INFO.OPERATE_TIME;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public Integer component2() {
        return getAppId();
    }

    @Override
    public String component3() {
        return getAutoTune();
    }

    @Override
    public String component4() {
        return getTunePrimaryTime();
    }

    @Override
    public String component5() {
        return getTuneGroupConfig();
    }

    @Override
    public String component6() {
        return getRiskSwitch();
    }

    @Override
    public String component7() {
        return getAutoDispatch();
    }

    @Override
    public String component8() {
        return getAdvancedSetup();
    }

    @Override
    public String component9() {
        return getTimeZone();
    }

    @Override
    public String component10() {
        return getTuneTimeTag();
    }

    @Override
    public String component11() {
        return getOperateTime();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public Integer value2() {
        return getAppId();
    }

    @Override
    public String value3() {
        return getAutoTune();
    }

    @Override
    public String value4() {
        return getTunePrimaryTime();
    }

    @Override
    public String value5() {
        return getTuneGroupConfig();
    }

    @Override
    public String value6() {
        return getRiskSwitch();
    }

    @Override
    public String value7() {
        return getAutoDispatch();
    }

    @Override
    public String value8() {
        return getAdvancedSetup();
    }

    @Override
    public String value9() {
        return getTimeZone();
    }

    @Override
    public String value10() {
        return getTuneTimeTag();
    }

    @Override
    public String value11() {
        return getOperateTime();
    }

    @Override
    public ConfigInfoRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value2(Integer value) {
        setAppId(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value3(String value) {
        setAutoTune(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value4(String value) {
        setTunePrimaryTime(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value5(String value) {
        setTuneGroupConfig(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value6(String value) {
        setRiskSwitch(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value7(String value) {
        setAutoDispatch(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value8(String value) {
        setAdvancedSetup(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value9(String value) {
        setTimeZone(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value10(String value) {
        setTuneTimeTag(value);
        return this;
    }

    @Override
    public ConfigInfoRecord value11(String value) {
        setOperateTime(value);
        return this;
    }

    @Override
    public ConfigInfoRecord values(Integer value1, Integer value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10, String value11) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ConfigInfoRecord
     */
    public ConfigInfoRecord() {
        super(ConfigInfo.CONFIG_INFO);
    }

    /**
     * Create a detached, initialised ConfigInfoRecord
     */
    public ConfigInfoRecord(Integer id, Integer appId, String autoTune, String tunePrimaryTime, String tuneGroupConfig, String riskSwitch, String autoDispatch, String advancedSetup, String timeZone, String tuneTimeTag, String operateTime) {
        super(ConfigInfo.CONFIG_INFO);

        setId(id);
        setAppId(appId);
        setAutoTune(autoTune);
        setTunePrimaryTime(tunePrimaryTime);
        setTuneGroupConfig(tuneGroupConfig);
        setRiskSwitch(riskSwitch);
        setAutoDispatch(autoDispatch);
        setAdvancedSetup(advancedSetup);
        setTimeZone(timeZone);
        setTuneTimeTag(tuneTimeTag);
        setOperateTime(operateTime);
    }
}
