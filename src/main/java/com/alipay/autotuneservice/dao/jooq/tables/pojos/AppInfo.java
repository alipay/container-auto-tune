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
public class AppInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       id;
    private Integer       userId;
    private String        accessToken;
    private String        nodeIds;
    private String        appName;
    private String        appAsName;
    private String        appDesc;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String        status;
    private String        appDefaultJvm;
    private String        clusterName;
    private String        appTag;
    private String        namespace;
    private String        clusterId;
    private String        serverType;

    public AppInfo() {}

    public AppInfo(AppInfo value) {
        this.id = value.id;
        this.userId = value.userId;
        this.accessToken = value.accessToken;
        this.nodeIds = value.nodeIds;
        this.appName = value.appName;
        this.appAsName = value.appAsName;
        this.appDesc = value.appDesc;
        this.createdTime = value.createdTime;
        this.updatedTime = value.updatedTime;
        this.status = value.status;
        this.appDefaultJvm = value.appDefaultJvm;
        this.clusterName = value.clusterName;
        this.appTag = value.appTag;
        this.namespace = value.namespace;
        this.clusterId = value.clusterId;
        this.serverType = value.serverType;
    }

    public AppInfo(
        Integer       id,
        Integer       userId,
        String        accessToken,
        String        nodeIds,
        String        appName,
        String        appAsName,
        String        appDesc,
        LocalDateTime createdTime,
        LocalDateTime updatedTime,
        String        status,
        String        appDefaultJvm,
        String        clusterName,
        String        appTag,
        String        namespace,
        String        clusterId,
        String        serverType
    ) {
        this.id = id;
        this.userId = userId;
        this.accessToken = accessToken;
        this.nodeIds = nodeIds;
        this.appName = appName;
        this.appAsName = appAsName;
        this.appDesc = appDesc;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.status = status;
        this.appDefaultJvm = appDefaultJvm;
        this.clusterName = clusterName;
        this.appTag = appTag;
        this.namespace = namespace;
        this.clusterId = clusterId;
        this.serverType = serverType;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.ID</code>. ??????ID;??????ID
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.ID</code>. ??????ID;??????ID
     */
    public AppInfo setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.USER_ID</code>. ???????????????id
     */
    public Integer getUserId() {
        return this.userId;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.USER_ID</code>. ???????????????id
     */
    public AppInfo setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.ACCESS_TOKEN</code>. ?????????token
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.ACCESS_TOKEN</code>. ?????????token
     */
    public AppInfo setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.NODE_IDS</code>. ??????????????????
     */
    public String getNodeIds() {
        return this.nodeIds;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.NODE_IDS</code>. ??????????????????
     */
    public AppInfo setNodeIds(String nodeIds) {
        this.nodeIds = nodeIds;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.APP_NAME</code>. ??????????????????;??????????????????
     */
    public String getAppName() {
        return this.appName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.APP_NAME</code>. ??????????????????;??????????????????
     */
    public AppInfo setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.APP_AS_NAME</code>. ????????????
     */
    public String getAppAsName() {
        return this.appAsName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.APP_AS_NAME</code>. ????????????
     */
    public AppInfo setAppAsName(String appAsName) {
        this.appAsName = appAsName;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.APP_DESC</code>. ????????????;????????????
     */
    public String getAppDesc() {
        return this.appDesc;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.APP_DESC</code>. ????????????;????????????
     */
    public AppInfo setAppDesc(String appDesc) {
        this.appDesc = appDesc;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.CREATED_TIME</code>. ????????????;????????????
     */
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.CREATED_TIME</code>. ????????????;????????????
     */
    public AppInfo setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.UPDATED_TIME</code>. ????????????;????????????
     */
    public LocalDateTime getUpdatedTime() {
        return this.updatedTime;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.UPDATED_TIME</code>. ????????????;????????????
     */
    public AppInfo setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.STATUS</code>. ??????;??????
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.STATUS</code>. ??????;??????
     */
    public AppInfo setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.APP_DEFAULT_JVM</code>. ??????jvm????????????;??????jvm????????????
     */
    public String getAppDefaultJvm() {
        return this.appDefaultJvm;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.APP_DEFAULT_JVM</code>. ??????jvm????????????;??????jvm????????????
     */
    public AppInfo setAppDefaultJvm(String appDefaultJvm) {
        this.appDefaultJvm = appDefaultJvm;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.CLUSTER_NAME</code>. k8s?????????
     */
    public String getClusterName() {
        return this.clusterName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.CLUSTER_NAME</code>. k8s?????????
     */
    public AppInfo setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.APP_TAG</code>. ??????????????????
     */
    public String getAppTag() {
        return this.appTag;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.APP_TAG</code>. ??????????????????
     */
    public AppInfo setAppTag(String appTag) {
        this.appTag = appTag;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.NAMESPACE</code>.
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.NAMESPACE</code>.
     */
    public AppInfo setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.CLUSTER_ID</code>. ??????ID
     */
    public String getClusterId() {
        return this.clusterId;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.CLUSTER_ID</code>. ??????ID
     */
    public AppInfo setClusterId(String clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.APP_INFO.SERVER_TYPE</code>. ????????????
     */
    public String getServerType() {
        return this.serverType;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.APP_INFO.SERVER_TYPE</code>. ????????????
     */
    public AppInfo setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AppInfo (");

        sb.append(id);
        sb.append(", ").append(userId);
        sb.append(", ").append(accessToken);
        sb.append(", ").append(nodeIds);
        sb.append(", ").append(appName);
        sb.append(", ").append(appAsName);
        sb.append(", ").append(appDesc);
        sb.append(", ").append(createdTime);
        sb.append(", ").append(updatedTime);
        sb.append(", ").append(status);
        sb.append(", ").append(appDefaultJvm);
        sb.append(", ").append(clusterName);
        sb.append(", ").append(appTag);
        sb.append(", ").append(namespace);
        sb.append(", ").append(clusterId);
        sb.append(", ").append(serverType);

        sb.append(")");
        return sb.toString();
    }
}
