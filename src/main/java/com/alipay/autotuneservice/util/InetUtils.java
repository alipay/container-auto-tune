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
package com.alipay.autotuneservice.util;

import com.alipay.autotuneservice.controller.model.meter.ValidateMeterResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author huangkaifei
 * @version : InetUtils.java, v 0.1 2022年08月23日 11:37 AM huangkaifei Exp $
 */
@Slf4j
public class InetUtils {

    public static ValidateMeterResult isReachable(String serverAddress) {
        try {
            if (StringUtils.isEmpty(serverAddress)) {
                return ValidateMeterResult.builder().message("serverAddress is empty, pls check.")
                    .build();
            }
            InetAddress byName = InetAddress.getByName(serverAddress);
            System.out.println(byName.isReachable(3000));
            return ValidateMeterResult.builder().success(true).build();
        } catch (IOException e) {
            System.out.println("sss " + e.getMessage());
            log.error("serverAddress {} is unReachable, errMsg={}", serverAddress, e);
            return ValidateMeterResult.builder().message(e.getMessage()).build();
        }
    }
}