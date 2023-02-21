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
/********************************************************************************
* Copyright (c) 2021 Contributors to the Eclipse Foundation
*
* See the NOTICE file(s) distributed with this work for additional
* information regarding copyright ownership.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
********************************************************************************/

/**
* @plasma147 provided this solution:
* https://stackoverflow.com/a/11385215/813561
* https://creativecommons.org/licenses/by-sa/3.0/
*/

package org.eclipse.jifa.common.util;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.eclipse.jifa.common.JifaException;

import java.io.IOException;
import java.lang.reflect.Method;

public class AccessorBasedTypeAdaptor<T> extends TypeAdapter<T> {
    private Gson gson;

    public AccessorBasedTypeAdaptor(Gson gson) {
        this.gson = gson;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(JsonWriter out, T value) throws IOException {
        out.beginObject();
        for (Method method : value.getClass().getMethods()) {
            boolean nonBooleanAccessor = method.getName().startsWith("get");
            boolean booleanAccessor = method.getName().startsWith("is");
            if ((nonBooleanAccessor || booleanAccessor) && !method.getName().equals("getClass") && method.getParameterTypes().length == 0) {
                try {
                    String name = method.getName().substring(nonBooleanAccessor ? 3 : 2);
                    name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
                    Object returnValue = method.invoke(value);
                    if(returnValue != null) {
                        TypeToken<?> token = TypeToken.get(returnValue.getClass());
                        TypeAdapter adapter = gson.getAdapter(token);
                        out.name(name);
                        adapter.write(out, returnValue);
                    }
                } catch (Exception e) {
                    throw new JifaException(e);
                }
            }
        }
        out.endObject();
    }

    @Override
    public T read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Only supports writes.");
    }
}