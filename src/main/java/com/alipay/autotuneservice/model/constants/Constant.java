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
package com.alipay.autotuneservice.model.constants;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author huangkaifei
 * @version : Constant.java, v 0.1 2022年12月08日 3:41 PM huangkaifei Exp $
 */
public class Constant {
    public static final String      ARTHAS_JAR_FILE_NAME = "arthas-bin.tar.gz";
    public static final Set<String> ARTHAS_CMD_WHITE_SET = Sets.newHashSet("help", "auth", "keymap", "sc", "sm", "classloader", "jad",
            "getstatic", "monitor", "stack", "thread", "trace", "watch", "tt", "jvm", "memory", "perfcounter", "ognl",
            "mc", "redefine", "retransform", "dashboard", "dump", "heapdump", "options", "cls", "reset",
            "version", "session", "sysprop", "sysenv", "vmoption", "logger", "history", "cat", "base64", "echo", "pwd", "mbean", "grep",
            "tee", "profiler", "iler", "vmtool", "stop", "jfr");

}