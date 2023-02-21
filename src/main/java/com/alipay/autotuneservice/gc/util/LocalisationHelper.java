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
package com.alipay.autotuneservice.gc.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Helperclass to support localisation.
 * @author t-rex
 * @version LocalisationHelper.java, v 0.1 2021年12月29日 12:07 下午 t-rex
 */
public class LocalisationHelper {
    private static       ResourceBundle resourceBundle;
    private static final Object[]       EMPTY_ARRAY = new Object[]{};

    /**
     * Returns localised text as result of lookup with <code>key</code>.
     *
     * @param key key to look up localised text for
     * @return localised text
     */
    public static String getString(String key) {
        return getString(key, EMPTY_ARRAY);
    }

    /**
     * Returns localised text as result of lookup with <code>key</code> using <code>values</code>
     * as parameters for the text.
     *
     * @param key key to look up localised text for
     * @param values values to be inserted into the text
     * @return localised text
     */
    public static String getString(String key, Object... values) {
        if (getBundle().containsKey(key)) {
            return MessageFormat.format(getBundle().getString(key), values);
        }
        else {
            return "\"" + key + "\" not found";
        }
    }

    private static ResourceBundle getBundle() {
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle("localStrings");
        }

        return resourceBundle;
    }
}