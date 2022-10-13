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
package com.alipay.autotuneservice.agent.twatch;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.agent.twatch.model.ActionMethodRequest;
import com.alipay.autotuneservice.agent.twatch.model.AgentActionRequest;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import com.alipay.autotuneservice.util.SpringFactoryUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author chenqu
 * @version : AgentCallInvoke.java, v 0.1 2022年04月13日 16:44 chenqu Exp $
 */
@Slf4j
public class AgentCallInvoke {

    private ActionTemplate      template;
    private Map<String, Object> args;
    private DoInvokeRunner      doInvokeRunner;
    private CountDownLatch      countDownLatch;

    private AgentCallInvoke(BuildInvoke buildInvoke) {
        this.template = buildInvoke.getTemplate();
        this.args = buildInvoke.args;
        this.doInvokeRunner = (DoInvokeRunner) SpringFactoryUtils.getBean("doInvokeRunner");
        this.countDownLatch = new CountDownLatch(1);
    }

    public String fire() {
        try {
            AgentActionRequest agentActionRequest = new AgentActionRequest();
            //1、组建远程action
            ActionMethodRequest actionMethodRequest = new ActionMethodRequest();
            actionMethodRequest.setImportPkg(this.template.importPkg());
            actionMethodRequest.setMethodBody(this.template.methodBody());
            actionMethodRequest.setMethodName(this.template.methodName());
            Class[] classes = this.template.classTypes();
            if (classes == null) {
                //进行赋值转换
                List<Class> methodClass = Lists.newLinkedList();
                this.args.values().forEach(v -> methodClass.add(v.getClass()));
                Class[] array = new Class[methodClass.size()];
                classes = methodClass.toArray(array);
            }
            actionMethodRequest.setClassTypes(classes);
            actionMethodRequest.setMethodArgs(args.values().toArray());
            //2、返回sessionId
            String sessionId = generateUUID();
            //获取agentName
            String agentName = "";
            List<TwatchInfoDo> twatchInfoDos = Lists.newArrayList();
            if (args.containsKey(ActionTemplate.POD_NAME)) {
                String podName = (String) args.get(ActionTemplate.POD_NAME);
                twatchInfoDos = doInvokeRunner.findInfoByPod(podName);
            }
            if (args.containsKey(ActionTemplate.CONTAINER_ID)) {
                String containerId = (String) args.get(ActionTemplate.CONTAINER_ID);
                twatchInfoDos = doInvokeRunner.findInfoByContainer(containerId);
            }
            if (CollectionUtils.isNotEmpty(twatchInfoDos)) {
                agentName = twatchInfoDos.get(0).getAgentName();
            }
            if (StringUtils.isEmpty(agentName)) {
                log.warn(String.format("agentName is empty,args=[%s]", JSONObject.toJSONString(args)));
                return "";
            }
            //3、发送redis
            log.info("fire sessionId={}, agentName={}, podName={}", sessionId, agentName, args.get(ActionTemplate.POD_NAME));
            agentActionRequest.setAgentName(agentName);
            agentActionRequest.setSessionId(sessionId);
            agentActionRequest.setActionMethodRequest(actionMethodRequest);
            doInvokeRunner.invoke(agentActionRequest, countDownLatch);
            countDownLatch.await(5, TimeUnit.SECONDS);
            return sessionId;
        } catch (Exception e) {
            throw new RuntimeException("fire is error,please check!");
        }
    }

    public static class BuildInvoke {

        @Getter
        private Map<String, Object> args = new LinkedHashMap<>();
        @Getter
        private ActionTemplate      template;

        public BuildInvoke(ActionTemplate template) {
            this.template = template;
        }

        public BuildInvoke args(String key, Object arg) {
            args.put(key, arg);
            return this;
        }

        public AgentCallInvoke build() {
            return new AgentCallInvoke(this);
        }
    }

    private String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}