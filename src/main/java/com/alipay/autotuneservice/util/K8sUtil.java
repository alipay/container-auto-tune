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

import io.fabric8.kubernetes.api.model.EnvVar;

/**
 * @author huangkaifei
 * @version : K8sUtil.java, v 0.1 2022年11月16日 8:10 PM huangkaifei Exp $
 */
public class K8sUtil {

    public static EnvVar buildEnvVar(String key, String value) {
        EnvVar envVar = new EnvVar();
        envVar.setName(key);
        envVar.setValue(value);
        return envVar;
    }
}