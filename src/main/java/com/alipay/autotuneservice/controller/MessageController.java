/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller;

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.message.TuneMessageBroker;
import com.alipay.autotuneservice.message.TuneMessageEvent;
import com.alipay.autotuneservice.model.pipeline.TuneEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangkaifei
 * @version : MessageController.java, v 0.1 2022年11月08日 8:08 PM huangkaifei Exp $
 */
@Slf4j
@NoLogin
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private TuneMessageBroker messageBroker;

    @PostMapping("/pub")
    public void pubMessage(@RequestBody TuneEvent event){
        messageBroker.pub(new TuneMessageEvent(this, event));
    }
}