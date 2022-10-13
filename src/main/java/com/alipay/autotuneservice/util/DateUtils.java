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

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author dutianze
 * @version DateUtils.java, v 0.1 2022年02月16日 17:46 dutianze
 */
@Slf4j
public class DateUtils {

    private static final ZoneId            ZONE_ID                   = ZoneId.systemDefault();
    public static final String             CUSTOM_FORMATTER          = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter
                                                                         .ofPattern(CUSTOM_FORMATTER);

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZONE_ID).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZONE_ID).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZONE_ID).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZONE_ID).toLocalDateTime();
    }

    public static LocalDateTime parse(String dateString) {
        return CUSTOM_DATETIME_FORMATTER.parse(dateString, LocalDateTime::from);
    }

    public static String of(LocalDateTime localDateTime) {
        return localDateTime.format(CUSTOM_DATETIME_FORMATTER);
    }

    public static long asTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    public static LocalDateTime now() {
        return ZonedDateTime.now(ZONE_ID).toLocalDateTime();
    }

    public static long nowTimestamp() {
        return asTimestamp(now());
    }

    public static String formatTimestampToStr(long timestamp, DateTimeFormatter formatter) {
        return Instant.ofEpochMilli(timestamp).atZone(ZONE_ID).toLocalDateTime().format(formatter);
    }

    public static long truncate2Minute(long timestamp) {
        Date date = new Date(timestamp);
        Date truncate = org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.MINUTE);
        return truncate.getTime();
    }

    public static LocalDateTime asLocalData(Long time) {
        return LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.ofHours(8));
    }

    public static long asLocalDataWithGMT(Long time) {
        return LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.ofHours(0))
            .toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    public static long getDayHeadSecond() {
        LocalDateTime now = DateUtils.now();
        ZonedDateTime dateHead = ZonedDateTime.of(now.getYear(), now.getMonthValue(),
            now.getDayOfMonth(), 0, 0, 0, 0, ZONE_ID);
        return dateHead.toInstant().getEpochSecond();
    }

    public static long getNowDt() {
        return Long.parseLong(DateUtils.formatTimestampToStr(System.currentTimeMillis(), DateTimeFormatter.ofPattern("yyyyMMdd")));
    }
}