/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContainerProcessInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String podName;
    private Long   gmtModified;
    private Long   appId;
    private String containerId;
    private String data;

    public ContainerProcessInfo() {}

    public ContainerProcessInfo(ContainerProcessInfo value) {
        this.podName = value.podName;
        this.gmtModified = value.gmtModified;
        this.appId = value.appId;
        this.containerId = value.containerId;
        this.data = value.data;
    }

    public ContainerProcessInfo(
        String podName,
        Long   gmtModified,
        Long   appId,
        String containerId,
        String data
    ) {
        this.podName = podName;
        this.gmtModified = gmtModified;
        this.appId = appId;
        this.containerId = containerId;
        this.data = data;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.POD_NAME</code>. pod name
     */
    public String getPodName() {
        return this.podName;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.POD_NAME</code>. pod name
     */
    public ContainerProcessInfo setPodName(String podName) {
        this.podName = podName;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.GMT_MODIFIED</code>. 修改时间
     */
    public Long getGmtModified() {
        return this.gmtModified;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.GMT_MODIFIED</code>. 修改时间
     */
    public ContainerProcessInfo setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.APP_ID</code>. app id
     */
    public Long getAppId() {
        return this.appId;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.APP_ID</code>. app id
     */
    public ContainerProcessInfo setAppId(Long appId) {
        this.appId = appId;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.CONTAINER_ID</code>. container Id
     */
    public String getContainerId() {
        return this.containerId;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.CONTAINER_ID</code>. container Id
     */
    public ContainerProcessInfo setContainerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.DATA</code>.
     */
    public String getData() {
        return this.data;
    }

    /**
     * Setter for <code>TMAESTRO-LITE.CONTAINER_PROCESS_INFO.DATA</code>.
     */
    public ContainerProcessInfo setData(String data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ContainerProcessInfo (");

        sb.append(podName);
        sb.append(", ").append(gmtModified);
        sb.append(", ").append(appId);
        sb.append(", ").append(containerId);
        sb.append(", ").append(data);

        sb.append(")");
        return sb.toString();
    }
}
