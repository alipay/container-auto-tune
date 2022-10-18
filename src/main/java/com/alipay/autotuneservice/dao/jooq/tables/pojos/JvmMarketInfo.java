/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 调参管理表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JvmMarketInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       id;
    private LocalDateTime createdTime;
    private String        jvmConfig;
    private String        recommend;
    private String        createdBy;

    public JvmMarketInfo() {}

    public JvmMarketInfo(JvmMarketInfo value) {
        this.id = value.id;
        this.createdTime = value.createdTime;
        this.jvmConfig = value.jvmConfig;
        this.recommend = value.recommend;
        this.createdBy = value.createdBy;
    }

    public JvmMarketInfo(
        Integer       id,
        LocalDateTime createdTime,
        String        jvmConfig,
        String        recommend,
        String        createdBy
    ) {
        this.id = id;
        this.createdTime = createdTime;
        this.jvmConfig = jvmConfig;
        this.recommend = recommend;
        this.createdBy = createdBy;
    }

    /**
     * Getter for <code>tmaestro-lite.jvm_market_info.ID</code>. 主键ID
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>tmaestro-lite.jvm_market_info.ID</code>. 主键ID
     */
    public JvmMarketInfo setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>tmaestro-lite.jvm_market_info.CREATED_TIME</code>. 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    /**
     * Setter for <code>tmaestro-lite.jvm_market_info.CREATED_TIME</code>. 创建时间
     */
    public JvmMarketInfo setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    /**
     * Getter for <code>tmaestro-lite.jvm_market_info.JVM_CONFIG</code>. jvm配置
     */
    public String getJvmConfig() {
        return this.jvmConfig;
    }

    /**
     * Setter for <code>tmaestro-lite.jvm_market_info.JVM_CONFIG</code>. jvm配置
     */
    public JvmMarketInfo setJvmConfig(String jvmConfig) {
        this.jvmConfig = jvmConfig;
        return this;
    }

    /**
     * Getter for <code>tmaestro-lite.jvm_market_info.RECOMMEND</code>. 简介
     */
    public String getRecommend() {
        return this.recommend;
    }

    /**
     * Setter for <code>tmaestro-lite.jvm_market_info.RECOMMEND</code>. 简介
     */
    public JvmMarketInfo setRecommend(String recommend) {
        this.recommend = recommend;
        return this;
    }

    /**
     * Getter for <code>tmaestro-lite.jvm_market_info.CREATED_BY</code>. 创建人
     */
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * Setter for <code>tmaestro-lite.jvm_market_info.CREATED_BY</code>. 创建人
     */
    public JvmMarketInfo setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JvmMarketInfo (");

        sb.append(id);
        sb.append(", ").append(createdTime);
        sb.append(", ").append(jvmConfig);
        sb.append(", ").append(recommend);
        sb.append(", ").append(createdBy);

        sb.append(")");
        return sb.toString();
    }
}