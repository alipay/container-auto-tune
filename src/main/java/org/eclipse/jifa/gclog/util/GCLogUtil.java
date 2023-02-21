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

import org.eclipse.jifa.gclog.event.evnetInfo.CpuTime;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.eclipse.jifa.gclog.util.Constant.MS2S;
import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_INT;

public class GCLogUtil {
    private GCLogUtil() {
    }

    private static final long BYTE_UNIT_GAP = 1024;

    /**
     * parse a string that represents a memory size and convert into size in B
     * e.g. "10m" -> 10485760, "111 KB" -> 113664
     * do not check format, do not consider b(bit)
     */
    public static long toByte(String sizeString, long divideIfNoUnit) {
        sizeString = sizeString.toLowerCase();
        int mid;
        for (mid = 0; mid < sizeString.length(); mid++) {
            char c = sizeString.charAt(mid);
            if (!Character.isDigit(c) && c != '.') {
                break;
            }
        }

        double size = Double.parseDouble(sizeString.substring(0, mid));
        String unit = sizeString.substring(mid).trim();
        switch (unit) {
            case "b":
                return (long) size;
            case "k":
            case "kb":
                return (long) (size * BYTE_UNIT_GAP);
            case "m":
            case "mb":
                return (long) (size * BYTE_UNIT_GAP * BYTE_UNIT_GAP);
            case "g":
            case "gb":
                return (long) (size * BYTE_UNIT_GAP * BYTE_UNIT_GAP * BYTE_UNIT_GAP);
            case "t":
            case "tb":
                return (long) (size * (BYTE_UNIT_GAP * BYTE_UNIT_GAP * BYTE_UNIT_GAP * BYTE_UNIT_GAP));
            default:
                return (long) (size / divideIfNoUnit);
        }
    }

    public static long toByte(String sizeString) {
        return toByte(sizeString, BYTE_UNIT_GAP);
    }

    /**
     * find the last time and parse it into time in ms
     * e.g. "123ms" -> 123, "12s" -> 12000
     * do not check format
     */
    public static double toMillisecond(String timeString) {
        int mid;
        for (mid = 0; mid < timeString.length(); mid++) {
            char c = timeString.charAt(mid);
            if (!Character.isDigit(c) && c != '.') {
                break;
            }
        }
        double number = Double.parseDouble(timeString.substring(0, mid));
        double unit;
        switch (timeString.substring(mid)) {
            case "ns":
                unit = 1 / MS2S / MS2S;
                break;
            case "ms":
                unit = 1;
                break;
            default: // default unit is s
                unit = MS2S;
        }
        return number * unit;
    }

    /**
     * e.g. "user=0.15s sys=0.01s real=0.02s","user=0.04 sys=0.00, real=0.01 secs"
     */
    private final static Pattern CPU_TIME_PATTERN = Pattern.compile("user=(.*?)s? sys=(.*?)[s,] real=(.*?)[s ]");

    public static CpuTime parseCPUTime(String s) {
        Matcher matcher = CPU_TIME_PATTERN.matcher(s.toLowerCase());
        if (matcher.find()) {
            double user = Double.parseDouble(matcher.group(1)) * 1000;
            double sys = Double.parseDouble(matcher.group(2)) * 1000;
            double real = Double.parseDouble(matcher.group(3)) * 1000;
            return new CpuTime(user, sys, real);
        }
        return null;
    }

    /**
     * e.g.  "Concurrent Clear Claimed Marks 0.009ms", "Concurrent Clear Claimed Marks"  -> "0.009ms"
     * "Pre Evacuate Collection Set: 0.0ms"    , "Pre Evacuate Collection Set"     -> "0.0ms"
     * do not check format
     */
    public static String parseValueOfPrefix(String s, String prefix) {
        int index = prefix.length();
        // filter some signs
        int begin;
        for (begin = index; begin < s.length(); begin++) {
            char c = s.charAt(begin);
            if (c != ' ' && c != ':') {
                break;
            }
        }
        return s.substring(begin);
    }

    /**
     * e.g. "3604K->3608K(262144K)" -> [null, "3604K", "3608K", "262144K"]
     * "3604K->3608K" -> ["3604K", null, "3608K", null]
     * "3604K(262144K)->3608K(262144K)" -> ["3604K", "262144K", "3608K", "262144K"]
     * "608K(262144K)" -> [null, null, "3608", "262144"]
     * do not check format
     */
    public static String[] parseFromToString(String s) {
        String[] result = new String[4];
        String[] parts = s.split("->");
        int base = parts.length == 1 ? 2 : 0;
        for (String part : parts) {
            int indexLeftBracket = part.indexOf('(');
            if (indexLeftBracket >= 0) {
                result[base] = part.substring(0, indexLeftBracket);
                result[base + 1] = part.substring(indexLeftBracket + 1, part.length() - 1);
            } else {
                result[base] = part;
            }
            base += 2;
        }
        return result;
    }

