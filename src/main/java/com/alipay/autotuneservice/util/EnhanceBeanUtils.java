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

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version BeanUtils.java, v 0.1 2022年04月15日 17:17 dutianze
 */
public class EnhanceBeanUtils {

    /**
     * Copy the property non-null values of the given source bean into the target bean.
     *
     * @param src    the source bean
     * @param target the target bean
     */
    public static void copyPropertiesIgnoreNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    /**
     * return null property names
     *
     * @param source source object
     * @return null properties
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {emptyNames.add(pd.getName());}
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * convert record to map with predicate
     *
     * @param from         from record
     * @param ignoreFields ignoreFields
     * @return Map<Field < ?>, Object>
     */
    public static Map<Field<?>, Object> parseRecordNonNullValueIntoMap(Record from, Field<?>... ignoreFields) {
        return parseRecordIntoMap(from, Objects::nonNull, ignoreFields);
    }

    /**
     * convert record to map with predicate
     *
     * @param from         from record
     * @param predicate    field value predicate
     * @param ignoreFields ignoreFields
     * @return Map<Field < ?>, Object>
     */
    public static Map<Field<?>, Object> parseRecordIntoMap(Record from, Predicate<Object> predicate, Field<?>... ignoreFields) {
        Field<?>[] fields = from.fields();
        Set<Field<?>> ignoreFieldsSet = Arrays.stream(ignoreFields).collect(Collectors.toSet());
        Map<Field<?>, Object> map = new HashMap<>();
        if (ArrayUtils.isEmpty(fields)) {
            return map;
        }
        Field<?>[] filteredFields = Arrays.stream(fields)
                .filter(field -> !ignoreFieldsSet.contains(field))
                .filter(field -> predicate.test(field.getValue(from)))
                .toArray(Field<?>[]::new);
        for (Field<?> field : filteredFields) {
            map.put(field, field.getValue(from));
        }
        return map;
    }
}