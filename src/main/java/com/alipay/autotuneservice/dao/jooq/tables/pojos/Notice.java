/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       id;
    private String        noticeType;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String        accept;
    private String        accessToken;
    private String        noticeStatus;

    public Notice() {}

    public Notice(Notice value) {
        this.id = value.id;
        this.noticeType = value.noticeType;
        this.createdTime = value.createdTime;
        this.updatedTime = value.updatedTime;
        this.accept = value.accept;
        this.accessToken = value.accessToken;
        this.noticeStatus = value.noticeStatus;
    }

    public Notice(
        Integer       id,
        String        noticeType,
        LocalDateTime createdTime,
        LocalDateTime updatedTime,
        String        accept,
        String        accessToken,
        String        noticeStatus
    ) {
        this.id = id;
        this.noticeType = noticeType;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.accept = accept;
        this.accessToken = accessToken;
        this.noticeStatus = noticeStatus;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NOTICE.ID</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NOTICE.ID</code>.
     */
    public Notice setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NOTICE.NOTICE_TYPE</code>.
     */
    public String getNoticeType() {
        return this.noticeType;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NOTICE.NOTICE_TYPE</code>.
     */
    public Notice setNoticeType(String noticeType) {
        this.noticeType = noticeType;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NOTICE.CREATED_TIME</code>.
     */
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NOTICE.CREATED_TIME</code>.
     */
    public Notice setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NOTICE.UPDATED_TIME</code>.
     */
    public LocalDateTime getUpdatedTime() {
        return this.updatedTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NOTICE.UPDATED_TIME</code>.
     */
    public Notice setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NOTICE.ACCEPT</code>.
     */
    public String getAccept() {
        return this.accept;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NOTICE.ACCEPT</code>.
     */
    public Notice setAccept(String accept) {
        this.accept = accept;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NOTICE.ACCESS_TOKEN</code>.
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NOTICE.ACCESS_TOKEN</code>.
     */
    public Notice setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NOTICE.NOTICE_STATUS</code>.
     */
    public String getNoticeStatus() {
        return this.noticeStatus;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NOTICE.NOTICE_STATUS</code>.
     */
    public Notice setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Notice (");

        sb.append(id);
        sb.append(", ").append(noticeType);
        sb.append(", ").append(createdTime);
        sb.append(", ").append(updatedTime);
        sb.append(", ").append(accept);
        sb.append(", ").append(accessToken);
        sb.append(", ").append(noticeStatus);

        sb.append(")");
        return sb.toString();
    }
}