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
package com.alipay.autotuneservice.infrastructure.saas.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author huoyuqi
 * @version GsonUtil.java, v 0.1 2022年03月11日 5:41 下午 huoyuqi
 */
public class GsonUtil {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting().create();
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException,
                                                           JsonSyntaxException {
        return GSON.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

    public static String toJson(Object object) {
        if (object == null) {
            return "";
        }
        return GSON.toJson(object);
    }

    public static String toPrettyFormat(String jsonString) {
        JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);
        return GSON.toJson(jsonObject);
    }

    public static boolean isValidJson(String str) {
        try {
            GSON.fromJson(str, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String prettyPrintJson(String str) {
        return GSON.toJson(GSON.fromJson(str, JsonElement.class));
    }

    public static String parseJson(String str) {
        try {
            GSON.fromJson(str, JsonElement.class);
        } catch (JsonSyntaxException e) {
            return e.getCause().getMessage();
        }
        return "";
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime date, Type typeOfSrc,
                                     JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
}