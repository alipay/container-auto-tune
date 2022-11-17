/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.model.common;

/**
 * @author dutianze
 * @version NodeStatus.java, v 0.1 2022年03月10日 14:47 dutianze
 */
public enum CommandStatus {

    /**
     * 准备
     */
    INIT,
    /**
     * 待办
     */
    PENDING,
    /**
     * 存活
     */
    RUNNING,
    /**
     * 完成
     */
    FINISH,
    /**
     * 失败
     */
    FAILED,
    ;
}