    /**
     * e.g. "3604K->3608K(262144K)" -> [3604, 3608, 262144]
     * "3604K->3608K" -> [3604, 3608, -1]
     * "3604K(262144K)->3608K(262144K)" -> [3604, 3608, 262144] do not record size before change
     * "608K(262144K)" -> [-1, 3608, 262144]
     * do not check format
     */
    public static long[] parseMemorySizeFromTo(String s, long divideIfNoUnit) {
        long[] result = new long[3];
        String[] parts = parseFromToString(s);
        result[0] = parts[0] == null ? UNKNOWN_INT : toByte(parts[0], divideIfNoUnit);
        result[1] = parts[2] == null ? UNKNOWN_INT : toByte(parts[2], divideIfNoUnit);
        result[2] = parts[3] == null ? UNKNOWN_INT : toByte(parts[3], divideIfNoUnit);
        return result;
    }

    public static long[] parseMemorySizeFromTo(String s) {
        return parseMemorySizeFromTo(s, BYTE_UNIT_GAP);
    }

    // e.g "  Pause Young (Normal) (G1 Evacuation Pause)   " -> ["Pause", "Young", "Normal", "(G1", "Evacuation", "Pause)"]
    public static String[] splitBySpace(String s) {
        List<String> list = new ArrayList<>();
        int index = 0;
        while (index < s.length()) {
            int left = index;
            while (left < s.length() && s.charAt(left) == ' ') {
                left++;
            }
            if (left >= s.length()) {
                break;
            }
            int right = left + 1;
            while (right < s.length() && s.charAt(right) != ' ') {
                right++;
            }
            list.add(s.substring(left, right));
            index = right;
        }
        return list.toArray(new String[0]);
    }

    // e.g "   Pause Young (Normal) (G1 Evacuation Pause)   " -> ["Pause Young", "Normal", "G1 Evacuation Pause"]
    public static String[] splitByBracket(String s) {
        List<String> list = new ArrayList<>();
        int index = 0;
        while (index < s.length()) {
            int indexLeftBracket = s.indexOf('(', index);
            int indexRightBracket = s.indexOf(')', index);
            int nextIndex;
            if (indexLeftBracket < 0) {
                nextIndex = indexRightBracket;
            } else if (indexRightBracket < 0) {
                nextIndex = indexLeftBracket;
            } else {
                nextIndex = Math.min(indexLeftBracket, indexRightBracket);
            }
            if (nextIndex < 0) {
                nextIndex = s.length();
            }
            String word = s.substring(index, nextIndex).trim();
            if (word.length() > 0) {
                list.add(word);
            }
            index = nextIndex + 1;
        }
        return list.toArray(new String[0]);
    }

    // e.g.  Pause Young (Normal) (System.gc())
    //                    input->|           |<-output
    public static int nextBalancedRightBracket(String text, int leftBracketIndex) {
        int balance = 1;
        for (int i = leftBracketIndex + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }
            if (balance == 0) {
                return i;
            }
        }
        return -1;
    }

    // return true if text starting from index is equal to pattern
    public static boolean stringSubEquals(String text, int index, String pattern) {
        if (text.length() < pattern.length() + index) {
            return false;
        }
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(i + index) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static String stringSubEqualsAny(String text, int index, String[] searchStrings) {
        for (String searchString : searchStrings) {
            if (stringSubEquals(text, index, searchString)) {
                return searchString;
            }
        }
        return null;
    }

    public static boolean isDatestamp(String text) {
        return isDatestamp(text, 0);
    }

    public static final int DATESTAMP_LENGTH = "2021-11-24T23:23:44.225-0800".length();

    // we don't check strictly for efficiency
    public static boolean isDatestamp(String text, int index) {
        return text.length() - index >= DATESTAMP_LENGTH &&// check range
                text.charAt(index + 4) == '-' &&
                text.charAt(index + 7) == '-' &&
                text.charAt(index + 10) == 'T' &&
                text.charAt(index + 13) == ':' &&
                text.charAt(index + 19) == '.';
    }

    public static long parseDateStamp(String text) {
        // need an additional ':' so that datetime can be correctly parsed
        text = text.substring(0, text.length() - 2) + ":" + text.substring(text.length() - 2);
        OffsetDateTime odt = OffsetDateTime.parse(text);
        return odt.toInstant().toEpochMilli();
    }

    // check text is a decimal from index and there are digitNumberAfterDot after dot
    // return end index if matching, else -1
    public static int isDecimal(String text, int index, int digitNumberAfterDot) {
        for (int i = index; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            }
            if (c != '.') {
                return -1;
            }
            for (int j = 1; j <= digitNumberAfterDot; j++) {
                if (!Character.isDigit(text.charAt(i + j))) {
                    return -1;
                }
            }
            return i + digitNumberAfterDot + 1;
        }
        return -1;
    }

    public static double convert2Double(Object object){
        if(object instanceof Long){
            return ((Long) object).doubleValue();
        }
        if(object instanceof String){
            return Double.parseDouble((String) object);
        }

        return (double)object;
    }

}
