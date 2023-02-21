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

import com.google.common.base.Preconditions;

import java.util.UUID;

/**
 * @author huangkaifei
 * @version : ObjectUtil.java, v 0.1 2022年05月20日 11:18 PM huangkaifei Exp $
 */
public final class ObjectUtil {

    public static Integer checkIntegerPositive(final Integer num) {
        Preconditions.checkArgument(num != null && num > 0, "Input integer number must be positive.");
        return num;
    }

    public static Integer checkIntegerPositive(final Integer num, String errMsg) {
        Preconditions.checkArgument(num != null && num > 0, errMsg);
        return num;
    }

    public static Boolean checkInteger(Integer num) {
        try {
            Preconditions.checkArgument(num != null && num > 0, "Input integer number must be positive.");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}