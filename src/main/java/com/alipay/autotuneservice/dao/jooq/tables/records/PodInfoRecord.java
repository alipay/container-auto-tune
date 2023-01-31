/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.dao.jooq.tables.records;


import com.alipay.autotuneservice.dao.jooq.tables.PodInfo;

import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PodInfoRecord extends UpdatableRecordImpl<PodInfoRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.ID</code>. 主键ID
     */
    public PodInfoRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.ID</code>. 主键ID
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.APP_ID</code>. 关联的应用id
     */
    public PodInfoRecord setAppId(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.APP_ID</code>. 关联的应用id
     */
    public Integer getAppId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.NODE_ID</code>. 关联的集群id
     */
    public PodInfoRecord setNodeId(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.NODE_ID</code>. 关联的集群id
     */
    public Integer getNodeId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.POD_NAME</code>. 所属节点名称
     */
    public PodInfoRecord setPodName(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.POD_NAME</code>. 所属节点名称
     */
    public String getPodName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.IP</code>. IP地址
     */
    public PodInfoRecord setIp(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.IP</code>. IP地址
     */
    public String getIp() {
        return (String) get(4);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.STATUS</code>. 状态;状态;包含：存活、失效
     */
    public PodInfoRecord setStatus(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.STATUS</code>. 状态;状态;包含：存活、失效
     */
    public String getStatus() {
        return (String) get(5);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.CREATED_TIME</code>. 创建时间
     */
    public PodInfoRecord setCreatedTime(LocalDateTime value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.CREATED_TIME</code>. 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.POD_JVM</code>. 生效jvm配置
     */
    public PodInfoRecord setPodJvm(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.POD_JVM</code>. 生效jvm配置
     */
    public String getPodJvm() {
        return (String) get(7);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.ENV</code>. 环境变量
     */
    public PodInfoRecord setEnv(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.ENV</code>. 环境变量
     */
    public String getEnv() {
        return (String) get(8);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.POD_DEPLOY_TYPE</code>. pod部署类型
     */
    public PodInfoRecord setPodDeployType(String value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.POD_DEPLOY_TYPE</code>. pod部署类型
     */
    public String getPodDeployType() {
        return (String) get(9);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.POD_TEMPLATE</code>. pod基础模板信息;pod部署类型：4C8G
     */
    public PodInfoRecord setPodTemplate(String value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.POD_TEMPLATE</code>. pod基础模板信息;pod部署类型：4C8G
     */
    public String getPodTemplate() {
        return (String) get(10);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.POD_TAGS</code>. 标签
     */
    public PodInfoRecord setPodTags(String value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.POD_TAGS</code>. 标签
     */
    public String getPodTags() {
        return (String) get(11);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.ACCESS_TOKEN</code>. 关联的token
     */
    public PodInfoRecord setAccessToken(String value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.ACCESS_TOKEN</code>. 关联的token
     */
    public String getAccessToken() {
        return (String) get(12);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.CLUSTER_NAME</code>. k8s集群名
     */
    public PodInfoRecord setClusterName(String value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.CLUSTER_NAME</code>. k8s集群名
     */
    public String getClusterName() {
        return (String) get(13);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.K8S_NAMESPACE</code>. k8s命名空间
     */
    public PodInfoRecord setK8sNamespace(String value) {
        set(14, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.K8S_NAMESPACE</code>. k8s命名空间
     */
    public String getK8sNamespace() {
        return (String) get(14);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.POD_STATUS</code>. 状态;状态;包含：存活、失效
     */
    public PodInfoRecord setPodStatus(String value) {
        set(15, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.POD_STATUS</code>. 状态;状态;包含：存活、失效
     */
    public String getPodStatus() {
        return (String) get(15);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.UPDATED_TIME</code>.
     */
    public PodInfoRecord setUpdatedTime(LocalDateTime value) {
        set(16, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.UPDATED_TIME</code>.
     */
    public LocalDateTime getUpdatedTime() {
        return (LocalDateTime) get(16);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.CPU_CORE_LIMIT</code>. cpu core limit
     */
    public PodInfoRecord setCpuCoreLimit(Integer value) {
        set(17, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.CPU_CORE_LIMIT</code>. cpu core limit
     */
    public Integer getCpuCoreLimit() {
        return (Integer) get(17);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.MEM_LIMIT</code>. memory limit
     */
    public PodInfoRecord setMemLimit(Integer value) {
        set(18, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.MEM_LIMIT</code>. memory limit
     */
    public Integer getMemLimit() {
        return (Integer) get(18);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.CPU_LIMIT</code>. cpu limit
     */
    public PodInfoRecord setCpuLimit(String value) {
        set(19, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.CPU_LIMIT</code>. cpu limit
     */
    public String getCpuLimit() {
        return (String) get(19);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.AGENT_INSTALL</code>. 是否安装autotuneagent.
1 - 安装
0 - 未安装
     */
    public PodInfoRecord setAgentInstall(Integer value) {
        set(20, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.AGENT_INSTALL</code>. 是否安装autotuneagent.
1 - 安装
0 - 未安装
     */
    public Integer getAgentInstall() {
        return (Integer) get(20);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.D_HOSTNAME</code>.
     */
    public PodInfoRecord setDHostname(String value) {
        set(21, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.D_HOSTNAME</code>.
     */
    public String getDHostname() {
        return (String) get(21);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.NODE_IP</code>.
     */
    public PodInfoRecord setNodeIp(String value) {
        set(22, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.NODE_IP</code>.
     */
    public String getNodeIp() {
        return (String) get(22);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.NODE_NAME</code>.
     */
    public PodInfoRecord setNodeName(String value) {
        set(23, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.NODE_NAME</code>.
     */
    public String getNodeName() {
        return (String) get(23);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.SERVER_TYPE</code>.
     */
    public PodInfoRecord setServerType(String value) {
        set(24, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.SERVER_TYPE</code>.
     */
    public String getServerType() {
        return (String) get(24);
    }

    /**
     * Setter for <code>TMAESTRO-LITE.POD_INFO.UNICODE</code>.
     */
    public PodInfoRecord setUnicode(String value) {
        set(25, value);
        return this;
    }

    /**
     * Getter for <code>TMAESTRO-LITE.POD_INFO.UNICODE</code>.
     */
    public String getUnicode() {
        return (String) get(25);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PodInfoRecord
     */
    public PodInfoRecord() {
        super(PodInfo.POD_INFO);
    }

    /**
     * Create a detached, initialised PodInfoRecord
     */
    public PodInfoRecord(Integer id, Integer appId, Integer nodeId, String podName, String ip, String status, LocalDateTime createdTime, String podJvm, String env, String podDeployType, String podTemplate, String podTags, String accessToken, String clusterName, String k8sNamespace, String podStatus, LocalDateTime updatedTime, Integer cpuCoreLimit, Integer memLimit, String cpuLimit, Integer agentInstall, String dHostname, String nodeIp, String nodeName, String serverType, String unicode) {
        super(PodInfo.POD_INFO);

        setId(id);
        setAppId(appId);
        setNodeId(nodeId);
        setPodName(podName);
        setIp(ip);
        setStatus(status);
        setCreatedTime(createdTime);
        setPodJvm(podJvm);
        setEnv(env);
        setPodDeployType(podDeployType);
        setPodTemplate(podTemplate);
        setPodTags(podTags);
        setAccessToken(accessToken);
        setClusterName(clusterName);
        setK8sNamespace(k8sNamespace);
        setPodStatus(podStatus);
        setUpdatedTime(updatedTime);
        setCpuCoreLimit(cpuCoreLimit);
        setMemLimit(memLimit);
        setCpuLimit(cpuLimit);
        setAgentInstall(agentInstall);
        setDHostname(dHostname);
        setNodeIp(nodeIp);
        setNodeName(nodeName);
        setServerType(serverType);
        setUnicode(unicode);
    }
}
