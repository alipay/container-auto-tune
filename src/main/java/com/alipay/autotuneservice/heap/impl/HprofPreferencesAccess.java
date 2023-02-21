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
package com.alipay.autotuneservice.heap.impl;

/**
 * @author huoyuqi
 * @version HprofPreferencesAccess.java, v 0.1 2022年10月19日 11:44 上午 huoyuqi
 */
public class HprofPreferencesAccess {

    private static HprofPreferences.HprofStrictness parseStrictness(String strictness) {
        if (strictness == null) {
            return HprofPreferences.DEFAULT_STRICTNESS;
        }
        switch (strictness) {
            case "warn":
                return HprofPreferences.HprofStrictness.STRICTNESS_WARNING;
            case "permissive":
                return HprofPreferences.HprofStrictness.STRICTNESS_PERMISSIVE;
            default:
                return HprofPreferences.DEFAULT_STRICTNESS;
        }
    }

    public static void setStrictness(String strictness) {
        HprofPreferences.setStrictness(parseStrictness(strictness));
    }
}