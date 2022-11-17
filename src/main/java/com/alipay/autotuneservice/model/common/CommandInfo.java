/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.model.common;

import lombok.Data;

/**
 * @author dutianze
 * @version AppModel.java, v 0.1 2022年03月10日 17:16 dutianze
 */
@Data
public class CommandInfo {

    private long          id;
    private String        sessionId;
    private String        command;
    private String        unionCode;
    private String        context;
    private CommandStatus commandStatus;
    private String        ruleAction;

    public void verfication() {

    }

}