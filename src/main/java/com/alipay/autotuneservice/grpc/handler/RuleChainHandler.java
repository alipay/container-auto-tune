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
package com.alipay.autotuneservice.grpc.handler;

import com.auto.tune.client.MetricsGrpcRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dutianze
 * @version RuleProcessorChain.java, v 0.1 2022年02月15日 11:58 dutianze
 */
public class RuleChainHandler implements RuleHandler {

    private final List<RuleHandler> rules = new ArrayList<>();
    private int                     index = 0;

    @Override
    public void process(MetricsGrpcRequest request, RuleProcessResponse response,
                        RuleChainHandler chainHandler) {
        if (index == rules.size()) {
            return;
        }
        RuleHandler r = rules.get(index);
        index++;
        r.process(request, response, chainHandler);
    }

    public RuleChainHandler add(RuleHandler r) {
        rules.add(r);
        return this;
    }
}