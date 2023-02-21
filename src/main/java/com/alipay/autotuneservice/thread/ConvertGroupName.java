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
package com.alipay.autotuneservice.thread;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huoyuqi
 * @version ConvertGroupName.java, v 0.1 2022年11月29日 12:25 下午 huoyuqi
 */
public class ConvertGroupName {

    private static final Pattern NAME_INDEX = Pattern.compile("(?<name>\\w+)-.*");

    private static final Pattern NAME_INDEX1 = Pattern.compile("(?<name>.*)#.*");

    private static final Pattern NAME_INDEX2 = Pattern.compile("(?<name>\\s+)\\(.*");

    /**
     * 构建数组名称
     * grpc-xx-xx  转换为 grpc
     * gc task Thread#()  转换为 gc task Thread
     */
    public static String convertName(String name) {

        //破折号
        Matcher matcher = NAME_INDEX.matcher(name);
        if (matcher.matches()) {
            return matcher.group("name");
        }

        //#号前面的
        Matcher matcher1 = NAME_INDEX1.matcher(name);
        if (matcher1.matches()) {
            return matcher1.group("name");
        }
        if (NAME_INDEX2.matcher(name).matches()) {
            return NAME_INDEX2.matcher(name).group("name");
        }
        return name;
    }

}