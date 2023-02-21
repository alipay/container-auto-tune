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
package org.eclipse.jifa.gclog.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class I18nStringView {
    private String name;// should be i18n name in frontend
    private Map<String, Object> params;

    public I18nStringView(String name) {
        this.name = name;
    }

    public I18nStringView(String name, Object... params) {
        this.name = name;
        if (params.length % 2 != 0) {
            throw new RuntimeException("params number should be multiple of 2");
        }
        if (params.length == 0) {
            return;
        }
        this.params = new HashMap<>();
        for (int i = 0; i < params.length; i += 2) {
            this.params.put(params[i].toString(), params[i + 1]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        I18nStringView that = (I18nStringView) o;
        return Objects.equals(name, that.name) && Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, params);
    }
}
