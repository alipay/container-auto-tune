/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * This file is generated by jOOQ.
 */
package com.alipay.autotuneservice.agent.twatch;

import com.alipay.autotuneservice.model.agent.AutoTuneField;

import java.util.List;

/**
 * 操作模板
 *
 * @author chenqu
 * @version : ActionTemplate.java, v 0.1 2021年12月24日 11:02 chenqu Exp $
 */
public abstract class ActionTemplate {

    protected final static String POD_NAME     = "podName";
    protected final static String CONTAINER_ID = "containerId";
    protected final static String CMD_NAME     = "cmd";

    protected <T> AutoTuneField<T> createField(String name) {
        return new AutoTuneField<>(name);
    }

    /**
     * 方法体
     *
     * @return
     */
    public abstract String methodBody();

    /**
     * 引入的jar包
     *
     * @return
     */
    public abstract List<String> importPkg();

    /**
     * 方法名
     *
     * @return
     */
    public abstract String methodName();

    /**
     * 执行类型
     *
     * @return
     */
    public abstract Class[] classTypes();
}