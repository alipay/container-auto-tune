/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.message;

import org.springframework.context.ApplicationEvent;

/**
 * @author huangkaifei
 * @version : TuneMessageEvent.java, v 0.1 2022年11月08日 7:21 PM huangkaifei Exp $
 */
public class TuneMessageEvent<TuneEvent> extends ApplicationEvent {

    private TuneEvent tuneEvent;

    public TuneMessageEvent(Object source, TuneEvent tuneEvent) {
        super(source);
        this.tuneEvent = tuneEvent;
    }

    public TuneEvent getTuneEvent(){
        return tuneEvent;
    }
}