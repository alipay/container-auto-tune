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
package org.eclipse.jifa.common.listener;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DefaultProgressListener implements ProgressListener {

    private StringBuffer log = new StringBuffer();

    private int total;

    private int done;

    private String lastSubTask;

    private void append(String msg) {
        log.append(msg);
        log.append(System.lineSeparator());
    }

    @Override
    public void beginTask(String s, int i) {
        total += i;
        if (s != null && s.length() > 0) {
            append(String.format("[Begin task] %s", s));
        }
    }

    @Override
    public void subTask(String s) {
        if (lastSubTask == null || !lastSubTask.equals(s)) {
            lastSubTask = s;
            append(String.format("[Sub task] %s", s));
        }
    }

    @Override
    public void worked(int i) {
        done += i;
    }

    @Override
    public void sendUserMessage(Level level, String s, Throwable throwable) {
        StringWriter sw = new StringWriter();
        switch (level) {
            case INFO:
                sw.append("[INFO] ");
                break;
            case WARNING:
                sw.append("[WARNING] ");
                break;
            case ERROR:
                sw.append("[ERROR] ");
                break;
            default:
                sw.append("[UNKNOWN] ");
        }

        sw.append(s);

        if (throwable != null) {
            sw.append(System.lineSeparator());
            throwable.printStackTrace(new PrintWriter(sw));
        }

        append(sw.toString());
    }

    @Override
    public String log() {
        return log.toString();
    }

    @Override
    public double percent() {
        return total == 0 ? 0 : ((double) done) / ((double) total);
    }

    @Override
    public void reset() {
        log = new StringBuffer();
        total = 0 ;
        done = 0;
        lastSubTask = null;
    }
}
