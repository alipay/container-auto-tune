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
package com.alipay.autotuneservice.configuration;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.service.chronicmap.ChronicleMapService;
import com.alipay.autotuneservice.util.AgentConstant;
import com.auto.tune.client.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dutianze
 * @version TuneEventProducer.java, v 0.1 2022年04月06日 15:33 dutianze
 */
@Slf4j
@Component
public class ArthasProducer implements InitializingBean {

    @Autowired
    private ChronicleMapService redisClient;

    @Override
    public void afterPropertiesSet() {
        if(MapUtils.isNotEmpty(AgentConstant.ARTHAS_MAP)){
            AgentConstant.ARTHAS_MAP.forEach(
                    (k,v) -> {

                        String hostName = v.getHostName();
                        if (StringUtils.equals(v.getCommand(), AgentConstant.CHECK_ARTHAS_INSTALL)) {
                            if (AgentConstant.ARTHAS_STREAM_POOL.containsKey(hostName)) {
                                //存储
                                redisClient.set(AgentConstant.generateArthasCallBackKey(v.getUuid()), Boolean.TRUE.toString());
                                return;
                            }
                            return;
                        }
                        log.info("ArthasProducer event is {}", JSONObject.toJSONString(v));
                        if (!AgentConstant.ARTHAS_STREAM_POOL.containsKey(hostName)) {
                            return;
                        }
                        AgentConstant.ARTHAS_STREAM_POOL.get(hostName)
                                .onNext(ResponseMessage.newBuilder().setSessionId(v.getUuid()).setRspMsg(v.getCommand()).build());
                    }
            );
        }
    }


}