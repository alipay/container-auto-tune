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
public class NodeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       id;
    private String        nodeName;
    private String        ip;
    private String        status;
    private LocalDateTime createdTime;
    private String        nodeTags;
    private String        accessToken;
    private LocalDateTime updatedTime;

    public NodeInfo() {}

    public NodeInfo(NodeInfo value) {
        this.id = value.id;
        this.nodeName = value.nodeName;
        this.ip = value.ip;
        this.status = value.status;
        this.createdTime = value.createdTime;
        this.nodeTags = value.nodeTags;
        this.accessToken = value.accessToken;
        this.updatedTime = value.updatedTime;
    }

    public NodeInfo(
        Integer       id,
        String        nodeName,
        String        ip,
        String        status,
        LocalDateTime createdTime,
        String        nodeTags,
        String        accessToken,
        LocalDateTime updatedTime
    ) {
        this.id = id;
        this.nodeName = nodeName;
        this.ip = ip;
        this.status = status;
        this.createdTime = createdTime;
        this.nodeTags = nodeTags;
        this.accessToken = accessToken;
        this.updatedTime = updatedTime;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NODE_INFO.ID</code>. 主键ID
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NODE_INFO.ID</code>. 主键ID
     */
    public NodeInfo setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NODE_INFO.NODE_NAME</code>. 节点名称
     */
    public String getNodeName() {
        return this.nodeName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NODE_INFO.NODE_NAME</code>. 节点名称
     */
    public NodeInfo setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NODE_INFO.IP</code>. IP地址
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NODE_INFO.IP</code>. IP地址
     */
    public NodeInfo setIp(String ip) {
        this.ip = ip;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NODE_INFO.STATUS</code>. 状态;状态;包含：存活、失效
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NODE_INFO.STATUS</code>. 状态;状态;包含：存活、失效
     */
    public NodeInfo setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NODE_INFO.CREATED_TIME</code>. 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NODE_INFO.CREATED_TIME</code>. 创建时间
     */
    public NodeInfo setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NODE_INFO.NODE_TAGS</code>. 标签
     */
    public String getNodeTags() {
        return this.nodeTags;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NODE_INFO.NODE_TAGS</code>. 标签
     */
    public NodeInfo setNodeTags(String nodeTags) {
        this.nodeTags = nodeTags;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NODE_INFO.ACCESS_TOKEN</code>. 关联的token
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NODE_INFO.ACCESS_TOKEN</code>. 关联的token
     */
    public NodeInfo setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.NODE_INFO.UPDATED_TIME</code>.
     */
    public LocalDateTime getUpdatedTime() {
        return this.updatedTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.NODE_INFO.UPDATED_TIME</code>.
     */
    public NodeInfo setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NodeInfo (");

        sb.append(id);
        sb.append(", ").append(nodeName);
        sb.append(", ").append(ip);
        sb.append(", ").append(status);
        sb.append(", ").append(createdTime);
        sb.append(", ").append(nodeTags);
        sb.append(", ").append(accessToken);
        sb.append(", ").append(updatedTime);

        sb.append(")");
        return sb.toString();
    }
}
