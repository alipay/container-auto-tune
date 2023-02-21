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

import org.eclipse.core.runtime.Platform;
import org.eclipse.mat.hprof.HprofPlugin;

/**
 * @author huoyuqi
 * @version HprofPreferences.java, v 0.1 2022年10月19日 11:44 上午 huoyuqi
 */
public class HprofPreferences {

    public static final String STRICTNESS_PREF = "hprofStrictness"; //$NON-NLS-1$

    public static final HprofStrictness DEFAULT_STRICTNESS = HprofStrictness.STRICTNESS_STOP;

    public static final String ADDITIONAL_CLASS_REFERENCES = "hprofAddClassRefs"; //$NON-NLS-1$

    public static ThreadLocal<HprofStrictness> TL = new ThreadLocal<>();

    public static void setStrictness(HprofStrictness strictness) {
        TL.set(strictness);
    }

    public static HprofStrictness getCurrentStrictness() {
       HprofStrictness strictness = TL.get();
        return strictness != null ? strictness : DEFAULT_STRICTNESS;
    }

    public static boolean useAdditionalClassReferences() {
        return Platform.getPreferencesService().getBoolean(HprofPlugin.getDefault().getBundle().getSymbolicName(),
                HprofPreferences.ADDITIONAL_CLASS_REFERENCES, false, null);
    }

    public enum HprofStrictness {
        STRICTNESS_STOP("hprofStrictnessStop"), //$NON-NLS-1$

        STRICTNESS_WARNING("hprofStrictnessWarning"), //$NON-NLS-1$

        STRICTNESS_PERMISSIVE("hprofStrictnessPermissive"); //$NON-NLS-1$

        private final String name;

        HprofStrictness(String name) {
            this.name = name;
        }

        public static HprofStrictness parse(String value) {
            if (value != null && value.length() > 0) {
                for (HprofStrictness strictness : values()) {
                    if (strictness.toString().equals(value)) {
                        return strictness;
                    }
                }
            }
            return DEFAULT_STRICTNESS;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}