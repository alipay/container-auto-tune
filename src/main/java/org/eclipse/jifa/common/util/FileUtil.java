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
package org.eclipse.jifa.common.util;

import org.eclipse.jifa.common.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.eclipse.jifa.common.util.ErrorUtil.throwEx;

public class FileUtil {

    public static String content(File f) {
        String result = null;
        try {
            result = content(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            throwEx(e);
        }
        return result;
    }

    public static String content(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(Constant.LINE_SEPARATOR);
            }
        } catch (IOException e) {
            throwEx(e);
        }
        return sb.toString();
    }

    public static void write(File f, String msg, boolean append) {
        try (FileWriter fw = new FileWriter(f, append)) {
            fw.write(msg);
        } catch (IOException e) {
            throwEx(e);
        }
    }
}
