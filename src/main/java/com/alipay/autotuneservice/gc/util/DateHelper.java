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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author t-rex
 * @version DateHelper.java, v 0.1 2021年12月29日 11:20 上午 t-rex
 */
public class DateHelper {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static ZonedDateTime parseDate(String dateStampAsString) {
        return ZonedDateTime.parse(dateStampAsString, DateHelper.DATE_TIME_FORMATTER);
    }

    public static String formatDate(ZonedDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

}